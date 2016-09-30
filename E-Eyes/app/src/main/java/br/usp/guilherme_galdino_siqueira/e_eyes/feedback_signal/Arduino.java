package br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal;

/**
 * Created by gsiqueira on 9/20/16.
 */
public class Arduino {

    private static boolean on;
    private static int obstacleDistance;
    private static boolean obstacleGettingCloser = false;
    private static boolean obstacleGettingIn = false;
    private static boolean obstacleGettingAway = false;
    private static boolean obstacleGone = false;

    private static boolean lookingForArduino;

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        Arduino.on = on;
    }

    public static int getObstacleDistance() {
        return obstacleDistance;
    }

    public static void setObstacleDistance(int obstacleDistance) {
        Arduino.obstacleDistance = obstacleDistance;
    }

    public static boolean isObstacleGettingCloser() {
        return obstacleGettingCloser;
    }

    public static void setObstacleGettingCloser(boolean obstacleGettingCloser) {
        Arduino.obstacleGettingCloser = obstacleGettingCloser;
    }

    public static boolean isLookingForArduino() {
        return lookingForArduino;
    }

    public static void setLookingForArduino(boolean lookingForArduino) {
        Arduino.lookingForArduino = lookingForArduino;
    }

    public static boolean isObstacleGone() {
        return obstacleGone;
    }

    public static void setObstacleGone(boolean obstacleGone) {
        Arduino.obstacleGone = obstacleGone;
    }

    public static boolean isObstacleGettingIn() {
        return obstacleGettingIn;
    }

    public static void setObstacleGettingIn(boolean obstacleGettingIn) {
        Arduino.obstacleGettingIn = obstacleGettingIn;
    }

    public static boolean isObstacleGettingAway() {
        return obstacleGettingAway;
    }

    public static void setObstacleGettingAway(boolean obstacleGettingAway) {
        Arduino.obstacleGettingAway = obstacleGettingAway;
    }
}
