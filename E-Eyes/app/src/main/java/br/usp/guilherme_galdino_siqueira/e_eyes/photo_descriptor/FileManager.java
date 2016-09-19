package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by gsiqueira on 9/18/16.
 */
public class FileManager {

    public static void save(Activity activity, byte[] contentInBytes, String folderName, String fileName, String fileType)
    {
        FileOutputStream fos = null;

        try {

            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/E-EYES/" + folderName + "/");

            if (!dir.exists())
            {
                if(!dir.mkdirs()){
                    Log.e("ALERT", "could not create the directories");
                }
            }

            File myFile = new File(dir, fileName + "." + fileType);

            int i = 1;
/*
            while (myFile.exists())
            {
                myFile = new File(dir, fileName + i + "." + fileType);
                i++;
            }
            */

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
}
