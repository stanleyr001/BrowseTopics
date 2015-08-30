package com.topicplaces.browsetopics;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;
import java.util.TreeMap;

import main.java.SNSController;

public class PublicMessagesList extends AppCompatActivity {

    private ListView publicMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_messages_list);

        /**
         * Initialize the ListView to store message titles in
         */
        publicMessagesList = (ListView)findViewById(R.id.publicMessagesList);

        /**
         * Grabs the TID of the selected topic in PublicTopicsActivity
         */
        Intent publicTopicsIntent = getIntent();
        String TID = publicTopicsIntent.getStringExtra(PublicTopicsActivity.EXTRA_MESSAGE);

        /**
         * Allows the main thread to process internet traffic, rather than providing the connection
         * a thread of its own.
         *
         * Separate threading for internet traffic will be implemented later.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /**
         * Obtains a list of active networks on the device.
         */
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        /**
         * Checks to see if there there are active networks on the device and if they are connected.
         * If there are connected networks a list of public topics is generated and displayed in
         * a ListView.
         */
        if (networkInfo != null && networkInfo.isConnected()) {
            /**
             * Generates Strings needed for generating and using an SNS Controller then initializes
             * one. Verifies the provided username and obtains its corresponding "u-[id]".
             */
            String USER = "jeffbooth";
            String PW = "jeffbooth";
            String ENDPOINT = "http://tse.topicplaces.com/api/2/";
            SNSController messageController = new SNSController(ENDPOINT);
            String authKey = messageController.acquireKey(USER, PW);
            String verifiedUserID = messageController.verifyUsername(USER);

            Map<String, String> messageMap = messageController.getPublicMessageMap(TID, authKey);
            TreeMap messageTreeMap = new TreeMap(messageMap);
            String[] messageKeys
                    = (String[])messageTreeMap.keySet().toArray(new String[messageTreeMap.size()]);

            ArrayAdapter<String> publicTopics =
                    new ArrayAdapter<>(this,R.layout.topic_list_white_text, R.id.topicListWhiteText,
                            messageKeys);
            publicMessagesList.setAdapter(publicTopics);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_public_messages_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
