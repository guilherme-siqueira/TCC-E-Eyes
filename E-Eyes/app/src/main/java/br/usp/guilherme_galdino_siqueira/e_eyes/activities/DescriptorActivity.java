package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

/**
 * Created by gsiqueira on 7/14/16.
 */
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageContext;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.ApiKeys;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.FeedbackMessage;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.PhotoDescriptor;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Effects;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class DescriptorActivity extends Activity implements SurfaceHolder.Callback {

    private Bitmap bmp;

    private static final String CLOUD_VISION_API_KEY = ApiKeys.getCloudVisionKey();
    private static final String TAG = DescriptorActivity.class.getSimpleName();

    //private boolean isCloudVisionDisabled = false;
    private boolean isViewClickable = true;
    private boolean isResultVisible = false;
    private boolean isTransfering = false;
    private boolean isPreviewRunning = false;

    private boolean rot = false;

    BatchAnnotateImagesResponse response;
    ArrayList array;
    PhotoDescriptor photoDescriptor;

    FeedbackMessage feedbackMessage;
    Effects effects;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback jpegCallback;

    TextView textView;

    int dimensions[] = {2560, 1600, 1200, 1024, 768, 640, 480};

    int dimension = 0;

    int defaultDimension;

    int defaultDimensionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.descriptor_activity);

        Intent intent = this.getIntent();

        effects = new Effects(getApplicationContext(), this);
        feedbackMessage = new FeedbackMessage(this);
        photoDescriptor = new PhotoDescriptor(this);

        array = new ArrayList();

        addRequests(intent, "LABEL");
        addRequests(intent, "TEXT");
        addRequests(intent, "FACE");
        addRequests(intent, "LANDMARK");
        addRequests(intent, "LOGO");

        defaultDimension = intent.getIntExtra("DIMENSION",640);

        for (int i = 0; i < dimensions.length; i++) {
            if (defaultDimension == dimensions[i])
            {
                defaultDimensionIndex = i;
                break;
            }
        }



        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        textView = (TextView) findViewById(R.id.textView);
        textView.setEnabled(false);

        if (array.size() == 5)
        {
            textView.setBackgroundResource(R.color.yellowOpacity);
            //surfaceView.setBackgroundResource(R.color.greenLightOpacity);
        }



        else
        {
            textView.setBackgroundResource(R.color.redOpacity);
            //surfaceView.setBackgroundResource(R.color.redLightOpacity);
        }


        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setVisibility(View.GONE);

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                gestureDetector.setIsLongpressEnabled(true);
                return false;
            }

            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        public void onLongPress(MotionEvent e) {
                            copyToClipBoard();
                        }

/*                        public boolean onDoubleTap(MotionEvent e) {
                            gestureDetector.setIsLongpressEnabled(false);
                            if (isResultVisible) {
                                textView.setEnabled(false);
                                textView.setVisibility(View.GONE);

                                photoDescriptor.stopAudioDescription();
                                feedbackMessage.stopAudio();

                                feedbackMessage.guideTakePicture();

                                isResultVisible = false;
                            }
                            return true;


                        }
                        */
                    });
        });

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                effects.playCameraShutter();

                //feedbackMessage.savePhoto();

                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                /*
                Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                if(display.getRotation() == Surface.ROTATION_0)
                {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bmp = Bitmap.createBitmap(bmp, 0, 0,
                            bmp.getWidth(), bmp.getHeight(), matrix, true);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    data = stream.toByteArray();
                }

                */

                /*
                if(display.getRotation() == Surface.ROTATION_270)
                {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    bmp = Bitmap.createBitmap(bmp, 0, 0,
                            bmp.getWidth(), bmp.getHeight(), matrix, true);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    data = stream.toByteArray();
                }
                */

                //photoDescriptor.savePicture(data);

                    //outStream = new FileOutputStream(String.format("/sdcard/%s.jpg", "E_Eyes_temp2"));

                    //outStream.write(data);
                    //outStream.close();


                uploadImage(bmp);




                //catch (FileNotFoundException e) {
                    //e.printStackTrace();
                //}




                //Toast.makeText(getApplicationContext(), "Foto gerada", Toast.LENGTH_LONG).show();
                //refreshCamera();
            }
        };
    }

