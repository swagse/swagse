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
import com.app.swagse.SubscriberUserProfileActivity;
import com.app.swagse.model.subscription.SubscriptionlistItem;
import com.app.swagse.model.swagTube.SwagtubedataItem;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscriptionRecyclerViewAdapter extends RecyclerView.Adapter<SubscriptionRecyclerViewAdapter.SubscriptionViewHolder> {

    Context context;
    List<SubscriptionlistItem> subscriptionlistItemList;

    public SubscriptionRecyclerViewAdapter(Context context, List<SubscriptionlistItem> subscriptionlistItemList) {
        this.context = context;
        this.subscriptionlistItemList = subscriptionlistItemList;
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubscriptionViewHolder(LayoutInflater.from(context).inflate(R.layout.subscription_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        SubscriptionlistItem subscriptionlistItem = subscriptionlistItemList.get(position);
        if (subscriptionlistItem.getChannelName().equals("")) {
            holder.subscriberName.setText(subscriptionlistItem.getUserName());
        } else {
            holder.subscriberName.setText(subscriptionlistItem.getChannelName());
        }
        if (!subscriptionlistItem.getImg().equals("")) {
            Glide.with(context).load(subscriptionlistItem.getImg()).placeholder(R.drawable.ic_user).into(holder.subscriberPic);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, SubscriberUserProfileActivity.class).putExtra(SubscriptionRecyclerViewAdapter.class.getSimpleName(),subscriptionlistItemList.get(position).getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionlistItemList == null ? 0 : subscriptionlistItemList.size();
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView subscriberName;
        CircleImageView subscriberPic;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            subscriberName = itemView.findViewById(R.id.subscriberName);
            subscriberPic = itemView.findViewById(R.id.subscriberPic);
        }
    }
}
