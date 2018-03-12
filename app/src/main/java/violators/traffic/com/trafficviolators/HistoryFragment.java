package violators.traffic.com.trafficviolators;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HistoryFragment extends Fragment implements SearchView.OnQueryTextListener {

    private List<ReportListItem> reportList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private TextView emptyText;
    private RadioGroup radioFilter;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history,container,false);
        setHasOptionsMenu(true);
        initialize(view);
        setReportList();
        return view;
    }

    public void initialize(View view) {


        emptyText = (TextView) view.findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_report);
        radioFilter = (RadioGroup) view.findViewById(R.id.radio_filter);

        reportAdapter = new ReportAdapter(this.getContext(),reportList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

    public void setReportList() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("reports").orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportList.clear();
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_saveReport).setVisible(false);

        MenuItem searchMenuItem = (MenuItem) menu.findItem(R.id.action_searchReport);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        reportAdapter.setFilter(reportList);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }
                });

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onQueryTextChange(String searchText) {
        final List<ReportListItem> filteredList = filterByVehicleNo(reportList, searchText);
        reportAdapter.setFilter(filteredList);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ReportListItem> filterByVehicleNo(List<ReportListItem> reportList, String query) {
        query = query.toLowerCase();
        final List<ReportListItem> filteredList = new ArrayList<>();
        for (ReportListItem item : reportList) {
            if (item.getVehicleNo().toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }
        return filteredList;
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