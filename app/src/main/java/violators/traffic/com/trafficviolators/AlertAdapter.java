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

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder> {

    private Context context;
    private List<AlertListItem> alertList;

    public AlertAdapter(Context context,List<AlertListItem> alertList) {
        this.context = context;
        this.alertList = alertList;
    }

    @Override
    public AlertAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new AlertAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlertAdapter.MyViewHolder holder, int position) {
        final AlertListItem alertItem = alertList.get(position);
        holder.image.setBackgroundResource(alertItem.getPhoto());
        holder.vehicle.setText(alertItem.getVehicleNo());
        holder.model.setText(alertItem.getVehicleModel());
        holder.color.setText(alertItem.getVehicleColor());
        holder.alertID.setText(alertItem.getAlertID());

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,ViewAlertActivity.class).putExtra("alertID",alertItem.getAlertID()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public void setFilter(List<AlertListItem> list) {
        alertList = new ArrayList<>();
        alertList.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vehicle, model, color,alertID;
        public ImageView image;
        public RelativeLayout listItem;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.iv_item_icon);
            vehicle = (TextView) view.findViewById(R.id.txt_item_vehicleNo);
            model = (TextView) view.findViewById(R.id.txt_item_modelValue);
            color = (TextView) view.findViewById(R.id.txt_item_colorValue);
            alertID = (TextView) view.findViewById(R.id.txt_item_alertID);
            listItem = (RelativeLayout) view.findViewById(R.id.list_item);
        }
    }
}
