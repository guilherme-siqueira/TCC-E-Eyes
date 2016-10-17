package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.os.SystemClock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.text.InputType;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor.FileManager;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Constants;

/**
 * Created by gsiqueira on 7/24/16.
 */
public class ViewFileActivity extends Activity {

    Bitmap myBitmap;
    Bitmap mutableBitmap;

    Display display;
    Point size;

    float screenWidth;
    float screenHeight ;
    float realPhotoWidth;
    float realPhotoHeight;

    float onScreenPhotoHeight;
    float onScreenPhotoWidth;

    float photoTopMargin;
    float photoBottom;
    float photoRight;
    float photoLeftMargin;

    String folder;
    String textFileName = "Texto";
    String textFileType = "txt";

    String newName = "";

    TextView myText;

    Canvas canvas;
    Paint paint;

    //String faces;

    ArrayList<Float> faces = new ArrayList<Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file_activity);

        Intent intent = this.getIntent();

        folder = intent.getStringExtra("FOLDER");

        File imgFile = new  File(Constants.DIRECTORY_PATH + folder + "/Imagem.jpg");

        File txtFile = new  File(Constants.DIRECTORY_PATH + folder + "/" + textFileName + ".txt");

        File txtFaceFile = new  File(Constants.DIRECTORY_PATH + folder + "/PosiçãoFaces.txt");

        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.imageFile);

            myImage.setOnTouchListener(new View.OnTouchListener() {

                private long mLastClickTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return true;
                    }

                    mLastClickTime = SystemClock.elapsedRealtime();

                    onPhotoClick(event.getX(), event.getY());

                    return true;
                }



            });
            ////mutableBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
            ////canvas = new Canvas(mutableBitmap);
            ////paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ////paint.setColor(Color.BLACK);

            //Rect rect = new Rect(100,200,200,250);

            //canvas.drawRect(rect, paint);
            //canvas.drawCircle(100, 100, 100, paint);


            //myImage.setRotation(90);

            ////myImage.setImageBitmap(mutableBitmap);

            myImage.setImageBitmap(myBitmap);

            display = getWindowManager().getDefaultDisplay();
            size = new Point();
            display.getSize(size);

            screenWidth = size.x;
            screenHeight = size.y;

            System.out.println("screen: " + screenWidth + "-" + screenHeight);

            realPhotoWidth = myBitmap.getWidth();
            realPhotoHeight = myBitmap.getHeight();

            System.out.println("photo: " + realPhotoWidth + "-" + realPhotoHeight);


            if (realPhotoWidth > realPhotoHeight)
            {
                onScreenPhotoWidth = screenWidth;
                onScreenPhotoHeight = onScreenPhotoWidth * realPhotoHeight / realPhotoWidth;
            }
            else
            {
                onScreenPhotoHeight = screenHeight/2;
                onScreenPhotoWidth = onScreenPhotoHeight * realPhotoWidth / realPhotoHeight;
            }

            System.out.println("normalized photo: " + onScreenPhotoWidth + "-" + onScreenPhotoHeight);


            photoTopMargin = (screenHeight/2 - onScreenPhotoHeight)/2;



            //photoBottom = screenHeight/2 + onScreenPhotoHeight/2;
            //photoRight = screenWidth/2 + onScreenPhotoWidth/2;
            photoLeftMargin = (screenWidth - onScreenPhotoWidth)/2;

            System.out.println("top/left: " + photoTopMargin + "-" + photoLeftMargin);


            //photoBottom = photoBottom - photoTopMargin;
            //photoTopMargin = 0;
            //photoRight = photoRight - photoLeftMargin;
            //photoLeftMargin = 0;

            ////System.out.println("photoTopMargin: "+photoTopMargin);
            ////System.out.println("photoLeftMargin: "+photoLeftMargin);

        }

        if(txtFaceFile.exists()){

            //Read text from file
            //StringBuilder facePositions = new StringBuilder();

            float coord;

            try {
                BufferedReader br = new BufferedReader(new FileReader(txtFaceFile));
                String line;
                int lineCounter = 0;

                //if (realPhotoWidth > realPhotoHeight)
                //{
                    while ((line = br.readLine()) != null) {
                        //facePositions.append(line);

                        coord = Integer.parseInt(line);

                        if (lineCounter == 0) {

                            coord = coord* onScreenPhotoWidth /realPhotoWidth + photoLeftMargin;
                            lineCounter = 1;
                            System.out.print("x"+coord+"-");

                        }
                        else
                        {
                            //coord = onScreenPhotoWidth * realPhotoHeight / realPhotoWidth - photoTopMargin;

                            //coord = coord*screenHeight/realPhotoHeight;
                            coord = coord* onScreenPhotoHeight /realPhotoHeight + photoTopMargin;

                            lineCounter = 0;
                            System.out.println("y" + coord);

                        }

                        faces.add(coord);

                        //facePositions.append('\n');
                    }
                //}
               /* else
                {
                    while ((line = br.readLine()) != null) {
                        //facePositions.append(line);

                        coord = Integer.parseInt(line);

                        if (lineCounter == 0) {
                            coord = onScreenPhotoHeight * realPhotoWidth / realPhotoHeight - photoLeftMargin;
                            lineCounter = 1;
                            System.out.print("x"+coord+"-");

                        }
                        else
                        {
                            coord = screenHeight/2;
                            lineCounter = 0;
                            System.out.println("y" + coord);

                        }

                        faces.add(coord);

                        //facePositions.append('\n');
                    }
                }
*/

                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

            //faces = facePositions.toString();

            //Rect rect;
            //rect = new Rect(faces.get(0),faces.get(5),faces.get(4),faces.get(1));
            //canvas.drawRect(rect,paint);

           // Toast.makeText(getApplicationContext(), facePositions.toString(), Toast.LENGTH_LONG).show();

        }

        if(txtFile.exists()){

            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(txtFile));
                String line;

                while ((line = br.readLine()) != null) {

                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

            myText = (TextView) findViewById(R.id.textFile);

            myText.setText(text.toString());

            myText.setContentDescription(getString(R.string.activity_descriptor_text) + text.toString());

            myText.setMovementMethod(new ScrollingMovementMethod());

        }

    }

    public void onPhotoClick(float x, float y) {

        //Toast.makeText(getApplicationContext(), "width: " + screenWidth + "X: " + xClick, Toast.LENGTH_LONG).show();

        //for (int i = 0; i < faces.size(); i++)
            //System.out.println(faces.get(i));

        for (int i = 0; i < faces.size(); i+=8)
        {


            float x1 = faces.get(i); //- photoLeftMargin;//*onScreenPhotoWidth/realPhotoWidth + photoLeftMargin;
            float y1 = faces.get(i+1);// - photoTopMargin;//*onScreenPhotoHeight/realPhotoHeight + photoTopMargin;



            float x2 = faces.get(i+2);// - photoLeftMargin;//*onScreenPhotoWidth/realPhotoWidth + photoLeftMargin;
            float y2 = faces.get(i+3);// - photoTopMargin;//*onScreenPhotoHeight/realPhotoHeight + photoTopMargin;
            float x3 = faces.get(i+4);// - photoLeftMargin;//*onScreenPhotoWidth/realPhotoWidth + photoLeftMargin;
            float y3 = faces.get(i+5);// - photoTopMargin;//*onScreenPhotoHeight/realPhotoHeight + photoTopMargin;
            float x4 = faces.get(i+6); //- photoLeftMargin;//*onScreenPhotoWidth/realPhotoWidth + photoLeftMargin;
            float y4 = faces.get(i+7);// - photoTopMargin;//*onScreenPhotoHeight/realPhotoHeight + photoTopMargin;





            //System.out.println("clicked: "+x + "-" + y);






            if (x > x1 && y > y1 && x < x2 && y > y2 && x < x3 && y < y3 && x > x4 && y < y4)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewFileActivity.this);
                builder.setTitle("Você clicou em uma pessoa. Deseja inserir um nome?");
                final EditText input = new EditText(ViewFileActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                final int personNumber = i/8 + 1;




                    //Toast.makeText(getApplicationContext(), "Você tocou no rosto da " + personNumber + "ª pessoa.", Toast.LENGTH_LONG).show();
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String newName = input.getText().toString();
                            newName = newName.replace(newName,". " + newName + " não está");

                            String description = myText.getText().toString();

                            Pattern p = Pattern.compile("\\.\\s[\\w\\s]*não\\sestá", Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(description);
                            boolean result;

                            for (int j = 0; j < personNumber; j++)
                            {
                                m.find();
                            }

                            String oldName = m.group(0);

                            description = description.replace(oldName, newName);

                            myText.setText(description);
                            FileManager.save(ViewFileActivity.this, description.getBytes(), folder, textFileName, textFileType);

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });


                builder.show();

            }


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        if(myBitmap!=null)        {
            myBitmap.recycle();
            myBitmap=null;
        }
        if(mutableBitmap!=null)        {
            mutableBitmap.recycle();
            mutableBitmap=null;
        }

        super.onStop();
    }
}