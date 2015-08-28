package com.topicplaces.browsetopics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import main.java.SNSController;

public class PublicTopicsActivity extends AppCompatActivity {

    private ListView publicTopicsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_topics);

        /**
         * Initializes the ListView container that will store and display available public topics.
         */
        publicTopicsList = (ListView)findViewById(R.id.publicTopicsList);

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
            /* Not currently used: String PW = "jeffbooth"; */
            String ENDPOINT = "http://tse.topicplaces.com/api/2/";
            SNSController publicTopicsController = new SNSController(ENDPOINT);
            // Not currently used: String authKey = publicTopicsController.acquireKey(USER, PW);
            String verifiedUserID = publicTopicsController.verifyUsername(USER);

            /**
             * Gets a Map of public topics then generates a String[] to store all returned
             * keys (topic titles). Keys are used to generate an ArrayAdapter to be displayed
             * for the user in a ListView
             */
            Map<String, String> publicTopicMap =
                    publicTopicsController.getPublicTopicMap(verifiedUserID);
            TreeMap publicTopicTree = new TreeMap(publicTopicMap);
            Set publicTopicMapKeys = publicTopicTree.keySet();
            String[] publicTopicKeyArray =
                    (String[]) publicTopicMapKeys.toArray(new String[publicTopicMapKeys.size()]);
            ArrayAdapter<String> publicTopics =
                    new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, publicTopicKeyArray);
            publicTopicsList.setAdapter(publicTopics);

            /**
             * Generates a String[] of the public topic map's values, in "t-[id]" format, which
             * will be passed along to an Activity for viewing a list of messages if a topic is
             * chosen from the ListView.
             */
            String[] publicTopicMapValues = (String[])publicTopicTree.values().toArray();
            publicTopicsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_public_topics, menu);
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
