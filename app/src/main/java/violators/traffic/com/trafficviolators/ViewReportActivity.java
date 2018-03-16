package violators.traffic.com.trafficviolators;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewReportActivity extends AppCompatActivity {

    private TextView vehicleNo,licenseNo,reason,fine,description,date,time,status;
    private ImageView photo;
    FloatingActionButton statusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        initialize();
        getReport();

        statusIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus();
            }
        });
    }

    public void initialize() {
        vehicleNo = (TextView) findViewById(R.id.txt_vehicleNoValue);
        licenseNo = (TextView) findViewById(R.id.txt_licenseNoValue);
        reason = (TextView) findViewById(R.id.txt_reasonValue);
        fine = (TextView) findViewById(R.id.txt_fineValue);
        description = (TextView) findViewById(R.id.txt_descriptionValue);
        date = (TextView) findViewById(R.id.txt_dateValue);
        time = (TextView) findViewById(R.id.txt_timeValue);
        photo = (ImageView) findViewById(R.id.img_photo);
        statusIndicator = (FloatingActionButton) findViewById(R.id.status);
        status = (TextView) findViewById(R.id.txt_statusValue);
    }

    public void getReport() {
        Bundle bundle = getIntent().getExtras();
        String reportID = bundle.getString("reportID");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Report");
        progressDialog.setMessage("Retrieving details...");
        progressDialog.show();

        final StorageReference pathReference =   FirebaseStorage.getInstance().getReference().child("images/reports/" + reportID);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("reports").child(reportID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Report report = dataSnapshot.getValue(Report.class);
                vehicleNo.setText(report.getVehicleNo());
                licenseNo.setText(report.getLicenseNo());
                reason.setText(report.getReason());
                fine.setText(String.valueOf(report.getFine()));
                description.setText(report.getDescription());
                time.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(report.getDatetime()));
                date.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(report.getDatetime()));

                if(report.isFinePaid()) {
                    status.setText("Paid");
                    statusIndicator.setEnabled(false);
                    statusIndicator.setImageResource(R.drawable.ic_true);
                }
                else {
                    status.setText("Pending");
                    statusIndicator.setEnabled(true);
                    statusIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDD835")));
                    statusIndicator.setImageResource(R.drawable.ic_false);

                }

                Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(pathReference).into(photo);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateStatus() {
        Bundle bundle = getIntent().getExtras();
        String reportID = bundle.getString("reportID");

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("reports");
        dbReference.child(reportID).child("finePaid").setValue(true);
        statusIndicator.setEnabled(false);
        statusIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        statusIndicator.setImageResource(R.drawable.ic_true);
    }
}
