package com.sufamily.wordrestart.wordrestart;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sufamily.wordrestart.wordrestart.db.repository.DictionaryRepository;
import com.sufamily.wordrestart.wordrestart.db.repository.PreferencesRepository;
import com.sufamily.wordrestart.wordrestart.domain.model.UserInfo;
import com.sufamily.wordrestart.wordrestart.enums.ButtonEnableType;
import com.sufamily.wordrestart.wordrestart.enums.ImageButtonType;
import com.sufamily.wordrestart.wordrestart.helper.StringUtils;
import com.sufamily.wordrestart.wordrestart.util.SystemUiHider;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FlashcardTimerTaskActivity extends Activity implements TextToSpeech.OnInitListener {
    private final String TAG = "FlashcardTimerTaskActivity";

    private PreferencesRepository preferencesRepository;
    private UserInfo userInfo;

    private static TimerTask flashTimerTask;
    private long FLASH_TIMER_DELAY = 500;
    private Timer flashTimer = new Timer();

    private List<String> currentWordChain;

    private ImageButton startBtn;
    private ImageButton stopBtn;
    private ImageButton previousBtn;
    private ImageButton nextBtn;
    private ImageButton replayBtn;
    private ImageButton soundBtn;
    private ImageButton soundOffBtn;
    private TextView textViewFlashcard;
    private Boolean soundToggle;
    private float WordTextSize = 90;
    private float WordMeaningTextSize = 70;
    private int indexWordChains = 0;
    private int currentBasketNo = 1;

    private TextToSpeech myTTS;
    private String WordRemovePattern = "[0-9]";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
//    private static final boolean AUTO_HIDE = true;
    private static boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_timertask);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.startBtn).setOnTouchListener(mDelayHideTouchListener);

        //
        preferencesRepository = new PreferencesRepository(this);
        userInfo = preferencesRepository.getPreferences();

        Bundle bundle = getIntent().getExtras(); // 값을 받아온다.
        currentBasketNo = bundle.getInt("currentBasketNo");

        // Basket 가져오기
        initializeWordSet(currentBasketNo);
        soundToggle = true;
        indexWordChains = 0;

        textViewFlashcard = (TextView) findViewById(R.id.fullscreen_content);

        stopBtn = (ImageButton) findViewById(R.id.stopBtn);
        soundBtn = (ImageButton) findViewById(R.id.soundBtn);
        soundOffBtn = (ImageButton) findViewById(R.id.soundBtn);
        startBtn = (ImageButton) findViewById(R.id.startBtn);
        replayBtn = (ImageButton) findViewById(R.id.replayBtn);
        previousBtn = (ImageButton) findViewById(R.id.previousBtn);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        userInfo = preferencesRepository.getPreferences();

        // Time setting
        flashTimer = new Timer();

        // TTS setting
        if (myTTS == null) {
            myTTS = new TextToSpeech(this, this);
        }

        //onResume we start our timer so it can start when the app comes from the background
        startTimer(FLASH_TIMER_DELAY, userInfo.getFlashcardFrequence());

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


    private void popupToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void FlashcardButtonClickHandler(View target) {

        //TODO  : 삭제할 것
        AUTO_HIDE = false;

        switch (target.getId()) {
            case R.id.startBtn:
                changeButton(R.id.stopBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                changeButton(R.id.startBtn, ButtonEnableType.DISABLE, ImageButtonType.DISABLE);
                changeButton(R.id.previousBtn, ButtonEnableType.DISABLE, ImageButtonType.DISABLE);
                changeButton(R.id.nextBtn, ButtonEnableType.DISABLE, ImageButtonType.DISABLE);
                changeButton(R.id.replayBtn, ButtonEnableType.DISABLE, ImageButtonType.DISABLE);
                startTimer(FLASH_TIMER_DELAY, userInfo.getFlashcardFrequence());
//                AUTO_HIDE = true;
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                break;
            case R.id.stopBtn:
                changeButton(R.id.stopBtn, ButtonEnableType.DISABLE, ImageButtonType.DISABLE);
                changeButton(R.id.startBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                changeButton(R.id.previousBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                changeButton(R.id.nextBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                changeButton(R.id.replayBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                stopTimerTask();
                if (indexWordChains > 0) {
                    indexWordChains -= 1;
                }
                AUTO_HIDE = false;
                break;
            case R.id.previousBtn:
                if (indexWordChains < 1) {
                    indexWordChains = 0;
                } else {
                    indexWordChains -= 1;
                }
                displayFlashcard(indexWordChains);

                break;
            case R.id.nextBtn:
                int count = currentWordChain.size();
                if (count == 1) {
                    indexWordChains = 0;
                } else if (count <= indexWordChains) {
                    indexWordChains = count - 1;
                } else {
                    indexWordChains += 1;
                }
                displayFlashcard(indexWordChains);

                break;
            case R.id.replayBtn:
                if (indexWordChains > 0 && indexWordChains < (currentWordChain.size()-1)) {
                    displayFlashcard(indexWordChains);
                }

                break;
            case R.id.soundBtn:
                if (soundToggle) {
                    changeButton(R.id.soundBtn, ButtonEnableType.ENABLE, ImageButtonType.DISABLE);
                    soundToggle = soundToggle ? false : true;
                    if (myTTS != null) {
                        myTTS.shutdown();
                    }
                } else {
                    changeButton(R.id.soundBtn, ButtonEnableType.ENABLE, ImageButtonType.ENABLE);
                    soundToggle = soundToggle ? false : true;
                    myTTS = new TextToSpeech(getApplicationContext(), null);
                }
                break;
            default :
                return;
        }


    }


    /**
     *
     * @param target
     * @param buttonEnableType :
     *          - true : enable true
     *          - false : enable false
     * @param imageButtonType :
     *          - true : red
     *          - false : gray
     */
    private void changeButton(int target, ButtonEnableType buttonEnableType, ImageButtonType imageButtonType) {
        switch (target) {
            case R.id.startBtn :
                startBtn.setEnabled(buttonEnableType.getValue());
                if (imageButtonType.getValue())
                    startBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_red));
                else
                    startBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_gray));
                break;

            case R.id.stopBtn :
                stopBtn.setEnabled(buttonEnableType.getValue());
                if (imageButtonType.getValue())
                    stopBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_red));
                else
                    stopBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_gray));
                break;

            case R.id.previousBtn :
                previousBtn.setEnabled(buttonEnableType.getValue());
                if (imageButtonType.getValue())
                    previousBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_previous_red));
                else
                    previousBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_previous_gray));
                break;

            case R.id.nextBtn :
                nextBtn.setEnabled(buttonEnableType.getValue());
                if (imageButtonType.getValue())
                    nextBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_red));
                else
                    nextBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_gray));
                break;

            case R.id.replayBtn :
                replayBtn.setEnabled(buttonEnableType.getValue());
                if (imageButtonType.getValue())
                    replayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_replay_red));
                else
                    replayBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_replay_gray));
                break;

            case R.id.soundBtn :
                if (imageButtonType.getValue())
                    soundBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_sound_on_red));
                else
                    soundBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_sound_off_red));
                break;

        }

    }



    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /**
     * Flashcard data setting
     */
    private void initializeWordSet(int basketNo) {
        DictionaryRepository dictionaryRepository = new DictionaryRepository(getApplicationContext());
        currentWordChain = dictionaryRepository.getBasketWordChains(basketNo);
    }

    /**
     * TimerTask
     */
    public void startTimer(long delay, long frequence) {
        //set a new Timer
        if (flashTimer == null) {
            flashTimer = new Timer();
        }

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        flashTimer.scheduleAtFixedRate(flashTimerTask, delay, frequence);
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
                if (indexWordChains >= currentWordChain.size()) {
                    indexWordChains = 0;
                }

                runOnUiThread(new Runnable() //run on ui thread
                {
                    public void run() {

                        if (currentWordChain.size() > indexWordChains) {
                            displayFlashcard(indexWordChains);
                            indexWordChains++;
                        }
                    }
                });
            }
        };
    }


    public void displayFlashcard(int indexWord) {
        String word4tts;
        String flashWord;
        int dataCount;

        dataCount = currentWordChain.size();

        if (indexWord < dataCount) {
            flashWord = currentWordChain.get(indexWord);
            if (((indexWord + 1) % 2) == 1) {
                // 단어
                word4tts = flashWord;
                if (myTTS != null) {
                    myTTS.setLanguage(Locale.US);
                    myTTS.setSpeechRate(0.7f);
                    myTTS.speak(word4tts, TextToSpeech.QUEUE_FLUSH, null);
                }
                textViewFlashcard.setTypeface(null, Typeface.BOLD);
                textViewFlashcard.setTextSize(WordTextSize);
                textViewFlashcard.setText(flashWord);
            } else {
                // 의미
                textViewFlashcard.setTypeface(null, Typeface.BOLD);
                textViewFlashcard.setTextSize(WordMeaningTextSize);
                textViewFlashcard.setText(flashWord.replace("^", "\n"));
            }
        }
    }

}