package com.app.swagse.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.app.swagse.R;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fretchain.R;
import com.fretchain.common.app.App;
import com.fretchain.owner.models.UserRoute;
import com.fretchain.owner.ownerCommon.callBacks.OkCallBack;
import com.fretchain.owner.ownerCommon.web.OwnerAPIClient;
import com.fretchain.owner.ownerCommon.web.OwnerApiService;
import com.fretchain.owner.ui.OwnerAddVehicleActivity;
import com.fretchain.owner.ui.OwnerRegisterActivity;
import com.fretchain.owner.ui.OwnerSplashActivity;
import com.fretchain.owner.ui.dialogs.OwnerSelectItemDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;*/

public class OwnerGlobal {
    private static String truckId, truckName, cityId, cityName;
//    Polyline line;
//    List<UserRoute.Routes> routesDetails = new ArrayList<>();
//    List<LatLng> route = new ArrayList<>();
//    private ArrayList<UserRoute.Legs> Legs=new ArrayList<>();
//    private ArrayList<UserRoute.Steps> Steps=new ArrayList<>();
//    OwnerApiService apiService;


 /*   public static BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        Drawable vectorDrawable = context.getResources().getDrawable(id);
        vectorDrawable.setBounds(0, 0, 100, 100);
        Bitmap bm = Bitmap.createBitmap(100, 100, Bitmap.OwnerConfig.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }
*/
   /* public static BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        BitmapDrawable bitmapDrawable=(BitmapDrawable)context.getResources().getDrawable(id);
        Bitmap bitmap=bitmapDrawable.getBitmap();
        return  BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void getItemSelectDialog(final Context context, FragmentManager fragmentManager, final String itemType, final EditText editText) {
        final OwnerSelectItemDialog dialog = new OwnerSelectItemDialog();
        dialog.setCallBack(new OkCallBack() {
            @Override
            public void okClicked(Bundle bundle) {

                if (itemType.equals(Constants.CITIES)) {
                    cityId = bundle.getString(Constants.CITY_ID);
                    cityName = bundle.getString(Constants.CITY_NAME);
                    editText.setText(cityName);
                    OwnerRegisterActivity.cityId = cityId;
                } else {
                    truckId = bundle.getString(Constants.TRUCK_ID);
                    truckName = bundle.getString(Constants.TRUCK_NAME);
                    editText.setText(truckName);
                    OwnerAddVehicleActivity.truckTypeId = truckId;
                }
                dialog.dismiss();
            }

        });
        dialog.setCancelable(true);
        dialog.setMessage(itemType);
        if (itemType.equals(Constants.CITIES)) {
            dialog.setTitle(context.getString(R.string.select_city));
        } else {
            dialog.setTitle(context.getString(R.string.truck_type_select));
        }

        dialog.show(fragmentManager, "ItemSelect");

    }

    public void getDirections(final Context context, final LatLng pickup, final LatLng drop, final GoogleMap mMap) {
        apiService = OwnerAPIClient.getGoogleRetrofit().create(OwnerApiService.class);
        if (pickup != null && drop != null) {
            final String origin = "" + pickup.latitude + "," + pickup.longitude;
            String destination = "" + drop.latitude + "," + drop.longitude;

            if (App.getmInstance().isOnline()) {
                Call<UserRoute> call = apiService.getRoute(Constants.GOOGLEAPI_KEY,origin, destination);
                call.enqueue(new Callback<UserRoute>() {
                    @Override
                    public void onResponse(Call<UserRoute> call, Response<UserRoute> response) {
                        if (response.body().getStatus().equalsIgnoreCase("OK")) {
                            Log.e("latlng", "getDirections: origin" + origin);
                            if (line != null) {
                                line.remove();
                            }

                            routesDetails.clear();
                            route.clear();
                            routesDetails.addAll(response.body().getRoutes());

                            //get values using Overview_polyLine
                            *//*route.addAll(PolyUtil.decode(routesDetails.get(0).getOverview_polyline().getPoints()));
                            Log.e("NewBookingFrag", "onResponse:UserRoute " + route);
                            line = mMap.addPolyline(new PolylineOptions()
                                    .width(6)
                                    .color(context.getResources().getColor(R.color.colorDarkOrange)));
                            line.setPoints(route);*//*

                            //get values using legs Value
                            parseData();

                            line = mMap.addPolyline(new PolylineOptions()
                                    .width(6)
                                    .color(context.getResources().getColor(R.color.colorDarkOrange)));
                            line.setPoints(route);

                            //Zoom camera
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(pickup).include(drop);
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), 20);
                            mMap.animateCamera(cu);

                        } else if (response.body().getStatus().equalsIgnoreCase("OVER_QUERY_LIMIT")) {
                            Toast.makeText(context, "UserRoute Limit Exceeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("latlng", "getDirections: fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRoute> call, Throwable t) {
                        Log.e("NewBookingFrag", "onResponse:MapException " + t);
                    }
                });
            } else {
                OwnerGlobal.networkToast(context);
            }
        }
    }

    private void parseData() {
        *//** Traversing all routes *//*
        for (int i = 0; i < routesDetails.size(); i++) {
            Legs = routesDetails.get(i).getLegs();

            *//** Traversing all legs *//*
            for (int j = 0; j < Legs.size(); j++) {
                Steps = Legs.get(j).getSteps();

                *//** Traversing all steps *//*
                for (int k = 0; k < Steps.size(); k++) {
                    String polyline = "";
                    polyline = Steps.get(k).getPolyline().getPoints();
                    List list = decodePoly(polyline);

                    */

