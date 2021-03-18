package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sufamily.wordrestart.wordrestart.ActivityAdapter.DictionaryAdapter;
import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.db.repository.PreferencesRepository;
import com.sufamily.wordrestart.wordrestart.domain.model.JoinEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.UserInfo;
import com.sufamily.wordrestart.wordrestart.enums.BasketType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WordSkipActivity extends Activity  implements TextToSpeech.OnInitListener {
    private final String TAG = "WordSkipActivity";

    private PreferencesRepository preferencesRepository;
    private UserInfo userInfo;
    private DictionaryRepository dictionaryRepository;
    private List<JoinEntity> levelEntities;
    private ArrayList<JoinEntity> arrayEntities;
    private DictionaryAdapter dataAdapter = null;

    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_skip);

        preferencesRepository = new PreferencesRepository(this);
        userInfo = preferencesRepository.getPreferences();

        dictionaryRepository = new DictionaryRepository(getApplicationContext());
        levelEntities = dictionaryRepository.getLevelAll(userInfo.getLevel());

        // ListView Setting
        arrayEntities = new ArrayList<JoinEntity>();
        for (JoinEntity entity : levelEntities) {
            JoinEntity item = new JoinEntity();
            item.setWord_seq(entity.getWord_seq());
            item.setWord(entity.getWord());
            item.setMeaning(entity.getMeaning());
            item.setLevel(entity.getLevel());
            item.setMy_seq(entity.getMy_seq());
            arrayEntities.add(item);
        }

        dataAdapter = new DictionaryAdapter(this,
                R.layout.word_info, arrayEntities);
        ListView listView = (ListView) findViewById(R.id.listViewWordList);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                JoinEntity joinEntity = (JoinEntity) parent.getItemAtPosition(position);
//                updateMyWordStatus(joinEntity);
                if (myTTS != null) {
                    myTTS.setLanguage(Locale.US);
                    myTTS.speak(joinEntity.getWord(), TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });
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
        getMenuInflater().inflate(R.menu.menu_word_skip, menu);
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

    private void updateMyWordStatus(JoinEntity entity) {
        dictionaryRepository.updateTestResult(entity, BasketType.COMPLETED_BASKET.getIntValue());
    }
}
