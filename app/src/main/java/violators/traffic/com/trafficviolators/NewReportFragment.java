package violators.traffic.com.trafficviolators;

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
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


public class NewReportFragment extends Fragment {

    private EditText txt_reportDT,txt_vehicleNo,txt_licenseNo,txt_description,txt_fine;
    private Spinner sp_reason;
    private Switch fine_paid;
    private FloatingActionButton btn_addPhoto;
    private ImageView img_photo;

    Calendar myCalendar;
    Uri filePath;
    String downloadUrl;

    private static final int SELECTED_PIC = 1;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public NewReportFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newreport, container, false);
        setHasOptionsMenu(true);

        initialize(view);

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
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR),
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

        return view;
    }

    public void initialize(View view) {
        img_photo = (ImageView) view.findViewById(R.id.img_photo);
        myCalendar = Calendar.getInstance();
        sp_reason = (Spinner) view.findViewById(R.id.sp_reason);
        txt_reportDT = (EditText) view.findViewById(R.id.txt_reportDT);
        txt_vehicleNo = (EditText) view.findViewById(R.id.txt_vehicleNo);
        txt_licenseNo = (EditText) view.findViewById(R.id.txt_driverLicense);
        txt_description = (EditText) view.findViewById(R.id.txt_description);
        txt_fine = (EditText) view.findViewById(R.id.txt_fine);
        btn_addPhoto = (FloatingActionButton) view.findViewById(R.id.btn_vehiclephoto);
        fine_paid = (Switch) view.findViewById(R.id.switch_fine);
    }

    private void updateDateTime() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        txt_reportDT.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode == RESULT_OK) {
                    filePath = data.getData();
                    String[] projection = { MediaStore.Images.Media.DATA };

                    Cursor cursor =  getActivity().getContentResolver().query(filePath, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);
                    cursor.close();

                    if (!EasyPermissions.hasPermissions(getContext(), galleryPermissions)) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_searchReport).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_saveReport) {
            saveData();
        }
        return super.onOptionsItemSelected(item);
    }

    private Report getReport(String reportID) {
        String vehicleNo = txt_vehicleNo.getText().toString().toUpperCase();
        String licenseNo = txt_licenseNo.getText().toString().toUpperCase();
        String reason = sp_reason.getSelectedItem().toString();
        String description = txt_description.getText().toString();
        int fine = Integer.parseInt(txt_fine.getText().toString());
        boolean finePaid = fine_paid.isChecked();
        Date datetime = new Date();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            datetime = new SimpleDateFormat("dd-MM-yy HH:mm:ss").parse(txt_reportDT.getText().toString());
        }
        catch(ParseException e) { }
        return new Report(vehicleNo,licenseNo,reason,description,fine,finePaid,datetime,uid);
    }

    private void clearReport() {
        txt_vehicleNo.setText("");
        txt_licenseNo.setText("");
        txt_description.setText("");
        txt_fine.setText("");
        txt_reportDT.setText("");
        img_photo.setImageDrawable(null);
        sp_reason.setSelection(0);
        fine_paid.setChecked(false);

        downloadUrl = "";
        filePath = null;
    }

    private void saveData() {
        if (validate()) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Saving report...");
            progressDialog.show();

            final DatabaseReference reportsDatabase = FirebaseDatabase.getInstance().getReference("reports");
            final String reportID = reportsDatabase.push().getKey();
            reportsDatabase.child(reportID).setValue(getReport(reportID));

            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + reportID);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            clearReport();
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            reportsDatabase.child(reportID).removeValue();
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean validate() {

        if(txt_vehicleNo.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(),"Vehicle No. is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_fine.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(),"Fine is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(txt_reportDT.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(),"Date and time is required",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
}
