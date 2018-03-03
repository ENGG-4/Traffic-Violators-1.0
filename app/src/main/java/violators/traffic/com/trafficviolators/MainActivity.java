package violators.traffic.com.trafficviolators;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNavigationDrawer();
        initializeTabLayout();
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
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("users").child(user.getUid()).child("name");

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

    public void initializeTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new DashboardFragment(),"Dashboard");
        adapter.AddFragment(new ReportFragment(),"Report");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.txtLabel);
        tabContent.setText("Dashboard");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dashboard_white, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabContent);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabContent = (TextView) tabLinearLayout.findViewById(R.id.txtLabel);
        tabContent.setText("Report");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_white, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabContent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_driver) {
            startActivity(new Intent(MainActivity.this,DriverActivity.class));
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(MainActivity.this,NewReportActivity.class));
        } else if (id == R.id.nav_pending) {
            startActivity(new Intent(MainActivity.this,PendingActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
