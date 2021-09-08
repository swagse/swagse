package com.app.swagse.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.model.swagTube.CommentdataItem;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwagTubeCommentAdapter extends RecyclerView.Adapter<SwagTubeCommentAdapter.SwagTubeCommentViewHolder> {

    Context context;
    private List<CommentdataItem> commentDataList;

    public SwagTubeCommentAdapter(Context context, List<CommentdataItem> commentDataList) {
        this.context = context;
        this.commentDataList = commentDataList;
    }

    @NonNull
    @Override
    public SwagTubeCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SwagTubeCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.swagtube_comment_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SwagTubeCommentViewHolder holder, int position) {
        CommentdataItem commentdataItem = commentDataList.get(position);
        holder.comment_user_name.setText(commentdataItem.getCommentName());
        holder.comment_user_time.setText(commentdataItem.getCommentTimeago());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.comment_user_message.setText(Html.fromHtml(commentdataItem.getComment(),Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.comment_user_message.setText(Html.fromHtml(commentdataItem.getComment()));
        }
//        holder.comment_user_message.setText(commentdataItem.getComment());
        Glide.with(context).load(commentdataItem.getCommentPic()).placeholder(R.drawable.ic_user).into(holder.comment_user_pic);
    }

    public void notifyList(List<CommentdataItem> commentDataList1) {
        this.commentDataList.clear();
        this.commentDataList.addAll(commentDataList1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return commentDataList == null ? 0 : commentDataList.size();
    }

    public class SwagTubeCommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView comment_user_pic;
        AppCompatTextView comment_user_name, comment_user_time, textViewOptions, comment_user_message;

        public SwagTubeCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            comment_user_pic = itemView.findViewById(R.id.comment_user_pic);
            comment_user_name = itemView.findViewById(R.id.comment_user_name);
            comment_user_time = itemView.findViewById(R.id.comment_user_time);
            textViewOptions = itemView.findViewById(R.id.textViewOptions);
            comment_user_message = itemView.findViewById(R.id.comment_user_message);
        }
    }
}
