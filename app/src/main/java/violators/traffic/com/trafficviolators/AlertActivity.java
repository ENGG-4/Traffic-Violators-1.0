package violators.traffic.com.trafficviolators;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlertActivity extends AppCompatActivity {

    private List<AlertListItem> alertList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertAdapter alertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        initializeList();
        setAlertList();
    }

    public void initializeList() {
        recyclerView = (RecyclerView) findViewById(R.id.rv_alertList);
        alertAdapter = new AlertAdapter(this,alertList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void setAlertList() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("alerts");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String alertID = postSnapshot.getKey();
                    Alert alert = postSnapshot.getValue(Alert.class);
                    AlertListItem item = new AlertListItem(alertID,
                            alert.getVehicleNo(),
                            alert.getVehicleModel(),
                            alert.getVehicleColor(),
                            getItemBackground(alert.getVehicleType()));
                    alertList.add(item);
                }
                recyclerView.setAdapter(alertAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getItemBackground(String type) {
        if(type.equals("Scooter"))
            return R.drawable.ic_scooter;
        else if(type.equals("Bike"))
            return R.drawable.ic_bike;
        else if(type.equals("Car"))
            return R.drawable.ic_car;
        else if(type.equals("Van"))
            return R.drawable.ic_van;
        else if(type.equals("Bus"))
            return R.drawable.ic_bus;
        else if(type.equals("Auto Rickshaw"))
            return R.drawable.ic_rickshaw;
        else if(type.equals("Truck"))
            return R.drawable.ic_truck;
        else
            return R.drawable.ic_default;
    }
}
