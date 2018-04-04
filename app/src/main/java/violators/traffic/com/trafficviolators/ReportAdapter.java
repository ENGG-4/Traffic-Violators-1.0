package violators.traffic.com.trafficviolators;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    private Context context;
    private List<ReportListItem> reportList;

    public ReportAdapter(Context context,List<ReportListItem> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ReportListItem reportItem = reportList.get(position);
        holder.image.setBackgroundResource(reportItem.getPhoto());
        holder.vehicle.setText(reportItem.getVehicleNo());
        holder.reason.setText(reportItem.getReason());
        holder.date.setText(reportItem.getReportDate());
        holder.time.setText(reportItem.getReportTime());
        holder.fine.setText(reportItem.getFine());
        holder.reportID.setText(reportItem.getReportID());
        if(reportItem.isFinePaid())
            holder.listItem.setBackgroundResource(R.drawable.card_border_paid);
        else
            holder.listItem.setBackgroundResource(R.drawable.card_border_pending);

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,ViewReportActivity.class).putExtra("reportID",reportItem.getReportID()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void setFilter(List<ReportListItem> list) {
        reportList = new ArrayList<>();
        reportList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vehicle, reason, date,time,fine,reportID;
        public ImageView image;
        public RelativeLayout listItem;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.iv_item_icon);
            vehicle = (TextView) view.findViewById(R.id.txt_item_vehicleno);
            reason = (TextView) view.findViewById(R.id.txt_item_reason);
            date = (TextView) view.findViewById(R.id.txt_item_date);
            time = (TextView) view.findViewById(R.id.txt_item_time);
            fine = (TextView) view.findViewById(R.id.txt_item_fine);
            reportID = (TextView) view.findViewById(R.id.txt_item_reportID);
            listItem = (RelativeLayout) view.findViewById(R.id.list_item);
        }
    }
}
