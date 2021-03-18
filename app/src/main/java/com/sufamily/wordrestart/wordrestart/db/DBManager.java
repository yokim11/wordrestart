package com.sufamily.wordrestart.wordrestart.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public class DBManager extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.sufamily.wordrestart.wordrestart/databases/";
    private static final String DB_NAME = "word_restart.db";
    private static final String DB_FULL_PATH = DB_PATH + DB_NAME;
    private static final int DB_VER = 1;

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DBManager(Context context) {
        super(context, DB_FULL_PATH, null, DB_VER);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (Exception e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    public void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_FULL_PATH; //DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_FULL_PATH; //DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



//    private void createDatabase(SQLiteDatabase db) {
//        // 새로운 테이블을 생성한다.
//        // create table 테이블명 (컬럼명 타입 옵션);
//        db.execSQL("CREATE TABLE if not exists dictionary ( word_seq INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, word TEXT, meaning TEXT);");
//        db.execSQL("CREATE TABLE if not exists my_dictionary ( my_seq INTEGER PRIMARY KEY AUTOINCREMENT, basket_no INTEGER, word_seq INTEGER);");
//        db.execSQL("CREATE TABLE if not exists wise_saying ( wise_seq INTEGER PRIMARY KEY AUTOINCREMENT, sentence TEXT, speaker TEXT);");
//
//        db.execSQL("CREATE UNIQUE INDEX idx1_dictionary ON dictionary (word)");
//        db.execSQL("CREATE UNIQUE INDEX idx1_my_dictionary ON my_dictionary (basket_no, word_seq)");
//    }







//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // 새로운 테이블을 생성한다.
//        // create table 테이블명 (컬럼명 타입 옵션);
//        db.execSQL("CREATE TABLE if not exists dictionary ( word_seq INTEGER PRIMARY KEY AUTOINCREMENT, level TEXT, word TEXT, meaning TEXT);");
//        db.execSQL("CREATE TABLE if not exists my_dictionary ( my_seq INTEGER PRIMARY KEY AUTOINCREMENT, basket_no INTEGER, word_seq INTEGER);");
//        db.execSQL("CREATE TABLE if not exists wise_saying ( wise_seq INTEGER PRIMARY KEY AUTOINCREMENT, sentence TEXT, speaker TEXT);");
//
//        db.execSQL("CREATE UNIQUE INDEX idx1_dictionary ON dictionary (word)");
//        db.execSQL("CREATE UNIQUE INDEX idx1_my_dictionary ON my_dictionary (basket_no, word_seq)");
//    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void create(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public Cursor select(String query, String[] params) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, params);
        return cursor;
    }

    public long selectCount(String table, String queryWhere, String[] params) {
        SQLiteDatabase db = getReadableDatabase();
        long count = 0;
        if (queryWhere == null) {
            count = DatabaseUtils.queryNumEntries(db, table);
        } else {
            count = DatabaseUtils.queryNumEntries(db, table, queryWhere, params);
        }
        db.close();
        return count;
    }

    public String printData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from dictionary", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : level "
                    + cursor.getString(1)
                    + ", word = "
                    + cursor.getString(2)
                    + ", meaning = "
                    + cursor.getString(3)
                    + "\n";
        }

        cursor.close();
        db.close();

        return str;
    }

}
