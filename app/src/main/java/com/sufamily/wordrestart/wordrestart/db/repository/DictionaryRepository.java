package com.sufamily.wordrestart.wordrestart.db.repository;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sufamily.wordrestart.wordrestart.db.DBManager;
import com.sufamily.wordrestart.wordrestart.domain.model.JoinEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.WiseSayingEntity;
import com.sufamily.wordrestart.wordrestart.domain.model.command.AddBasketCommand;
import com.sufamily.wordrestart.wordrestart.enums.BasketType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by N1245 on 2015-03-05.
 */
public class DictionaryRepository {
    private final String TAG = "DictionaryRepository";

    private final String TABLE_DICTIONARY = "dictionary";
    private final String TABLE_MY_DICTIONARY = "my_dictionary";

    private DBManager dbManager;

    public DictionaryRepository(Context context) {
        this.dbManager = new DBManager(context);

        try {
            this.dbManager.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public long getCountAll(String table) {
        long count = 0;
        count = dbManager.selectCount(table, null, null);
        return (count);
    }

    public long getCount(String table, String queryWhere, String[] params) {
        long count = 0;
        count = dbManager.selectCount(table, queryWhere, params);
        return (count);
    }

    public Cursor getList(String query, String[] params) {
        Cursor cursor = dbManager.select(query, params);
        return cursor;
    }

    public void add(String query) {
        dbManager.insert(query);
    }

    public void remove(String query) {
        dbManager.delete(query);
    }

    public long getMyDictionaryCountAll() {
        long count = 0;
        count = dbManager.selectCount(TABLE_MY_DICTIONARY, null, null);
        return (count);
    }

    public long getDictionaryLevelCount(String level) {
        long count = 0;
        String queryWhere = "level=?";
        count = dbManager.selectCount(TABLE_DICTIONARY, queryWhere, new String[]{level});
        return (count);
    }

    public long getBasketCount(int basket_no) {
        long count = 0;
        count = dbManager.selectCount(TABLE_MY_DICTIONARY, "basket_no = ?", new String[]{Integer.toString(basket_no)});
        return (count);
    }

    public void removeAllBasket() {
        String sql_string_delete =
                "DELETE FROM my_dictionary";

        dbManager.delete(sql_string_delete);
    }


    public void removeBasket(int basket_no) {
        String sql_string = "DELETE FROM my_dictionary WHERE basket_no = %d";
        String query = String.format(sql_string, basket_no);
        dbManager.delete(query);
    }

    public int getMyDictionaryLastSeq() {
        int resultValue = 0;
        String sql_string_select =
                "SELECT MAX(word_seq) FROM my_dictionary";

        Cursor cursor = dbManager.select(sql_string_select, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            resultValue = cursor.getInt(0);
        }
        cursor.close();
        return resultValue;
    }

    public void addDictionary(String level, String word, String meaning) {
        String sql_string =
                "INSERT INTO dictionary (level, word, meaning) VALUES ( '%s', '%s', '%s' )";
        String query = String.format(sql_string, level, word, meaning);
        try {
            dbManager.insert(query);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public int addBasket(AddBasketCommand command) {
        int resultValue = 0;
        int basketNo = command.getBasketNo();
        String sql_string_insert =
                "INSERT INTO my_dictionary (basket_no, word_seq) VALUES ( %d, %d )";

        String sql_string =
                "SELECT word_seq, level, word, meaning "
                        + " FROM dictionary d "
                        + " WHERE level='%s' "
                        + " AND word_seq not in (SELECT word_seq FROM my_dictionary)"
                        + " ORDER BY word_seq LIMIT %d";
        String query = String.format(sql_string, command.getLevel(), command.getLimit());
        Cursor cursor = dbManager.select(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    query = String.format(sql_string_insert, basketNo, cursor.getInt(0));
                    dbManager.insert(query);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                resultValue++;
            }
        }
        cursor.close();
        return resultValue;
    }

    public List<String> getBasketWordChains(int basket_no) {
        List<String> items = new ArrayList<String>();
        String sql_string =
                "SELECT word, meaning "
                        + " FROM dictionary d INNER JOIN my_dictionary m ON d.word_seq = m.word_seq "
                        + " WHERE m.basket_no = %d";

        String query = String.format(sql_string, basket_no);
        Cursor cursor = dbManager.select(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                items.add(cursor.getString(0));
                items.add(cursor.getString(1));
            }
        }
        cursor.close();
        return items;
    }

    public List<JoinEntity> getBasketEntityList(int basket_no) {
        List<JoinEntity> items = new ArrayList<JoinEntity>();
        String sql_string =
                "SELECT d.word_seq, d.level, d.word, d.meaning, m.my_seq "
                        + " FROM dictionary d INNER JOIN my_dictionary m ON d.word_seq = m.word_seq "
                        + " WHERE m.basket_no = %d";
        String query = String.format(sql_string, basket_no);
        Cursor cursor = dbManager.select(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                JoinEntity joinEntity = new JoinEntity();
                joinEntity.setWord_seq(cursor.getInt(0));
                joinEntity.setLevel(cursor.getString(1));
                joinEntity.setWord(cursor.getString(2));
                joinEntity.setMeaning(cursor.getString(3));
                joinEntity.setMy_seq(cursor.getInt(4));
                items.add(joinEntity);
            }
        }
        cursor.close();
        return items;
    }

    public List<JoinEntity> getLevelAll(String level) {
        List<JoinEntity> items = new ArrayList<>();
        String sql_string =
                "SELECT d.word_seq, d.level, d.word, d.meaning, m.word_seq as my_seq "
                        + " FROM dictionary d LEFT OUTER JOIN my_dictionary m "
                        + "                                ON d.word_seq = m.word_seq AND m.basket_no = 5"
                        + " WHERE d.level = '%s'"
                        + " ORDER BY d.word_seq ";
        String query = String.format(sql_string, level);
        Cursor cursor = dbManager.select(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                JoinEntity entity = new JoinEntity();
                entity.setWord_seq(cursor.getInt(0));
                entity.setLevel(cursor.getString(1));
                entity.setWord(cursor.getString(2));
                entity.setMeaning(cursor.getString(3));
                entity.setMy_seq(cursor.getInt(4));
                items.add(entity);
            }
        }
        cursor.close();
        return items;
    }

