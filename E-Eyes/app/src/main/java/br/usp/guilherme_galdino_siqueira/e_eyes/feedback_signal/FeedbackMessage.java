package br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal;

import android.app.Activity;
import android.widget.Toast;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;
import br.usp.guilherme_galdino_siqueira.e_eyes.activities.R;

/**
 * Created by gsiqueira on 7/21/16.
 */
public class FeedbackMessage {

    private Activity activity;

    private final String COPIED_TO_CLIP_BOARD;
    private final String FAIL_PROCESS;
    private final String PROCESSING;
    private final String END_PROCESS;
    private final String IMAGE_STORED;
    private final String NO_CONNECTION;
    private final String TEXT_STORED;
    private final String FOCUS_PROBLEM;

    public FeedbackMessage(Activity activity) {
        this.activity = activity;

        COPIED_TO_CLIP_BOARD = this.activity.getString(R.string.copied_to_clip_board);
        FAIL_PROCESS = this.activity.getString(R.string.fail_process);
        PROCESSING = this.activity.getString(R.string.processing);
        END_PROCESS = this.activity.getString(R.string.end_process);
        IMAGE_STORED = this.activity.getString(R.string.image_stored);
        NO_CONNECTION = this.activity.getString(R.string.no_connection);
        TEXT_STORED = this.activity.getString(R.string.text_stored);
        FOCUS_PROBLEM = this.activity.getString(R.string.focus_problem);
    }

    public void copiedToClipBoard() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), COPIED_TO_CLIP_BOARD, Toast.LENGTH_LONG).show();
    }

    public void failProcess() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), FAIL_PROCESS, Toast.LENGTH_LONG).show();
    }

    public void processing() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), PROCESSING, Toast.LENGTH_LONG).show();
    }

    public void endProcess() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), END_PROCESS, Toast.LENGTH_LONG).show();
    }

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

    public void noConnection() {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), NO_CONNECTION, Toast.LENGTH_LONG).show();
    }

    public void focusProblem()
    {
        if (Preferences.isTextualMessageEnable)
            Toast.makeText(activity.getApplicationContext(), FOCUS_PROBLEM, Toast.LENGTH_LONG).show();
    }

}