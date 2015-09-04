package com.topicplaces.browsetopics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import main.java.SNSController;

public class ViewMessage extends AppCompatActivity {

    private String messageID;
    private ImageView messageImage;
    private TextView titleTextView, commentsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        Intent viewMessage = getIntent();
        messageID = viewMessage.getStringExtra(PublicMessagesList.EXTRA_MESSAGE);

        messageImage = (ImageView)findViewById(R.id.postPhoto);
        titleTextView = (TextView)findViewById(R.id.titleTextView);
        commentsTextView = (TextView)findViewById(R.id.commentsTextView);

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


            titleTextView.setText(messageController.getMessageTitle(messageID, false, authKey));
            commentsTextView.setText(messageController.getMessageDescription(messageID, false, authKey));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_message, menu);
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