    public long updateTestResult(JoinEntity entity, int basket_no) {
        String query = null;
        long count = 0;
        String sql_string_insert =
                "INSERT INTO my_dictionary (basket_no, word_seq) VALUES ( %d, %d )";
        String sql_string_update =
                "UPDATE my_dictionary SET "
                        + " basket_no = %d "
                        + " WHERE word_seq = %d";

        try {
            if (entity == null) return 0;

            query = "word_seq = ?";
            count = dbManager.selectCount(TABLE_MY_DICTIONARY, query, new String[]{Integer.toString(entity.getWord_seq())});
            if (count > 0) {
                query = String.format(sql_string_update, basket_no, entity.getWord_seq());
                dbManager.update(query);
                count = 1;
            } else {
                query = String.format(sql_string_insert, BasketType.COMPLETED_BASKET.getIntValue(), entity.getWord_seq());
                dbManager.insert(query);
                count = 1;
            }
        } catch (Exception e) {
            count = 0;
            Log.v(TAG, e.getMessage());
        }
        return count;
    }

    /**
     * 현재 레벨에서 Skip 할 단어를 Check하면 Completed Basket으로 넣고, un-Check하면 my_dictionary에서 삭제한다.
     *
     * @param entity
     * @return
     */
    public long updateMyDictionaryToggle(JoinEntity entity, boolean isChecked) {
        String sql_string;
        String query;
        long count;
        String sql_string_insert =
                "INSERT INTO my_dictionary (basket_no, word_seq) VALUES ( %d, %d )";
        String sql_string_update =
                "UPDATE my_dictionary SET "
                        + " basket_no = %d "
                        + " WHERE word_seq = %d";

        try {
            if (entity == null) return 0;

            query = "word_seq = ?";
            count = dbManager.selectCount(TABLE_MY_DICTIONARY, query, new String[]{Integer.toString(entity.getWord_seq())});
            if (isChecked) {
                if (count > 0) {
                    query = String.format(sql_string_update, BasketType.COMPLETED_BASKET.getIntValue(), entity.getWord_seq());
                    dbManager.update(query);
                    count = 1;
                } else {
                    query = String.format(sql_string_insert, BasketType.COMPLETED_BASKET.getIntValue(), entity.getWord_seq());
                    dbManager.insert(query);
                    count = 1;
                }
            } else {
                if (count > 0) {
                    sql_string =
                            "DELETE FROM my_dictionary"
                                + " WHERE word_seq = %d";
                    query = String.format(sql_string, entity.getWord_seq());
                    dbManager.update(query);
                    count = 1;
                }
            }

        } catch (Exception e) {
            count = 0;
            Log.v(TAG, e.getMessage());
        }
        return count;
    }

    public void removeMyDictionary(JoinEntity entity) {
        String sql_string =
                "DELETE FROM my_dictionary"
                        + " WHERE word_seq = %d";
        String query = String.format(sql_string, entity.getWord_seq());
        dbManager.delete(query);
    }

    /**
     * 명언 Table
     */
    public void createWiseSaying() {
        String sql_string = "CREATE TABLE if not exists wise_saying ( wise_seq INTEGER PRIMARY KEY AUTOINCREMENT, sentence TEXT, speaker TEXT);";
        String query = String.format(sql_string);
        dbManager.insert(query);
    }

    public void addWiseSaying(String arg1, String arg2) {
        String sql_string = "INSERT INTO wise_saying (sentence, speaker) VALUES ('%s', '%s')";
        String query = String.format(sql_string, arg1, arg2);
        dbManager.insert(query);
    }

    public void removeAllWiseSaying() {
        String sql_string = "DELETE FROM wise_saying";
        String query = String.format(sql_string);
        dbManager.insert(query);
    }

    public List<WiseSayingEntity> getAllWiseSaying() {
        List<WiseSayingEntity> items = new ArrayList<>();
        String sql_string = "SELECT wise_seq, sentence, speaker "
                            + " FROM wise_saying ";
        String query = String.format(sql_string);
        Cursor cursor = dbManager.select(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                WiseSayingEntity entity = new WiseSayingEntity();
                entity.setWise_seq(cursor.getInt(0));
                entity.setSentence(cursor.getString(1));
                entity.setSpeaker(cursor.getString(2));
                items.add(entity);
            }
        }
        cursor.close();
        return items;
    }

}