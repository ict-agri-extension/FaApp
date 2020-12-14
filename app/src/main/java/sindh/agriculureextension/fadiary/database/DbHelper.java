package sindh.agriculureextension.fadiary.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sindh.agriculureextension.fadiary.form.VisitModel;

public class DbHelper {

    private static volatile DbHelper dbHelper;

    private Context context;

    private DbHelper(Context context) {
        this.context = context;
        SQLiteDatabase db=new Database(context).getWritableDatabase();
        db.execSQL(Queries.LOCUST_CREATE);
        db.execSQL(Queries.DIARY_TABLE_CREATE);
        db.execSQL(Queries.CREATE_IMAGE_TABLE);
        db.close();
    }

    public static synchronized DbHelper getInstance(Context context) {
        if (dbHelper == null)
            dbHelper = new DbHelper(context);
        return dbHelper;

    }


    public boolean addRecord(VisitModel model) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Queries._ADDRESS, model.getAddress());
        values.put(Queries._FARMER, model.getFarmer());
        values.put(Queries._IMAGE, model.getImage());
        values.put(Queries._LANG, model.getLANG());
        values.put(Queries._LAT, model.getLAT());
        values.put(Queries._QUESTION, model.getQuestion());
        values.put(Queries._SUGGESTION, model.getSuggestion());
        values.put(Queries._STATUS, model.getStatus());
        values.put(Queries._LOCATION_NAME, model.getLocationName());
        values.put(Queries._FARMER_PHONE, model.getFarmerPhone());
        values.put(Queries._TIME,Calendar.getInstance().getTimeInMillis());
        long val = db.insert(Queries.IMAGE_TABLE, null, values);
        db.close();
        return val == 0;

    }

    public boolean addDiaryRecord(String imagePath) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Queries._STATUS, 0);
        values.put(Queries.DIARY_IMAGE, imagePath);
        values.put(Queries.DIARY_IMG_TAKEN, Calendar.getInstance().getTimeInMillis());
        long val = db.insert(Queries.DIARY_TABLE, null, values);
        db.close();
        return val == 0;

    }

    public boolean addLocustRecord(String imagePath, double lat,double lang) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        Calendar calendar=Calendar.getInstance();
        ContentValues values = new ContentValues();
        values.put(Queries._STATUS, 0);
        values.put(Queries._IMAGE, imagePath);
        values.put(Queries._TIME, calendar.getTimeInMillis());
        values.put(Queries._LAT,lat);
        values.put(Queries._LANG,lang);

        long val = db.insert(Queries.LOCUST_TABLE, null, values);
        db.close();
        System.out.println("LOCUST ADDED WITH CODE "+val);
        return val == 0;

    }


    public List<Map<String,String>> locustRecordsToBeUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.LOCUST_TABLE + " WHERE " + Queries._STATUS + "=0";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<Map<String,String>> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                Map<String,String> map=new HashMap<>();
                map.put(Queries._IMAGE,cursor.getString(4));
                map.put(Queries._TIME,String.valueOf(cursor.getLong(6)));
                map.put(Queries._ID,String.valueOf(cursor.getInt(0)));
                map.put(Queries._LANG,String.valueOf(cursor.getDouble(1)));
                map.put(Queries._LAT,String.valueOf(cursor.getInt(2)));
                map.put(Queries._ADDRESS,String.valueOf(cursor.getString(5)));
                list.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Map<String,String>> locustRecordUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.LOCUST_TABLE + " WHERE " + Queries._STATUS + "=1 "+
                "ORDER BY "+Queries._ID+" DESC";;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<Map<String,String>> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                Map<String,String> map=new HashMap<>();
                map.put(Queries._IMAGE,cursor.getString(4));
                map.put(Queries._TIME,String.valueOf(cursor.getLong(6)));
                map.put(Queries._ID,String.valueOf(cursor.getInt(0)));
                map.put(Queries._LANG,String.valueOf(cursor.getDouble(1)));
                map.put(Queries._LAT,String.valueOf(cursor.getInt(2)));
                map.put(Queries._ADDRESS,String.valueOf(cursor.getString(5)));
                list.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Map<String,String>> diaryRecordsToBeUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.DIARY_TABLE + " WHERE " + Queries._STATUS + "=0";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<Map<String,String>> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                Map<String,String> map=new HashMap<>();
                map.put(Queries.DIARY_IMAGE,cursor.getString(2));
                map.put(Queries.DIARY_IMG_TAKEN,String.valueOf(cursor.getLong(1)));
                map.put(Queries._ID,String.valueOf(cursor.getInt(0)));
                list.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Map<String,String>> diaryRecordUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.DIARY_TABLE + " WHERE " + Queries._STATUS + "=1"
                +" ORDER BY "+Queries._ID+" DESC";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<Map<String,String>> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                Map<String,String> map=new HashMap<>();
                map.put(Queries.DIARY_IMAGE,cursor.getString(2));
                map.put(Queries.DIARY_IMG_TAKEN,String.valueOf(cursor.getLong(1)));
                map.put(Queries._ID,String.valueOf(cursor.getInt(0)));
                list.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }


    public List<VisitModel> recordsToBeUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.IMAGE_TABLE + " WHERE " + Queries._STATUS + "=0";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<VisitModel> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                VisitModel model = new VisitModel();
                model.setID(cursor.getInt(0));
                model.setAddress(cursor.getString(1));
                model.setLAT(cursor.getDouble(2));
                model.setLANG(cursor.getDouble(3));
                model.setImage(cursor.getString(4));
                model.setFarmer(cursor.getString(5));
                model.setQuestion(cursor.getString(6));
                model.setSuggestion(cursor.getString(7));
                model.setStatus(cursor.getInt(8));
                model.setLocationName(cursor.getString(9));
                model.setFarmerPhone(cursor.getString(10));
                model.setTime(cursor.getLong(11));
                list.add(model);
                System.out.println(model.toString());
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<VisitModel> recordsUploaded() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        db.execSQL(Queries.DIARY_TABLE_CREATE);
        String query = "SELECT * FROM " + Queries.IMAGE_TABLE + " WHERE " + Queries._STATUS + "=1";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<VisitModel> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                VisitModel model = new VisitModel();
                model.setID(cursor.getInt(0));
                model.setAddress(cursor.getString(1));
                model.setLAT(cursor.getDouble(2));
                model.setLANG(cursor.getDouble(3));
                model.setImage(cursor.getString(4));
                model.setFarmer(cursor.getString(5));
                model.setQuestion(cursor.getString(6));
                model.setSuggestion(cursor.getString(7));
                model.setStatus(cursor.getInt(8));
                model.setLocationName(cursor.getString(9));
                model.setFarmerPhone(cursor.getString(10));
                model.setTime(cursor.getLong(11));
                list.add(model);
                System.out.println(model.toString());
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<VisitModel> records() {
        SQLiteDatabase db = new Database(context).getReadableDatabase();
        String query = "SELECT * FROM " + Queries.IMAGE_TABLE + " WHERE "+Queries._STATUS+"=1 ORDER BY "+Queries._ID+" DESC";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        List<VisitModel> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            do {
                VisitModel model = new VisitModel();
                model.setID(cursor.getInt(0));
                model.setAddress(cursor.getString(1));
                model.setLAT(cursor.getDouble(2));
                model.setLANG(cursor.getDouble(3));
                model.setImage(cursor.getString(4));
                model.setFarmer(cursor.getString(5));
                model.setQuestion(cursor.getString(6));
                model.setSuggestion(cursor.getString(7));
                model.setStatus(cursor.getInt(8));
                model.setLocationName(cursor.getString(9));
                model.setFarmerPhone(cursor.getString(10));
                model.setTime(cursor.getLong(11));
                list.add(model);
                System.out.println(model.toString());
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public void updateStatus(int id) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Queries._STATUS, 1);
        int val = db.update(Queries.IMAGE_TABLE, contentValues, Queries._ID + "=" + id, null);
        System.out.println(val);
        db.close();
    }



    public void updateDiaryStatus(int id) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Queries._STATUS, 1);
        int val = db.update(Queries.DIARY_TABLE, contentValues, Queries._ID + "=" + id, null);
        System.out.println(val);
        db.close();
    }


    public void updateLocustStatus(int id,String address) {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Queries._STATUS, 1);
        contentValues.put(Queries._ADDRESS,address);
        int val = db.update(Queries.LOCUST_TABLE, contentValues, Queries._ID + "=" + id, null);
        System.out.println(val);
        db.close();
    }


    public void updateData(VisitModel model) {
        try {
            ContentValues values=new ContentValues();
            values.put(Queries._ADDRESS,model.getAddress());
            values.put(Queries._IMAGE,model.getImage());
            SQLiteDatabase db=new Database(context).getWritableDatabase();
            db.update(Queries.IMAGE_TABLE,values,Queries._ID+"="+model.getID(),null);
            db.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void create() {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        db.execSQL(Queries.DROP_IMAGE_TABLE);
        db.execSQL(Queries.CREATE_IMAGE_TABLE);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = new Database(context).getWritableDatabase();
        db.execSQL("DELETE FROM " + Queries.IMAGE_TABLE);
        db.execSQL("DELETE FROM "+Queries.LOCUST_TABLE);
        db.execSQL("DELETE FROM "+Queries.DIARY_TABLE);
        db.close();
    }
}
