package br.usp.guilherme_galdino_siqueira.e_eyes.properties;

import android.content.Context;
import android.os.Environment;

/**
 * Created by gsiqueira on 9/25/16.
 */
public class Constants {

    public static final int MAX_IMAGE_DIMENSION = 2560;

    public static final int MIN_IMAGE_DIMENSION = 480;

    public static final String DIRECTORY_PATH = Environment.getExternalStorageDirectory().toString()+"/E-EYES/";

    public static int defaultDimension;

    public static int dimensions[] = {2560, 1600, 1200, 1024, 768, 640, 480};

    public static int defaultDimensionIndex = 0;

    public static int fontSize = 40;

}
