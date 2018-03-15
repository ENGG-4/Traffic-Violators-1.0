package violators.traffic.com.trafficviolators;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

public class NewAlertActivity extends AppCompatActivity {

    private EditText txt_alertDate,txt_vehicleNo,txt_vehicleModel,txt_vehicleColor,txt_description;
    private Spinner sp_type;
    private FloatingActionButton btn_addPhoto;
    private ImageView img_photo;
    private Uri filePath;
    GPSTracker gps;

    private static final int SELECTED_PIC = 1;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newalert);
        initialize();

        txt_alertDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });

        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECTED_PIC);
            }
        });

        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null)
                    startActivity(new Intent(Intent.ACTION_VIEW,filePath));
            }
        });
    }

    private void initialize() {
        img_photo = (ImageView) findViewById(R.id.img_photo);
        sp_type = (Spinner) findViewById(R.id.sp_vehicleType);
        txt_alertDate = (EditText) findViewById(R.id.txt_date);

        txt_vehicleNo = (EditText) findViewById(R.id.txt_vehicleNo);
        txt_vehicleModel= (EditText) findViewById(R.id.txt_vehicleModel);
        txt_vehicleColor= (EditText) findViewById(R.id.txt_vehicleColor);
        txt_description = (EditText) findViewById(R.id.txt_descriptionValue);
        btn_addPhoto = (FloatingActionButton) findViewById(R.id.btn_vehiclephoto);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        txt_alertDate.setText(day + "-" + (month+1) + "-" + year);

        gps = new GPSTracker(this,NewAlertActivity.this);
    }

    private void updateDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(NewAlertActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                txt_alertDate.setText(dayOfMonth + "-" + (month+1) + "-" + year);
            }
        },mYear,mMonth,mDay).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newalert, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode == RESULT_OK) {
                    filePath = data.getData();
                    String[] projection = { MediaStore.Images.Media.DATA };

                    Cursor cursor =  NewAlertActivity.this.getContentResolver().query(filePath, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);
                    cursor.close();

                    if (!EasyPermissions.hasPermissions(this, galleryPermissions)) {
                        EasyPermissions.requestPermissions(this, "Access for storage",
                                101, galleryPermissions);
                    }

                    Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                    Drawable drawable = new BitmapDrawable(getResources(),bitmap);
                    img_photo.setImageDrawable(drawable);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_start) {
            saveData();
        }

        return super.onOptionsItemSelected(item);
    }

    private Alert getAlert() {
        String startUID = FirebaseAuth.getInstance().getUid();
        double startLatitude = 0.0;;
        double startLongitude = 0.0;;

        String closeUID = "";
        String closeDate = "";
        double closeLatitude = 0.0;
        double closeLongitude = 0.0;

        String vehicleNo = txt_vehicleNo.getText().toString();
        String vehicleModel = txt_vehicleModel.getText().toString();
        String vehicleColor = txt_vehicleColor.getText().toString();
        String vehicleType = sp_type.getSelectedItem().toString();
        String description = txt_description.getText().toString();

        startLatitude = gps.getLatitude();
        startLongitude = gps.getLongitude();

        Date startDate = new Date();
        try {
            startDate = new SimpleDateFormat("dd-MM-yy HH:mm:ss").parse(txt_alertDate.getText().toString());
        }
        catch (ParseException e) {}

        return new Alert(startUID,startDate,startLatitude,startLongitude,closeUID,closeDate,closeLatitude,closeLongitude,vehicleNo,vehicleModel,vehicleColor,vehicleType,description);
    }

    private void clearAll() {
        txt_vehicleNo.setText("");
        txt_alertDate.setText("");
        txt_description.setText("");
        txt_vehicleModel.setText("");
        txt_vehicleColor.setText("");
        img_photo.setImageDrawable(null);
        sp_type.setSelection(0);
        filePath = null;
    }

    private void saveData() {
        if (validate()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Starting Alert...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final DatabaseReference alertsDatabase = FirebaseDatabase.getInstance().getReference("alerts");
            alertsDatabase.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    final String alertID = alertsDatabase.push().getKey();
                    alertsDatabase.child(alertID).setValue(getAlert());

                    if(filePath != null) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/alerts/" + alertID);
                        ref.putFile(filePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(NewAlertActivity.this, "Alert started", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alertsDatabase.child(alertID).removeValue();
                                        Toast.makeText(NewAlertActivity.this, "Failed to start alert", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage("Initializing " + (int) progress + "%");
                                    }
                                });
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    clearAll();
                    progressDialog.dismiss();
                }
            });











        }
    }

    private boolean validate() {
        if(txt_alertDate.getText().toString().isEmpty()) {
            Toast.makeText(this,"Date is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_vehicleModel.getText().toString().isEmpty()) {
            Toast.makeText(this,"Vehicle model is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_vehicleColor.getText().toString().isEmpty()) {
            Toast.makeText(this,"Vehicle color is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_description.getText().toString().isEmpty()) {
            Toast.makeText(this,"Description is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!gps.canGetLocation()) {
            gps.showSettingsAlert();
            return false;
        }
        else
            return true;
    }
}


