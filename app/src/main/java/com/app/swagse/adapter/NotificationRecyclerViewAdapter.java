package com.app.swagse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.SubscriberUserProfileActivity;
import com.app.swagse.activity.SwagTubeDetailsActivity;
import com.app.swagse.model.notification.NotificationResponse;
import com.app.swagse.model.notification.NotificationsItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.NotificationViewHolder> {

    Context context;
    List<NotificationsItem> notificationItemList;
    public static final String TAG = NotificationRecyclerViewAdapter.class.getSimpleName();

    public NotificationRecyclerViewAdapter(Context context, List<NotificationsItem> notificationItemList) {
        this.context = context;
        this.notificationItemList = notificationItemList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_layout_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        NotificationsItem notificationItem = notificationItemList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(notificationItem.getDate());
            format = new SimpleDateFormat("MMM dd");
            Log.e("date", format.format(date));
            holder.notification_date.setText(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            holder.notification_time.setText(DateUtils.formatDateFromDateString(DateUtils.DATE_FORMAT_8, DateUtils.DATE_FORMAT_1, notificationItem.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.notification_message.setText(notificationItem.getMessage());
        if (notificationItem.getStatus().equals("0")) {
            holder.notification_layout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        } else if (notificationItem.getStatus().equals("1")) {
            holder.notification_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        if (notificationItem.getStatus().equals("Unread")) {
            holder.notification_layout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        } else if (notificationItem.getStatus().equals("Read")) {
            holder.notification_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsReadNotification(position);
            }
        });
    }

    private void markAsReadNotification(int position) {
        Api apiInterface = RetrofitClient.getInstance().getApi();
        Call<NotificationResponse> notificationResponseCall = apiInterface.changeNotificationStatus(notificationItemList.get(position).getId());
        notificationResponseCall.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("1")) {
                        String action = notificationItemList.get(position).getAction();
                        if (action != null) {
                            String[] type = action.split("-");
                            String id = type[0];
                            String from = type[1];
                            if (from.equals("swagtube_followers")) {
                                context.startActivity(new Intent(context, SwagTubeDetailsActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), id));
                            } else if (from.equalsIgnoreCase("swagtube_post")) {
                                context.startActivity(new Intent(context, SubscriberUserProfileActivity.class).putExtra(NotificationRecyclerViewAdapter.class.getSimpleName(), id));
                            }

                            if (notificationItemList.get(position).getStatus().equals("0")) {
                                notificationItemList.get(position).setStatus("1");
                                notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout notification_layout;
        TextView notification_time, notification_message, notification_date;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_layout = itemView.findViewById(R.id.notification_layout);
            notification_time = itemView.findViewById(R.id.notification_time);
            notification_message = itemView.findViewById(R.id.notification_message);
            notification_date = itemView.findViewById(R.id.notification_date);
        }
    }
}
