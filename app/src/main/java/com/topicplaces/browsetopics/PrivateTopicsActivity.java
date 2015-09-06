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

    private ListView privateTopicsList;
    private Button optionsButton, postTopicButton;

    public  boolean isPrivate;
    public static final String EXTRA_MESSAGE = "com.topicplaces.browsetopics.privatetopicsactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_topics);

        Intent privacy = getIntent();
        isPrivate = privacy.getExtras().getBoolean(HomeActivity.EXTRA_MESSAGE);
        Log.v("isPrivate", "" + isPrivate);

        /*
         * Allows the main thread to process internet traffic, rather than providing the connection
         * a thread of its own.
         *
         * This will likely be changed in the future.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*
         * Populate the list view with a list of public topics
         */
        privateTopicsList = (ListView)findViewById(R.id.privateTopicsList);

        postTopicButton = (Button)findViewById(R.id.postTopicButton);


        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String USER = "jeffbooth";
            String ENDPOINT = "http://tse.topicplaces.com/api/2/";

            SNSController privateTopicsController = new SNSController(ENDPOINT);

            String verifiedUser = privateTopicsController.verifyUsername(USER);

            Map<String, String> privateTopicMap =
                    privateTopicsController.getPrivateTopicMap(verifiedUser);
            TreeMap privateTopicTree = new TreeMap(privateTopicMap);
            Set privateTopicMapKeys = privateTopicTree.keySet();

            String[] privateTopicKeyArray =
                    (String[]) privateTopicMapKeys.toArray(new String[privateTopicMapKeys.size()]);

            ArrayAdapter<String> publicTopics =
                    new ArrayAdapter<>(this, R.layout.topic_list_white_text,
                             R.id.topicListWhiteText, privateTopicKeyArray);

            privateTopicsList.setAdapter(publicTopics);

            privateTopicsList.setOnItemClickListener(
                    new TopicsListListener(privateTopicKeyArray, privateTopicTree));
        }
    }

    private class TopicsListListener implements AdapterView.OnItemClickListener {

        private final String[] keys;
        private final TreeMap topicTree;

        TopicsListListener(String[] keys, TreeMap topicTree) {
            super();
            this.topicTree = topicTree;
            this.keys = keys;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent topicMessages = new Intent(getBaseContext(), PublicMessagesList.class);
            String groupID = (String)topicTree.get(keys[position]);
            topicMessages.putExtra(EXTRA_MESSAGE, groupID);
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
