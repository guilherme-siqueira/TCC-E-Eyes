package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;

import static br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.TextAdapterConstants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gsiqueira on 7/6/16.
 */
public class TextAdapter {

    BatchAnnotateImagesResponse response;

    private FeatureContent labelElement = new FeatureContent();
    private FeatureContent textElement = new FeatureContent();
    private FeatureContent faceElement = new FeatureContent();
    private FeatureContent landmarkElement = new FeatureContent();
    private FeatureContent logoElement = new FeatureContent();
    private FeatureContent statistics = new FeatureContent();

    private FeatureContent facePosition = new FeatureContent();

    private int nFaces = 0;

    private String textualDescription;

    private float minScore = 0.8f;

    private void writeLabel() {
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();

        if (labels != null) {

            for (EntityAnnotation label : labels) {

                statistics.concat(label.getDescription() + ": " + label.getScore());

                if (label.getScore() >= minScore )
                    labelElement.concat(label.getDescription());

            }
            labelElement.concat("\n");
            statistics.concat("\n");
        }
    }

    private void writeText() {
        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();

        if (texts != null) {

            statistics.concat(texts.get(0).getDescription() + ": " + texts.get(0).getScore());

            //if (texts.get(0).getScore() >= minScore )
                textElement.concat(texts.get(0).getDescription());
        }
    }

