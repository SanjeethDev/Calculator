package com.sanjeethdev.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionBarContextView;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "CalculatorHistory.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE EquationDetails(id INTEGER PRIMARY KEY AUTOINCREMENT,expression TEXT NOT NULL,result TEXT NOT NULL,timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE if exists EquationDetails");
    }

    public boolean insertEquation(String expression, String result) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expression", expression );
        contentValues.put("result", result);
        long resultCode = sqLiteDatabase.insert("EquationDetails", null, contentValues);

        if (resultCode == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteAllEquation() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM EquationDetails");
    }

    public Cursor getHistory() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM EquationDetails ORDER BY timestamp DESC", null);
        return cursor;
    }
}
