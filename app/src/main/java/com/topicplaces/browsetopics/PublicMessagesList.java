package com.topicplaces.browsetopics;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

import main.java.SNSController;

public class PublicMessagesList extends AppCompatActivity {

    /*
     * Fields for UI Views
     */
    private ListView publicMessagesList;
    private Button newMessageButton;

    /*
     * Fields for passing extra values through Intents to other activities or apps
     */
    public static String TID;
    public static boolean isPrivate;
    public static final String EXTRA_MESSAGE = "com.topicplaces.browsetopics.publicmessageslist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_messages_list);

        /*
         * Initialize the ListView to store message titles in and the New Message Button
         */
        publicMessagesList = (ListView)findViewById(R.id.messagesList);
        newMessageButton = (Button)findViewById(R.id.postNewMessageButton);

        /*
         * Grabs the TID of the selected topic in PublicTopicsActivity and the status of isPrivate
         */
        Intent topicsIntent = getIntent();
        TID = topicsIntent.getStringExtra(PublicTopicsActivity.EXTRA_MESSAGE);
        TextView checkTID = (TextView)findViewById(R.id.checkTID);
        checkTID.setText(TID);
        // Log.d("TID", TID);
        isPrivate = topicsIntent.getExtras().getBoolean(PublicTopicsActivity.EXTRA_MESSAGE);
        // Log.v("isPrivate", "" + isPrivate);

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
         * If there are connected networks a list of public topics is generated and displayed in
         * a ListView.
         */
        if (networkInfo != null && networkInfo.isConnected()) {
            /*
             * Generates Strings needed for generating and using an SNS Controller then initializes
             * one. Verifies the provided username and obtains its corresponding "u-[id]".
             */
            String USER = "jeffbooth";
            String PW = "jeffbooth";
            String ENDPOINT = "http://tse.topicplaces.com/api/2/";
            SNSController messageController = new SNSController(ENDPOINT);
            String authKey = messageController.acquireKey(USER, PW);

            if (!isPrivate) {
                Map<String, String> messageMap = messageController.getPublicMessageMap(TID, authKey);
                TreeMap messageTreeMap = new TreeMap(messageMap);
                String[] messageKeys
                        = (String[]) messageTreeMap.keySet().toArray(new String[messageTreeMap.size()]);

                ArrayAdapter<String> publicTopics =
                        new ArrayAdapter<>(this, R.layout.topic_list_white_text, R.id.topicListWhiteText,
                                messageKeys);
                publicMessagesList.setAdapter(publicTopics);

                publicMessagesList.setOnItemClickListener(
                        new TopicsListListener(messageKeys, messageTreeMap));

                newMessageButton.setOnClickListener(new NewMessageListener());
            }else {

                Map<String, String> messageMap = messageController.getPrivateMessageMap(TID, authKey);
                TreeMap messageTreeMap = new TreeMap(messageMap);
                String[] messageKeys
                        = (String[]) messageTreeMap.keySet().toArray(new String[messageTreeMap.size()]);

                ArrayAdapter<String> privateTopics =
                        new ArrayAdapter<>(this, R.layout.topic_list_white_text, R.id.topicListWhiteText,
                                messageKeys);
                publicMessagesList.setAdapter(privateTopics);

                publicMessagesList.setOnItemClickListener(
                        new TopicsListListener(messageKeys, messageTreeMap));

                newMessageButton.setOnClickListener(new NewMessageListener());
            }

        }

    }

    private class TopicsListListener implements AdapterView.OnItemClickListener {

        private final String[] keys;
        private final TreeMap messageTree;

        TopicsListListener(String[] keys, TreeMap messageTree) {
            super();
            this.messageTree = messageTree;
            this.keys = keys;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent viewMessage = new Intent(getBaseContext(), ViewMessage.class);
            String messageID = (String)messageTree.get(keys[position]);
            viewMessage.putExtra(EXTRA_MESSAGE, messageID);
            startActivity(viewMessage);

        }
    }

    private class NewMessageListener implements View.OnClickListener {

        /**
        private final String[] keys;
        private final TreeMap messageTree;

        NewMessageListener(String[] keys, TreeMap messageTree) {
            super();
            this.messageTree = messageTree;
            this.keys = keys;
        }
        */
        @Override
        public void onClick(View v) {

            Intent topicMessages = new Intent(getBaseContext(), PostMessage.class);
            topicMessages.putExtra(EXTRA_MESSAGE, TID);
            startActivity(topicMessages);

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
