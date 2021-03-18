package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;


public class TTSActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech myTTS;
    private TextView textViewLine1;
    private TextView textViewLine2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);

        TextView textViewLine1 = (TextView) findViewById(R.id.line1Col1);
        TextView textViewLine2 = (TextView) findViewById(R.id.line2Col1);

        String speakingMessage;
        speakingMessage = (String) getResources().getText(R.string.tts_text_1);
        textViewLine1.setText(speakingMessage);

        speakingMessage = (String) getResources().getText(R.string.tts_text_2);
        textViewLine2.setText(speakingMessage);

    }


    public void SpeakingButtonClickHandler(View target) {
        String speakingMessage;

        switch (target.getId()) {
            case R.id.line1Btn:
                if (myTTS != null) {
                    myTTS.setLanguage(Locale.KOREAN);
                    speakingMessage = (String) getResources().getText(R.string.tts_text_1);
                    myTTS.speak(speakingMessage, TextToSpeech.QUEUE_FLUSH, null);
                }

                break;
            case R.id.line2Btn:
                if (myTTS != null) {
                    myTTS.setLanguage(Locale.US);
                    speakingMessage = (String) getResources().getText(R.string.tts_text_2);
                    myTTS.speak(speakingMessage, TextToSpeech.QUEUE_FLUSH, null);
                }

                break;
        }
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // TTS set
        if (myTTS == null) {
            myTTS = new TextToSpeech(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTTS != null) {
            myTTS.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
