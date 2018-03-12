package violators.traffic.com.trafficviolators;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private List<ReportListItem> reportList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private TextView emptyText;
    private RadioGroup radioFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.rv_searchList);
        reportAdapter = new ReportAdapter(this,reportList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        initialize();
    }

    public void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        radioFilter = (RadioGroup) findViewById(R.id.radio_filter);
        final Spinner filter_option = (Spinner) findViewById(R.id.sp_filterOption);
        final EditText txt_searchValue = (EditText) findViewById(R.id.txt_searchValue);
        emptyText = (TextView) findViewById(R.id.empty_view);

        txt_searchValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchIndex;
                    if(filter_option.getSelectedItem().toString().equals("Vehicle"))
                        searchIndex = "vehicleNo";
                    else
                        searchIndex = "licenseNo";

                    searchRecord(searchIndex,txt_searchValue.getText().toString());
                    return true;
                }
                return false;
            }
        });

        radioFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_all)
                    reportAdapter.setFilter(reportList);
                else if(checkedId == R.id.rb_pending)
                    reportAdapter.setFilter(filterByStatus(reportList,false));
                else if(checkedId == R.id.rb_completed)
                    reportAdapter.setFilter(filterByStatus(reportList,true));
            }
        });
    }

    public void searchRecord(String searchIndex,String searchValue) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("reports").orderByChild(searchIndex).equalTo(searchValue.toUpperCase());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportList.clear();
                radioFilter.clearCheck();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String reportID = postSnapshot.getKey();
                    Report report = postSnapshot.getValue(Report.class);
                    String timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(report.getDatetime());
                    String dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(report.getDatetime());
                    ReportListItem item = new ReportListItem(reportID,
                            report.getVehicleNo(),
                            report.getReason(),
                            "₹ " + String.valueOf(report.getFine()),
                            report.isFinePaid(),
                            dateFormat,
                            timeFormat,
                            getItemBackground(report.getReason()));
                    reportList.add(item);
                }

                if(reportList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setAdapter(reportAdapter);
                    radioFilter.check(R.id.rb_all);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getItemBackground(String reason) {
        if(reason.toLowerCase().equals("speed limit"))
            return R.drawable.ic_speed;
        else if(reason.toLowerCase().equals("drinking and driving"))
            return R.drawable.ic_drink;
        else if(reason.toLowerCase().equals("parking violations"))
            return R.drawable.ic_parking;
        else if(reason.toLowerCase().equals("jumping signal"))
            return R.drawable.ic_signal;
        else
            return R.drawable.ic_default;
    }

    private List<ReportListItem> filterByStatus(List<ReportListItem> reportList, boolean status) {
        final List<ReportListItem> filteredList = new ArrayList<>();
        for (ReportListItem item : reportList) {
            if (item.isFinePaid() == status) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }
}
