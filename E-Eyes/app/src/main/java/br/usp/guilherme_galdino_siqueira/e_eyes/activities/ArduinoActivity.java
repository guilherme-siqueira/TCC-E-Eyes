package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

/**
 * Created by gsiqueira on 7/11/16.
 */
import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import android.widget.ListView;

import android.widget.Button;

import java.util.ArrayList;

import java.util.UUID;

import android.bluetooth.BluetoothSocket;

import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Arduino;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Effects;

//import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.Voice;


public class ArduinoActivity extends Activity {
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;

    Effects effects;

    static boolean active = false;

    //private BluetoothSocket mmSocket;

    TextView textView;

    ArrayList<BluetoothSocket> socketList = new ArrayList<BluetoothSocket>();

    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino);

        textView =  (TextView) findViewById(R.id.ArduinoText);
        textView.setEnabled(true);
        textView.setVisibility(View.VISIBLE);

        effects = new Effects(getApplicationContext(), this);

        BA = BluetoothAdapter.getDefaultAdapter();

        turnBlueToothOn();

        new ArduinoCommunication().execute();

        /*

        new AsyncTask<Object, String, Void>()
        {
            InputStream mmInputStream;
            BluetoothDevice arduino = null;
            int distance, previousDistance;
            String[] obstAlert = new String[2];
            String[] info = new String[4];

            @Override
            protected Void doInBackground(Object... params) {

                while (active) {

                    if (arduino == null)
                    {
                        info[0] = null;
                        info[1] = null;
                        info[2] = null;
                        info[3] = "Procurando Arduíno";
                        publishProgress(info);
                    }

                    while (arduino == null) {
                        //play bip

                        //procura Arduino
                        arduino = getArduino();
                    }

                    mmInputStream = connect(arduino);

                    if (mmInputStream == null)
                    {
                        info[0] = null;
                        info[1] = null;
                        info[2] = null;
                        info[3] = "Arduíno Desligado";
                        publishProgress(info);
                    }


                    while (mmInputStream == null) {
                        mmInputStream = connect(arduino);
                    }

                    info[0] = null;
                    info[1] = null;
                    info[2] = null;
                    info[3] = "Arduíno Ligado";
                    publishProgress(info);

                    //previousDistance = read(mmInputStream);
                    distance = read(mmInputStream);

                    while (distance != -1)
                    {
                        obstAlert = classify(distance, previousDistance);

                        info[0] = obstAlert[0];

                        info[1] = obstAlert[1];

                        info[2] = String.format( "%.2f", (float)distance/100);

                        info[3] = null;

                        publishProgress(info);
                        //publishProgress(""+distance);

                        previousDistance = distance;
                        distance = read(mmInputStream);
                    }
                    //publishProgress("Problema de leitura");
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... message) {

                int msTime;

                if (Arduino.isObstacleGettingIn())
                {
                    msTime = 2000;
                    effects.vibrate(msTime);
                    effects.playObstacleAlert(msTime);


                }
                if (Arduino.isObstacleGettingCloser())
                {
                    // Toast.makeText(getApplicationContext(),message[1], Toast.LENGTH_LONG).show();
                    effects.vibrate(6000);
                    effects.playObstacleAlert();
                }

                if (Arduino.isObstacleGettingAway())
                {
                    effects.stopVibration();
                    effects.pauseObstacleAlert();

                    msTime = 2000;

                    effects.vibrate(msTime);
                    effects.playObstacleAlert(msTime);
                }


                if (Arduino.isObstacleGone())
                {
                    //Toast.makeText(getApplicationContext(),message[0], Toast.LENGTH_LONG).show();
                    effects.stopVibration();
                    effects.pauseObstacleAlert();
                }



                if (message[2] != null)
                {
                    textView.setText(message[2] + "m");
                }

                if (message[3] != null)
                {
                    Toast.makeText(getApplicationContext(),message[3], Toast.LENGTH_LONG).show();
                }
            }

            protected void onPostExecute(Void values){


            }
        }.execute();

        */

    }

    private void turnBlueToothOn()
    {
        if (!BA.isEnabled()) {

            BA.enable();

            //Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(turnOn, 0);

            Toast.makeText(getApplicationContext(),"BlueTooth ligado",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"BlueTooth já está ligado", Toast.LENGTH_LONG).show();
        }
    }

    private void turnBlueToothOff() {
        BA.disable();
        Toast.makeText(getApplicationContext(),"BlueTooth desligado", Toast.LENGTH_LONG).show();
    }

    public BluetoothDevice getArduino(){
        pairedDevices = BA.getBondedDevices();
        //ArrayList list = new ArrayList();

        for(BluetoothDevice device : pairedDevices)
        {
            if(device.getName().equals("HC-05"))
            {
                return device;
            }
        }

        return null;
    }

    private InputStream connect(BluetoothDevice device)
    {
        BluetoothSocket mmSocket;
        InputStream mmInputStream;



        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmInputStream = mmSocket.getInputStream();
            return mmInputStream;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int read(InputStream mmInputStream)
    {
        int i;
        char c;
        String value = "";

        try {

            while ((c = (char) mmInputStream.read()) != '\n')
            {
                value+=c;
            }

            value = value.substring(0, value.length()-1);

            i = Integer.parseInt(value);

            return i;

        } catch (IOException e) {

            e.printStackTrace();
            return -1;
        }
    }

    public String[] classify(int value, int pValue) {
        int yellowSignDistance = 100;
        int redSignDistance = 50;

        String[] alert = new String[2];

        //saindo do vermehlo e indo pra fora
        if (value > yellowSignDistance && pValue < redSignDistance) {
            Arduino.setObstacleGettingAway(false);
            Arduino.setObstacleGettingCloser(false);
            Arduino.setObstacleGettingIn(false);
            Arduino.setObstacleGone(true);
        }

        //saindo do vermelho e indo pro amarelo
        else if (value > redSignDistance && pValue < redSignDistance) {
            Arduino.setObstacleGettingAway(true);
            Arduino.setObstacleGettingCloser(false);
            Arduino.setObstacleGettingIn(false);
            Arduino.setObstacleGone(false);

            alert[0] = "Não há obstáculos";
            alert[1] = null;
        }
        //saindo do amarelo e indo para fora
        else if (value > yellowSignDistance && pValue < yellowSignDistance) {
            Arduino.setObstacleGettingAway(false);
            Arduino.setObstacleGettingCloser(false);
            Arduino.setObstacleGettingIn(false);
            Arduino.setObstacleGone(true);

            alert[0] = "Não há obstáculos";
            alert[1] = null;
        }


        //saindo de fora e entrando no vermelho
        else if (value < redSignDistance && pValue > redSignDistance) {
            Arduino.setObstacleGettingAway(false);
            Arduino.setObstacleGettingCloser(true);
            Arduino.setObstacleGettingIn(false);
            Arduino.setObstacleGone(false);

            alert[0] = null;
            alert[1] = "Cuidado com obstáculos a sua frente";
        }

        //saindo de fora e indo para o amarelo ou vermelho
        else if (value < yellowSignDistance && pValue > yellowSignDistance)
        {
            Arduino.setObstacleGettingAway(false);
            Arduino.setObstacleGettingCloser(false);
            Arduino.setObstacleGettingIn(true);
            Arduino.setObstacleGone(false);

            alert[0] = null;
            alert[1] = "Cuidado com obstáculos a sua frente";
        }






        else
        {
            Arduino.setObstacleGettingAway(false);
            Arduino.setObstacleGettingCloser(false);
            Arduino.setObstacleGettingIn(false);
            Arduino.setObstacleGone(false);

            alert[0] = null;
            alert[1] = null;

        }

        return alert;
    }

    public void onBackPressed() {

        active = false;
        turnBlueToothOff();
        super.onBackPressed();
    }

    private class ArduinoCommunication extends AsyncTask <Object, String, Void>
    {
        InputStream mmInputStream;
        BluetoothDevice arduino = null;
        int distance, previousDistance;
        String[] obstAlert = new String[2];
        String[] info = new String[4];

        @Override
        protected Void doInBackground(Object... params) {

            while (active) {

                if (arduino == null)
                {
                    info[0] = null;
                    info[1] = null;
                    info[2] = null;
                    info[3] = "Procurando Arduíno";
                    publishProgress(info);
                }

                while (arduino == null) {
                    //play bip

                    //procura Arduino
                    arduino = getArduino();
                }

                mmInputStream = connect(arduino);

                if (mmInputStream == null)
                {
                    info[0] = null;
                    info[1] = null;
                    info[2] = null;
                    info[3] = "Arduíno Desligado";
                    publishProgress(info);
                }


                while (mmInputStream == null) {
                    mmInputStream = connect(arduino);
                }

                info[0] = null;
                info[1] = null;
                info[2] = null;
                info[3] = "Arduíno Ligado";
                publishProgress(info);

                //previousDistance = read(mmInputStream);
                distance = read(mmInputStream);

                while (distance != -1)
                {
                    obstAlert = classify(distance, previousDistance);

                    info[0] = obstAlert[0];

                    info[1] = obstAlert[1];

                    info[2] = String.format( "%.2f", (float) distance /100);

                    info[3] = null;

                    publishProgress(info);
                    //publishProgress(""+distance);

                    previousDistance = distance;
                    distance = read(mmInputStream);
                }
                //publishProgress("Problema de leitura");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... message) {

            int msTime;

            if (Arduino.isObstacleGettingIn())
            {
                msTime = 2000;
                effects.vibrate(msTime);
                effects.playObstacleAlert(msTime);


            }
            if (Arduino.isObstacleGettingCloser())
            {
                // Toast.makeText(getApplicationContext(),message[1], Toast.LENGTH_LONG).show();
                effects.vibrate(6000);
                effects.playObstacleAlert();
            }

            if (Arduino.isObstacleGettingAway())
            {
                effects.stopVibration();
                effects.pauseObstacleAlert();

                msTime = 2000;

                effects.vibrate(msTime);
                effects.playObstacleAlert(msTime);
            }


            if (Arduino.isObstacleGone())
            {
                //Toast.makeText(getApplicationContext(),message[0], Toast.LENGTH_LONG).show();
                effects.stopVibration();
                effects.pauseObstacleAlert();
            }



            if (message[2] != null)
            {
                textView.setText(message[2] + "m");
            }

            if (message[3] != null)
            {
                Toast.makeText(getApplicationContext(),message[3], Toast.LENGTH_LONG).show();
            }
        }
    }
}