package com.app.swagse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.swagse.R;
import com.app.swagse.activity.ShowPhoneNumberActivity;
import com.app.swagse.activity.SubcriptionPackageActivity;
import com.app.swagse.activity.UploadSwagTubeVideoActivity;
import com.app.swagse.activity.VerifyOTPActivity;
import com.app.swagse.constants.Constants;
import com.app.swagse.controller.App;
import com.app.swagse.helper.OwnerGlobal;
import com.app.swagse.model.SubPackageResponse;
import com.app.swagse.model.subPackage.PackageResponse;
import com.app.swagse.model.subPackage.SubscriptionItem;
import com.app.swagse.network.Api;
import com.app.swagse.network.RetrofitClient;
import com.app.swagse.sharedpreferences.PrefConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.swagse.helper.OwnerGlobal.toast;

public class PackageRecyclerViewAdapter extends RecyclerView.Adapter<PackageRecyclerViewAdapter.PackageViewHolder> {

    Context context;
    List<SubscriptionItem> packageList;

    public PackageRecyclerViewAdapter(Context context, List<SubscriptionItem> packageList) {
        this.context = context;
        this.packageList = packageList;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PackageViewHolder(LayoutInflater.from(context).inflate(R.layout.package_layout_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        SubscriptionItem subscriptionItem = packageList.get(position);
        holder.packageName.setText(subscriptionItem.getTitle());
        holder.packagePrice.setText("₹ " + subscriptionItem.getPrice());
//        holder.credentials_card.setBackgroundColor(Color.parseColor(subscriptionItem.getColorCode()));
        holder.packagePlan.setText("Valid for " + subscriptionItem.getDuration() + " days");
        holder.videolenth.setText("Upload max video length " + subscriptionItem.getVideoLength() + " sec");
        holder.videosize.setText("Upload max video size " + subscriptionItem.getVideoSize() + " MB");
//        holder.packageDes.setText(Html.fromHtml(subscriptionItem.getPackageDetails()));
        holder.packageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Your Subscription");
                builder.setMessage("Do you want to Buy this subscription. So please confirm is order.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ((SubcriptionPackageActivity) context).openPaymentGetWay(packageList.get(position).getId(),subscriptionItem.getPrice());
//                        requestForPackage(packageList.get(position).getId());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return packageList == null ? 0 : packageList.size();
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView videosize, videolenth, packageDes, packageStatus, packagePrice, packageName, packagePlan;
        AppCompatButton packageDetails;
        CardView credentials_card;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            videolenth = itemView.findViewById(R.id.videolenth1);
            packagePrice = itemView.findViewById(R.id.packagePrice);
            packageName = itemView.findViewById(R.id.packageName);
            packagePlan = itemView.findViewById(R.id.packagePlan);
            credentials_card = itemView.findViewById(R.id.credentials_card);
            videosize = itemView.findViewById(R.id.videosize);
            packageDetails = itemView.findViewById(R.id.packageDetails);
        }
    }
}
