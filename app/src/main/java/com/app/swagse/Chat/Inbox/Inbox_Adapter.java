package com.app.swagse.Chat.Inbox;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.SimpleClasses.Functions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;


public class Inbox_Adapter extends RecyclerView.Adapter<Inbox_Adapter.CustomViewHolder > implements Filterable {
    public Context context;
    ArrayList<Inbox_Get_Set> inbox_dataList = new ArrayList<>();
    ArrayList<Inbox_Get_Set> inbox_dataList_filter = new ArrayList<>();
    private OnItemClickListener listener;
    private OnLongItemClickListener longlistener;

    Integer today_day=0;

    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(Inbox_Get_Set item);
    }
    public interface OnLongItemClickListener{
        void onLongItemClick(Inbox_Get_Set item);
    }

    public Inbox_Adapter(Context context, ArrayList<Inbox_Get_Set> user_dataList, OnItemClickListener listener, OnLongItemClickListener longlistener) {
        this.context = context;
        this.inbox_dataList=user_dataList;
        this.inbox_dataList_filter=user_dataList;
        this.listener = listener;
        this.longlistener=longlistener;

        // get the today as a integer number to make the dicision the chat date is today or yesterday
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inbox_list,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return inbox_dataList_filter.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username,last_message,date_created;
        ImageView user_image;

        public CustomViewHolder(View view) {
            super(view);
            user_image=itemView.findViewById(R.id.user_image);
            username=itemView.findViewById(R.id.username);
            last_message=itemView.findViewById(R.id.message);
            date_created=itemView.findViewById(R.id.datetxt);
        }

        public void bind(final Inbox_Get_Set item, final OnItemClickListener listener,final  OnLongItemClickListener longItemClickListener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longItemClickListener.onLongItemClick(item);
                    return true;
                }
            });

        }

    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {

        final Inbox_Get_Set item=inbox_dataList_filter.get(i);
        holder.username.setText(item.getName());
        holder.last_message.setText(item.getMsg());
        holder.date_created.setText(Functions.ChangeDate_to_today_or_yesterday(context,item.getDate()));

        if(item.getPic()!=null && !item.getPic().equals(""))
        Picasso.get().
                load(item.getPic())
                .resize(100,100)
                .placeholder(R.drawable.image_placeholder).into(holder.user_image);


        // check the status like if the message is seen by the receiver or not
        String status = "" + item.getStatus();
        if (status.equals("0")) {
            holder.last_message.setTypeface(null, Typeface.BOLD);
            holder.last_message.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.last_message.setTypeface(null, Typeface.NORMAL);
            holder.last_message.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }


        holder.bind(item,listener,longlistener);

   }




    // that function will filter the result
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    inbox_dataList_filter = inbox_dataList;
                } else {
                    ArrayList<Inbox_Get_Set> filteredList = new ArrayList<>();
                    for (Inbox_Get_Set row : inbox_dataList) {

                          if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    inbox_dataList_filter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = inbox_dataList_filter;
                return filterResults;

            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                inbox_dataList_filter = (ArrayList<Inbox_Get_Set>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}