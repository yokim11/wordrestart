package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.helper.RawDataUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringTokenizer;


public class DBCreateActivity extends Activity {
    private String TAG = "DBCreateActivity";
    private static final String LINE_FEED_CHAR = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbcreate);

        DictionaryRepository dictionaryRepository = new DictionaryRepository(getApplicationContext());
        RawDataUtils dbutils = new RawDataUtils();

        int wordRawId;
        List<String> words;

        Field[] ID_Fields = R.raw.class.getFields();
        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                wordRawId = ID_Fields[i].getInt(null);
                words = dbutils.readRawResource(getApplicationContext(), wordRawId, LINE_FEED_CHAR);
                int maxCount = words.size();
                if (words != null && maxCount > 0) {
                    for (int j = 0; j < maxCount; j++) {
                        String wordRow = words.get(j);
                        StringTokenizer tokens = new StringTokenizer(wordRow, "|");
                        if (tokens.countTokens() < 4) {
                            Log.d("FLASH", "word" + wordRow + " / count : " + tokens.countTokens());
                            continue;
                        }
                        String level = tokens.nextToken();
                        String level_no = tokens.nextToken();
                        String word = tokens.nextToken();
                        String meaning = tokens.nextToken().replace("^", "\n");

                        dictionaryRepository.addDictionary(level.trim(), word.trim(), meaning.trim());
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        TextView tv = (TextView) findViewById(R.id.dbMessage);
        tv.setText("DB CREATED");
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
