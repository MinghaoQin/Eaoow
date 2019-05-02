package com.minghaoqin.q.eaoow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class RecommendedRestaurantActivity extends AppCompatActivity {


    private static TextView rcomdRestaurant;
    Button yes_btn;
    Button no_btn;

    public static ArrayList<String> passPricePref = new ArrayList<>();
    static String pricePara = "";
    static String ratingPara = "";
    static Double ratingPara_Double;
    public static String randomRestNameOnScreen;

    private PriorityTable pt;
    private static RestNameDatabase restNameDatabase = null;

    public List<String> list = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.activity_recommended_restaurant);
        rcomdRestaurant = (TextView) findViewById(R.id.rcomdRestaurant_text);
        pricePara = setPriceLevel();
        ratingPara_Double = Double.parseDouble(setRating());
        System.out.println(ratingPara);
        restNameDatabase = RestNameDatabase.getInstance(RecommendedRestaurantActivity.this);
        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();
                new YesTask(recommendedRestaurantActivity,pt).execute();
                Toast.makeText(getBaseContext(), "Thank you for the feedback!", Toast.LENGTH_SHORT).show();

            }
        });


        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();
                new NoTask(recommendedRestaurantActivity,pt).execute();
                Toast.makeText(getBaseContext(), "Sorry about that, go back and try again!", Toast.LENGTH_SHORT).show();
            }
        });

        setRating();

    }


    public  class GetNearbyPlaces extends AsyncTask<Object, String, String> {
        private String googleplaceData, url;
        private GoogleMap mMap;
        public List<String> restNameList = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Object... objects) {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];

            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googleplaceData = downloadUrl.ReadTheURL(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googleplaceData;
        }

        @Override
        protected void onPostExecute(String s) {


            List<HashMap<String, String>> nearbyPlacesListPost = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesListPost = dataParser.parse(s);
            HashMap <String,String> eachElement = new HashMap<>();

            String restName = "";


            for(int i = 0; i < nearbyPlacesListPost.size(); i ++) {
                eachElement = nearbyPlacesListPost.get(i);

                if (eachElement.containsValue(pricePara)) {
                    restName = eachElement.get("place_name");
                    restNameList.add(restName);
                }
            }

            String restNameR = "";
            List<String> restNameListR = new ArrayList<>();


            for(int i = 0; i < nearbyPlacesListPost.size(); i ++) {
                eachElement = nearbyPlacesListPost.get(i);
                String RestRating = eachElement.get("rating");
                double getRestRating = Double.parseDouble(RestRating);

                if(getRestRating > ratingPara_Double) {
                    restNameR = eachElement.get("place_name");
                    restNameListR.add(restNameR);
                    System.out.println(restNameListR.toString());
                    Log.d("RcomdRestaurantPage", "Satisfied Rating: " + getRestRating);
                }

            }

            restNameList.retainAll(restNameListR);
            // using retainAll get the restName in common between two arrayLists

            //rcomdRestaurant.setText(restNameList.toString());
            RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();
            recommendedRestaurantActivity.randomRestNameOnScreen = "";
            recommendedRestaurantActivity.randomRestNameOnScreen = recommendedRestaurantActivity.getRandomElement(restNameList);

            rcomdRestaurant.setText(recommendedRestaurantActivity.randomRestNameOnScreen);
            Log.d("RcomndRestActi", "random restaurant name check: " + recommendedRestaurantActivity.randomRestNameOnScreen);


            // get distinct restaurant name from database
            new GetDistinctRestNameTask(RecommendedRestaurantActivity.this,pt).execute();

            // get distinct restaurant name which have priority level greater than zero from database
            new GetRestNamePriorityGreaterZeroTask(RecommendedRestaurantActivity.this,pt).execute();
        }

    }

    protected String setPriceLevel(){ //set<String> pricePref
        String paraPricePref = "";
        SharedPreferences sharedPricePref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Set <String> pricePref  =  sharedPricePref.getStringSet("key_restaurant_price",null);
        //System.out.println("Current pricePref value: " + pricePref);
        this.passPricePref.addAll(pricePref);
        //System.out.println(passPricePref);
        //Log.d("RcomndRestActi", "above Arraylist is setPriceLevel Print check");

        for (int i = 0; i < passPricePref.size(); i ++){
            paraPricePref = passPricePref.get(i);
            //System.out.println(paraPricePref);
            //Log.d("RcomndRestActi", "above String is paraPricePref Print check");
        }
        return paraPricePref;

    }

    protected String setRating(){  // String ratingPref
        String paraRatingPref = "";

        SharedPreferences sharedRatingPref = PreferenceManager.getDefaultSharedPreferences(this);
        String ratingPref = sharedRatingPref.getString("key_restaurant_rating", null);
        //System.out.println("Current RatingPref value: " + ratingPref);
        paraRatingPref = ratingPref;

        return paraRatingPref;
    }

    protected String getRandomElement(List<String> list){
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    // 这个async task 意在 完成 初始化表格的一条数据
    private class InsertTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<RecommendedRestaurantActivity> activityReference;
        private PriorityTable pt;

        // only retain a weak reference to the activity
        // constructor
        InsertTask(RecommendedRestaurantActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            // retrieve auto incremented note id
            long passIndex;
            passIndex = activityReference.get().restNameDatabase.getDaoAccess().insert(pt);
            pt.setIndex(passIndex);
            Log.e("ID ", "doInBackground: " + passIndex );
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().finish();
            }
        }
    }


    private  class GetDistinctRestNameTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<RecommendedRestaurantActivity> activityReference;
        private PriorityTable pt;
        RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();

        // constructor
        GetDistinctRestNameTask(RecommendedRestaurantActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {

            List<String> distinctRestName = activityReference.get().restNameDatabase.getDaoAccess().getDistinctRestName();

                new ArrayList<>(distinctRestName);
                if (distinctRestName.contains(recommendedRestaurantActivity.randomRestNameOnScreen)) {
                    Log.e("ID ", "doInBackground: " + " ============================== Duplicate Found, No insert this time ==============================");
                } else {
                    pt = new PriorityTable(recommendedRestaurantActivity.randomRestNameOnScreen,0);
                    new InsertTask(RecommendedRestaurantActivity.this,pt).execute();
                    Log.e("ID ", "doInBackground: " + " ++++++++++++++++++++++++++++++ No Duplicate, insert new rest_name ++++++++++++++++++++++++++++++");
                }
                Log.d("ID ", "Print Distinct Restaurant Name in current database " + distinctRestName.toString());
                return true;

        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().finish();
            }
        }
    }

    private class YesTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<RecommendedRestaurantActivity> activityReference;
        private PriorityTable pt;
        RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();

        // only retain a weak reference to the activity
        // constructor
        YesTask(RecommendedRestaurantActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            activityReference.get().restNameDatabase.getDaoAccess().updatePriorityAfterYes(
                    recommendedRestaurantActivity.randomRestNameOnScreen);
            Log.e("Feedback ", "doInBackground: " + " YES click detected, Database updated ");

            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                //activityReference.get().setResult(pt,1);
                activityReference.get().finish();
            }
        }
    }

    private class NoTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<RecommendedRestaurantActivity> activityReference;
        private PriorityTable pt;
        RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();

        // only retain a weak reference to the activity
        // constructor
        NoTask(RecommendedRestaurantActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }
        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            activityReference.get().restNameDatabase.getDaoAccess().updatePriorityAfterNo(
                    recommendedRestaurantActivity.randomRestNameOnScreen);
            Log.e("Feedback ", "doInBackground: " + " NO click detected,  Database updated");

            return true;
        }
        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().finish();
            }
        }
    }

    private class GetRestNamePriorityGreaterZeroTask extends AsyncTask<Void,Void,List<String>> {

        private WeakReference<RecommendedRestaurantActivity> activityReference;
        private PriorityTable pt;
        RecommendedRestaurantActivity recommendedRestaurantActivity = new RecommendedRestaurantActivity();

        // only retain a weak reference to the activity
        // constructor
        GetRestNamePriorityGreaterZeroTask(RecommendedRestaurantActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }

        // doInBackground methods runs on a worker thread
        @Override
        public List doInBackground(Void... objs) {
            List<String> sortedRest;

            sortedRest = activityReference.get().restNameDatabase.getDaoAccess().getRestNamePriorityGreaterZero();

            return sortedRest;
        }
        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(List list) {
                recommendedRestaurantActivity.list = list;
                activityReference.get().finish();

        }
    }

}
