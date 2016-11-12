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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import android.widget.ListView;

import android.widget.Button;

import java.util.ArrayList;

import java.util.UUID;

import android.bluetooth.BluetoothSocket;

import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Arduino;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Effects;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.FeatureContent;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.FileManager;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.TextAdapter;

//import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.Voice;


public class ArduinoActivity extends Activity {
    Button b1,b2,b3,b4;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;

    Effects effects;

    FeatureContent arduinoData = new FeatureContent();

    ArrayList<Integer> distanceSample = new ArrayList<>();
    float arduinoMean = 0;
    float arduinoSD = 0;

    long startTime;

    int yellowSignDistance;
    int redSignDistance;

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

        startTime = System.currentTimeMillis();

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

            value = value.substring(0, value.length() - 1);

            i = Integer.parseInt(value);

            i = (int)(0.7843*i - 0.7551);

            //System.out.println("distancia: " + i);

            return i;

        } catch (IOException e) {

            e.printStackTrace();
            return -1;
        }
    }

    public String[] classify(int value, int pValue) {
/*
        float delta = Math.abs(Arduino.getDistance() - Arduino.getPreviousDistance());

        if (delta <= 400)
            redSignDistance = (int)delta;
        else
            redSignDistance = 400;


        if (delta <= 100)
            yellowSignDistance = 4*(int)delta;
        else
            yellowSignDistance = 400;

*/


        redSignDistance = 30;

        yellowSignDistance = 200;

        String[] alert = new String[2];

        if (value < redSignDistance)
        {
            if (pValue >= redSignDistance)
                Arduino.setGotInRedArea(true);
            else
                Arduino.setGotInRedArea(false);

            Arduino.setGotInYellowArea(false);
            Arduino.setInYellowArea(false);
            Arduino.setOut(false);
        }
        else if (value >= redSignDistance  && value < yellowSignDistance)
        {
            if (pValue < redSignDistance || pValue >= yellowSignDistance)
                Arduino.setGotInYellowArea(true);
            else
                Arduino.setGotInYellowArea(false);

            Arduino.setGotInRedArea(false);
            Arduino.setInYellowArea(true);
            Arduino.setOut(false);
        }
        else
        {
            if (pValue < yellowSignDistance)
            {
                Arduino.setGotInRedArea(false);
                Arduino.setGotInYellowArea(false);
            }
            Arduino.setOut(true);
            Arduino.setInYellowArea(false);
        }

        //Arduino.setDistance(value);




        /*

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

        */

        return alert;
    }

    private void calculateData()
    {
        long msTime = System.currentTimeMillis() - startTime;

        int sum = 0;
        float mean = 0;
        double var = 0;
        double sd = 0;

        Date date = new Date();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int sample : distanceSample)
        {
            sum = sum + sample;
        }

        mean = sum / distanceSample.size();

        for (int sample : distanceSample)
        {
            var = var + (sample - mean)*(sample - mean);
        }

        var = var / distanceSample.size();
        sd = Math.sqrt(var);



        String arduinoStat = "amostras: " + distanceSample.size() + "\n" + "tempo: " +msTime + "ms"
                + "\n" + "media: " + mean + "\n" + "desvio padrao: " + sd + "\n\n"
                + arduinoData.getText();

        FileManager.save(this, arduinoStat.getBytes(), "arduinoData", mean + " "
                + dateFormat.format(date),"txt");
    }

    public void onBackPressed() {

        active = false;
        turnBlueToothOff();

        effects.pauseObstacleAlert();
        effects.pauseConstantSound();

        if (distanceSample.size() > 0)
        {
            calculateData();
        }


        super.onBackPressed();
    }

    private class ProgressUpdate {

        private boolean out;

        private boolean gotInRedArea;
        private boolean gotInYellowArea;
        private boolean isInYellowArea;

        private int distance;
        private int previousDistance;

        private String message;

        private String formattedDistance;



        private float volume;


        public ProgressUpdate()
        {
            clear();
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public void clear()
        {
            setOut(false);
            setGotInRedArea(false);
            setGotInYellowArea(false);
            setIsInYellowArea(false);
            setDistance(-2);
            setPreviousDistance(-2);
            setMessage(null);
            setVolume(0);
        }

        public boolean isGotInRedArea() {
            return gotInRedArea;
        }

        public void setGotInRedArea(boolean gotInRedArea) {
            this.gotInRedArea = gotInRedArea;
        }

        public boolean isGotInYellowArea() {
            return gotInYellowArea;
        }

        public void setGotInYellowArea(boolean gotInYellowArea) {
            this.gotInYellowArea = gotInYellowArea;
        }

        public boolean isInYellowArea() {
            return isInYellowArea;
        }

        public void setIsInYellowArea(boolean isInYellowArea) {
            this.isInYellowArea = isInYellowArea;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
            this.formattedDistance = String.format( "%.2f", (float) distance /100) + "m";
        }

        public String getFormattedDistance()
        {
            return formattedDistance;
        }

        public int getPreviousDistance() {
            return previousDistance;
        }

        public void setPreviousDistance(int previousDistance) {
            this.previousDistance = previousDistance;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isOut() {
            return out;
        }

        public void setOut(boolean out) {
            this.out = out;
        }


    }

    private class ArduinoCommunication extends AsyncTask <Object, ProgressUpdate, Void>
    {
        InputStream mmInputStream;
        BluetoothDevice arduino = null;
        int distance = -2;
        int previousDistance = 500;
        String[] obstAlert = new String[2];
        String[] info = new String[4];

        public ProgressUpdate progress = new ProgressUpdate();

        @Override
        protected Void doInBackground(Object... params) {



            while (active) {

                if (arduino == null)
                {
                    info[0] = null;
                    info[1] = null;
                    info[2] = null;
                    info[3] = "Procurando Arduíno";
                    Arduino.setMessage("Procurando Arduíno");

                    progress.setMessage("Procurando Arduíno");


                    publishProgress(progress);
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
                    progress.setMessage("Arduíno Desligado");




                    publishProgress(progress);
                }


                while (mmInputStream == null) {
                    mmInputStream = connect(arduino);
                }

                info[0] = null;
                info[1] = null;
                info[2] = null;
                info[3] = "Arduíno Ligado";
                progress.setMessage("Arduíno Ligado");
                publishProgress(progress);

                //previousDistance = read(mmInputStream);
                distance = read(mmInputStream);

                //Arduino.setDistance(distance);

                while (distance > 0)
                {
                    obstAlert = classify(distance, previousDistance);

                    distanceSample.add(distance);

                    arduinoData.concat("distancia = " + distance + "cm"
                    );

                    int v = distance - previousDistance;

                    arduinoData.concat("velocidade = " + v + "cm/s");



                    //Arduino.setDistance(distance);

                    /*
                    info[0] = obstAlert[0];

                    info[1] = obstAlert[1];

                    info[2] = String.format( "%.2f", (float) distance /100);
*/

                    progress.clear();

                    progress.setPreviousDistance(previousDistance);
                    progress.setDistance(distance);

                    if (Arduino.isGotInRedArea())
                    {
                        progress.setGotInRedArea(true);
                        arduinoData.concat("entrou na area vermelha");
                    }
                    else if (Arduino.isGotInYellowArea())
                    {
                        progress.setGotInYellowArea(true);
                        arduinoData.concat("entrou na area amarela");
                    }
                    else if (Arduino.isInYellowArea())
                    {

                        progress.setIsInYellowArea(true);
                        float maxDistVol = 0.005f;
                        float minDistVol = 0.05f;


                        float volume = (float) (Math.log(((maxDistVol - minDistVol) *(distance - redSignDistance)) + minDistVol*(yellowSignDistance - redSignDistance))/Math.log(yellowSignDistance - redSignDistance));

                        progress.setVolume(volume);
                        arduinoData.concat("volume = " + volume);

                    }

                    else if (Arduino.isOut())
                    {
                        progress.setOut(true);
                        arduinoData.concat("está fora");
                    }

                    arduinoData.concat("\n");

                    //Arduino.setDistance(distance);

                    //info[3] = null;

                    publishProgress(progress);
                    //publishProgress(""+distance);


                    previousDistance = distance;//Arduino.getDistance();
                    //Arduino.setPreviousDistance(previousDistance);

                    distance = read(mmInputStream);

                }
                //publishProgress("Problema de leitura");
            }



            return null;
        }

        @Override
        protected void onProgressUpdate(ProgressUpdate... progress) {

            int msTime;

            float volume = -5;

            if (progress[0].isGotInRedArea())
            {
                //arduinoData.concat("entrou na area vermelha");

                effects.pauseConstantSound();

                //effects.vibrate(6000);
                effects.playObstacleAlert();
            }
            else if (progress[0].isGotInYellowArea())
            {
                //arduinoData.concat("entrou na area amarela");

                //System.out.println("entrou na area amarela");

                effects.pauseObstacleAlert();

                effects.playConstantSound();
            }

            else if (progress[0].isInYellowArea())
            {

                //float maxDistVol = 0.05f;
                //float minDistVol = 1;


                //volume = (float) (Math.log(((maxDistVol - minDistVol) *(progress[0].getDistance() - redSignDistance)) + minDistVol*(yellowSignDistance - redSignDistance))/Math.log(yellowSignDistance - redSignDistance));

                //arduinoData.concat("volume amarelo = "+volume);

                //float volume = (float) ( 1 - (Math.log(Arduino.getDistance() - 30)/Math.log(200.0 - 30)));

                effects.setConstantSoundVolume(progress[0].getVolume());
            }

            else if (progress[0].isOut())
            {
                //arduinoData.concat("está fora");
                effects.pauseConstantSound();
                effects.pauseObstacleAlert();
            }

            /*
            if (Arduino.isObstacleGettingIn())
            {
                //msTime = 2000;
                //effects.vibrate(msTime);
                //effects.playObstacleAlert(msTime);

                effects.playConstantSound();
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

*/

            if (progress[0].getDistance() > 0 /*message[2] != null*/)
            {
                textView.setText(progress[0].getFormattedDistance());
            }

            if (progress[0].getMessage() /*message[3]*/ != null)
            {
                Toast.makeText(getApplicationContext(),progress[0].getMessage() /*message[3]*/, Toast.LENGTH_LONG).show();
                Arduino.setMessage(null);
            }
        }
    }
}