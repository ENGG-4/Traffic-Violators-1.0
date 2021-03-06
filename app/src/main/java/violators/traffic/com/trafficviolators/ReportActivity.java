package violators.traffic.com.trafficviolators;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Bundle bundle = getIntent().getExtras();
        initializeTabLayout(bundle.getInt("select"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_saveReport:
                return false;
            case R.id.action_searchReport:
                return false;
            default:
                break;
        }
        return false;
    }

    public void initializeTabLayout(int id) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        Bundle args = new Bundle();
        args.putInt("option",getIntent().getExtras().getInt("option"));
        HistoryFragment history = new HistoryFragment();
        history.setArguments(args);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new NewReportFragment(),"New Report");
        adapter.AddFragment(history,"History");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(id);
        tabLayout.setupWithViewPager(viewPager);
    }
}
