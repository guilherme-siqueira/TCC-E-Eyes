package br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal;

import android.app.Activity;
import android.widget.Toast;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Messages;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.Speaker;
import br.usp.guilherme_galdino_siqueira.e_eyes.activities.R;

/**
 * Created by gsiqueira on 7/21/16.
 */
public class FeedbackMessage {

    private Speaker speaker;

    private Activity activity;

    private String COPIED_TO_CLIP_BOARD;
    private String FAIL_PROCESS;
    private String PROCESSING;
    private String END_PROCESS;
    private String IMAGE_STORED;
    private String CLOSING_ACTIVITY;
    private String NO_CONNECTION;
    private String TEXT_STORED;
    private String FOCUS_PROBLEM;

    public FeedbackMessage(Activity activity) {
        this.activity = activity;
        speaker = new Speaker(activity);

        COPIED_TO_CLIP_BOARD = this.activity.getString(R.string.copied_to_clip_board);
        FAIL_PROCESS = this.activity.getString(R.string.fail_process);
        PROCESSING = this.activity.getString(R.string.processing);
        END_PROCESS = this.activity.getString(R.string.end_process);
        IMAGE_STORED = this.activity.getString(R.string.image_stored);
        CLOSING_ACTIVITY = this.activity.getString(R.string.closing_activity);
        NO_CONNECTION = this.activity.getString(R.string.no_connection);
        TEXT_STORED = this.activity.getString(R.string.text_stored);
        FOCUS_PROBLEM = this.activity.getString(R.string.focus_problem);
    }

    public void copiedToClipBoard() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), COPIED_TO_CLIP_BOARD, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(COPIED_TO_CLIP_BOARD);
    }

    public void failProcess() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), FAIL_PROCESS, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(FAIL_PROCESS);
    }

    public void processing() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), PROCESSING, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(PROCESSING);
    }

    public void endProcess() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), END_PROCESS, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(END_PROCESS);
    }
/*
    public void guideTakePicture() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), Messages.TAKE_PICTURE, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(Messages.TAKE_PICTURE);
    }

    public void guideBackCamera() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), Messages.GO_BACK_TO_CAMERA, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(Messages.GO_BACK_TO_CAMERA);
    }

    public void guideCopy() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), Messages.COPY_TO_CLIPBOARD, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(Messages.COPY_TO_CLIPBOARD);
    }

*/
    public void savePhoto()
    {
        if (Preferences.isDescriptionAutoSaveEnable)
            Toast.makeText(activity.getApplicationContext(), IMAGE_STORED, Toast.LENGTH_LONG).show();
    }

    public void saveText()
    {
        if (Preferences.isDescriptionAutoSaveEnable)
            Toast.makeText(activity.getApplicationContext(), TEXT_STORED, Toast.LENGTH_LONG).show();
    }

    public void stopAudio() {
            speaker.stopSpeaking();
    }

    public void closeActivity()
    {
        if (Preferences.isAudioMessageEnable)
            speaker.speak(CLOSING_ACTIVITY);
    }

    public void noConnection() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
        if (Preferences.isAudioMessageEnable)
            speaker.speak(NO_CONNECTION);
    }

    public void focusProblem()
    {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), FOCUS_PROBLEM, Toast.LENGTH_LONG).show();
    }

    public void turningFlashOn()
    {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), "FLASH IS ON", Toast.LENGTH_LONG).show();
    }
}
