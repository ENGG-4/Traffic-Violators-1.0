package violators.traffic.com.trafficviolators;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Spinner filter_option = (Spinner) findViewById(R.id.sp_filterOption);
        final EditText txt_searchValue = (EditText) findViewById(R.id.txt_searchValue);

        recyclerView = (RecyclerView) findViewById(R.id.rv_searchList);
        reportAdapter = new ReportAdapter(this,reportList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
    }

    public void searchRecord(String searchIndex,String searchValue) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = databaseRef.child("reports").orderByChild(searchIndex).equalTo(searchValue);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reportList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class);
                    String timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(report.getDatetime());
                    String dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH).format(report.getDatetime());
                    ReportListItem item = new ReportListItem(report.getVehicleNo(),report.getReason(),"₹ " + String.valueOf(report.getFine()),dateFormat,timeFormat,getItemBackground(report.getReason()));
                    reportList.add(item);
                }
                recyclerView.setAdapter(reportAdapter);
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
}
