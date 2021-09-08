package com.app.swagse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.CategoryListActivity;
import com.app.swagse.model.category.CategoryItem;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder> {

    Context context;
    private List<CategoryItem> categoryList;

    public CategoryRecyclerViewAdapter(Context context, List<CategoryItem> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.category_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem categoryItem = categoryList.get(position);
        Glide.with(context).load(categoryItem.getIcon()).into(holder.categoryImage);
        holder.categoryName.setText(categoryItem.getCategoryName());
        holder.categoryBg.setBackgroundColor(randomColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CategoryListActivity) context).selectCategory(categoryList.get(position).getCategoryName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView categoryImage;
        private AppCompatTextView categoryName;
        private RelativeLayout categoryBg;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryBg = itemView.findViewById(R.id.categoryBg);
        }
    }

    public int randomColor() {
        Random random = new Random(); // Probably really put this somewhere where it gets executed only once
        return random.nextInt(0xFFFFFF);
    }
}
