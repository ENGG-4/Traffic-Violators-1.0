package violators.traffic.com.trafficviolators;

import android.app.ProgressDialog;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewAlertActivity extends AppCompatActivity {
    private TextView vehicleNo,vehicleType,vehicleModel,vehicleColor,description,startdate,starttime,startuser,closedate,closetime,closeuser;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);
        initialize();
        getAlert();
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

    public void getAlert() {
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

                if(!alert.getCloseUID().isEmpty()) {
                    try {
                        String time = alert.getCloseDateTime().split(" ")[1];
                        DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
                        Date d = f1.parse(time);
                        DateFormat f2 = new SimpleDateFormat("h:mm a");
                        time = f2.format(d);
                        closetime.setText(time);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    closedate.setText(alert.getCloseDateTime().split(" ")[0]);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_update) {
            updateAlert();
        } else if(id == R.id.action_close) {
            closeAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    public Track getTrack() {
        GPSTracker gps = new GPSTracker(this,ViewAlertActivity.this);
        try {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            String sDate = day + "/" + (month+1) + "/" + year + " " + hour + ":" + min;
            Date datetime = new SimpleDateFormat("dd/MM/yy h:mm").parse(sDate);
            return  new Track(FirebaseAuth.getInstance().getUid(), datetime,gps.getLatitude(),gps.getLongitude());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return new Track();
    }

    public void updateAlert() {
        if(closeuser.getText().toString().isEmpty()) {
            Bundle bundle = getIntent().getExtras();
            String alertID = bundle.getString("alertID");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tracking");
            String trackID = ref.push().getKey();
            ref.child(alertID).child(trackID).setValue(getTrack());
        }
        else {
            Toast.makeText(this,"Alert already closed",Toast.LENGTH_SHORT).show();
        }
    }

    public void closeAlert() {
        if(closeuser.getText().toString().isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Closing alert...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Bundle bundle = getIntent().getExtras();
            String alertID = bundle.getString("alertID");

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            String sDate = day + "/" + (month+1) + "/" + year + " " + hour + ":" + min;

            GPSTracker gps = new GPSTracker(this,ViewAlertActivity.this);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("alerts");
            ref.child(alertID).child("closeUID").setValue(FirebaseAuth.getInstance().getUid());
            ref.child(alertID).child("closeLatitude").setValue(gps.getLatitude());
            ref.child(alertID).child("closeLongitude").setValue(gps.getLongitude());
            ref.child(alertID).child("closeDateTime").setValue(sDate);

            ref =  FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    closeuser.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            closedate.setText(day + "/" + (month+1) + "/" + year);
            if(c.get(Calendar.AM_PM) == Calendar.PM)
                closetime.setText(c.get(Calendar.HOUR) + ":" + min + " PM");
            else
                closetime.setText(c.get(Calendar.HOUR) + ":" + min + " AM");
            progressDialog.dismiss();
            Toast.makeText(this,"Alert closed",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Alert already closed",Toast.LENGTH_SHORT).show();
        }
    }
}