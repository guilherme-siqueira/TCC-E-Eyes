package br.usp.guilherme_galdino_siqueira.e_eyes.google_cloud_vision;

import android.graphics.Bitmap;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.ApiKeys;

/**
 * Created by gsiqueira on 7/12/16.
 */
public class CloudVisionConnection {

    private static final String CLOUD_VISION_API_KEY = ApiKeys.getCloudVisionKey();

    private static final String TAG = CloudVisionConnection.class.getSimpleName();

    BatchAnnotateImagesResponse response;

    public BatchAnnotateImagesResponse getDescription(final Bitmap bitmap) throws IOException {
        // Switch text to loading

        //mImageDetails.setText(R.string.loading_message);

        Thread translation = new Thread() {
            @Override
            public void run() {

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
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            Feature textDetection = new Feature();
                            Feature faceDetection = new Feature();
                            Feature landmarkDetection = new Feature();
                            Feature logoDetection = new Feature();

                            labelDetection.setType("LABEL_DETECTION");
                            textDetection.setType("TEXT_DETECTION");
                            faceDetection.setType("FACE_DETECTION");
                            landmarkDetection.setType("LANDMARK_DETECTION");
                            logoDetection.setType("LOGO_DETECTION");

                            labelDetection.setMaxResults(10);
                            textDetection.setMaxResults(10);
                            faceDetection.setMaxResults(10);
                            landmarkDetection.setMaxResults(10);
                            logoDetection.setMaxResults(10);

                            add(labelDetection);
                            add(textDetection);
                            add(faceDetection);
                            add(landmarkDetection);
                            add(logoDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    response = annotateRequest.execute();

                    //return response;//convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
            }
        };

        translation.start();

        try {
            translation.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

/*
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
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
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            Feature textDetection = new Feature();
                            Feature faceDetection = new Feature();
                            Feature landmarkDetection = new Feature();
                            Feature logoDetection = new Feature();

                            labelDetection.setType("LABEL_DETECTION");
                            textDetection.setType("TEXT_DETECTION");
                            faceDetection.setType("FACE_DETECTION");
                            landmarkDetection.setType("LANDMARK_DETECTION");
                            logoDetection.setType("LOGO_DETECTION");

                            labelDetection.setMaxResults(10);
                            textDetection.setMaxResults(10);
                            faceDetection.setMaxResults(10);
                            landmarkDetection.setMaxResults(10);
                            logoDetection.setMaxResults(10);

                            add(labelDetection);
                            add(textDetection);
                            add(faceDetection);
                            add(landmarkDetection);
                            add(logoDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    response = annotateRequest.execute();

                    //return response;//convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }

                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {

                if (response == null)
                {

                }

                else
                {

                }

                //mImageDetails.setText(result);

            }

        }.execute();

 */

        return response;

    }
}
