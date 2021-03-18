package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.db.repository.PreferencesRepository;
import com.sufamily.wordrestart.wordrestart.domain.model.DictionaryEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.JoinEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.TestResultInfo;
import com.sufamily.wordrestart.wordrestart.domain.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WordTestActivity extends Activity implements TextToSpeech.OnInitListener {

    private final String TAG = "WordTestActivity";

    private Context context;
    private PreferencesRepository preferencesRepository;
    private UserInfo userInfo;

    private boolean isWordFirst = true;
    private static TimerTask flashTimerTask;
    private long FLASH_TIMER_DELAY;
    private long FLASH_TIMER_PERIO;
    private Timer flashTimer = new Timer();
    private boolean isStartButtonPressed = false;


    private DictionaryRepository dictionaryRepository;
    private List<JoinEntity> levelEntityList;
    private List<JoinEntity> basketEntityList;
    private TestResultInfo testResultInfo;
    private ArrayList<JoinEntity> arrayEntities;

    private ListView listView;
    private LinearLayout linearLayoutQuestionItems;
    private TextView textViewTestWordTotalCount;
    private TextView textViewTableRowStatCol2;
    private TextView textViewtableRowStatCol4;
    private TextView textViewTestWord;
    private TextView textViewMessage;

    private int currentBasketNo = 1;
    private int currentWordChainCount;
    private int indexWordChains = 0;

    private CountDownTimer countDownTimer;
    private long startTime;
    private long interval;

    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_test);
        context = getApplicationContext();

        /**
         * Repository initialize & userInfo setting
         */
        preferencesRepository = new PreferencesRepository(this);
        userInfo = preferencesRepository.getPreferences();

        /**
         * Request param Setting
         */
        Bundle bundle = getIntent().getExtras(); // 값을 받아온다.
        currentBasketNo = bundle.getInt("currentBasketNo");

        /**
         * 노출 순서 세팅
         * 홀수 : (Q) 단어 (A) 의미 노출
         * 짝수 : (Q) 의미 (A) 단어 노출
         */
        isWordFirst = (currentBasketNo % 2) == 1 ? true : false;

        /**
         * Basket Info setting
         */
        dictionaryRepository = new DictionaryRepository(getApplicationContext());
        basketEntityList = dictionaryRepository.getBasketEntityList(currentBasketNo);
        currentWordChainCount = basketEntityList.size();
        levelEntityList = dictionaryRepository.getLevelAll(userInfo.getLevel());

        /**
         * 테스트 결과 저장 Entity
         */
        testResultInfo = new TestResultInfo();

        /**
         * 문항 노출 영역 Setting
         */
        linearLayoutQuestionItems = (LinearLayout) findViewById(R.id.linearLayoutQuestionItems);

        /**
         * 진행상태 노출 영역
         */
        textViewTestWordTotalCount = (TextView) findViewById(R.id.tableRowStatCol6);
        textViewTestWordTotalCount.setText(Integer.toString(currentWordChainCount));
        textViewTestWord = (TextView) findViewById(R.id.textViewWord);
        textViewTableRowStatCol2 = (TextView) findViewById(R.id.tableRowStatCol2);
        textViewMessage = (TextView) findViewById(R.id.textViewMessage);
        textViewtableRowStatCol4 = (TextView) findViewById(R.id.tableRowStatCol4);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_test, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Testcard Timer setting
        FLASH_TIMER_DELAY = 500;
        FLASH_TIMER_PERIO = userInfo.getTestcardFrequence();

        // Timer 세팅
        if (flashTimer == null) {
            flashTimer = new Timer();

        }

        // TTS set
        if (myTTS == null) {
            myTTS = new TextToSpeech(this, this);
        }

        // TIme on
        if (isStartButtonPressed) {
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimerTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTTS != null) {
            myTTS.shutdown();
        }
    }

    @Override
    public void onInit(int status) {

    }

    public void startTestClickHandler(View target) {
        isStartButtonPressed = true;

        // Start button disable
        TableRow tableRowTestMessage = (TableRow) findViewById(R.id.tableRowTestStartMessage);
        tableRowTestMessage.setVisibility(View.GONE);
        TableRow tableRowTestButton = (TableRow) findViewById(R.id.tableRowTestStartButton);
        tableRowTestButton.setVisibility(View.GONE);

        // TIme on
        startTimer();

        // Countdown 세팅
        startTime = FLASH_TIMER_PERIO + 1000;
        interval = 1 * 1000;
        countDownTimer = new MyCountDownTimer(startTime, interval);
    }

    public void startTimer() {
        //set a new Timer
        flashTimer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        flashTimer.scheduleAtFixedRate(flashTimerTask, FLASH_TIMER_DELAY, FLASH_TIMER_PERIO); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (flashTimer != null) {
            flashTimer.cancel();
            flashTimer = null;
        }
    }

    public void initializeTimerTask() {
        flashTimerTask = new TimerTask() {
            public void run() {

                runOnUiThread(new Runnable() //run on ui thread
                {
                    public void run() {

                        if (indexWordChains >= basketEntityList.size()) {
                            testEndingMessage();
                            stopTimerTask();
                        } else {

                            String word4tts;
                            DictionaryEntity dictionaryEntity;
                            if (basketEntityList.size() > indexWordChains) {
                                dictionaryEntity = basketEntityList.get(indexWordChains);
                                word4tts = isWordFirst == true ? dictionaryEntity.getWord() : dictionaryEntity.getMeaning().replace("^", "  ");
                                if (isWordFirst && myTTS != null) {
                                    myTTS.setLanguage(Locale.US);
                                    myTTS.setSpeechRate(1.0f);
                                    myTTS.speak(word4tts, TextToSpeech.QUEUE_FLUSH, null);
                                }
                                textViewTestWord.setTypeface(null, Typeface.BOLD);
                                textViewTestWord.setText(word4tts);
                                textViewtableRowStatCol4.setText((indexWordChains+1) + "");

                                displayQuestionListView();

                                countDownTimer.start();
                                indexWordChains ++;
                            }
                        }
                    }
                });
            }
        };
    }

    public void displayQuestionListView() {
        Random r = new Random();
        int maxQuestionItemCount = 6;
        if (levelEntityList.size() < maxQuestionItemCount)
            maxQuestionItemCount = levelEntityList.size();
        int minRandom = maxQuestionItemCount;
        int startPoint = r.nextInt((levelEntityList.size() - minRandom) - minRandom);
        int no1 = startPoint + 1;
        int no2 = no1 + 1;
        int no3 = no2 + 1;
        int no4 = no3 + 1;
        int no5 = no4 + 1;
        int answerNumber = r.nextInt(maxQuestionItemCount - 1) + 1;

        int[] arrayNo = new int[] {no1, no2, no3, no4, no5};
        arrayNo[answerNumber-1] = indexWordChains;

        // 문항 리스트 만들기
        arrayEntities = new ArrayList<JoinEntity>();
        for (int index : arrayNo) {
            JoinEntity item = new JoinEntity();
            item.setWord_seq(levelEntityList.get(index).getWord_seq());
            item.setWord(levelEntityList.get(index).getWord());
            item.setMeaning(levelEntityList.get(index).getMeaning().replace("^", "  "));
            item.setLevel(levelEntityList.get(index).getLevel());
            arrayEntities.add(item);
        }
        // 문항 리스트 - Answer 세팅
        final DictionaryEntity answerItem = basketEntityList.get(indexWordChains);
        arrayEntities.get(answerNumber-1).setWord_seq(answerItem.getWord_seq());
        arrayEntities.get(answerNumber-1).setLevel(answerItem.getLevel());
        arrayEntities.get(answerNumber-1).setWord(answerItem.getWord());
        arrayEntities.get(answerNumber-1).setMeaning(answerItem.getMeaning().replace("^", "  "));

        if (isWordFirst) {
            TestMeaningDictionaryAdapter dataAdapter = new TestMeaningDictionaryAdapter(context,
                    R.layout.word_info, arrayEntities);
            listView = (ListView) findViewById(R.id.listViewWordList);
            listView.setAdapter(dataAdapter);
        } else {
            TestWordDictionaryAdapter dataAdapter = new TestWordDictionaryAdapter(context,
                    R.layout.word_info, arrayEntities);
            listView = (ListView) findViewById(R.id.listViewWordList);
            listView.setAdapter(dataAdapter);
        }
        listView.setEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                JoinEntity joinEntity = (JoinEntity) parent.getItemAtPosition(position);
                if (joinEntity.getWord_seq() == answerItem.getWord_seq()) {
                    updateAnswer(true);
                    popupToast("Success");
                    listView.setEnabled(false);
                } else {
                    updateAnswer(false);
                    popupToast("Fail");
                    listView.setEnabled(false);
                }
            }
        });
    }

    public void exitButtonClickHandler(View target) {
        finish();
    }

    private void updateAnswer(boolean type) {
        if (type) {
            dictionaryRepository.updateTestResult(basketEntityList.get(indexWordChains - 1), currentBasketNo + 1);
            testResultInfo.addSuccessCount();
        } else {
            testResultInfo.addFailCount();
        }
    }

    private void testEndingMessage() {

        ListView listView1 = (ListView) findViewById(R.id.listViewWordList);
        listView1.setVisibility(View.GONE);
//        linearLayoutQuestionItems.setVisibility(View.GONE);

        StringBuffer sb = new StringBuffer();
        sb.append("Success : " + testResultInfo.getSuccessCount()).append("\n")
                .append("Fail : " + testResultInfo.getFailCount()).append("\n")
                .append("Skip : " + (basketEntityList.size() - (testResultInfo.getSuccessCount() + testResultInfo.getFailCount())));

        textViewTestWord.setText("테스트 종료");
        textViewMessage.setTextSize(24);
        textViewMessage.setText(sb.toString());
        textViewMessage.setVisibility(View.VISIBLE);


        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setVisibility(View.VISIBLE);
    }

    private void popupToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 카운트다운 Class
     */

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }


        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textViewTableRowStatCol2.setText("" + millisUntilFinished / 1000);
        }
    }


    /**
     * TestAdapter (Meaning Test)
     */
    public class TestMeaningDictionaryAdapter extends ArrayAdapter<JoinEntity> {

        private ArrayList<JoinEntity> joinEntityList;
        private Context context;
        private DictionaryRepository dictionaryRepository;

        public TestMeaningDictionaryAdapter(Context context, int textViewResourceId, ArrayList<JoinEntity> joinEntityList) {
            super(context, textViewResourceId, joinEntityList);
            this.context = context;
            this.joinEntityList = new ArrayList<JoinEntity>();
            this.joinEntityList.addAll(joinEntityList);
        }

        class ViewHolder {
            RelativeLayout lo;
            TextView no;
            TextView word;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.word_test_info, null);

                holder = new ViewHolder();
                holder.lo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutList);
                holder.no = (TextView) convertView.findViewById(R.id.textViewNo);
                holder.word = (TextView) convertView.findViewById(R.id.code);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            JoinEntity joinEntity = joinEntityList.get(position);
            holder.no.setText(" (" + (position+1) + ") ");
            holder.word.setText(joinEntity.getMeaning().replace("\n", " "));
            holder.no.setTag(joinEntity);

            return convertView;
        }
    }

    /**
     * TestAdapter (Word Test)
     */
    public class TestWordDictionaryAdapter extends ArrayAdapter<JoinEntity> {

        private ArrayList<JoinEntity> joinEntityList;
        private Context context;
        private DictionaryRepository dictionaryRepository;

        public TestWordDictionaryAdapter(Context context, int textViewResourceId, ArrayList<JoinEntity> joinEntityList) {
            super(context, textViewResourceId, joinEntityList);
            this.context = context;
            this.joinEntityList = new ArrayList<JoinEntity>();
            this.joinEntityList.addAll(joinEntityList);
        }

        class ViewHolder {
            RelativeLayout lo;
            TextView no;
            TextView word;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.word_test_info, null);

                holder = new ViewHolder();
                holder.lo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutList);
                holder.no = (TextView) convertView.findViewById(R.id.textViewNo);
                holder.word = (TextView) convertView.findViewById(R.id.code);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            JoinEntity joinEntity = joinEntityList.get(position);
            holder.no.setText(" (" + (position+1) + ") ");
            holder.word.setText(joinEntity.getWord());
            holder.no.setTag(joinEntity);

            return convertView;
        }
    }

}
