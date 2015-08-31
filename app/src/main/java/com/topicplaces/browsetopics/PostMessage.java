package com.topicplaces.browsetopics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import main.java.SNSController;

public class PostMessage extends AppCompatActivity {

    private String messageID;

    // Integer code will be set to, then sent back from, camera to verify the intended camera action
    public final static int CAPTURE_ACTIVITY_REQUEST_CODE = 100;

    // Fields to store Views which the user will interact with.
    private ImageView postPhoto;
    private EditText postTitleText, postCommentText;
    private Button cameraButton, postButton;
    private CheckBox publicMessage, privateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        Intent messageIntent = getIntent();
        messageID = messageIntent.getStringExtra(PublicMessagesList.EXTRA_MESSAGE);

        /*
         * Allows the main thread to process internet traffic, rather than providing the connection
         * a thread of its own.
         *
         * This will likely be changed in the future.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Views which will respond based on user input
        postPhoto = (ImageView)findViewById(R.id.postPhoto);

        postTitleText = (EditText)findViewById(R.id.postTitleText);
        postTitleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (postTitleText.getText().toString().equals("Enter here"))
                    postTitleText.setText("");
            }
        });

        postCommentText = (EditText)findViewById(R.id.postCommentText);
        postCommentText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (postCommentText.getText().toString().equals("Enter here"))
                    postCommentText.setText("");
            }

        });

        publicMessage = (CheckBox)findViewById(R.id.publicButton);
        privateMessage = (CheckBox)findViewById(R.id.privateButton);

        publicMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(privateMessage.isChecked())
                    privateMessage.setChecked(false);
            }

        });

        privateMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(publicMessage.isChecked())
                    publicMessage.setChecked(false);
            }

        });

        cameraButton = (Button)findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent capturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(capturePhoto, CAPTURE_ACTIVITY_REQUEST_CODE);
            }

        });

        postButton = (Button)findViewById(R.id.postTopicButton);
        postButton.setOnClickListener(new PostMessageListener());
    }

    /**
     * Handles the returned Intent from the camera and pushes the photo to the postPhoto View
     *
     * @param requestCode an integer that represents the requested action of the Intent
     * @param resultCode an integer which indicates whether the Intent was successful or failed
     * @param camera The returned camera Intent which contains the photo and file URi location
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent camera) {

        if (requestCode == CAPTURE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap)camera.getExtras().get("data");
            postPhoto.setImageBitmap(photo);
        }else {
            int messageResID = R.string.cameraFailure;

            Toast.makeText(this, messageResID, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * OnClickListener for the postButton View. Obtains an authKey, location to post the message,
     * and then determines if the message is private or public before posting. Alerts user with a
     * Toast if an internet connection is unavailable.
     *
     * This listener may become anonymous within the onCreate method in the future.
     */

    private class PostMessageListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String USER = "jeffbooth";
                String PW = "jeffbooth";
                String ENDPOINT = "http://tse.topicplaces.com/api/2/";

                if (publicMessage.isChecked()) {
                    SNSController message = new SNSController(ENDPOINT);
                    String authKey = message.acquireKey(USER, PW);
                    String messageTitle = postTitleText.getText().toString();
                    String messageBody = postCommentText.getText().toString();

                    String TID = message.newPublicTopic(messageTitle, authKey);
                    String GID = message.newPublicMessage(messageTitle, messageBody, null, TID, authKey);
                }else if (privateMessage.isChecked()){
                    SNSController message = new SNSController(ENDPOINT);
                    String authKey = message.acquireKey(USER, PW);
                    String messageTitle = postTitleText.getText().toString();
                    String messageBody = postCommentText.getText().toString();

                    String TID = message.newPrivateTopic(messageTitle, authKey);
                    String GID = message.newPrivateMessage(messageTitle, messageBody, null, TID, authKey);
                }else{
                    int messageResID = R.string.choosePublicOrPrivate;
                    Toast.makeText(getBaseContext(), messageResID, Toast.LENGTH_SHORT).show();
                }

            }else{
                int messageResID = R.string.postFailure;
                Toast.makeText(getBaseContext(), messageResID, Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_message, menu);
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