    private void writeFace() {

        List<FaceAnnotation> faces = response.getResponses().get(0).getFaceAnnotations();

        String head, anger, hat, joy, surprise, sorrow;

        int i;

        if (faces != null) {

            nFaces = faces.size();

            for (FaceAnnotation face : faces) {

                ArrayList<String> unlikely = new ArrayList<>();
                ArrayList<String> possible = new ArrayList<>();
                ArrayList<String> likely = new ArrayList<>();

                String content = "";

                if (nFaces == 1)
                    head = "Ela ";

                else {
                    i = faces.indexOf(face) + 1;
                    head = "A " + i + "ª pessoa ";
                    statistics.concat(head);

                }

                for (int j=0; j<face.getBoundingPoly().getVertices().size();j++)
                {
                    try {
                        facePosition.concat(face.getBoundingPoly().getVertices().get(j).getX().toString());
                        facePosition.concat(face.getBoundingPoly().getVertices().get(j).getY().toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                anger = face.getAngerLikelihood();
                hat = face.getHeadwearLikelihood();
                joy = face.getJoyLikelihood();
                surprise = face.getSurpriseLikelihood();
                sorrow = face.getSorrowLikelihood();

                statistics.concat("anger: "+anger);

                if (anger.contains("UNLIKELY")) {
                    unlikely.add(FACE_ANGER);
                }
                else if (anger.contains("LIKELY")) {
                    likely.add(FACE_ANGER);
                }
                else if (anger.contains("POSSIBLE")) {
                    possible.add(FACE_ANGER);
                }


                statistics.concat("joy: "+joy);

                if (joy.contains("UNLIKELY")) {
                    unlikely.add(FACE_JOY);
                }
                else if (joy.contains("LIKELY")) {
                    likely.add(FACE_JOY);
                }
                else if (joy.contains("POSSIBLE")) {
                    possible.add(FACE_JOY);
                }


                statistics.concat("surprise: "+surprise);

                if (surprise.contains("UNLIKELY")) {
                    unlikely.add(FACE_SURPRISE);
                }
                else if (surprise.contains("LIKELY")) {
                    likely.add(FACE_SURPRISE);
                }
                else if (surprise.contains("POSSIBLE")) {
                    possible.add(FACE_SURPRISE);
                }


                statistics.concat("sorrow: "+sorrow);

                if (sorrow.contains("UNLIKELY")) {
                    unlikely.add(FACE_SORROW);
                }
                else if (sorrow.contains("LIKELY")) {
                    likely.add(FACE_SORROW);
                }
                else if (sorrow.contains("POSSIBLE")) {
                    possible.add(FACE_SORROW);
                }


                statistics.concat("hat: "+hat);

                if (!hat.contains("UNLIKELY")) {

                    if (hat.contains("LIKELY")) {
                        likely.add(FACE_HAT);
                    }
                    else if (hat.contains("POSSIBLE")) {
                        possible.add(FACE_HAT);
                    }
                }


                if (unlikely.size()>0)
                {
                    content += IS_NOT + unlikely.get(0);

                    for (int j=1; j< unlikely.size();j++)
                    {
                        content += NOR + unlikely.get(j);
                    }
                }
                if (likely.size()>0)
                {
                    content += BUT + IS + likely.get(0);

                    for (int j=1; j< likely.size() - 1;j++)
                    {
                        content += COMMA + likely.get(j);
                    }

                    if (likely.size() > 1)
                        content += AND + likely.get(likely.size() - 1);
                }
                if (possible.size()>0)
                {
                    content += AND + SEEMS_TO_BE + possible.get(0);

                    for (int j=1; j< possible.size() - 1;j++)
                    {
                        content += COMMA + possible.get(j);
                    }

                    if (possible.size() > 1)
                        content += AND + possible.get(possible.size() - 1);
                }

                content += END;

                faceElement.concat(head + content);
            }

        }
    }

    private void writeLandmarks() {
        List<EntityAnnotation> landmarks = response.getResponses().get(0).getLandmarkAnnotations();


        if (landmarks != null) {

            for (EntityAnnotation landmark : landmarks) {
                statistics.concat(landmark.getDescription() + ": " + landmark.getScore());

                if (landmark.getScore() >= minScore )
                    landmarkElement.concat(landmark.getDescription());
            }
        }
    }

    private void writeLogo() {

        List<EntityAnnotation> logos = response.getResponses().get(0).getLogoAnnotations();

        if (logos != null) {

            logoElement.concat("Um ou mais logotipos foram identificados:\n");

            for (EntityAnnotation logo : logos) {

                statistics.concat(logo.getDescription()+ ": "+ logo.getScore());

                if (logo.getScore() >= minScore )
                    logoElement.concat(logo.getDescription());

            }
        }
    }

    private void writeTextualDescription() {
        if (landmarkElement.getText() != null) {

            landmarkElement.translate();

            textualDescription = LANDMARK_INTRO + landmarkElement.getText() + ". ";

            if (nFaces == 1)
                textualDescription += FACE_INTRO + ONE_FACE + faceElement.getText();
            else if (nFaces == 2)
                textualDescription += FACE_INTRO + TWO_FACES + faceElement.getText();
            else if (nFaces > 2)
                textualDescription += FACE_INTRO + MORE_FACES_BEGINNING + nFaces + MORE_FACES_END + faceElement.getText();

            if (labelElement.getText() != null) {
                labelElement.translate();
                textualDescription += LABEL_INTRO + labelElement.getText();
            }

            if (textElement.getText() != null) {
                textualDescription += TEXT_INTRO + textElement.getText();
            }
        } else if (nFaces != 0) {
            textualDescription = FACE_INTRO;
            if (nFaces == 1)
                textualDescription += ONE_FACE + faceElement.getText();
            else if (nFaces == 2)
                textualDescription += TWO_FACES + faceElement.getText();
            else if (nFaces > 2)
                textualDescription += MORE_FACES_BEGINNING + nFaces + MORE_FACES_END + faceElement.getText();

            if (labelElement.getText() != null) {
                labelElement.translate();
                textualDescription += LABEL_INTRO + labelElement.getText();
            }

            if (textElement.getText() != null) {
                textualDescription += TEXT_INTRO + textElement.getText();
            }
        } else if (labelElement.getText() != null) {

            labelElement.translate();

            textualDescription = LABEL_INTRO + labelElement.getText();

            if (textElement.getText() != null) {
                textualDescription += TEXT_INTRO + textElement.getText();
            }
        } else if (textElement.getText() != null) {
            textualDescription = TEXT_INTRO + textElement.getText();
        }

    }

    public void rewrite(BatchAnnotateImagesResponse response) {

        this.response = response;

        writeLabel();
        writeText();
        writeFace();
        writeLogo();
        writeLandmarks();

        writeTextualDescription();
    }

    public String getTextualDescription() {

        return textualDescription;
    }

    public void fakeRewrite()
    {
        textualDescription = "Isso é apenas um teste: " +
                "Ouviram do Ipiranga as margens plácidas, " +
                "de um povo o brado heróico retumbante. " +
                "E o sol da liberdade em raios fúlgidos, " +
                "brilhou no céu da pátria nesse instante. " +
                "Se o penhor dessa igualdade conseguimos conquistar com braço forte. " +
                "Em teu seio, ó liberdade, desafia o nosso peito a própria morte. " +
                "Ó patria amada, idolatrada, salve, salve. " +
                "Brasil um sonho intenso, um raio vívido.";
    }

    public void erase() {
        textualDescription = null;
        textElement.setText(null);
        labelElement.setText(null);
        logoElement.setText(null);
        landmarkElement.setText(null);
        faceElement.setText(null);
        statistics.setText(null);
        facePosition.setText(null);
    }

    public String getStatistics()
    {
        return statistics.getText();
    }

    public String getFacesPosition()
    {
        return facePosition.getText();
    }
}