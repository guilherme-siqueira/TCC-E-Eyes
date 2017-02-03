package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.content.Intent;
import android.widget.Toast;

/**
 * Created by gsiqueira on 7/12/16.
 */
public class MainMenuActivity extends Activity {

    private int dimension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
/*
        if (!checkPermissionForCamera())
            requestPermissionForCamera();
*/

        int MULTIPLE_PERMISSION_CODE = 1;

        String READ_CONTACTS = android.Manifest.permission.READ_CONTACTS;
        String WRITE_CONTACTS = android.Manifest.permission.WRITE_CONTACTS;
        String WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String READ_SMS = android.Manifest.permission.READ_SMS;
        String CAMERA = android.Manifest.permission.CAMERA;

        String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE, CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSION_CODE);
        }






    }

    public void fullDescription(View view) {
        if (!isOnline())
        {
            String NO_CONNECTION = this.getString(R.string.no_connection);
            Toast.makeText(getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, DescriptorActivity.class);

        intent.putExtra("LABEL","LABEL_DETECTION");
        intent.putExtra("TEXT","TEXT_DETECTION");
        intent.putExtra("FACE","FACE_DETECTION");
        intent.putExtra("DIMENSION",1600);
        intent.putExtra("LANDMARK", "LANDMARK_DETECTION");
        //intent.putExtra("LOGO", "LOGO_DETECTION");
        startActivity(intent);
    }

    public void selectFile(View view)
    {
        Intent intent = new Intent(this, SelectFileActivity.class);
        startActivity(intent);
    }

    public void textDescription(View view)
    {
        if (!isOnline())
        {
            String NO_CONNECTION = this.getString(R.string.no_connection);
            Toast.makeText(getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, DescriptorActivity.class);
        intent.putExtra("TEXT","TEXT_DETECTION");

        intent.putExtra("DIMENSION",1024);

        startActivity(intent);
    }
    public void obstacleDetection(View view)
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

    public boolean checkPermissionForCamera(){
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForCamera(){
        //if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
        //Toast.makeText(this, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        //} else {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 3);
        //}
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



}
