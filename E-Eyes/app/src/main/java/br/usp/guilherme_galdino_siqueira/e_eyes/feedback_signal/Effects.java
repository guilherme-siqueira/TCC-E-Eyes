package br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal;

/**
 * Created by gsiqueira on 7/18/16.
 */

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import br.usp.guilherme_galdino_siqueira.e_eyes.activities.R;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;

public class Effects {

    private MediaPlayer shutter, clockTicks, obstacleAlert,constantSound;

    private Vibrator vibrator;

    private ImageView clockImageView;

    private Animation clockAnimation;

    public Effects(Context context, Activity activity) {
        shutter = MediaPlayer.create(context, R.raw.camera);

        obstacleAlert = MediaPlayer.create(context, R.raw.alert);

        obstacleAlert.setLooping(true);

        constantSound = MediaPlayer.create(context, R.raw.constant);

        constantSound.setLooping(true);

        clockTicks = MediaPlayer.create(context, R.raw.time);

        clockTicks.setLooping(true);

        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);

        clockImageView = (ImageView) activity.findViewById(R.id.blinkImage);

        clockAnimation = new AlphaAnimation(1, 0);
    }

    public void playCameraShutter() {
        if (Preferences.isSoundEffectEnable)
            shutter.start();
    }

    public void playObstacleAlert(int msTime) {

        obstacleAlert.start();

        try {
            Thread.sleep(msTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        obstacleAlert.pause();

    }

    public void setConstantSoundVolume(float volume)
    {
        constantSound.setVolume(volume, volume);
    }

    public void playConstantSound() {

        constantSound.start();
    }

    public void pauseConstantSound() {

        if (constantSound.isPlaying())
            constantSound.pause();
    }

    public void playObstacleAlert() {

        obstacleAlert.start();
    }

    public void pauseObstacleAlert()
    {
        if (obstacleAlert.isPlaying())
            obstacleAlert.pause();
    }

    public void playClockTicks() {
        if (Preferences.isSoundEffectEnable)
            clockTicks.start();
    }

    public void vibrate(int msTime)
    {
        if (Preferences.isVibrationEffectEnable)
            vibrator.vibrate(msTime);
    }

    public void stopVibration()
    {
        vibrator.cancel();
    }

    public void stopClockTicks()
    {
        if (clockTicks.isPlaying())
            clockTicks.stop();
    }

    public void blinkClockImage(int msTime)
    {
        if (Preferences.isImageEffectEnable)
        {
            clockAnimation.setDuration(msTime);
            clockAnimation.setInterpolator(new LinearInterpolator());
            clockAnimation.setRepeatCount(Animation.INFINITE);
            clockAnimation.setRepeatMode(Animation.REVERSE);
            clockImageView.setVisibility(View.VISIBLE);


            clockImageView.startAnimation(clockAnimation);
        }
    }

    public void stopBlinking()
    {
        if (Preferences.isImageEffectEnable)
        {
            clockImageView.clearAnimation();
            clockImageView.setVisibility(View.GONE);
        }
    }
}
