package violators.traffic.com.trafficviolators;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initializeTabLayout();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_report, menu);
        return true;
    }

    public void initializeTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new NewReportFragment(),"New Report");
        adapter.AddFragment(new ViewReportFragment(),"View Report");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.txtLabel);
        tabContent.setText("New");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_white, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabContent);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabContent = (TextView) tabLinearLayout.findViewById(R.id.txtLabel);
        tabContent.setText("View");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabContent);
    }
}
