package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

/**
 * This class shows the preview image from camera, takes a picture and shows a transparent textbox
 * with the description
 * Created by gsiqueira on 7/14/16.
 */
import android.app.Activity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Constants;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.FeedbackMessage;
import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.PhotoDescriptor;
import br.usp.guilherme_galdino_siqueira.e_eyes.feedback_signal.Effects;

@SuppressWarnings("deprecation")
public class DescriptorActivity extends Activity implements SurfaceHolder.Callback {

    private Bitmap bitmap;

    private static final String CLOUD_VISION_API_KEY = ApiKeys.CLOUD_VISION_KEY;
    private static final String TAG = DescriptorActivity.class.getSimpleName();

    //private boolean isCloudVisionDisabled = false;
    private boolean isViewClickable = true;
    private boolean isResultVisible = false;
    private boolean isTransfering = false;

    ArrayList<Feature> requestsArrayList;
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

        useIntentInfo(this.getIntent());

        startUpSurface();
        startUpTextView();

        effects = new Effects(getApplicationContext(), this);
        feedbackMessage = new FeedbackMessage(this);
        photoDescriptor = new PhotoDescriptor(this);

        jpegCallback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                effects.playCameraShutter();

                feedbackMessage.savePhoto();

                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                uploadImage();
            }
        };
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

            isResultVisible = false;

            return;
        }

        super.onBackPressed();
        // This above line close correctly
    }

    @Override
    protected void onStop() {

        if(bitmap!=null)        {
            bitmap.recycle();
            bitmap=null;
        }

        super.onStop();
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

        menu.findItem(R.id.dim).setTitle("Dimensão: " + defaultDimension);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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

                    else if (defaultDimension == Constants.MAX_IMAGE_DIMENSION)
                    {
                        Preferences.increaseDimension = false;
                        defaultDimensionIndex++;
                        defaultDimension = dimensions[defaultDimensionIndex];
                    }

                    else if (defaultDimension == Constants.MIN_IMAGE_DIMENSION)
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        }

        catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Camera.Parameters parameters = camera.getParameters();

        //Camera.Size size = getBestPreviewSize(width, height, parameters);

        parameters.setPreviewSize(height, width);

        camera.setDisplayOrientation(90);

        camera.setParameters(parameters);

        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;

        effects.stopClockTicks();
    }

    private void useIntentInfo(Intent intent){
        requestsArrayList = new ArrayList<>();

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
    }

    private void startUpSurface()
    {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void startUpTextView()
    {
        textView = (TextView) findViewById(R.id.textView);
        textView.setEnabled(false);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setVisibility(View.GONE);

        if (requestsArrayList.size() == 5)
            textView.setBackgroundResource(R.color.yellowOpacity);

        else
            textView.setBackgroundResource(R.color.redOpacity);



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
                    });
        });
    }


    public void copyToClipBoard()
    {
        feedbackMessage.copiedToClipBoard();
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
            requestsArrayList.add(featureDetection);
        }
    }

    public void captureImage(View v) throws IOException {

        //se a foto já foi tirada - esta em processamento - não faz nada
        if (!isViewClickable)
            return;

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
    }

    public void refreshCamera() {

        if (!isTransfering)
        {
            if (surfaceHolder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }


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

    public void getDescription() throws IOException {

        isTransfering = true;

        effects.blinkClockImage(500);
        effects.playClockTicks();
        feedbackMessage.processing();

        if (Preferences.isCloudVisionDisabled)
        {
            new FakeCloudVisionConnection().execute();
            return;
        }

        new GoogleCloudVisionConnection().execute();
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
        effects.stopClockTicks();

        if (result)
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

            photoDescriptor.setTimes(times);

            photoDescriptor.saveTextDescription();

            feedbackMessage.saveText();
        }

        else
            feedbackMessage.failProcess();

        refreshCamera();
    }

    public void uploadImage() {
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


                        getDescription();
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

                    getDescription();
                }



            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        }
        else {
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

    private class FakeCloudVisionConnection extends AsyncTask <Object, Void, Boolean>{

        long startTime;

        long estimatedTime;

        @Override
        protected Boolean doInBackground(Object... params) {

            try {
                startTime = System.currentTimeMillis();
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            photoDescriptor.callFakeTextAdaptation();
            return null;
        }
        protected void onPostExecute(Boolean result){

            estimatedTime = System.currentTimeMillis() - startTime;

            long times[] = new long[6];

            displayResults(true,times);

            if (Preferences.isSequenceRequestsEnable)
            {
                photoDescriptor.deleteAdaptedText();
                uploadImage();
            }
        }
    }

    private class GoogleCloudVisionConnection extends AsyncTask <Object, Void, Boolean>{

        long startCompressTime;
        long endCompressTime;

        long startEncodeTime;
        long endEncodeTime;

        long startTransferTime;
        long endTransferTime;

        long startWritingTime;
        long endWritingTime;

        long width = bitmap.getWidth();
        long height = bitmap.getHeight();

        protected boolean connect(){
            try {

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
                    annotateImageRequest.setFeatures(requestsArrayList);

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

                startWritingTime = System.currentTimeMillis();
                photoDescriptor.callTextAdaptation(response);
                endWritingTime = System.currentTimeMillis();

                return true;
            }
            catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            }
            catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
            }
            return false;
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            return connect();
        }

        protected void onPostExecute(Boolean success) {

            long compress = endCompressTime - startCompressTime;
            long encode = endEncodeTime - startEncodeTime;
            long transfer = endTransferTime - startTransferTime;
            long writing = endWritingTime - startWritingTime;

            long times[] = {compress, encode, transfer, writing, width, height};

            displayResults(success,times);

            if (Preferences.isSequenceRequestsEnable)
            {
                photoDescriptor.deleteAdaptedText();
                uploadImage();
            }
        }
    }
}