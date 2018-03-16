package violators.traffic.com.trafficviolators;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewAlertActivity extends AppCompatActivity {
    private TextView vehicleNo,vehicleType,vehicleModel,vehicleColor,description,startdate,starttime,startuser,closedate,closetime,closeuser;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);
        initialize();
        getReport();
    }

    public void initialize() {
        vehicleNo = (TextView) findViewById(R.id.txt_vehicleNoValue);
        vehicleType = (TextView) findViewById(R.id.txt_typeValue);
        vehicleModel = (TextView) findViewById(R.id.txt_modelValue);
        vehicleColor = (TextView) findViewById(R.id.txt_colorValue);
        description = (TextView) findViewById(R.id.txt_descriptionValue);
        photo = (ImageView) findViewById(R.id.img_photo);

        startuser = (TextView) findViewById(R.id.txt_startUserValue);
        startdate = (TextView) findViewById(R.id.txt_startDateValue);
        starttime = (TextView) findViewById(R.id.txt_startTimeValue);

        closeuser = (TextView) findViewById(R.id.txt_closeUserValue);
        closedate = (TextView) findViewById(R.id.txt_closeDateValue);
        closetime = (TextView) findViewById(R.id.txt_closeTimeValue);
    }

    public void getReport() {
        Bundle bundle = getIntent().getExtras();
        String alertID = bundle.getString("alertID");
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Alert");
        progressDialog.setMessage("Retrieving details...");
        progressDialog.show();

        final StorageReference pathReference =   FirebaseStorage.getInstance().getReference().child("images/alerts/" + alertID);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("alerts").child(alertID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alert alert = dataSnapshot.getValue(Alert.class);
                vehicleNo.setText(alert.getVehicleNo());
                vehicleType.setText(alert.getVehicleType());
                vehicleModel.setText(alert.getVehicleModel());
                vehicleColor.setText(alert.getVehicleColor());
                description.setText(alert.getDescription());

                Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(pathReference).into(photo);

                //set start user record
                starttime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(alert.getStartDateTime()));
                startdate.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(alert.getStartDateTime()));
                Query query2 = FirebaseDatabase.getInstance().getReference().child("users").child(alert.getStartUID());
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        startuser.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });


                //set close user record
                if(!alert.getCloseUID().isEmpty()) {
                    closetime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(Date.valueOf(alert.getCloseDateTime())));
                    closedate.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(Date.valueOf(alert.getCloseDateTime())));
                    Query query3 = FirebaseDatabase.getInstance().getReference().child("users").child(alert.getCloseUID());
                    query3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            closeuser.setText(user.getName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
