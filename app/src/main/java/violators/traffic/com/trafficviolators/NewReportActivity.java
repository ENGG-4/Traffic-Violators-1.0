package violators.traffic.com.trafficviolators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import pub.devrel.easypermissions.EasyPermissions;

public class NewReportActivity extends AppCompatActivity {

    private EditText txt_reportDT,txt_vehicleNo,txt_licenseNo,txt_description,txt_fine;
    private Spinner sp_reason;
    private FloatingActionButton btn_addPhoto;
    private ImageView img_photo;

    Calendar myCalendar;
    Uri filePath;
    String downloadUrl;

    private static final int SELECTED_PIC = 1;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newreport);

        initialize();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTime();
            }
        };

        txt_reportDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewReportActivity.this, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

    //Initialize all widgets
    public void initialize() {
        img_photo = (ImageView)findViewById(R.id.img_photo);
        myCalendar = Calendar.getInstance();
        sp_reason = (Spinner) findViewById(R.id.sp_reason);
        txt_reportDT = (EditText) findViewById(R.id.txt_reportDT);
        txt_vehicleNo = (EditText) findViewById(R.id.txt_vehicleNo);
        txt_licenseNo = (EditText) findViewById(R.id.txt_driverLicense);
        txt_description = (EditText) findViewById(R.id.txt_description);
        txt_fine = (EditText) findViewById(R.id.txt_fine);
        btn_addPhoto = (FloatingActionButton) findViewById(R.id.btn_vehiclephoto);
    }

    //Updates date and time in edittext after selection in datetime picker
    private void updateDateTime() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        txt_reportDT.setText(sdf.format(myCalendar.getTime()));
    }

    //function to get image from gallery and set as source to imageview widget
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode == RESULT_OK) {
                    filePath = data.getData();
                    String[] projection = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(filePath, projection, null, null, null);
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

    //function to add save button in actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //function to process selection of option in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_saveReport) {
           saveData();
        }
        return super.onOptionsItemSelected(item);
    }

    //function to create report object and put values in it
    private Report getReport(String reportID,String photoUrl) {
        String vehicleNo = txt_vehicleNo.getText().toString();
        String licenseNo = txt_licenseNo.getText().toString();
        String reason = sp_reason.getSelectedItem().toString();
        String description = txt_description.getText().toString();
        int fine = Integer.parseInt(txt_fine.getText().toString());
        Date datetime = new Date();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            datetime = new SimpleDateFormat("dd-MM-yy HH:mm:ss").parse(txt_reportDT.getText().toString());
        }
        catch(ParseException e) { }
        return new Report(vehicleNo,licenseNo,reason,description,fine,datetime,photoUrl,uid);
    }

    //function to clear report form
    private void clearReport() {
        txt_vehicleNo.setText("");
        txt_licenseNo.setText("");
        txt_description.setText("");
        txt_fine.setText("");
        txt_reportDT.setText("");
        img_photo.setImageDrawable(null);
        sp_reason.setSelection(0);

        downloadUrl = "";
        filePath = null;
    }

    //function to save report data in firebase
    private void saveData() {
        if (validate()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving report...");
            progressDialog.show();

            StorageReference ref = storageRef.child("images/" + txt_vehicleNo.getText().toString() + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();

                            DatabaseReference reportsDatabase = FirebaseDatabase.getInstance().getReference("reports");
                            String reportID = reportsDatabase.push().getKey();

                            reportsDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(reportID).setValue(getReport(reportID,downloadUrl));

                            clearReport();
                            progressDialog.dismiss();
                            Toast.makeText(NewReportActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(NewReportActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    //function to validate fields
    private boolean validate() {

        if(txt_vehicleNo.getText().toString().isEmpty()) {
            Toast.makeText(NewReportActivity.this,"Vehicle No. is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_fine.getText().toString().isEmpty()) {
            Toast.makeText(NewReportActivity.this,"Fine is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_reportDT.getText().toString().isEmpty()) {
            Toast.makeText(NewReportActivity.this,"Date and time is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
}