package br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by gsiqueira on 9/20/16.
 */
public class Arduino {

    private static boolean arduinoOn;
    private static int obstacleDistance;
    private static boolean obstacleAhead;
    private static boolean lookingForArduino;

    public static boolean isArduinoOn() {
        return arduinoOn;
    }

    public static void setArduinoOn(boolean arduinoOn) {
        Arduino.arduinoOn = arduinoOn;
    }

    public static int getObstacleDistance() {
        return obstacleDistance;
    }

    public static void setObstacleDistance(int obstacleDistance) {
        Arduino.obstacleDistance = obstacleDistance;
    }

    public static boolean isObstacleAhead() {
        return obstacleAhead;
    }

    public static void setObstacleAhead(boolean obstacleAhead) {
        Arduino.obstacleAhead = obstacleAhead;
    }

    public static boolean isLookingForArduino() {
        return lookingForArduino;
    }

    public static void setLookingForArduino(boolean lookingForArduino) {
        Arduino.lookingForArduino = lookingForArduino;
    }
}
