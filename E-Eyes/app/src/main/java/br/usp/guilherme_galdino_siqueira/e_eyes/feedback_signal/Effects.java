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

    private MediaPlayer shutter, progress;

    private Vibrator vibrator;

    private final ImageView progressIcon;

    private final Animation animation;

    public Effects(Context context, Activity activity) {
        shutter = MediaPlayer.create(context, R.raw.camera);
        progress = MediaPlayer.create(context, R.raw.time);

        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);

        progress.setLooping(true);

        progressIcon = (ImageView) activity.findViewById(R.id.blinkImage);

        animation = new AlphaAnimation(1, 0);
    }

    public void playCameraShutter() {
        if (Preferences.isSoundEffectEnable)
            shutter.start();
    }

    public void playClockTicks() {
        if (Preferences.isSoundEffectEnable)
            progress.start();
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

    public void stopSounds()
    {
        if (progress.isPlaying())
            progress.stop();
    }

    public void blinkClock(int msTime)
    {
        if (Preferences.isImageEffectEnable)
        {
            animation.setDuration(msTime);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            progressIcon.setVisibility(View.VISIBLE);


            progressIcon.startAnimation(animation);
        }
    }

    public void stopBlinking()
    {
        if (Preferences.isImageEffectEnable)
        {
            progressIcon.clearAnimation();
            progressIcon.setVisibility(View.GONE);
        }
    }
}
