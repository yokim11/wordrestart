package com.sufamily.wordrestart.wordrestart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.db.repository.PreferencesRepository;
import com.sufamily.wordrestart.wordrestart.domain.model.LevelWordInfo;
import com.sufamily.wordrestart.wordrestart.domain.model.UserInfo;
import com.sufamily.wordrestart.wordrestart.domain.model.WiseSayingEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.command.AddBasketCommand;
import com.sufamily.wordrestart.wordrestart.enums.BasketType;
import com.sufamily.wordrestart.wordrestart.helper.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class TopActivity extends FragmentActivity implements TextToSpeech.OnInitListener {

    private Context context;
    private UserInfo userInfo;
    private DictionaryRepository dictionaryRepository;
    private PreferencesRepository preferencesRepository;
    private LevelWordInfo myBasketInfo = new LevelWordInfo();
    private List<WiseSayingEntity> wiseSayingEntities;
    private int wiseSayingEntityIndex;
    private int trashBasketIndex;

    private TextView textViewUserName;
    private TextView textViewWiseSaying;
    private TextView textViewStat;
    private TextView textViewRow1Col2;
    private TextView textViewRow2Col2;
    private TextView textViewRow3Col2;
    private TextView textViewRow4Col2;
    private TextView textViewRow5Col2;
    private ImageButton addFlashcardButton;

    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        context = getApplicationContext();
        preferencesRepository = new PreferencesRepository(this);
        userInfo = preferencesRepository.getPreferences();
        dictionaryRepository = new DictionaryRepository(getApplicationContext());
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
        userInfo = preferencesRepository.getPreferences();
        initializeViewData();
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
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO 아래 해결하기
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
        } else if (id == R.id.action_help) {
            Intent settingIntent = new Intent(this, ConceptActivity.class);
            startActivity(settingIntent);
        } else if (id == R.id.action_tts) {
            TTSTesting();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initializeViewData() {
        String levelMsg = userInfo.getLevel().replace("l", "Level");
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText(userInfo.getNickname() + "님, " + levelMsg + " Fighting!");

        textViewWiseSaying = (TextView) findViewById(R.id.textViewWiseSaying);
        wiseSayingEntities = dictionaryRepository.getAllWiseSaying();
        if (wiseSayingEntities != null && wiseSayingEntities.size() > 0) {
            wiseSayingEntityIndex = getRandomInt(0, wiseSayingEntities.size());
            textViewWiseSaying.setText(wiseSayingEntities.get(wiseSayingEntityIndex++).getFullSentence());
        }
        myBasketInfo.setLevelTotalCount(dictionaryRepository.getDictionaryLevelCount(userInfo.getLevel()));
        myBasketInfo.setBasketCount1(dictionaryRepository.getBasketCount(BasketType.BASKET_NO1.getIntValue()));
        myBasketInfo.setBasketCount2(dictionaryRepository.getBasketCount(BasketType.BASKET_NO2.getIntValue()));
        myBasketInfo.setBasketCount3(dictionaryRepository.getBasketCount(BasketType.BASKET_NO3.getIntValue()));
        myBasketInfo.setBasketCount4(dictionaryRepository.getBasketCount(BasketType.BASKET_NO4.getIntValue()));
        myBasketInfo.setBasketCount5(dictionaryRepository.getBasketCount(BasketType.COMPLETED_BASKET.getIntValue()));

        textViewStat = (TextView) findViewById(R.id.textViewStat);
        textViewStat.setText(makeStatMessage(myBasketInfo));

        textViewRow1Col2 = (TextView) findViewById(R.id.Row1Col2);
        textViewRow1Col2.setText(Long.toString(myBasketInfo.getBasketCount1()));

        textViewRow2Col2 = (TextView) findViewById(R.id.Row2Col2);
        textViewRow2Col2.setText(Long.toString(myBasketInfo.getBasketCount2()));

        textViewRow3Col2 = (TextView) findViewById(R.id.Row3Col2);
        textViewRow3Col2.setText(Long.toString(myBasketInfo.getBasketCount3()));

        textViewRow4Col2 = (TextView) findViewById(R.id.Row4Col2);
        textViewRow4Col2.setText(Long.toString(myBasketInfo.getBasketCount4()));

        textViewRow5Col2 = (TextView) findViewById(R.id.Row5Col2);
        textViewRow5Col2.setText(Long.toString(myBasketInfo.getBasketCount5()));

//        ImageButton ib = (ImageButton) findViewById(R.id.Row5Col5);
//        ib.setEnabled(false);

        addFlashcardButton = (ImageButton) findViewById(R.id.Row0Col6);
    }

    public void speakTextViewClieckHandler(View target) {
        if (myTTS != null) {
            myTTS.setLanguage(Locale.KOREAN);
            myTTS.setSpeechRate(1.0f);
            String speakingMessage = wiseSayingEntities.get(wiseSayingEntityIndex).getSentence().replaceAll("[-+]", "");
            myTTS.speak(speakingMessage, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void wiseWayingRefreshButtonClieckHandler(View target) {
        if (wiseSayingEntityIndex >= wiseSayingEntities.size())
            wiseSayingEntityIndex = 0;
        if (wiseSayingEntityIndex < wiseSayingEntities.size()) {
            textViewWiseSaying = (TextView) findViewById(R.id.textViewWiseSaying);
            textViewWiseSaying.setText(wiseSayingEntities.get(wiseSayingEntityIndex++).getFullSentence());
        }
    }

    public void FlashcardButtonClickHandler(View target) {
        int currentBasketNo = 1;
        boolean isFlashcard = false;

        switch (target.getId()) {
            case R.id.Row1Col3:
                currentBasketNo = 1;
                if (myBasketInfo.getBasketCount1() > 0) isFlashcard = true;
                break;
            case R.id.Row2Col3:
                currentBasketNo = 2;
                if (myBasketInfo.getBasketCount2() > 0) isFlashcard = true;
                break;
            case R.id.Row3Col3:
                currentBasketNo = 3;
                if (myBasketInfo.getBasketCount3() > 0) isFlashcard = true;
                break;
            case R.id.Row4Col3:
                currentBasketNo = 4;
                if (myBasketInfo.getBasketCount4() > 0) isFlashcard = true;
                break;
            case R.id.Row5Col3:
                currentBasketNo = 5;
                if (myBasketInfo.getBasketCount5() > 0) isFlashcard = true;
                break;
            default:
                currentBasketNo = 1;
                break;
        }

        if (isFlashcard) {
            Intent intent = new Intent(TopActivity.this, FlashcardTimerTaskActivity.class);
            intent.putExtra("userName", userInfo.getNickname());
            intent.putExtra("userLevel", userInfo.getLevel());
            intent.putExtra("wordCountPerDay", userInfo.getFlashcardCountOfDay());
            intent.putExtra("flashPerio", userInfo.getFlashcardFrequence());
            intent.putExtra("currentBasketNo", currentBasketNo);
            startActivity(intent);
        } else {
            popupToast("플래시카드가 없습니다.");
        }
    }

    public void WordTestFlashcardButtonClickHandler(View target) {
        int currentBasketNo = 1;

        boolean isFlashcard = false;

        switch (target.getId()) {
            case R.id.Row1Col4:
                currentBasketNo = 1;
                if (myBasketInfo.getBasketCount1() > 0) isFlashcard = true;
                break;
            case R.id.Row2Col4:
                currentBasketNo = 2;
                if (myBasketInfo.getBasketCount2() > 0) isFlashcard = true;
                break;
            case R.id.Row3Col4:
                currentBasketNo = 3;
                if (myBasketInfo.getBasketCount3() > 0) isFlashcard = true;
                break;
            case R.id.Row4Col4:
                currentBasketNo = 4;
                if (myBasketInfo.getBasketCount4() > 0) isFlashcard = true;
                break;
            case R.id.Row5Col4:
                currentBasketNo = 5;
                if (myBasketInfo.getBasketCount5() > 0) isFlashcard = true;
                break;
            default:
                currentBasketNo = 1;
                break;
        }

        if (isFlashcard) {
            Intent intent = new Intent(TopActivity.this, WordTestActivity.class);
            intent.putExtra("userName", userInfo.getNickname());
            intent.putExtra("userLevel", userInfo.getLevel());
            intent.putExtra("wordCountPerDay", userInfo.getFlashcardCountOfDay());
            intent.putExtra("flashPerio", userInfo.getFlashcardFrequence());
            intent.putExtra("currentBasketNo", currentBasketNo);
            startActivity(intent);
        } else {
            popupToast("테스트할 플래시카드가 없습니다.");
        }

    }

    public void AddFlashcardButtonClickHandler(View target) throws ParseException {

        new AlertDialog.Builder(this)
                .setTitle("새 단어 담기")
                .setMessage("새로운 단어를 Basket에 넣으시겠습니까?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        int basketNo = 1;
                        int insertCount = 0;
                        int lastSeq = 0;

                        long maxCount = userInfo.getFlashcardCountOfDay() * 4;
                        long currentIngWordCount = myBasketInfo.getWorkingTotal();
                        String todayString = StringUtils.getDate(new Date(), "yyyy-MM-dd");

                        if (maxCount < currentIngWordCount + userInfo.getFlashcardCountOfDay()) {
                            popupToast("진행중인 단어가 너무 많습니다. 진행중인 단어를 암기한 후에 담기해 주세요.");
//                        } else if (todayString.equals(userInfo.getInputDictionaryDate())) {
//                            popupToast("새 단어 담기는 하루에 1회만 가능합니다.");
                        } else {
                            addFlashcardButton.setEnabled(false);
                            // 데이터 Add
                            AddBasketCommand addBasketCommand = new AddBasketCommand();
                            addBasketCommand.setLevel(userInfo.getLevel());
                            addBasketCommand.setBasketNo(basketNo);
                            addBasketCommand.setLastSeq(lastSeq);
                            addBasketCommand.setLimit(userInfo.getFlashcardCountOfDay());
                            insertCount = dictionaryRepository.addBasket(addBasketCommand);
                            // View 데이터 갱신
                            myBasketInfo.setBasketCount1(dictionaryRepository.getBasketCount(basketNo));
                            textViewRow1Col2.setText(Long.toString(myBasketInfo.getBasketCount1()));
                            textViewStat.setText(makeStatMessage(myBasketInfo));
                            // 환경설정 데이터 저장
                            Date today = new Date();
                            String nowDate = StringUtils.getDate(today, "yyyy-MM-dd");
                            preferencesRepository.savePreference("input_dictionary_date", nowDate);
                            userInfo.setInputDictionaryDate(nowDate);

                            // 토스트 메시지 출력
                            popupToast(insertCount + "개의 단어를 Basket #1에 담았습니다.");
                            addFlashcardButton.setEnabled(true);
                        }

                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    public void ThrashButtonClickHandler(View target) {
        trashBasketIndex = -1;

        switch (target.getId()) {
            case R.id.Row1Col5:
                trashBasketIndex = 1;
                break;
            case R.id.Row2Col5:
                trashBasketIndex = 2;
                break;
            case R.id.Row3Col5:
                trashBasketIndex = 3;
                break;
            case R.id.Row4Col5:
                trashBasketIndex = 4;
                break;
            case R.id.Row5Col5:
                trashBasketIndex = 5;
                break;
            default :
                trashBasketIndex = -1;
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle("Basket 비우기")
                .setMessage("Basket을 비우시겠습니까?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dictionaryRepository.removeBasket(trashBasketIndex);
                        initializeViewData();
                        popupToast("Basket is empty.");
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }


    public void WordSkipButtonClickHandler(View target) {
        Intent intent = new Intent(TopActivity.this, WordSkipActivity.class);
        startActivity(intent);
    }

    public void TTSTesting() {
        Intent intent = new Intent(TopActivity.this, TTSActivity.class);
        startActivity(intent);
    }

    public void deleteBasket() {
        dictionaryRepository.removeAllBasket();

        // 환경설정 데이터 저장

        preferencesRepository.savePreference("input_dictionary_date", "");

        userInfo.setInputDictionaryDate("");
        userInfo.setInputDictionaryLastSeq(0);

        myBasketInfo.setBasketCount1(0);
        myBasketInfo.setBasketCount2(0);
        myBasketInfo.setBasketCount3(0);
        myBasketInfo.setBasketCount4(0);
        myBasketInfo.setBasketCount5(0);

        textViewRow1Col2.setText("0");
        textViewRow2Col2.setText("0");
        textViewRow3Col2.setText("0");
        textViewRow4Col2.setText("0");
        textViewRow5Col2.setText("0");

        popupToast("Basket cleared.");
    }

    private void popupToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private String makeStatMessage(LevelWordInfo myBasketInfo) {
        StringBuffer sb = new StringBuffer();
        if (myBasketInfo != null) {
            sb.append("■ 현재 레벨 : " + myBasketInfo.getLevelTotalCount()).append(" word(s)").append("\n");
            sb.append("■ 암기한 단어 : " + myBasketInfo.getBasketCount5()).append(" word(s)").append("\n");
            sb.append("■ 진행중 단어 : " + myBasketInfo.getWorkingTotal()).append(" word(s)");
        }
        return(sb.toString());
    }

    private int getArrayIndex(int array, String findIndex) {
        String[] arrayString = getResources().getStringArray(array);
        for (int e = 0; e < arrayString.length; e++) {
            if (arrayString[e].equals(findIndex))
                return e;
        }
        return -1;
    }

    private int getRandomInt(int _minCount, int _maxCount) {
        int _index = 0;
        Random _r = new Random();
        _index= _r.nextInt(_maxCount - _minCount) + _minCount;
        return (_index);
    }

}