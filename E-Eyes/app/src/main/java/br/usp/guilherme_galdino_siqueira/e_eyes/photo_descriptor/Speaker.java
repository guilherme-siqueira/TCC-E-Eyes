package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;
import java.io.File;
import android.os.Bundle;
/**
 * Created by gsiqueira on 6/4/16.
 */

public class Speaker {

    private Activity activity;
    private TextToSpeech speaker;
    private Locale lang;
    private String langName;
    private String langCountry;
    private String text;

    private int queueMode;


    public Speaker(Activity activity)
    {
        this.activity = activity;
        this.langName = "pt";
        this.langCountry = "BR";

        speaker = new TextToSpeech(this.activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    lang = new Locale(langName,langCountry);
                    speaker.setLanguage(lang);
                }
            }
        });

        speaker.setPitch(1.5f);

        speaker.setSpeechRate(2.0f);

        queueMode = TextToSpeech.QUEUE_ADD;
    }

    public void speak(final String text)
    {
        if (text == null)
            return;

        new AsyncTask<Object, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Object... params) {

                try {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ttsGreater21(text);
                        }
                        else {
                            ttsUnder20(text);
                        }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    public void stopSpeaking() {
        if (speaker.isSpeaking())
        {
            queueMode = TextToSpeech.QUEUE_FLUSH;
            speak(" ");
            speaker.stop();
            queueMode = TextToSpeech.QUEUE_ADD;
        }
    }

    public boolean isSpeaking()
    {

        if (speaker.isSpeaking() == true)
            return true;
        else
            return false;
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        speaker.speak(text, queueMode, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        speaker.speak(text,queueMode, null, utteranceId);
    }

    public void save(String text)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            saveGreater21(text);
        }
        else {
            saveUnder20(text);
        }
    }

    @SuppressWarnings("deprecation")
    private void saveUnder20(String text)
    {
        HashMap<String, String> myHashRender = new HashMap();
        String textToConvert = text;
        String destinationFileName = "/sdcard/test.wav";
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, textToConvert);
        speaker.synthesizeToFile(textToConvert, myHashRender, destinationFileName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void saveGreater21(String text)
    {
        String utteranceId=this.hashCode() + "";
        File file = new File(String.format("/sdcard/%s.wav","Audio21"));
        speaker.synthesizeToFile(text, null,file, utteranceId);
    }

}


