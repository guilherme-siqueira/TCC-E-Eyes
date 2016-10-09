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

    public static boolean isAproaching() {
        return aproaching;
    }

    public static void setAproaching(boolean aproaching) {
        Arduino.aproaching = aproaching;
    }

    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int distance) {
        Arduino.distance = distance;
    }

    public static int getPreviousDistance() {
        return previousDistance;
    }

    public static void setPreviousDistance(int previousDistance) {
        Arduino.previousDistance = previousDistance;
    }

    private static int previousDistance;

    private static boolean aproaching;
    private static int distance;

    public static boolean isTooClose() {
        return tooClose;
    }

    public static void setTooClose(boolean tooClose) {
        Arduino.tooClose = tooClose;
    }

    public static boolean isInYellowArea() {
        return inYellowArea;
    }

    public static void setInYellowArea(boolean inYellowArea) {
        Arduino.inYellowArea = inYellowArea;
    }


    public static boolean isGotInYellowArea() {
        return gotInYellowArea;
    }

    public static void setGotInYellowArea(boolean gotInYellowArea) {
        Arduino.gotInYellowArea = gotInYellowArea;
    }

    public static boolean isLeftYellowArea() {
        return leftYellowArea;
    }

    public static void setLeftYellowArea(boolean leftYellowArea) {
        Arduino.leftYellowArea = leftYellowArea;
    }

    public static boolean isGotInRedArea() {
        return gotInRedArea;
    }

    public static void setGotInRedArea(boolean gotInRedArea) {
        Arduino.gotInRedArea = gotInRedArea;
    }

    public static boolean isLeftRedArea() {
        return leftRedArea;
    }

    public static void setLeftRedArea(boolean leftRedArea) {
        Arduino.leftRedArea = leftRedArea;
    }

    public static boolean isOut() {
        return out;
    }

    public static void setOut(boolean out) {
        Arduino.out = out;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        Arduino.message = message;
    }

    private static String message;

    private static boolean out;

    private static boolean gotInYellowArea;

    private static boolean leftYellowArea;

    private static boolean gotInRedArea;

    private static boolean leftRedArea;


    private static boolean inYellowArea;

    private static boolean tooClose;

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
