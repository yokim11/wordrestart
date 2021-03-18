package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.helper.RawDataUtils;

import java.util.List;
import java.util.StringTokenizer;


public class DBCreateWiseSayingActivity extends Activity {
    private String TAG = "DBCreateWiseSayingActivity";
    private static final String LINE_FEED_CHAR = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbcreate_wise_saying);

        DictionaryRepository dictionaryRepository = new DictionaryRepository(getApplicationContext());
        RawDataUtils dbutils = new RawDataUtils();

        int wordRawId;
        List<String> inputLine;
        wordRawId = R.raw.wise_saying;
        inputLine = dbutils.readRawResource(getApplicationContext(), wordRawId, LINE_FEED_CHAR);
        int maxCount = inputLine.size();
        if (inputLine != null && maxCount > 0) {
            dictionaryRepository.createWiseSaying();
            dictionaryRepository.removeAllWiseSaying();

            for (int i = 0; i < maxCount; i++) {
                String wordRow = inputLine.get(i);
                StringTokenizer tokens = new StringTokenizer(wordRow, "|");
                if (tokens.countTokens() < 2) {
                    Log.d("FLASH", "word" + wordRow + " / count : " + tokens.countTokens());
                    continue;
                }
                String sentence = tokens.nextToken();
                String speaker = tokens.nextToken();
                if (speaker.contains("강영우")) {
                    Log.d("AAA", speaker);
                }

                dictionaryRepository.addWiseSaying(sentence, speaker);
            }
        }

        TextView tv = (TextView) findViewById(R.id.dbMessage);
        tv.setText("WiseSaying DB CREATED");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dbcreate, menu);
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