    /**
     * Traversing all points
     *//*
                    for (int l = 0; l < list.size(); l++) {
                        Double lat = ((LatLng) list.get(l)).latitude;
                        Double lng = ((LatLng) list.get(l)).longitude;
                        LatLng latLng = new LatLng(lat, lng);
                        route.add(latLng);
                    }
                }
//                    route.add(path);
            }
        }
    }








    public static String setDate(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        String datePicked = sdf.format(date.getTime());
        return datePicked;
    }

    //set Black and grey color in same string
    public static String setMultipleColorText(String firstText, String secondText) {
        String text = "<font color=#c9adad>" + firstText + "</font> <font color=#000000>" + secondText + "</font>";
        return text;
    }

    //set green and grey color in same string
    public static String setMultipleColorText1(String firstText, String secondText) {
        String text = "<font color=#c9adad>" + firstText + "</font> <font color=#00a301>" + secondText + "</font>";
        return text;
    }

    //set green and grey color in same string
    public static String setMultipleColorText2(String firstText, String secondText, String thirdText, String fourthText) {
        String text = "<font color=#c9adad>" + firstText +
                "</font> <font color=#00a301>" + secondText +
                "</font> <font color=#00a301>" + thirdText +
                "</font> <font color=#00a301>" + fourthText + "</font>";
        return text;
    }

    public static String getDateFromString(String dateString, String perviousformat, String newFormat) {
        DateFormat oldDateFormat = new SimpleDateFormat(perviousformat, Locale.ENGLISH);
        DateFormat newDateFormat = new SimpleDateFormat(newFormat, Locale.ENGLISH);
        Date olddate = null;
        String newDate = null;
        try {
            olddate = oldDateFormat.parse(dateString);
            newDate = newDateFormat.format(olddate);
        } catch (ParseException e) {
            Log.e("OwnerGlobal", "getDateFromString:ParseException " + e);
        }

        return newDate;
    }*/
    public static void LoginRedirect(Activity activity) {
        // PrefConnect.clearAllPrefs(activity);
        Toast.makeText(activity, "Unauthorised . Login again", Toast.LENGTH_LONG).show();
//        activity.startActivity(new Intent(activity, AccountTypeActivity.class));
//        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        activity.finishAffinity();
    }

    public static void networkToast(Context context) {
        Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
    }

     public static void LoginToast(Context context) {
        Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show();
    }

    public static void toast(Activity activity, String message) {
        //getDecorView() uses rootview to display the snackbar
        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackView = snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(activity, R.color.logo_main_color));
        snackbar.show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
