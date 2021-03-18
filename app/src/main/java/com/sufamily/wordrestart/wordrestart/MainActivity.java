package com.sufamily.wordrestart.wordrestart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    private SharedPreferences myPref;
    private String userName;
    private String userLevel;
    private int index;
    private int wordCountPerDay;
    private int flashPerio;

    private Button flashcardBtnCtlr;
    private Button conceptBtnCtlr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPref = PreferenceManager.getDefaultSharedPreferences(this);
        getPreferencesData();
    }


    public class ConceptButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Intent intObj = new Intent(MainActivity.this, ConceptActivity.class);
            startActivity(intObj);
        }
    }


    public void TopButtonClickHandler(View target) {
        Intent intent = new Intent(MainActivity.this, TopActivity.class);
        startActivity(intent);
    }

    public void FlashcardButtonClickHandler(View target) {

        Intent intent = new Intent(MainActivity.this, FlashcardTimerTaskActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userLevel", userLevel);
        intent.putExtra("wordCountPerDay", wordCountPerDay);
        intent.putExtra("flashPerio", flashPerio);
        intent.putExtra("currentBasketNo", 1);

        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferencesData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);
        } else if (id == R.id.action_help) {
            Intent settingIntent = new Intent(this, ConceptActivity.class);
            startActivity(settingIntent);
        } else if (id == R.id.action_dbcreate) {
            Intent settingIntent = new Intent(this, DBCreateActivity.class);
            startActivity(settingIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 환경설정 읽어오기
     */
    private void getPreferencesData() {
        int index = 0;
        String temp;

        userName = myPref.getString("user_name", "Wow~");
        userLevel = myPref.getString("level_list", "l1");
        temp = myPref.getString("wordcount_per_day", "30");
        if (temp != null) {
            wordCountPerDay = Integer.parseInt(temp);
        }
        temp = myPref.getString("flashcard_frequency", "1000");
        if (temp != null) {
            flashPerio = Integer.parseInt(temp);
        }

//        Map<String, ?> allEntries = myPref.getAll();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
//        }

    }

    private int getArrayIndex(int array, String findIndex) {
        String[] arrayString = getResources().getStringArray(array);
        for (int e = 0; e < arrayString.length; e++) {
            if (arrayString[e].equals(findIndex))
                return e;
        }
        return -1;
    }

}
