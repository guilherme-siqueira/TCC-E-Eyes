package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import android.app.Activity;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BoundingPoly;

import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.File;

import android.os.Environment;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;

/**
 * Created by gsiqueira on 7/6/16.
 */
public class PhotoDescriptor {

    TextAdapter textAdapter;
    Speaker speaker;
    Activity activity;

    String folderName = null;

    String logData = null;

    public PhotoDescriptor(Activity activity) {
        this.activity = activity;
        textAdapter = new TextAdapter();
        speaker = new Speaker(activity);
    }

    public void callTextAdaptation(BatchAnnotateImagesResponse response) {
        textAdapter.rewrite(response);
        //textAdapter.fakeRewrite();
    }

    public void callFakeTextAdaptation() {
        //textAdapter.rewrite(response);
        textAdapter.fakeRewrite();
    }

    public String getAdaptedText() {
        return textAdapter.getTextualDescription();
    }

    public void playAudioDescription() {
        speaker.speak(textAdapter.getTextualDescription());
    }

    public void savePicture(byte[] photo) {
        if (Preferences.isDescriptionAutoSaveEnable) {
            Date date = new Date();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            folderName = dateFormat.format(date);

            save(photo, "Imagem", "jpg");
        }
    }

    public void saveTextDescription() {
        if (Preferences.isDescriptionAutoSaveEnable) {
            /*if (folderName == null) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                folderName = dateFormat.format(date);
            }
            */

            if (textAdapter.getTextualDescription() != null)
            {
                save(textAdapter.getTextualDescription().getBytes(), "Texto", "txt");

                if ((textAdapter.getFacesPosition()) != null)
                {
                    save(textAdapter.getFacesPosition().getBytes(), "PosiçãoFaces", "txt");
                }
            }
        }

        saveStatistics();

        //folderName = null;
    }

    public void setTimes(long[] statData)
    {
        logData = "TEMPO DE COMPRESSÃO: " + statData[0] + "ms\n";
        logData += "TEMPO DE CODIFICAÇÃO: " + statData[1] + "ms\n";
        logData += "TEMPO DE TRANSFERÊNCIA + PROCESSAMENTO: " + statData[2] + "ms\n";
        logData += "TEMPO DE REESCRITA: " + statData[3] + "ms\n";

        logData += "DIMENSÕES DA IMAGEM: " + statData[4] + "x" + statData[5] +"\n";
    }

    private void saveStatistics()
    {
        if (Preferences.isDescriptionAutoSaveEnable) {
            /*
            if (folderName == null) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                folderName = dateFormat.format(date);
            }
            */

            //if (textAdapter.getStatistics() != null)
            {
                String statistics = logData + textAdapter.getStatistics();
                save(statistics.getBytes(),"Estatísticas","txt");
            }

        }
        //folderName = null;
    }

    private void save(byte[] contentInBytes, String fileName, String fileType)
    {
        FileOutputStream fos = null;

        try {

            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/E-EYES/" + folderName + "/");

            if (!dir.exists())
            {
                if(!dir.mkdirs()){
                    Log.e("ALERT","could not create the directories");
                }
            }

            File myFile = new File(dir, fileName + "." + fileType);

            int i = 1;

            while (myFile.exists())
            {
                myFile = new File(dir, fileName + i + "." + fileType);
                i++;
            }

            if (!myFile.exists())
            {
                myFile.createNewFile();
            }



            fos = new FileOutputStream(myFile);

            fos.write(contentInBytes);
            fos.close();

            //if(myFile != null )//stopFlag = mSensorManager.cancelTriggerSensor(null, null);
            //Toast.makeText(activity.getBaseContext(), "A descrição foi salva no dispositivo.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity.getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopAudioDescription()
    {
        speaker.stopSpeaking();
    }

    public void deleteAdaptedText()
    {
        textAdapter.erase();
    }

}

