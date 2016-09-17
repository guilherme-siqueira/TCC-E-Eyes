package br.usp.guilherme_galdino_siqueira.e_eyes.photo_descriptor;

import br.usp.guilherme_galdino_siqueira.e_eyes.properties.ApiKeys;
import br.usp.guilherme_galdino_siqueira.e_eyes.properties.Preferences;
import br.usp.guilherme_galdino_siqueira.e_eyes.yandex_translator.Translate;

/**
 * Created by gsiqueira on 7/10/16.
 */
public class FeatureContent {

    //private boolean isYandexTranslatorDisabled = true;

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void concat(String add) {
        if (add == null)
            return;
        else {
            add = add.replaceAll("[\\r\\n]+", " ");
        }
        if (text == null)
            text = add + "\n";
        else
            text += add + "\n";
    }

    public void translate(){

        if (Preferences.isYandexTranslatorDisabled)
            return;

        Translate.setKey(ApiKeys.getYandexApiKey());
        text = Translate.execute(text);
    }
}
