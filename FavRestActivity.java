package com.minghaoqin.q.eaoow;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.dynamic.LifecycleDelegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class FavRestActivity extends AppCompatActivity {

    private RestNameDatabase restNameDatabase;
    private List<PriorityTable> pt;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private int pos;


    private PriorityTable ptObj;
    Button btnResetDatabase;
    private List<String> list = new ArrayList<String>();

    //private static RestNameDatabase restNameDatabase = null;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_restaurant);
        btnResetDatabase = findViewById(R.id.btnResetDatabase);

        restNameDatabase = RestNameDatabase.getInstance(FavRestActivity.this);


        btnResetDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ResetTask(FavRestActivity.this,ptObj).execute();
            }
        });
        initializeViews();
        displayList();

    }

    private void displayList(){
        restNameDatabase = RestNameDatabase.getInstance(FavRestActivity.this);
        new RetrieveTask(this).execute();
    }




    private static class RetrieveTask extends AsyncTask<Void,Void,List<PriorityTable>>{

        private WeakReference<FavRestActivity> activityReference;

        RetrieveTask(FavRestActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<PriorityTable> doInBackground(Void... voids) {
            if (activityReference.get()!=null)
                return activityReference.get().restNameDatabase.getDaoAccess().getPt();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<PriorityTable> pt) {
            if (pt!=null && pt.size()>0 ){
                activityReference.get().pt.clear();
                activityReference.get().pt.addAll(pt);
                activityReference.get().recyclerAdapter.notifyDataSetChanged();
            }
        }
    }



    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavRestActivity.this));
        pt = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter( pt,FavRestActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode > 0 ){
            if( resultCode == 1){
                pt.add((PriorityTable) data.getSerializableExtra("pt"));
            }else if( resultCode == 2){
                pt.set(pos,(PriorityTable) data.getSerializableExtra("pt"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        restNameDatabase.cleanUp();
        super.onDestroy();
    }


    private  class ResetTask extends AsyncTask<Void,Void,Boolean> {

        private WeakReference<FavRestActivity> activityReference;
        private PriorityTable pt;
        FavRestActivity favRestActivity = new FavRestActivity();

        // constructor
        ResetTask(FavRestActivity context, PriorityTable pt) {
            activityReference = new WeakReference<>(context);
            this.pt = pt;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {

            activityReference.get().restNameDatabase.getDaoAccess().deleteAll();
            Log.e("ID ", "DeleteAll: " + " Delete database detected ");

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


}
