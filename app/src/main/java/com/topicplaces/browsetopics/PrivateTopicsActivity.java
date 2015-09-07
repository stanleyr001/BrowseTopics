package com.topicplaces.browsetopics;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import main.java.SNSController;

public class PrivateTopicsActivity extends AppCompatActivity {

    /*
     * Fields for UI Views
     */
    private ListView privateTopicsList;
    private Button optionsButton, postTopicButton;

    /*
     * Fields for passing extra values through Intents to other activities or apps.
     */
    public  boolean isPrivate;
    public static final String EXTRA_MESSAGE = "com.topicplaces.browsetopics.privatetopicsactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_topics);

        /*
         * Initializes the ListView container that will store and display available private topics.
         * Initializes Options and New Topic buttons.
         */
        privateTopicsList = (ListView)findViewById(R.id.privateTopicsList);
        postTopicButton = (Button)findViewById(R.id.postTopicButton);
        optionsButton = (Button)findViewById(R.id.topicOptionsButton);

        /*
         * Grabs the Home Activity Intent and stores a boolean representing privacy status,
         * false for Public and true for Private.
         */
        Intent privacy = getIntent();
        isPrivate = privacy.getExtras().getBoolean(HomeActivity.EXTRA_MESSAGE);
        Log.v("isPrivate", "" + isPrivate);

        /*
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

        /*
         * Obtains a list of active networks on the device.
         */
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        /*
         * Checks to see if there there are active networks on the device and if they are connected.
         * If there are connected networks a list of private topics is generated and displayed in
         * a ListView.
         */
        if (networkInfo != null && networkInfo.isConnected()) {
            /*
             * Generates Strings needed for generating and using an SNS Controller then initializes
             * one. Verifies the provided username and obtains its corresponding "u-[id]".
             */
            String USER = "jeffbooth";
            String ENDPOINT = "http://tse.topicplaces.com/api/2/";
            SNSController privateTopicsController = new SNSController(ENDPOINT);
            String verifiedUser = privateTopicsController.verifyUsername(USER);

            /*
             * Gets a Map of public topics then generates a String[] to store all returned
             * keys (topic titles). Keys are used to generate an ArrayAdapter to be displayed
             * for the user in a ListView.
             *
             * ArrayAdapater is set to the ListView and an onItemClick listener is attached.
             */
            Map<String, String> privateTopicMap =
                    privateTopicsController.getPrivateTopicMap(verifiedUser);
            TreeMap privateTopicTree = new TreeMap(privateTopicMap);
            String[] privateTopicKeyArray =
                    (String[]) privateTopicTree.keySet().toArray(new String[privateTopicTree.size()]);
            ArrayAdapter<String> publicTopics =
                    new ArrayAdapter<>(this, R.layout.topic_list_white_text,
                             R.id.topicListWhiteText, privateTopicKeyArray);
            privateTopicsList.setAdapter(publicTopics);
            privateTopicsList.setOnItemClickListener(
                    new TopicsListListener(privateTopicKeyArray, privateTopicTree));
        }
    }

    /**
     * Accepts a String[] of topic map keys and a tree map then passes the t-[id] of the selected
     * topic to the Public Messages List activity
     */
    private class TopicsListListener implements AdapterView.OnItemClickListener {

        /*
         * Fields for the keys and topic tree map
         */
        private final String[] keys;
        private final TreeMap topicTree;

        /*
         * Constructor accepts key array and topic tree map and assigns them to respective fields
         */
        TopicsListListener(String[] keys, TreeMap topicTree) {
            super();
            this.topicTree = topicTree;
            this.keys = keys;
        }

        /*
         * Passes an Intent to the Public Messages List activity containing the t-[id] of the
         * selected topic and an isPrivate value of false (Public)
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent topicMessages = new Intent(getBaseContext(), PublicMessagesList.class);
            String TID = (String)topicTree.get(keys[position]);
            topicMessages.putExtra(EXTRA_MESSAGE, TID);
            topicMessages.putExtra(EXTRA_MESSAGE, isPrivate);
            startActivity(topicMessages);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_private_topics, menu);
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
