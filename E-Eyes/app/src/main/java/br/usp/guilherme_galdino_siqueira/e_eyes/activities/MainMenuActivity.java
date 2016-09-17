package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import android.content.Intent;
import android.widget.Toast;

/**
 * Created by gsiqueira on 7/12/16.
 */
public class MainMenuActivity extends Activity {

    private boolean optionClicked = false;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        isOnline = isOnline();
    }

    public void fullDescription(View view) {
        if (!isOnline)
        {
            String NO_CONNECTION = this.getString(R.string.no_connection);
            Toast.makeText(getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, DescriptorActivity.class);

        //intent.putExtra("LABEL","LABEL_DETECTION");
        //intent.putExtra("TEXT","TEXT_DETECTION");
        intent.putExtra("FACE","FACE_DETECTION");
        //intent.putExtra("LANDMARK","LANDMARK_DETECTION");
        //intent.putExtra("LOGO", "LOGO_DETECTION");
        startActivity(intent);
    }

    public void labelDescription(View view)
    {
        /*
        if (!isOnline)
        {
            String NO_CONNECTION = this.getString(R.string.no_connection);
            Toast.makeText(getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }

        */

        Intent intent = new Intent(this, SelectFileActivity.class);
        startActivity(intent);
    }

    public void textDescription(View view)
    {
        if (!isOnline)
        {
            String NO_CONNECTION = this.getString(R.string.no_connection);
            Toast.makeText(getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, DescriptorActivity.class);
        intent.putExtra("TEXT","TEXT_DETECTION");
        startActivity(intent);
    }
    public void detection(View view)
    {


        Intent intent = new Intent(this, ArduinoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        // This above line close correctly
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
