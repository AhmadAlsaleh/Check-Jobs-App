package com.crazy_iter.checkjobs.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CrazyITer on 3/27/2018.
 */

public class LocalDB extends SQLiteOpenHelper {

    private static final String dbName = "checkjobs";

    private static final String createUsers = "CREATE TABLE IF NOT EXISTS users (id VARCHAR, " +
            "name VARCHAR, image_url VARCHAR, details VARCHAR, is_pro VARCHAR, is_facebook VARCHAR)";
    private static final String createIdentity = "CREATE TABLE IF NOT EXISTS identity (id VARCHAR, value VARCHAR, user_id VARCHAR)";
    private static final String createLanguage = "CREATE TABLE IF NOT EXISTS language (id VARCHAR, name VARCHAR, code VARCHAR, description VARCHAR)";

    private static final String dropUsers = "DROP TABLE IF EXIST users";
    private static final String dropIdentity = "DROP TABLE IF EXIST identity";
    private static final String dropLanguage= "DROP TABLE IF EXIST language";

    public LocalDB(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createUsers);
        sqLiteDatabase.execSQL(createIdentity);
        sqLiteDatabase.execSQL(createLanguage);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(dropUsers);
        sqLiteDatabase.execSQL(dropIdentity);
        sqLiteDatabase.execSQL(dropLanguage);
        onCreate(sqLiteDatabase);
    }

    public void insertLanguage(String id, String name, String code, String description) {
        String insert = "INSERT INTO language(id, name, code, description) VALUES " +
                "('" + id + "','" + name + "','" + code + "','" + description + "')";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(insert);
        sqLiteDatabase.close();
    }

    public Language getLanguage() {
        String getLanguage = "SELECT idm name, code, description FROM language";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(getLanguage, null);
        Language language = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cursor.moveToPosition(0);
            language = new Language(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
        }
        sqLiteDatabase.close();
        cursor.close();
        return language;
    }

    public void insertUser(String id, String name, String imageURL, String details, boolean isPro, boolean isFacebook) {
        String insert = "INSERT INTO users(id, name, image_url, details, is_pro, is_facebook) VALUES " +
                "('" + id + "','" + name + "','" + imageURL + "','" + details + "','" + isPro + "','" + isFacebook + "')";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(insert);
        sqLiteDatabase.close();
    }

    public User getUserInfo() {
        String getUser = "SELECT id, name, image_url, details, is_pro, is_facebook FROM users";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(getUser, null);
        User user = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cursor.moveToPosition(0);
            user = new User(
                    cursor.getString(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), Boolean.parseBoolean(cursor.getString(4)),
                    Boolean.parseBoolean(cursor.getString(5)));
        }
        sqLiteDatabase.close();
        cursor.close();
        return user;
    }

    public void editUserInfo(String name, String details) {
        String update = "UPDATE users SET name = '" + name +"', details = '" + details + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(update);
        sqLiteDatabase.close();
    }

    public void logout() {
        String deleteUser = "DELETE FROM users";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(deleteUser);
        sqLiteDatabase.close();
    }
}
