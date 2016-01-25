package com.lucime.promfy;

import android.app.Application;
import android.net.Uri;

import java.io.File;

public class LucimeCamUser extends Application {
    private static int backCamera;
    private static int cameraSwitcher;
    private static int disph;
    private static int dispw;
    private static int frontCamera;
    private static UserDetails loggedin_user;
    private static Uri outputImage;
    private static int pin, takeSwitchFlag;
    private static int rotationOfCamera, scenemode, whitebalance ;
    private static String uuid, flashmode;

    public static String getUuid() {
        return uuid;
    }

    public static void setUuid(String uuid) {
        LucimeCamUser.uuid = uuid;
    }

    public static String getFlashmode() {
        return flashmode;
    }

    public static void setFlashmode(String flashmode) {
        LucimeCamUser.flashmode = flashmode;
    }

    public static int getScenemode() {
        return scenemode;
    }

    public static void setScenemode(int scenemode) {
        LucimeCamUser.scenemode = scenemode;
    }

    public static int getWhitebalance() {
        return whitebalance;
    }

    public static void setWhitebalance(int whitebalance) {
        LucimeCamUser.whitebalance = whitebalance;
    }

    public static int getTakeSwitchFlag() {
        return takeSwitchFlag;
    }

    public static void setTakeSwitchFlag(int takeSwitchFlag) {
        LucimeCamUser.takeSwitchFlag = takeSwitchFlag;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cameraSwitcher = 1;
        rotationOfCamera = 0;
        frontCamera = 1;
        backCamera = 0;
    }

    public static UserDetails getLoggedin_user() {
        return loggedin_user;
    }

    public static void setLoggedin_user(String u, String e, String p, String dob) {
        loggedin_user = new UserDetails(u, e, p, dob);
    }

    public static int getPin() {
        return pin;
    }

    public static int setPin() {
        pin = (int) (Math.random() * 9000) + 1000;
        return pin;
    }

    public static int getBackCamera() {
        return backCamera;
    }

    public static void setBackCamera(int backCamera) {
        LucimeCamUser.backCamera = backCamera;
    }

    public static int getCameraSwitcher() {
        return cameraSwitcher;
    }

    public static void setCameraSwitcher(int cameraSwitcher) {
        LucimeCamUser.cameraSwitcher = cameraSwitcher;
    }

    public static int getDisph() {
        return disph;
    }

    public static void setDisph(int disph) {
        LucimeCamUser.disph = disph;
    }

    public static int getDispw() {
        return dispw;
    }

    public static void setDispw(int dispw) {
        LucimeCamUser.dispw = dispw;
    }

    public static int getFrontCamera() {
        return frontCamera;
    }

    public static void setFrontCamera(int frontCamera) {
        LucimeCamUser.frontCamera = frontCamera;
    }

    public static Uri getOutputImage() {
        return outputImage;
    }

    public static void setOutputImage(Uri outputImage) {
        LucimeCamUser.outputImage = outputImage;
    }

    public static int getRotationOfCamera() {
        return rotationOfCamera;
    }

    public static void setRotationOfCamera(int rotationOfCamera) {
        LucimeCamUser.rotationOfCamera = rotationOfCamera;
    }
}
