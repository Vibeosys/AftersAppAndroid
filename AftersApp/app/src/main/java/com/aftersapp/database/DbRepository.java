package com.aftersapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aftersapp.data.PartyDataDTO;
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

    public boolean deleteParties() {
        SQLiteDatabase sqLiteDatabase = null;
        sqLiteDatabase = getWritableDatabase();
        long count = -1;

        try {
            synchronized (sqLiteDatabase) {
                count = sqLiteDatabase.delete(SqlContract.Party.TABLE_NAME, null, null);
                Log.d(TAG, " ## delete parties data successfully");
            }

        } catch (Exception e) {

            e.printStackTrace();
            Log.d(TAG, "## Error to delete Party data" + e.toString());
        } finally {
            if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
        }
        return count != -1;
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

    public boolean insertParty(List<PartyDataDTO> partyDataDTOs) {
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
                for (PartyDataDTO partyDataDTO : partyDataDTOs) {
                    contentValues.put(SqlContract.Party.PARTY_ID, partyDataDTO.getPartyId());
                    contentValues.put(SqlContract.Party.PARTY_TITLE, partyDataDTO.getTitle());
                    contentValues.put(SqlContract.Party.DESCRIPTION, partyDataDTO.getDesc());
                    contentValues.put(SqlContract.Party.LATITUDE, partyDataDTO.getLatitude());
                    contentValues.put(SqlContract.Party.LONGITUDE, partyDataDTO.getLongitude());
                    contentValues.put(SqlContract.Party.LOCATION, partyDataDTO.getLocation());
                    contentValues.put(SqlContract.Party.MUSIC, partyDataDTO.getMusic());
                    contentValues.put(SqlContract.Party.AGE_RANGE, partyDataDTO.getAge());
                    contentValues.put(SqlContract.Party.INTEREST, partyDataDTO.getInterest());
                    contentValues.put(SqlContract.Party.ATTENDING, partyDataDTO.getAttending());
                    contentValues.put(SqlContract.Party.PARTY_DATE,
                            dateUtils.getDateAndTimeFromLong(partyDataDTO.getPdate()));
                    contentValues.put(SqlContract.Party.CREATED_DATE,
                            dateUtils.getDateAndTimeFromLong(partyDataDTO.getCreatedDate()));
                    contentValues.put(SqlContract.Party.IMAGE, partyDataDTO.getImage());
                    contentValues.put(SqlContract.Party.HOST_BY_ID, partyDataDTO.getHost());
                    contentValues.put(SqlContract.Party.HOST_NAME, partyDataDTO.getHostName());
                    contentValues.put(SqlContract.Party.IS_FAV, partyDataDTO.getIsFavourite());
                    contentValues.put(SqlContract.Party.IS_LIKE, partyDataDTO.getIsLike());
                    boolean checkId = checkDataAvailable(partyDataDTO.getPartyId(), sqLiteDatabase);
                    if (!sqLiteDatabase.isOpen()) sqLiteDatabase = getWritableDatabase();
                    if (!checkId)
                        count = sqLiteDatabase.insert(SqlContract.Party.TABLE_NAME, null, contentValues);
                    else
                        count = sqLiteDatabase.update(SqlContract.Party.TABLE_NAME, contentValues,
                                SqlContract.Party.PARTY_ID + "=?",
                                new String[]{String.valueOf(partyDataDTO.getPartyId())});
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

    public PartyDataDTO getPartyData(long partyId) {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        PartyDataDTO partyDataDTO = null;
        DateUtils dateUtils = new DateUtils();
        try {
            String[] whereClause = new String[]{String.valueOf(partyId)};
            sqLiteDatabase = getReadableDatabase();
            synchronized (sqLiteDatabase) {
                cursor = sqLiteDatabase.rawQuery("SELECT * From " + SqlContract.Party.TABLE_NAME + " where " +
                        SqlContract.Party.PARTY_ID + "=?", whereClause);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        long id = cursor.getLong(cursor.getColumnIndex(SqlContract.Party.PARTY_ID));
                        String title = cursor.getString(cursor.getColumnIndex(SqlContract.Party.PARTY_TITLE));
                        String desc = cursor.getString(cursor.getColumnIndex(SqlContract.Party.DESCRIPTION));
                        double latitude = cursor.getDouble(cursor.getColumnIndex(SqlContract.Party.LATITUDE));
                        double longitude = cursor.getDouble(cursor.getColumnIndex(SqlContract.Party.LONGITUDE));
                        String location = cursor.getString(cursor.getColumnIndex(SqlContract.Party.LOCATION));
                        String music = cursor.getString(cursor.getColumnIndex(SqlContract.Party.MUSIC));
                        String age = cursor.getString(cursor.getColumnIndex(SqlContract.Party.AGE_RANGE));
                        int interest = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.INTEREST));
                        int attending = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.ATTENDING));
                        String image = cursor.getString(cursor.getColumnIndex(SqlContract.Party.IMAGE));
                        int host = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.HOST_BY_ID));
                        String pdate = cursor.getString(cursor.getColumnIndex(SqlContract.Party.PARTY_DATE));
                        String createdDate = cursor.getString(cursor.getColumnIndex(SqlContract.Party.CREATED_DATE));
                        String hostName = cursor.getString(cursor.getColumnIndex(SqlContract.Party.HOST_NAME));
                        int isFavourite = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.IS_FAV));
                        int isLike = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.IS_LIKE));

                        long partyDate = dateUtils.getFormattedDate(pdate).getTime();
                        long cDate = dateUtils.getFormattedDate(createdDate).getTime();
                        partyDataDTO = new PartyDataDTO(partyId, title, desc, latitude, longitude, location,
                                music, age, interest, attending, image, host, partyDate,
                                cDate, hostName, isFavourite, isLike);
                    }
                } else {
                    partyDataDTO = new PartyDataDTO();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
        }
        return partyDataDTO;
    }


    public ArrayList<PartyDataDTO> getParties() {
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        ArrayList<PartyDataDTO> parties = new ArrayList<>();
        DateUtils dateUtils = new DateUtils();
        try {
            sqLiteDatabase = getReadableDatabase();
            synchronized (sqLiteDatabase) {
                cursor = sqLiteDatabase.rawQuery("SELECT * From " + SqlContract.Party.TABLE_NAME, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            long id = cursor.getLong(cursor.getColumnIndex(SqlContract.Party.PARTY_ID));
                            String title = cursor.getString(cursor.getColumnIndex(SqlContract.Party.PARTY_TITLE));
                            String desc = cursor.getString(cursor.getColumnIndex(SqlContract.Party.DESCRIPTION));
                            double latitude = cursor.getDouble(cursor.getColumnIndex(SqlContract.Party.LATITUDE));
                            double longitude = cursor.getDouble(cursor.getColumnIndex(SqlContract.Party.LONGITUDE));
                            String location = cursor.getString(cursor.getColumnIndex(SqlContract.Party.LOCATION));
                            String music = cursor.getString(cursor.getColumnIndex(SqlContract.Party.MUSIC));
                            String age = cursor.getString(cursor.getColumnIndex(SqlContract.Party.AGE_RANGE));
                            int interest = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.INTEREST));
                            int attending = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.ATTENDING));
                            String image = cursor.getString(cursor.getColumnIndex(SqlContract.Party.IMAGE));
                            int host = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.HOST_BY_ID));
                            String pdate = cursor.getString(cursor.getColumnIndex(SqlContract.Party.PARTY_DATE));
                            String createdDate = cursor.getString(cursor.getColumnIndex(SqlContract.Party.CREATED_DATE));
                            String hostName = cursor.getString(cursor.getColumnIndex(SqlContract.Party.HOST_NAME));
                            int isFavourite = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.IS_FAV));
                            int isLike = cursor.getInt(cursor.getColumnIndex(SqlContract.Party.IS_LIKE));

                            long partyDate = dateUtils.getFormattedDate(pdate).getTime();
                            long cDate = dateUtils.getFormattedDate(createdDate).getTime();
                            PartyDataDTO partyDataDTO = new PartyDataDTO(id, title, desc, latitude, longitude, location,
                                    music, age, interest, attending, image, host, partyDate,
                                    cDate, hostName, isFavourite, isLike);
                            parties.add(partyDataDTO);
                        } while (cursor.moveToNext());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
        }
        return parties;
    }
}
