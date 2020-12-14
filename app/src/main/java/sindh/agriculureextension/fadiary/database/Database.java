package sindh.agriculureextension.fadiary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final String DB = "FADB.db";

    public Database(@Nullable Context context) {
        super(context, DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Queries.CREATE_IMAGE_TABLE);
        sqLiteDatabase.execSQL(Queries.DIARY_TABLE_CREATE);
        sqLiteDatabase.execSQL(Queries.LOCUST_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Queries.DROP_IMAGE_TABLE);
        sqLiteDatabase.execSQL(Queries.DIARY_TABLE_DROP);
        sqLiteDatabase.execSQL(Queries.LOCUST_DROP);
    }
}
