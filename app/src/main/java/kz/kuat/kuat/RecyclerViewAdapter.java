package kz.kuat.kuat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.LinkedList;
import java.util.List;

import com.macroyau.thingspeakandroid.model.Channel;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ThingSpeakConnection.DataUpdateListener {

    private RecyclerViewAdapterOnClickHandler recyclerViewAdapterOnClickHandler;
    private ThingSpeakConnection thingSpeakConnection;
    private List<ListItem> listItems;

    public RecyclerViewAdapter(RecyclerViewAdapterOnClickHandler recyclerViewAdapterOnClickHandler, ThingSpeakConnection thingSpeakConnection) {
        this.thingSpeakConnection = thingSpeakConnection;
        this.recyclerViewAdapterOnClickHandler = recyclerViewAdapterOnClickHandler;
        this.listItems = new LinkedList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LineChart lineChart = holder.lineChart;
        lineChart.setTouchEnabled(false);
        TextView chartName = holder.chartName;
        ListItem listItem = listItems.get(position);
        CustomChart customChart = new CustomChart(lineChart, listItem.getFieldNum(), thingSpeakConnection);
        customChart.setTitle(chartName);
        listItem.setCustomChart(customChart);
        customChart.onDataUpdated();
    }

    @Override
    public int getItemCount() {
        if (listItems == null) return 0;
        return listItems.size();
    }

    public List<ListItem> getListItems() {
        return this.listItems;
    }

    public void setListItems(List<ListItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public void onDataUpdated() {
        Channel channel = thingSpeakConnection.getChannel();
        if (channel != null) for (int i = 1; i <= 8; i++) {
            if (channel.getFieldName(i) != null && !isFieldNumExist(i)) {
                listItems.add(new ListItem(i));
                notifyItemInserted(listItems.size() - 1);
            }
        }
        for (ListItem listItem : listItems) {
            if (listItem.getCustomChart() != null) listItem.getCustomChart().onDataUpdated();
        }
    }

    private boolean isFieldNumExist(int fieldNum) {
        for (ListItem listItem : listItems) {
            if (listItem.getFieldNum() == fieldNum) return true;
        }
        return false;
    }

    interface RecyclerViewAdapterOnClickHandler {
        void onClick(int fieldNum);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView chartName;
        LineChart lineChart;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            chartName = itemView.findViewById(R.id.chartName);
            lineChart = itemView.findViewById(R.id.chartPreview);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            recyclerViewAdapterOnClickHandler.onClick(listItems.get(adapterPosition).getFieldNum());
        }
    }
}
