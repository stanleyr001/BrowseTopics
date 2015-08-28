package com.topicplaces.browsetopics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private Button publicTopicsButton, privateTopicsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        publicTopicsButton = (Button)findViewById(R.id.publicTopicsButton);
        publicTopicsButton.setOnClickListener(new TopicsButtonListener());

        privateTopicsButton = (Button)findViewById(R.id.privateTopicsButton);
        privateTopicsButton.setOnClickListener(new TopicsButtonListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    private class TopicsButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Button clickedButton = (Button)v;

            if(clickedButton.getId() == R.id.publicTopicsButton) {
                Intent publicTopicsIntent = new Intent(getBaseContext(), PublicTopicsActivity.class);
                startActivity(publicTopicsIntent);
            }

            if(clickedButton.getId() == R.id.privateTopicsButton) {
                Intent privateTopicsIntent = new Intent(getBaseContext(), PrivateTopicsActivity.class);
                startActivity(privateTopicsIntent);
            }
        }
    }

}
