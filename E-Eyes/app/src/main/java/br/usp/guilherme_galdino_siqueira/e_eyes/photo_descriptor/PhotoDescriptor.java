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

    public void savePicture(byte[] photo) {
        if (Preferences.isDescriptionAutoSaveEnable) {
            Date date = new Date();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            folderName = dateFormat.format(date);

            FileManager.save(activity, photo, folderName, "Imagem", "jpg");
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
                FileManager.save(activity,textAdapter.getTextualDescription().getBytes(), folderName, "Texto", "txt");

                if ((textAdapter.getFacesPosition()) != null)
                {
                    FileManager.save(activity, textAdapter.getFacesPosition().getBytes(), folderName, "PosiçãoFaces", "txt");
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
                FileManager.save(activity, statistics.getBytes(), folderName, "Estatísticas", "txt");
            }

        }
    }

    public void deleteAdaptedText()
    {
        textAdapter.erase();
    }

}

