package br.usp.guilherme_galdino_siqueira.e_eyes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.view.View;
import android.widget.TextView;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Constants;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;

/**
 * Created by gsiqueira on 11/3/16.
 */
public class OptionMenuActivity extends Activity {

    LinearLayout save;
    LinearLayout sound;
    LinearLayout image;
    TextView resValue;
    TextView fontValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_menu_activity);

        save = (LinearLayout) findViewById(R.id.saveOption);
        sound = (LinearLayout) findViewById(R.id.soundOption);
        image = (LinearLayout) findViewById(R.id.imageOption);

        resValue = (TextView) findViewById(R.id.resValue);
        fontValue = (TextView) findViewById(R.id.fontValue);

        resValue.setText(String.valueOf(Constants.defaultDimension));
        fontValue.setText(String.valueOf(Constants.fontSize));

        if (Preferences.isSoundEffectEnable)
        {
            sound.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            sound.setContentDescription("Botão: Efeito sonoro de processamento ativado");
        }

        else {
            sound.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            sound.setContentDescription("Botão: Efeito sonoro de processamento desativado");
        }

        if (Preferences.isImageEffectEnable) {
            image.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            image.setContentDescription("Botão: Relógio piscando ativado");
        }
        else {
            image.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            image.setContentDescription("Botão: Relógio piscando desativado");
        }

        if (Preferences.isDescriptionAutoSaveEnable) {
            save.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            save.setContentDescription("Botão: Salvamento automático de descrição ativado");
        }
        else {
            save.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            save.setContentDescription("Botão: Salvamento automático de descrição desativado");
        }

    }


    public void autoSave(View v)
    {

        if (Preferences.isDescriptionAutoSaveEnable)
        {
            Preferences.isDescriptionAutoSaveEnable = false;
            save.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            save.setContentDescription("Botão: Salvamento automático de descrição desativado");
        }

        else
        {
            Preferences.isDescriptionAutoSaveEnable = true;
            save.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            save.setContentDescription("Botão: Salvamento automático de descrição ativado");
        }

    }

    public void clockSound(View v)
    {
        if (Preferences.isSoundEffectEnable)
        {
            Preferences.isSoundEffectEnable = false;
            sound.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            sound.setContentDescription("Botão: Efeito sonoro de processamento desativado");
        }

        else
        {
            Preferences.isSoundEffectEnable = true;
            sound.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            sound.setContentDescription("Botão: Efeito sonoro de processamento ativado");
        }

    }

    public void clockBlink(View v)
    {
        if (Preferences.isImageEffectEnable)
        {
            Preferences.isImageEffectEnable = false;
            image.setBackgroundColor(getResources().getColor(R.color.colorButton3));
            image.setContentDescription("Botão: Relógio piscando desativado");
        }

        else
        {
            Preferences.isImageEffectEnable = true;
            image.setBackgroundColor(getResources().getColor(R.color.colorButton2));
            image.setContentDescription("Botão: Relógio piscando ativado");
        }

    }

    public void increaseResolution(View v)
    {
        if (Constants.defaultDimensionIndex > 0)
        {
            Constants.defaultDimensionIndex --;
            Constants.defaultDimension = Constants.dimensions[Constants.defaultDimensionIndex];
            resValue.setText(String.valueOf(Constants.defaultDimension));
        }

    }

    public void decreaseResolution(View v)
    {
        if (Constants.defaultDimensionIndex < Constants.dimensions.length - 1)
        {
            Constants.defaultDimensionIndex ++;
            Constants.defaultDimension = Constants.dimensions[Constants.defaultDimensionIndex];
            resValue.setText(String.valueOf(Constants.defaultDimension));
            resValue.setContentDescription("Resolução: " + String.valueOf(Constants.defaultDimension));
        }

    }

    public void increaseFont(View v)
    {
        if (Constants.fontSize < 200)
        {
            Constants.fontSize+=10;
            fontValue.setText(String.valueOf(Constants.fontSize));
            fontValue.setContentDescription("Fonte tamanho: " + String.valueOf(Constants.fontSize));
        }

    }

    public void decreaseFont(View v)
    {
        if (Constants.fontSize > 10)
        {
            Constants.fontSize-=10;
            fontValue.setText(String.valueOf(Constants.fontSize));

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
