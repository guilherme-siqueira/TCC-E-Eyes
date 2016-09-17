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
import android.widget.Toast;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

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

    float photoHeight;
    float photoWidth;

    float photoTop;
    float photoBottom;
    float photoRight;
    float photoLeft;

    Canvas canvas;
    Paint paint;

    //String faces;

    ArrayList<Float> faces = new ArrayList<Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file_activity);

        Intent intent = this.getIntent();

        String folder = intent.getStringExtra("FOLDER");

        File imgFile = new  File("/sdcard/E-EYES/" + folder + "/Imagem.jpg");

        File txtFile = new  File("/sdcard/E-EYES/" + folder + "/Texto.txt");

        File txtFaceFile = new  File("/sdcard/E-EYES/" + folder + "/PosiçãoFaces.txt");

        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());



            ImageView myImage = (ImageView) findViewById(R.id.imageFile);

            myImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

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
                photoWidth = screenWidth;
                photoHeight = photoWidth * realPhotoHeight / realPhotoWidth;
            }
            else
            {
                photoHeight = screenHeight/2;
                photoWidth = photoHeight * realPhotoWidth / realPhotoHeight;
            }

            System.out.println("normalized photo: " + photoWidth + "-" + photoHeight);


            photoTop = (screenHeight/2 - photoHeight)/2;



            //photoBottom = screenHeight/2 + photoHeight/2;
            //photoRight = screenWidth/2 + photoWidth/2;
            photoLeft = (screenWidth - photoWidth)/2;

            System.out.println("top/left: " + photoTop + "-" + photoLeft);


            //photoBottom = photoBottom - photoTop;
            //photoTop = 0;
            //photoRight = photoRight - photoLeft;
            //photoLeft = 0;

            ////System.out.println("photoTop: "+photoTop);
            ////System.out.println("photoLeft: "+photoLeft);

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

                            coord = coord* photoWidth /realPhotoWidth + photoLeft;
                            lineCounter = 1;
                            System.out.print("x"+coord+"-");

                        }
                        else
                        {
                            //coord = photoWidth * realPhotoHeight / realPhotoWidth - photoTop;

                            //coord = coord*screenHeight/realPhotoHeight;
                            coord = coord* photoHeight /realPhotoHeight + photoTop;

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
                            coord = photoHeight * realPhotoWidth / realPhotoHeight - photoLeft;
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

            TextView myText = (TextView) findViewById(R.id.textFile);

            myText.setText(text.toString());

            myText.setMovementMethod(new ScrollingMovementMethod());

        }

    }

    public void onPhotoClick(float x, float y) {

        //Toast.makeText(getApplicationContext(), "width: " + screenWidth + "X: " + xClick, Toast.LENGTH_LONG).show();

        //for (int i = 0; i < faces.size(); i++)
            //System.out.println(faces.get(i));

        for (int i = 0; i < faces.size(); i+=8)
        {


            float x1 = faces.get(i); //- photoLeft;//*photoWidth/realPhotoWidth + photoLeft;
            float y1 = faces.get(i+1);// - photoTop;//*photoHeight/realPhotoHeight + photoTop;



            float x2 = faces.get(i+2);// - photoLeft;//*photoWidth/realPhotoWidth + photoLeft;
            float y2 = faces.get(i+3);// - photoTop;//*photoHeight/realPhotoHeight + photoTop;
            float x3 = faces.get(i+4);// - photoLeft;//*photoWidth/realPhotoWidth + photoLeft;
            float y3 = faces.get(i+5);// - photoTop;//*photoHeight/realPhotoHeight + photoTop;
            float x4 = faces.get(i+6); //- photoLeft;//*photoWidth/realPhotoWidth + photoLeft;
            float y4 = faces.get(i+7);// - photoTop;//*photoHeight/realPhotoHeight + photoTop;





            System.out.println("clicked: "+x + "-" + y);






            if (x > x1 && y > y1 && x < x2 && y > y2 && x < x3 && y < y3 && x > x4 && y < y4)
            {
                Toast.makeText(getApplicationContext(), "" + x + "-" + y, Toast.LENGTH_LONG).show();

                System.out.println(x + "-" + y);
                System.out.println(faces.get(i) + "-" + faces.get(i + 1));
                System.out.println(faces.get(i + 2) + "-" + faces.get(i + 3));
                System.out.println(faces.get(i + 4) + "-" + faces.get(i + 5));
                System.out.println(faces.get(i + 6) + "-" + faces.get(i + 7));

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