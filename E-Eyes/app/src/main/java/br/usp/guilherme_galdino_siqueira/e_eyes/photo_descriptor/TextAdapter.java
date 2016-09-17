package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gsiqueira on 7/6/16.
 */
public class TextAdapter {

    BatchAnnotateImagesResponse response;

    List<EntityAnnotation> labels;
    List<EntityAnnotation> texts;
    List<FaceAnnotation> faces;
    List<EntityAnnotation> landmarks;
    List<EntityAnnotation> logos;

    public FeatureContent labelElement = new FeatureContent();
    public FeatureContent textElement = new FeatureContent();
    public FeatureContent faceElement = new FeatureContent();
    public FeatureContent landmarkElement = new FeatureContent();
    public FeatureContent logoElement = new FeatureContent();
    public FeatureContent statistics = new FeatureContent();

    public FeatureContent facePosition = new FeatureContent();

    ArrayList<BoundingPoly> boundingPolyList = new ArrayList<BoundingPoly>();

    private static String LANDMARK_INTRO = "A foto parece ter sido tirada em ";
    private static String LABEL_INTRO = "Na imagem, existem elementos como: ";
    private static String FACE_INTRO = "A imagem mostra ";
    private static String ONE_FACE = "a face de uma pessoa. ";
    private static String TWO_FACES = "as faces de duas pessoas. ";
    private static String MORE_FACES_BEGINNING = "as faces de ";
    private static String MORE_FACES_END = " pessoas. ";
    private static String LABEL_AFTER_FACE_INTRO = "e pode ser descrita pelas seguintes palavras: ";

    private String FACE_LIKELY = " parece ";
    private String FACE_UNLIKELY = "não parece estar";

    private String FACE_ANGER = "com raiva";
    private String FACE_NO_ANGER = "amigável";

    private String FACE_JOY = "alegre";
    private String FACE_NO_JOY = "séria";

    private String FACE_SURPRISE = "surpresa";
    private String FACE_NO_SURPRISE = "";

    private String FACE_SORROW = "triste";
    private String FACE_NO_SORROW = ".";

    private String FACE_HAT = "usando boné";
    private String FACE_NO_HAT = "";


    private static String TEXT_INTRO = "nesta imagem está escrito: ";

    private int nFaces = 0;

    private String textualDescription;

    private float minScore = 0.8f;

    //private String statistics;

    private void writeLabel() {
        labels = response.getResponses().get(0).getLabelAnnotations();




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
        texts = response.getResponses().get(0).getTextAnnotations();

        if (texts != null) {

            statistics.concat(texts.get(0).getDescription() + ": " + texts.get(0).getScore());

            //if (texts.get(0).getScore() >= minScore )
                textElement.concat(texts.get(0).getDescription());
        }
    }

    private void writeFace() {
        faces = response.getResponses().get(0).getFaceAnnotations();

        String head, anger, hat, joy, surprise, sorrow;

        String VERY = "certamente ";
        String IS_NOT = "não está ";
        String IS = "está ";
        String SEEMS_TO_BE = "parece estar ";
        String NOR = ", nem ";
        String BUT = ", mas ";
        String COMMA = ", ";
        String AND = " e ";
        String END = ". ";



        //String likely = FACE_LIKELY;
        //String unlikely = FACE_UNLIKELY;

        int i;

        if (faces != null) {

            nFaces = faces.size();

            for (FaceAnnotation face : faces) {

                ArrayList<String> unlikely = new ArrayList<String>();
                ArrayList<String> possible = new ArrayList<String>();
                ArrayList<String> likely = new ArrayList<String>();

                //boundingPolyList.add(face.getBoundingPoly());


/*
                List<Vertex> vertexList = face.getBoundingPoly().getVertices();

                for (int j = 0; j < vertexList.size(); j++)
                {
                    positionY += vertexList.get(j).getY();
                    positionX += vertexList.get(j).getX();
                }

                positionY = PositionY/vertexList.size();
                positionX = PositionX/vertexList.size();
*/



                String content = "";

                if (nFaces == 1)
                    head = "Ela ";

                else {
                    i = faces.indexOf(face) + 1;
                    head = "A " + i + "ª pessoa ";
                    //facePosition.concat(head);
                    statistics.concat(head);

                }

                for (int j=0; j<face.getBoundingPoly().getVertices().size();j++)
                {
                    //String xy = face.getBoundingPoly().getVertices().get(j).getX() + "\n" + face.getBoundingPoly().getVertices().get(j).getY();
                    //String xy = "(" + face.getBoundingPoly().getVertices().get(j).getX() + ", " + face.getBoundingPoly().getVertices().get(j).getY() + ")";
                    //facePosition.concat(xy);
                    //facePosition.concat(face.getBoundingPoly().getVertices().get(j).getX().toString());
                    //facePosition.concat(face.getBoundingPoly().getVertices().get(j).getY().toString());
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

                if (hat.contains("UNLIKELY")) {
                    //unlikely.add(FACE_HAT);
                }
                else if (hat.contains("LIKELY")) {
                    likely.add(FACE_HAT);
                }
                else if (hat.contains("POSSIBLE")) {
                    possible.add(FACE_HAT);
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
        landmarks = response.getResponses().get(0).getLandmarkAnnotations();


        if (landmarks != null) {

            for (EntityAnnotation landmark : landmarks) {
                statistics.concat(landmark.getDescription() + ": " + landmark.getScore());

                if (landmark.getScore() >= minScore )
                    landmarkElement.concat(landmark.getDescription());
            }
        }
    }

    private void writeLogo() {
        logos = response.getResponses().get(0).getLogoAnnotations();

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
                textualDescription += ONE_FACE + ". " + faceElement.getText();
            else if (nFaces == 2)
                textualDescription += TWO_FACES + ". " + faceElement.getText();
            else if (nFaces > 2)
                textualDescription += MORE_FACES_BEGINNING + nFaces + MORE_FACES_END + ". " + faceElement.getText();

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

