package violators.traffic.com.trafficviolators;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private List<AlertListItem> alertList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertAdapter alertAdapter;
    private TextView displayName,txt_Total,txt_Pending,txt_Completed;
    int countTotalReport = 0,countPendingReport = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_Total = (TextView) findViewById(R.id.txt_totalReports);
        txt_Pending = (TextView) findViewById(R.id.txt_totalPending);
        txt_Completed = (TextView) findViewById(R.id.txt_totalCompleted);

        FloatingActionButton fab_report = (FloatingActionButton) findViewById(R.id.fab_report);
        fab_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this ,ReportActivity.class).putExtra("select",0).putExtra("option",0));
            }
        });

        FloatingActionButton fab_alert = (FloatingActionButton) findViewById(R.id.fab_alert);
        fab_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewAlertActivity.class));
            }
        });

        LinearLayout btn_reportsAll = (LinearLayout) findViewById(R.id.btn_reportsAll);
        btn_reportsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ReportActivity.class).putExtra("select",1).putExtra("option",0));
            }
        });

        LinearLayout btn_reportsPending = (LinearLayout) findViewById(R.id.btn_reportsPending);
        btn_reportsPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ReportActivity.class).putExtra("select",1).putExtra("option",1));
            }
        });

        LinearLayout btn_reports_Completed = (LinearLayout) findViewById(R.id.btn_reportsCompleted);
        btn_reports_Completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ReportActivity.class).putExtra("select",1).putExtra("option",2));
            }
        });

        initializeNavigationDrawer();
        initializeList();
        getStatistics();
        setAlertList();
    }

    public void initializeNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        displayName = (TextView) header.findViewById(R.id.txt_nav_name);
        TextView displayEmail = (TextView) header.findViewById(R.id.txt_nav_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("name");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    displayName.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            displayEmail.setText(user.getEmail());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search)
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            startActivity(new Intent(MainActivity.this,SearchActivity.class));
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(MainActivity.this,ReportActivity.class).putExtra("select",0).putExtra("option",0));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this,ReportActivity.class).putExtra("select",1).putExtra("option",0));
        } else if (id == R.id.nav_alert) {
            startActivity(new Intent(MainActivity.this,AlertActivity.class));
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("alerts");
        Query query = databaseRef.orderByChild("closeUID").equalTo("");
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

    public void getStatistics() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("reports").orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class);
                    if(!report.isFinePaid())
                        countPendingReport++;

                    countTotalReport++;
                }
                txt_Total.setText(String.valueOf(countTotalReport));
                txt_Pending.setText(String.valueOf(countPendingReport));
                txt_Completed.setText(String.valueOf(countTotalReport - countPendingReport));
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
