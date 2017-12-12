package com.example.yun.meetup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yun.meetup.R;
import com.example.yun.meetup.adapters.EventListViewAdapter;
import com.example.yun.meetup.managers.NetworkManager;
import com.example.yun.meetup.models.APIResult;
import com.example.yun.meetup.models.Event;
import com.example.yun.meetup.models.EventList;
import com.example.yun.meetup.models.UserInfo;
import com.example.yun.meetup.requests.EventListRequest;


import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static com.example.yun.meetup.models.EventList.eventListToArray;

public class EventListActivity extends AppCompatActivity {

    ListView listView;
    TextView txtView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Event List");

        /*Button that calls the Create an Event Activity*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventListActivity.this, CreateEventActivity.class);
                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtView = (TextView) findViewById((R.id.txtView));
        listView = (ListView) findViewById(R.id.lv_events);


        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        EventListRequest eventListRequest = new EventListRequest();
        eventListRequest.setHost_id(sharedPreferences.getString("id", null));

        new EventListTask().execute(eventListRequest);

//          DUMMY DATA




        /*Event[] eventList =
        for (int i = 0; i < response.length; i++){
            Event n = new Event();
            n.
         */

       /*Event[] eventList = new Event[]{
                new Event(),
                new Event(),
                new Event(),
                new Event(),
                new Event()
        };*/

        // SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        /*UserInfo userInfo;
        userInfo = new UserInfo();

        userInfo.setName(sharedPreferences.getString("name", null));


        eventList[0].set_id("111");
        eventList[0].setTitle("Nice Project Class");
        eventList[0].setUserInfo(userInfo);
        eventList[0].setDate("01/01/2013");
        eventList[0].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
        eventList[1].set_id("222");
        eventList[1].setTitle("Nice Project Class");
        eventList[1].setUserInfo(userInfo);
        eventList[1].setDate("01/01/2013");
        eventList[1].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
        eventList[2].set_id("333");
        eventList[2].setTitle("Nice Project Class");
        eventList[2].setUserInfo(userInfo);
        eventList[2].setDate("01/01/2013");
        eventList[2].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
        eventList[3].set_id("444");
        eventList[3].setTitle("Nice Project Class");
        eventList[3].setUserInfo(userInfo);
        eventList[3].setDate("01/01/2013");
        eventList[3].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");
        eventList[4].set_id("555");
        eventList[4].setTitle("Nice Project Class");
        eventList[4].setUserInfo(userInfo);
        eventList[4].setDate("01/01/2013");
        eventList[4].setAddress("941 Progress Ave, Toronto, ON M1K 5E9");*/

        // DUMMY DATA END

       /* listView = (ListView) findViewById(R.id.lv_events);

        EventListViewAdapter adapter = new EventListViewAdapter(Arrays.asList(eventList), getApplicationContext());

        listView.setAdapter(adapter);*/


    }


    private class EventListTask extends AsyncTask<EventListRequest, Void, APIResult> {

        @Override
        protected APIResult doInBackground(EventListRequest... eventListRequests) {

            NetworkManager networkManager = new NetworkManager();
            return networkManager.listEvents(eventListRequests[0]);
        }

        @Override
        protected void onPostExecute(APIResult apiResult) {

            //hideViews();

            if (!apiResult.isResultSuccess()) {
                txtView.setText(apiResult.getResultMessage());
            } else {

                Object listOfEvents = apiResult.getResultEntity();

                //cannot convert Object to List
                Event[] test = (Event[]) listOfEvents.toArray();
                UserInfo ui = new UserInfo();
                ui.setName("Test");


                for (int i = 0; i < test.length; i++) {
                    test[i].setUserInfo(ui);
                    test[i].setTitle("TestTitle");
                    test[i].setDate("01/01/2013");
                    test[i].setAddress("Testing, blahblah, blah");
                }

                //listView = (ListView) findViewById( R.id.lv_events);
                EventListViewAdapter ad = new EventListViewAdapter(Arrays.asList(test), getApplicationContext());


                listView.setAdapter(ad);

            }
        }
    }

}
