package com.aftersapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aftersapp.data.responsedata.PartyResponseDTO;
import com.aftersapp.utils.DateUtils;
import com.aftersapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay on 19-09-2016.
 */
public class DbRepository extends SQLiteOpenHelper {

    private static final String TAG = DbRepository.class.getSimpleName();

    private final static String DATABASE_NAME = "aftersapp.db";

    private final static String CREATE_PARTY = "CREATE TABLE " + SqlContract.Party.TABLE_NAME + "(" +
            SqlContract.Party.PARTY_ID + " INTEGER PRIMARY KEY," + SqlContract.Party.PARTY_TITLE + " TEXT," +
            SqlContract.Party.DESCRIPTION + " TEXT," + SqlContract.Party.LATITUDE + " DOUBLE ," +
            SqlContract.Party.LONGITUDE + " DOUBLE," + SqlContract.Party.LOCATION + " VARCHAR(255)," +
            SqlContract.Party.MUSIC + " VARCHAR(45)," + SqlContract.Party.AGE_RANGE + " VARCHAR(15)," +
            SqlContract.Party.INTEREST + " INTEGER," + SqlContract.Party.IMAGE + " TEXT," +
            SqlContract.Party.HOST_BY_ID + " INTEGER," + SqlContract.Party.PARTY_DATE + " datetime," +
            SqlContract.Party.ATTENDING + " INTEGER," + SqlContract.Party.CREATED_DATE + " datetime," +
            SqlContract.Party.HOST_NAME + " TEXT," + SqlContract.Party.IS_FAV + " INTEGER," +
            SqlContract.Party.IS_LIKE + " INTEGER)";

    public DbRepository(Context context, SessionManager sessionManager) {
        super(context, DATABASE_NAME, null, sessionManager.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_PARTY);
            Log.d(TAG, "##Party Table Create " + CREATE_PARTY);
        } catch (SQLiteException e) {
            Log.e(TAG, "##Could not create party table" + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteAllData() {

    }

    public void getDatabaseStructure() {
        final ArrayList<String> dirArray = new ArrayList<String>();

        SQLiteDatabase DB = getReadableDatabase();
        //SQLiteDatabase DB = sqlHelper.getWritableDatabase();
        Cursor c = DB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        c.moveToFirst();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                dirArray.add(c.getString(c.getColumnIndex("name")));

                c.moveToNext();
            }
        }
        Log.i(TAG, "##" + dirArray);
        c.close();

    }

    public boolean insertParty(List<PartyResponseDTO> partyResponseDTOs) {
        boolean flagError = false;
        String errorMessage = "";
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues = null;
        DateUtils dateUtils = new DateUtils();
        long count = -1;
        try {
            sqLiteDatabase = getWritableDatabase();
            synchronized (sqLiteDatabase) {
                contentValues = new ContentValues();
                for (PartyResponseDTO partyResponseDTO : partyResponseDTOs) {
                    contentValues.put(SqlContract.Party.PARTY_ID, partyResponseDTO.getPartyId());
                    contentValues.put(SqlContract.Party.PARTY_TITLE, partyResponseDTO.getTitle());
                    contentValues.put(SqlContract.Party.DESCRIPTION, partyResponseDTO.getDesc());
                    contentValues.put(SqlContract.Party.LATITUDE, partyResponseDTO.getLatitude());
                    contentValues.put(SqlContract.Party.LONGITUDE, partyResponseDTO.getLongitude());
                    contentValues.put(SqlContract.Party.LOCATION, partyResponseDTO.getLocation());
                    contentValues.put(SqlContract.Party.MUSIC, partyResponseDTO.getMusic());
                    contentValues.put(SqlContract.Party.AGE_RANGE, partyResponseDTO.getAge());
                    contentValues.put(SqlContract.Party.INTEREST, partyResponseDTO.getInterest());
                    contentValues.put(SqlContract.Party.ATTENDING, partyResponseDTO.getAttending());
                    contentValues.put(SqlContract.Party.PARTY_DATE,
                            dateUtils.getDateAndTimeFromLong(partyResponseDTO.getPdate()));
                    contentValues.put(SqlContract.Party.CREATED_DATE,
                            dateUtils.getDateAndTimeFromLong(partyResponseDTO.getCreatedDate()));
                    contentValues.put(SqlContract.Party.IMAGE, partyResponseDTO.getImage());
                    contentValues.put(SqlContract.Party.HOST_BY_ID, partyResponseDTO.getHost());
                    contentValues.put(SqlContract.Party.HOST_NAME, partyResponseDTO.getHostName());
                    contentValues.put(SqlContract.Party.IS_FAV, partyResponseDTO.getIsFavourite());
                    contentValues.put(SqlContract.Party.IS_LIKE, partyResponseDTO.getIsLike());
                    boolean checkId = checkDataAvailable(partyResponseDTO.getPartyId(), sqLiteDatabase);
                    if (!sqLiteDatabase.isOpen()) sqLiteDatabase = getWritableDatabase();
                    if (!checkId)
                        count = sqLiteDatabase.insert(SqlContract.Party.TABLE_NAME, null, contentValues);
                    else
                        count = sqLiteDatabase.update(SqlContract.Party.TABLE_NAME, contentValues,
                                SqlContract.Party.PARTY_ID + "=?",
                                new String[]{String.valueOf(partyResponseDTO.getPartyId())});
                    contentValues.clear();
                    Log.d(TAG, "## Party is Added Successfully");
                    flagError = true;
                }
            }
        } catch (Exception e) {
            flagError = false;
            errorMessage = e.getMessage();
            Log.e(TAG, "##Error while insert Party " + e.toString());
        } finally {
            if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
            if (!flagError)
                Log.e(TAG, "##Insert Party" + errorMessage);
        }
        return flagError;
    }

    private boolean checkDataAvailable(long partyId, SQLiteDatabase sqLiteDatabase) {
        boolean flag = false;
        Cursor cursor = null;
        try {

            String[] whereClause = new String[]{String.valueOf(partyId)};
            cursor = sqLiteDatabase.rawQuery("SELECT " + SqlContract.Party.
                    PARTY_ID + " From " + SqlContract.Party.TABLE_NAME + " where " +
                    SqlContract.Party.PARTY_ID + "=?", whereClause);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    flag = true;
                }
            } else {
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return flag;
    }
}
