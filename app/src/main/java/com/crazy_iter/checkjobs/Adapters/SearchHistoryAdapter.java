package com.crazy_iter.checkjobs.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crazy_iter.checkjobs.Data.SearchHistoryItem;
import com.crazy_iter.checkjobs.R;

import java.util.ArrayList;

/**
 * Created by CrazyITer on 4/10/2018.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchHistoryItem> searchHistoryItems;

    public SearchHistoryAdapter(Context context, ArrayList<SearchHistoryItem> searchHistoryItems) {
        this.context = context;
        this.searchHistoryItems = searchHistoryItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show();
            }
        });

        holder.searchWord.setText(searchHistoryItems.get(position).getSearchWord());
        holder.place.setText(searchHistoryItems.get(position).getPlace());
        holder.salaryCurrency.setText(searchHistoryItems.get(position).getSalaryCurrency());
        holder.hours.setText(searchHistoryItems.get(position).getHours());
        holder.days.setText(searchHistoryItems.get(position).getDays());
        holder.gender.setText(searchHistoryItems.get(position).getGender());

    }

    @Override
    public int getItemCount() {
        return searchHistoryItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout searchItemLL;
        private TextView searchWord, place, salaryCurrency, hours, days, gender;
        private ImageView deleteItem;

        ViewHolder(View itemView) {
            super(itemView);
            searchItemLL = itemView.findViewById(R.id.searchItemLL);
            searchWord = itemView.findViewById(R.id.searchWordItem);
            place = itemView.findViewById(R.id.placeSearchItem);
            salaryCurrency = itemView.findViewById(R.id.salaryCurrencySearchItem);
            hours = itemView.findViewById(R.id.hoursSearchItem);
            days = itemView.findViewById(R.id.daysSearchItem);
            gender = itemView.findViewById(R.id.genderSearchItem);
            deleteItem = itemView.findViewById(R.id.clearSearchItem);
        }
    }
}
