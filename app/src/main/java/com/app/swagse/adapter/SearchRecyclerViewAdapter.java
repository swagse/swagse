package com.app.swagse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.model.swagTube.SwagtubedataItem;

import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder> {

    Context context;
    List<SwagtubedataItem> swagTubeDataList;

    public SearchRecyclerViewAdapter(Context context, List<SwagtubedataItem> swagTubeDataList) {
        this.context = context;
        this.swagTubeDataList = swagTubeDataList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.searc_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SwagtubedataItem swagtubedataItem = swagTubeDataList.get(position);
        holder.searchTitle.setText(swagtubedataItem.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SwagTubeDetailsActivity.class).putExtra(SearchRecyclerViewAdapter.class.getSimpleName(), swagTubeDataList.get(position).getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return swagTubeDataList == null ? 0 : swagTubeDataList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView searchTitle;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            searchTitle = itemView.findViewById(R.id.searchTitle);
        }
    }
}