/*    private void savePicture(byte[] data)
    {
        if (Preferences.isPhotoAutoSaveEnable)
        {
            FileOutputStream outStream = null;

            try {
                outStream = new FileOutputStream(String.format("/sdcard/%s.jpg", "E_Eyes_temp2"));
                outStream.write(data);
                outStream.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    */

    private void askSavePicture(final byte[] data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("My Title");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                FileOutputStream outStream = null;

                try {
                    outStream = new FileOutputStream(String.format("/sdcard/%s.jpg", "E_Eyes_temp2"));
                    outStream.write(data);
                    outStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void copyToClipBoard()
    {
        //photoDescriptor.stopAudioDescription();
        //feedbackMessage.stopAudio();

        feedbackMessage.copiedToClipBoard();
        //feedbackMessage.guideBackCamera();

        effects.vibrate(50);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("E-EYES", photoDescriptor.getAdaptedText());
        clipboard.setPrimaryClip(clip);
    }

    public void addRequests(Intent intent, String request)
    {
        if (intent.getStringExtra(request) != null)
        {
            Feature featureDetection = new Feature();
            featureDetection.setMaxResults(10);
            featureDetection.setType(request + "_DETECTION");
            array.add(featureDetection);
        }
    }

    public void captureImage(View v) throws IOException {

        //final boolean[] erro = {false};

        //se a foto já foi tirada - esta em processamento - não faz nada
        if (!isViewClickable)
            return;

        /*
        if (isResultVisible) {
            textView.setEnabled(false);
            textView.setVisibility(View.GONE);
            //photoDescriptor.stopAudioDescription();
            isResultVisible = false;
            return;
        }
        */

        // senão, tenta tirar a foto.
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        camera.setParameters(params);

        Camera.AutoFocusCallback autoFocusCallBack = new Camera.AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {
                // se conseguiu tirar a foto, entra em estado de processamento
                if (success) {
                    photoDescriptor.deleteAdaptedText();
                    isViewClickable = false;
                    camera.takePicture(null, null, jpegCallback);
                }
                //senão
                else {
                    feedbackMessage.focusProblem();
                }
            }
        };

        camera.autoFocus(autoFocusCallBack);

/*
        //se tirou a foto, retorna
        if (!isViewClickable)
            return;

        //senão tenta tirar a foto com flash
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);

        camera.autoFocus(new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    photoDescriptor.deleteAdaptedText();
                    isViewClickable = false;
                    camera.takePicture(null, null, jpegCallback);
                }
            }
        });

        // se tirou a foto, retorna
        if (!isViewClickable)
            return;

        //senão, desliga  o flash e acusa erro
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        feedbackMessage.focusProblem();
        */

    }

    public void refreshCamera() {

        if (!isTransfering)
        {
            //View decorView = getWindow().getDecorView();
            // Hide the status bar.
            //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            //decorView.setSystemUiVisibility(uiOptions);

            //camera.setDisplayOrientation(90);

            if (surfaceHolder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
            }

            catch (Exception e) {
            }

            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
            catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        if (Preferences.isSequenceRequestsEnable)
            menu.findItem(R.id.seq).setTitle("Desabilitar teste");
        else
            menu.findItem(R.id.seq).setTitle("Habilitar teste");

        if (Preferences.isCloudVisionDisabled)
            menu.findItem(R.id.google).setTitle("Habilitar Google");
        else
            menu.findItem(R.id.google).setTitle("Desabilitar Google");

        if (Preferences.isYandexTranslatorDisabled)
            menu.findItem(R.id.yandex).setTitle("Habilitar Yandex");
        else
            menu.findItem(R.id.yandex).setTitle("Desabilitar Yandex");

        if (Preferences.isDescriptionAutoSaveEnable)
            menu.findItem(R.id.saveDescription).setTitle("Descartar automaticamente");
        else
            menu.findItem(R.id.saveDescription).setTitle("Salvar automaticamente");

        if (Preferences.isImageEffectEnable)
            menu.findItem(R.id.animation).setTitle("Desabilitar relógio");
        else
            menu.findItem(R.id.animation).setTitle("Habilitar relógio");

        if (Preferences.isSoundEffectEnable)
            menu.findItem(R.id.soundEffect).setTitle("Desabilitar tic-tac");
        else
            menu.findItem(R.id.soundEffect).setTitle("Habilitar tic-tac");

            menu.findItem(R.id.dim).setTitle("Dimensão: "+defaultDimension);


        return true;




        //MenuItem item = menu.findItem(R.id.seq);



        /*
        item = menu.findItem(R.id.google);

        if (Preferences.isCloudVisionDisabled)
            item.setTitle("Habilitar teste");
        else
            item.setTitle("Desabilitar teste");
        */

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.voiceGuide:
                if (Preferences.isAudioMessageEnable)
                    item.setTitle("habilitar guia de voz");
                else
                    item.setTitle("desabilitar guia de voz");
                Preferences.isAudioMessageEnable = !Preferences.isAudioMessageEnable;
                return true;

            case R.id.vibrate:
                if (Preferences.isVibrationEffectEnable)
                    item.setTitle("habilitar vibração");
                else
                    item.setTitle("desabilitar vibração");
                Preferences.isVibrationEffectEnable = !Preferences.isVibrationEffectEnable;
                return true;
                */

            case R.id.soundEffect:
                if (Preferences.isSoundEffectEnable)
                    item.setTitle("Habilitar tic-tac");
                else
                    item.setTitle("Desabilitar tic-tac");
                Preferences.isSoundEffectEnable = !Preferences.isSoundEffectEnable;
                return true;

            case R.id.animation:
                if (Preferences.isImageEffectEnable)
                    item.setTitle("Habilitar relógio");
                else
                    item.setTitle("Desabilitar relógio");
                Preferences.isImageEffectEnable = !Preferences.isImageEffectEnable;
                return true;
            /*
            case R.id.text:
                if (Preferences.isTextualMessageEnable)
                    item.setTitle("habilitar janela de texto");
                else
                    item.setTitle("desabilitar janela de texto");
                Preferences.isTextualMessageEnable = !Preferences.isTextualMessageEnable;
                return true;

            case R.id.savePhoto:
                if (Preferences.isPhotoAutoSaveEnable)
                    item.setTitle("salvar foto automaticamente");
                else
                    item.setTitle("não salvar foto");
                Preferences.isPhotoAutoSaveEnable = !Preferences.isPhotoAutoSaveEnable;
                return true;

            case R.id.saveAudio:
                if (Preferences.isAudioAutoSaveEnable)
                    item.setTitle("salvar audio automaticamente");
                else
                    item.setTitle("não salvar audio automaticamente");
                Preferences.isAudioAutoSaveEnable = !Preferences.isAudioAutoSaveEnable;
                return true;
            */
            case R.id.saveDescription:
                if (Preferences.isDescriptionAutoSaveEnable)
                    item.setTitle("Salvar automaticamente");
                else
                    item.setTitle("Descartar automaticamente");
                Preferences.isDescriptionAutoSaveEnable = !Preferences.isDescriptionAutoSaveEnable;
                return true;

            case R.id.google:
                if (Preferences.isCloudVisionDisabled)
                    item.setTitle("Desabilitar Google");
                else
                    item.setTitle("Habilitar Google");
                Preferences.isCloudVisionDisabled = !Preferences.isCloudVisionDisabled;
                return true;

            case R.id.yandex:
                if (Preferences.isYandexTranslatorDisabled)
                    item.setTitle("Desabilitar Yandex");
                else
                    item.setTitle("Habilitar Yandex");
                Preferences.isYandexTranslatorDisabled = !Preferences.isYandexTranslatorDisabled;
                return true;

            case R.id.seq:
                if (Preferences.isSequenceRequestsEnable)
                    item.setTitle("Habilitar teste");
                else
                    item.setTitle("Desabilitar teste");
                Preferences.isSequenceRequestsEnable = !Preferences.isSequenceRequestsEnable;

            case R.id.dim:
                if (Preferences.isChangeDimensionEnable)
                {
                    if (Preferences.increaseDimension && defaultDimensionIndex > 0)
                    {
                        defaultDimensionIndex--;
                        defaultDimension = dimensions[defaultDimensionIndex];
                    }

                    else if (!Preferences.increaseDimension && defaultDimensionIndex < dimensions.length - 1)
                    {
                        defaultDimensionIndex++;
                        defaultDimension = dimensions[defaultDimensionIndex];
                    }

                    else if (defaultDimension == Preferences.maxDimension)
                    {
                        Preferences.increaseDimension = false;
                        defaultDimensionIndex++;
                        defaultDimension = dimensions[defaultDimensionIndex];
                    }

                    else if (defaultDimension == Preferences.minDimension)
                    {
                        Preferences.increaseDimension = true;
                        defaultDimensionIndex--;
                        defaultDimension = dimensions[defaultDimensionIndex];
                    }

                }
                item.setTitle("Dimensão: " + defaultDimension);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)height / width;

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            //feedbackMessage.guideTakePicture();
        }

        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


/*
        if (isPreviewRunning)
        {
            camera.stopPreview();
        }

        */

        Camera.Parameters parameters = camera.getParameters();
        //Camera.Size size = getBestPreviewSize(width, height, parameters);

        /*
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();



        if(display.getRotation() == Surface.ROTATION_0)
        {
            rot = false;
            //Toast.makeText(getApplicationContext(), ""+display.getRotation(), Toast.LENGTH_LONG).show();
            parameters.setPreviewSize(height, width);
            camera.setDisplayOrientation(90);
            //parameters.setRotation(90);

            //Toast.makeText(getApplicationContext(), "ROTATION 0", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "display: "+display.getRotation() + "rot: " + Surface.ROTATION_0, Toast.LENGTH_LONG).show();
        }

        if(display.getRotation() == Surface.ROTATION_90) {


                camera.setDisplayOrientation(0);

                //parameters.setRotation(0);


            //Toast.makeText(getApplicationContext(), ""+display.getRotation(), Toast.LENGTH_LONG).show();


            parameters.setPreviewSize(width, height );
            //camera.setDisplayOrientation(0);
            //Toast.makeText(getApplicationContext(), "ROTATION 90", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "display: "+display.getRotation() + "rot: " + Surface.ROTATION_90, Toast.LENGTH_LONG).show();
        }


        if(display.getRotation() == Surface.ROTATION_180)
        {
            //Toast.makeText(getApplicationContext(), ""+display.getRotation(), Toast.LENGTH_LONG).show();
            parameters.setPreviewSize(height, width);
            //camera.setDisplayOrientation(270);

            //Toast.makeText(getApplicationContext(), "ROTATION 180", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "display: "+display.getRotation() + "rot: " + Surface.ROTATION_180, Toast.LENGTH_LONG).show();
        }





        if(display.getRotation() == Surface.ROTATION_270)
        {

            //parameters.setRotation(180);
                //camera.setDisplayOrientation(180);



            //Toast.makeText(getApplicationContext(), ""+display.getRotation(), Toast.LENGTH_LONG).show();
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(180);

            Toast.makeText(getApplicationContext(), "display: "+display.getRotation() + "rot: " + Surface.ROTATION_270, Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), "ROTATION 270", Toast.LENGTH_LONG).show();
        }

        */



        //Toast.makeText(getApplicationContext(), ""+display.getRotation(), Toast.LENGTH_LONG).show();

        parameters.setPreviewSize(height,width);

        camera.setDisplayOrientation(90);

        camera.setParameters(parameters);

        refreshCamera();
/*
        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {

        }

        */
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;

        //feedbackMessage.stopAudio();
        effects.stopSounds();
        //photoDescriptor.stopAudioDescription();

        //feedbackMessage.closeActivity();
    }

    public void getDescription(final Bitmap bitmap) throws IOException {


        isTransfering = true;

        effects.blinkClock(500);
        effects.playClockTicks();

        feedbackMessage.processing();



        if (Preferences.isCloudVisionDisabled)
        {
            new AsyncTask<Object, Void, Boolean>()
            {
                long startTime;

                long estimatedTime;

                long startDownloadTime;
                long endDownloadTime;

                int fileLength;

                int linkSpeed;

                @Override
                protected Boolean doInBackground(Object... params) {

                    try {

                        // Determine whether on campus or not.
                        //WifiManager wifiMgr = (WifiManager) DescriptorActivity.this.getSystemService(Context.WIFI_SERVICE);
                        //WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                        //linkSpeed = wifiInfo.getLinkSpeed();


                        //WifiInfo wifiInfo = wifiManger.getConnectionInfo();

                        /*
                        InputStream input = null;
                        OutputStream output = null;
                        HttpURLConnection connection = null;
                        String link = "http://download.thinkbroadband.com/5MB.zip";
                        URL url = new URL(link);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        String error;
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            error =  "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                        }
                        fileLength = connection.getContentLength();

                        startDownloadTime = System.currentTimeMillis();
                        input = connection.getInputStream();
                        byte data[] = new byte[1024];
                        while ((input.read(data)) != -1)
                        {

                        }
                        endDownloadTime = System.currentTimeMillis();
                        */

                        startTime = System.currentTimeMillis();
                        Thread.sleep(5000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /*catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
                    photoDescriptor.callFakeTextAdaptation();
                    return null;
                }
                protected void onPostExecute(Boolean result){

                    long download = endDownloadTime - startDownloadTime;

                    estimatedTime = System.currentTimeMillis() - startTime;

                    long times[] = new long[6];

                    displayResults(true,times);

                    if (Preferences.isSequenceRequestsEnable)
                    {
                        photoDescriptor.deleteAdaptedText();
                        uploadImage(bitmap);
                    }
                }
            }.execute();
            return;
        }

        new AsyncTask<Object, Void, Boolean>() {

            //long startTime;

            long startCompressTime;
            long endCompressTime;

            long startEncodeTime;
            long endEncodeTime;

            long startTransferTime;
            long endTransferTime;

            long startWritingTime;
            long endWritingTime;

            long startDownloadTime;
            long endDownloadTime;

            long width = bitmap.getWidth();
            long height = bitmap.getHeight();

            //long estimatedTime;

            @Override
            protected Boolean doInBackground(Object... params) {

                //startTime = System.currentTimeMillis();

                try {
/*
                    InputStream input = null;
                    OutputStream output = null;
                    HttpURLConnection connection = null;
                    String link = "http://download.thinkbroadband.com/5MB.zip";
                    URL url = new URL(link);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    String error;
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        error =  "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                    }
                    int fileLength = connection.getContentLength();

                    startDownloadTime = System.currentTimeMillis();
                    input = connection.getInputStream();
                    endDownloadTime = System.currentTimeMillis();

  */

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


                        startCompressTime = System.currentTimeMillis();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        endCompressTime = System.currentTimeMillis();

                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        //feedbackMessage.savePhoto();
                        //photoDescriptor.savePicture(imageBytes);

                        // Base64 encode the JPEG
                        startEncodeTime = System.currentTimeMillis();
                        base64EncodedImage.encodeContent(imageBytes);
                        endEncodeTime = System.currentTimeMillis();

                        annotateImageRequest.setImage(base64EncodedImage);

                        ImageContext imageContext = new ImageContext();
                        String [] languages = { "pt-BR" };
                        imageContext.setLanguageHints(Arrays.asList(languages));
                        annotateImageRequest.setImageContext(imageContext);


                        // add the features we want
                        annotateImageRequest.setFeatures(array);

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    startTransferTime = System.currentTimeMillis();
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    endTransferTime = System.currentTimeMillis();

                    //descriptor.describe(response, false);
                    //desc.describe(response, false);

                    startWritingTime = System.currentTimeMillis();
                    photoDescriptor.callTextAdaptation(response);
                    endWritingTime = System.currentTimeMillis();

                    return true;
                    //return convertResponseToString(response);

                }
                catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                }
                catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return false;
            }

            protected void onPostExecute(Boolean result) {

                //estimatedTime = System.currentTimeMillis() - startTime;

                long download = endDownloadTime - startDownloadTime;

                long compress = endCompressTime - startCompressTime;
                long encode = endEncodeTime - startEncodeTime;
                long transfer = endTransferTime - startTransferTime;
                long writing = endWritingTime - startWritingTime;

                long times[] = {compress, encode, transfer, writing, width, height};

                displayResults(result,times);

                if (Preferences.isSequenceRequestsEnable)
                {
                    photoDescriptor.deleteAdaptedText();
                    uploadImage(bitmap);
                }
            }
        }.execute();
    }

    public void displayResults(boolean result,long[] times)
    {
        //alerta tela disponível para clique
        isViewClickable = true;

        //alerta terminou a transferencia
        isTransfering = false;

        //apaga o ícone de espera
        effects.stopBlinking();

        //interrompe som de espera
        effects.stopSounds();

        if (result == true)
        {
            feedbackMessage.endProcess();

            //alerta tela exibindo resultado
            isResultVisible = true;

            //habilita e exibe a caixa de texto com resultado
            textView.setEnabled(true);
            textView.setVisibility(View.VISIBLE);

            textView.setText(photoDescriptor.getAdaptedText());

            textView.setContentDescription(getString(R.string.activity_descriptor_text) + photoDescriptor.getAdaptedText());

            surfaceView.setContentDescription(getString(R.string.activity_descriptor_no_camera));

            surfaceView.setEnabled(false);

            //inicia a audio descrição
            //photoDescriptor.playAudioDescription();

            photoDescriptor.setTimes(times);

            photoDescriptor.saveTextDescription();

            feedbackMessage.saveText();


        }

        else
        {
            feedbackMessage.failProcess();
        }

        //feedbackMessage.guideCopy();
        //feedbackMessage.guideBackCamera();



        refreshCamera();
    }

    public void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {

            try {
                // scale the image to save on bandwidth
                //bitmap = scaleBitmapDown(bitmap, 1200);
                if (Preferences.isSequenceRequestsEnable)
                {
                    if (dimension < dimensions.length)
                    {
                        bitmap = scaleBitmapDown(bitmap, dimensions[dimension]);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        feedbackMessage.savePhoto();
                        photoDescriptor.savePicture(imageBytes);


                        getDescription(bitmap);
                        dimension++;
                    }
                    else
                        dimension=0;
                }

                else
                {
                    bitmap = scaleBitmapDown(bitmap, defaultDimension);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();
                    feedbackMessage.savePhoto();
                    photoDescriptor.savePicture(imageBytes);

                    getDescription(bitmap);
                }



            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {

        if (isResultVisible) {
            textView.setEnabled(false);
            textView.setVisibility(View.GONE);

            surfaceView.setContentDescription(getString(R.string.activity_descriptor_camera));

            surfaceView.setEnabled(true);

            //photoDescriptor.stopAudioDescription();
            //feedbackMessage.stopAudio();
            //feedbackMessage.guideTakePicture();

            isResultVisible = false;

            return;
        }

        super.onBackPressed();
        // This above line close correctly
    }

    @Override
    protected void onStop() {

        if(bmp!=null)        {
            bmp.recycle();
            bmp=null;
        }

        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration _newConfig){
        super.onConfigurationChanged(_newConfig);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        if(width > height){



// layout for landscape
        }else{
// layout for portrait
        }
    }
}