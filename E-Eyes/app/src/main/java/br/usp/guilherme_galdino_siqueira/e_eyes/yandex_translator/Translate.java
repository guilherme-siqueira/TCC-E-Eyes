package br.usp.guilherme_galdino_siqueira.e_eyes.yandex_translator;

/**
 * Created by gsiqueira on 7/9/16.
 */

import java.net.URL;
import java.net.URLEncoder;

/**
 * Makes calls to the Yandex machine translation web service API
 */
public final class Translate extends YandexTranslatorAPI {

    private static final String SERVICE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    private static final String TRANSLATION_LABEL = "text";

    private static String translatedText;
    private static final Language FROM = Language.ENGLISH;
    private static final Language TO = Language.PORTUGUESE;

    //prevent instantiation
    private Translate(){};

    /**
     * Translates text from a given Language to another given Language using Yandex.
     *
     * @param text The String to translate.
     * @return The translated String.
     * @throws Exception on error.
     */
    public static String execute(final String text) {

        Thread translation = new Thread() {
            @Override
            public void run() {

                try {
                    validateServiceState(text);

                    final String params =
                            PARAM_API_KEY
                            + URLEncoder.encode(apiKey,ENCODING)
                            + PARAM_LANG_PAIR
                            + URLEncoder.encode(FROM.toString(),ENCODING)
                            + URLEncoder.encode("-",ENCODING)
                            + URLEncoder.encode(TO.toString(),ENCODING)
                            + PARAM_TEXT
                            + URLEncoder.encode(text,ENCODING);

                    final URL url = new URL(SERVICE_URL + params);

                    System.out.println("URL CRIADA: " + url.toString());

                    translatedText = retrievePropArrString(url, TRANSLATION_LABEL).trim();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        translation.start();
        try {
            translation.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return translatedText;
    }

    private static void validateServiceState(final String text) throws Exception {
        final int byteLength = text.getBytes(ENCODING).length;
        if(byteLength>10240) { // TODO What is the maximum text length allowable for Yandex?
            throw new RuntimeException("TEXT_TOO_LARGE");
        }
        validateServiceState();
    }
}