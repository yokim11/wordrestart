package com.sufamily.wordrestart.wordrestart.db.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sufamily.wordrestart.wordrestart.domain.model.UserInfo;

import java.util.Set;

public class PreferencesRepository {
    public final String DEFAULT_NICKNAME = "Wow";
    public final String DEFAULT_LEVEL = "l1";
    public final String DEFAULT_FLASHCARD_COUNT_OF_DAY = "30";
    public final String DEFAULT_FLASHCARD_FREQUNCE = "1000";
    public final String DEFAULT_TESTCARD_FREQUNCE = "3000";

    private Context context;
    private SharedPreferences myPref;

    public PreferencesRepository(Context context) {
        this.context = context;
        this.myPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Read preferences
     */
    public UserInfo getPreferences() {
        UserInfo userInfo = new UserInfo();

        userInfo.setNickname(myPref.getString("user_name", DEFAULT_NICKNAME));
        userInfo.setLevel(myPref.getString("level_list", DEFAULT_LEVEL));
        int count = Integer.parseInt(myPref.getString("wordcount_per_day", DEFAULT_FLASHCARD_COUNT_OF_DAY));
        userInfo.setFlashcardCountOfDay(count);
        count = Integer.parseInt(myPref.getString("flashcard_frequency", DEFAULT_FLASHCARD_FREQUNCE));
        userInfo.setFlashcardFrequence(count);
        count = Integer.parseInt(myPref.getString("testcard_frequency", DEFAULT_TESTCARD_FREQUNCE));
        userInfo.setTestcardFrequence(count);
        userInfo.setInputDictionaryDate(myPref.getString("input_dictionary_date", ""));
        userInfo.setInputDictionaryLastSeq(myPref.getInt("input_dictionary_last_seq", 0));

        return userInfo;
    }

    /**
     * Write preferences
     */
    public void savePreference(String key, Object object) {
        SharedPreferences.Editor editor = myPref.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (long) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Set<?>) {
            editor.putStringSet(key, (Set) object);
        }
        editor.commit();
    }

}
