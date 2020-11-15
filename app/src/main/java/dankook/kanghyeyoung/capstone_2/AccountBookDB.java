package dankook.kanghyeyoung.capstone_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_DB_FORMAT;

public class AccountBookDB {
    private static AccountBookDB mAccountDB;
    private static DatabaseHelper mDBHelper;
    private static SQLiteDatabase mDB;

    /* 데이터베이스 열기 */
    public static void databaseOpen(Context context) {
        Log.d("AccountBookDB", "open() 메소드 호출됨");
        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
            mAccountDB.mDBHelper = new DatabaseHelper(context);
            mAccountDB.mDB = mAccountDB.mDBHelper.getWritableDatabase();
        }
    }

    /* insert : 매개변수로 받은 Spec 객체에 대한 정보 데이터베이스에 추가 - 다중인 경우 세부 내역까지 저장 */
    public static int insert(Spec spec) {
        Log.d("AccountBookDB", "insert() 메소드 호출됨");

        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
        }

        mAccountDB.mDB.execSQL("INSERT INTO Spec (type, cat_main, cat_sub, price, place, date) VALUES "
                + "(" + spec.getType() + "," + spec.getCatMain() + "," + spec.getCatSub() + ","
                + spec.getPrice() + ",'" + spec.getPlace() + "','" + DATE_DB_FORMAT.format(spec.getDate()) + "')");

        // Spec 테이블에 마지막으로 추가한 데이터의 spec_id를 구함
        Cursor c = mAccountDB.mDB.rawQuery("SELECT last_insert_rowid()", null);
        c.moveToFirst();
        int specId = c.getInt(0);

        // 만약 insert한 내역(spec)의 카테고리가 다중인 경우 세부 내역까지 SpecDetail 테이블에 추가
        if (spec.getCatMain() == Spec.CAT_MAIN_MULTI) {
            for (int i = 0; i < spec.getSpecDetails().size(); i++) {
                mAccountDB.mDB.execSQL("INSERT INTO SpecDetail " +
                        "(spec_id, cat_main, cat_sub, spec_name, spec_price, place, date) VALUES ("
                        + specId + "," + spec.getSpecDetails().get(i).getCatMain() + ","
                        + spec.getSpecDetails().get(i).getCatSub() + ",'"
                        + spec.getSpecDetails().get(i).getSpecName() + "',"
                        + spec.getSpecDetails().get(i).getSpecPrice() + ",'"
                        + spec.getPlace() + "','"
                        + DATE_DB_FORMAT.format(spec.getDate()) + "')");
            }
        }
        return specId;
    }

    /* update : 내역 수정 */
    public static void update(int specId, Spec spec) {

        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
        }

        mAccountDB.mDB.execSQL("UPDATE Spec SET type=" + spec.getType() + ", cat_main=" + spec.getCatMain()
                + ", cat_sub=" + spec.getCatSub() + ", price=" + spec.getPrice() + ", place='" + spec.getPlace()
                + "', date='" + DATE_DB_FORMAT.format(spec.getDate()) + "' WHERE spec_id=" + specId);

        mAccountDB.mDB.execSQL("DELETE FROM SpecDetail WHERE spec_id=" + specId);

        for (SpecDetail specDetail : spec.getSpecDetails()) {
            mAccountDB.mDB.execSQL("INSERT INTO SpecDetail (spec_id, cat_main, cat_sub, spec_name, spec_price, place, date) VALUES (" +
                    specId + ", " + specDetail.getCatMain() + ", " + specDetail.getCatSub() + ", '" +
                    specDetail.getSpecName() + "', " + specDetail.getSpecPrice() + ", '" + spec.getPlace() + "', '" + spec.getDate() + "')");
        }
    }

    /* selectAllSpecs : 사용자가 지정한 연, 월에 대한 모든 메인 내역 ArrayList<Spec>으로 반환 */
    public static ArrayList<Spec> selectAllSpecs(int year, int month) {
        ArrayList<Spec> SpecItems = new ArrayList<>();
        Log.d("AccountBookDB", "selectAllSpecs 호출됨");

        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0); // Calendar 클래스에서 month는 0부터 시작함

        // 날짜가 해당 월의 1일부터 마지막 날 23시 59분까지인 Spec 데이터를 조회
        String startDate = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        String endDate = DATE_DB_FORMAT.format(cal.getTime());

        Cursor SpecCursor = mAccountDB.mDB.rawQuery("SELECT spec_id, type, cat_main, cat_sub, price, place, date" +
                " FROM Spec WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "' ORDER BY date DESC", null);

        int SpecRecordCount = SpecCursor.getCount();
        for (int i = 0; i < SpecRecordCount; i++) {
            SpecCursor.moveToNext();
            int specId = SpecCursor.getInt(0);
            int type = SpecCursor.getInt(1);
            int catMain = SpecCursor.getInt(2);
            int catSub = SpecCursor.getInt(3);
            int price = SpecCursor.getInt(4);
            String place = SpecCursor.getString(5);
            Date date = new Date();
            try {
                date = DATE_DB_FORMAT.parse(SpecCursor.getString(6));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Spec spec = new Spec(specId, type, price, place, catMain, catSub, date);

            // 조회한 Spec 테이블 데이터의 메인 카테고리가 다중인 경우,
            // SpecDetail 테이블에서 같은 spec_id를 foreign key로 갖는 데이터를 함께 반환함
            if (catMain == Spec.CAT_MAIN_MULTI) {
                Log.d("AccountBookDB", "catMain == CAT_MAIN_MULTI");
                Log.d("AccountBookDB", "specId = " + specId);

                Cursor SpecDetailCursor = mAccountDB.mDB.rawQuery("SELECT * FROM SpecDetail " +
                        "WHERE spec_id='" + specId + "'", null);
                int DetailRecordCount = SpecDetailCursor.getCount();
                Log.d("AccountBookDB", "SpecDetailCursor.getCount() : " + DetailRecordCount);

                for (int j = 0; j < DetailRecordCount; j++) {
                    SpecDetailCursor.moveToNext();
                    int specDetailId = SpecDetailCursor.getInt(1);
                    // SpecDetail에 대한 cat_main, cat_sub는 위에서 사용하고 있는 Spec에 대한 변수명과 중복 => 앞에 d를 붙였음
                    int dCatMain = SpecDetailCursor.getInt(2);
                    int dCatSub = SpecDetailCursor.getInt(3);
                    String specName = SpecDetailCursor.getString(4);
                    int specPrice = SpecDetailCursor.getInt(5);

                    SpecDetail specDetailItem = new SpecDetail(specId, specDetailId, dCatMain, dCatSub, specPrice, specName, place, date);
                    spec.addSpecDetail(specDetailItem);
                }
                SpecDetailCursor.close();
            }

            Log.d("AccountBookDB", "selectAllSpecs:" + i + " : " + specId + ", " + catMain + ", " +
                    catSub + ", " + type + ", " + price + ", " + place + ", " + date + ", " + spec.getSpecDetails().size());
            SpecItems.add(spec);
        }
        SpecCursor.close();
        return SpecItems;
    }


    public static ArrayList<Spec> getDaySpec(int year, int month, int day) {
        ArrayList<Spec> specItems = new ArrayList<>();

        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0); // Calendar 클래스에서 month는 0부터 시작함

        // 해당 날짜의 23시 59분 59초까지의 Spec 데이터를 조회
        String startDate = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, day, 23, 59, 59);
        String endDate = DATE_DB_FORMAT.format(cal.getTime());

        Cursor specCursor = mAccountDB.mDB.rawQuery("SELECT spec_id, type, cat_main, cat_sub, price, place, date" +
                " FROM Spec WHERE date BETWEEN '" + startDate + "' AND '" + endDate + "' ORDER BY date DESC", null);

        int specCount = specCursor.getCount();
        for (int i = 0; i < specCount; i++) {
            specCursor.moveToNext();
            int specId = specCursor.getInt(0);
            int type = specCursor.getInt(1);
            int catMain = specCursor.getInt(2);
            int catSub = specCursor.getInt(3);
            int price = specCursor.getInt(4);
            String place = specCursor.getString(5);
            Date date = new Date();
            try {
                date = DATE_DB_FORMAT.parse(specCursor.getString(6));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Spec spec = new Spec(specId, type, price, place, catMain, catSub, date);

            if (catMain == Spec.CAT_MAIN_MULTI) {
                Log.d("AccountBookDB", "catMain == CAT_MAIN_MULTI");
                Log.d("AccountBookDB", "specId = " + specId);

                Cursor specDetailCursor = mAccountDB.mDB.rawQuery("SELECT * FROM SpecDetail " +
                        "WHERE spec_id='" + specId + "'", null);
                int specDetailCount = specDetailCursor.getCount();
                Log.d("AccountBookDB", "SpecDetailCursor.getCount() : " + specDetailCount);

                for (int j = 0; j < specDetailCount; j++) {
                    specDetailCursor.moveToNext();
                    int specDetailId = specDetailCursor.getInt(1);
                    // SpecDetail에 대한 cat_main, cat_sub는 위에서 사용하고 있는 Spec에 대한 변수명과 중복 => 앞에 d를 붙였음
                    int dCatMain = specDetailCursor.getInt(2);
                    int dCatSub = specDetailCursor.getInt(3);
                    String specName = specDetailCursor.getString(4);
                    int specPrice = specDetailCursor.getInt(5);

                    SpecDetail specDetailItem = new SpecDetail(specId, specDetailId, dCatMain, dCatSub, specPrice, specName, place, date);
                    spec.addSpecDetail(specDetailItem);
                }
                specDetailCursor.close();
            }
            Log.d("AccountBookDB", "getDaySpec:" + i + " : " + specId + ", " + catMain + ", " +
                    catSub + ", " + type + ", " + price + ", " + place + ", " + date + ", " + spec.getSpecDetails().size());
            specItems.add(spec);
        }
        specCursor.close();
        return specItems;
    }

    /* getSumOfDay : 지정한 날짜에 대한 소비/지출 합산 금액 포함한 DayInfo 클래스 반환 */
    public static DayInfo getSumOfDay(int year, int month, int day) {

        if (mAccountDB == null) {
            mAccountDB = new AccountBookDB();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0);
        String startTime = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, day, 23, 59);
        String endTime = DATE_DB_FORMAT.format(cal.getTime());

        int incomeSum = 0;
        int expenseSum = 0;
        Cursor SpecCursor = mAccountDB.mDB.rawQuery("SELECT type, price FROM Spec WHERE date BETWEEN '"
                + startTime + "' AND '" + endTime + "'", null);

        int SpecRecordCount = SpecCursor.getCount();
        // 수입합과 지출합을 구함
        for (int i = 0; i < SpecRecordCount; i++) {
            SpecCursor.moveToNext();
            if (SpecCursor.getInt(0) == Spec.TYPE_INCOME) {
                incomeSum += SpecCursor.getInt(1);
            } else {
                expenseSum += SpecCursor.getInt(1);
            }
        }
        return new DayInfo(day, incomeSum, expenseSum);
    }

    /* delete : 인자로 받은 spec_id를 갖는 내역 및 세부 내역을 삭제 */
    public static void delete(int specId) {
        mAccountDB.mDB.execSQL("DELETE FROM Spec WHERE spec_id=" + specId);
        mAccountDB.mDB.execSQL("DELETE FROM SpecDetail WHERE spec_id=" + specId);
        Log.d("AccountBookDB", "delete 호출 : " + specId);
    }

    /* deleteSpecDetail : spedDetailId를 갖는 하나의 세부 내역 삭제 */
    public static void deleteSpecDetail(int specDetailId) {
        mAccountDB.mDB.execSQL("DELETE FROM SpecDetail WHERE spec_detail_id=" + specDetailId);
        Log.d("AccountBookDB", "deleteSpecDetail 호출 : " + specDetailId);
    }

    /* SumForCat : 인자로 연, 월, 카테고리를 받고, 해당 기간에 그 카테고리에 대해 지출한 금액을 합산하여 반환 */
    public static int SumForCat(int year, int month, int cat) {
        int sum = 0;
        Calendar cal = Calendar.getInstance();
        // Calendar 클래스에서 month는 0부터 시작함
        cal.set(year, month - 1, 1, 0, 0, 0);

        // 날짜가 해당 월의 1일부터 마지막 날 23시 59분까지인 Spec, SpecDetail 데이터를 조회
        String startDate = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        String endDate = DATE_DB_FORMAT.format(cal.getTime());

        Cursor SpecCursor = mAccountDB.mDB.rawQuery("SELECT sum(price)" +
                " FROM Spec WHERE (date BETWEEN '" + startDate + "' AND '" + endDate + "') AND (type=" + 1 +
                ") AND (cat_main=" + cat + ")", null);

        if (SpecCursor != null) {
            SpecCursor.moveToFirst();
            sum += SpecCursor.getInt(0);
            SpecCursor.close();
        }

        Cursor SpecDetailCursor = mAccountDB.mDB.rawQuery("SELECT sum(spec_price)" +
                " FROM SpecDetail WHERE (date BETWEEN '" + startDate + "' AND '" + endDate + "') AND (cat_main=" + cat + ")", null);

        if (SpecDetailCursor != null) {
            SpecDetailCursor.moveToFirst();
            sum += SpecDetailCursor.getInt(0);
            SpecDetailCursor.close();
        }
        return sum;
    }

    /* SumForCat : 인자로 연, 월, 카테고리를 받고, 해당 기간에 그 세부 카테고리에 대해 지출한 금액을 합산하여 반환 */
    public static int SumForSubCat(int year, int month, int catMain, int catSub) {
        int sum = 0;
        Calendar cal = Calendar.getInstance();
        // Calendar 클래스에서 month는 0부터 시작함
        cal.set(year, month - 1, 1, 0, 0, 0);

        // 날짜가 해당 월의 1일부터 마지막 날 23시 59분까지인 Spec, SpecDetail 데이터를 조회
        String startDate = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        String endDate = DATE_DB_FORMAT.format(cal.getTime());

        Cursor SpecCursor = mAccountDB.mDB.rawQuery("SELECT sum(price)" +
                " FROM Spec WHERE (date BETWEEN '" + startDate + "' AND '" + endDate + "') AND (type=" + 1 +
                ") AND (cat_main='" + catMain + "') AND (cat_sub= '" + catSub + "')", null);

        if (SpecCursor != null) {
            SpecCursor.moveToFirst();
            sum += SpecCursor.getInt(0);
            SpecCursor.close();
        }

        Cursor SpecDetailCursor = mAccountDB.mDB.rawQuery("SELECT sum(spec_price)" +
                " FROM SpecDetail WHERE (date BETWEEN '" + startDate + "' AND '" + endDate + "') " +
                " AND (cat_main='" + catMain + "') AND (cat_sub= '" + catSub + "')", null);

        if (SpecDetailCursor != null) {
            SpecDetailCursor.moveToFirst();
            sum += SpecDetailCursor.getInt(0);
            SpecDetailCursor.close();
        }
        return sum;
    }

    /* sumAll : 지정한 달의 누적 수입 or 지출 반환 */
    public static int sumAll(int year, int month, int type) {

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0); // Calendar 클래스에서 month는 0부터 시작함

        // 날짜가 해당 월의 1일부터 마지막 날 23시 59분까지인 Spec 데이터를 조회
        String startDate = DATE_DB_FORMAT.format(cal.getTime());
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        String endDate = DATE_DB_FORMAT.format(cal.getTime());

        Cursor c = mAccountDB.mDB.rawQuery("SELECT sum(price)" +
                " FROM Spec WHERE (date BETWEEN '" + startDate + "' AND '" + endDate + "') AND (type ==" + type + ")", null);

        int sum = 0;

        if (c != null) {
            c.moveToFirst();
            sum += c.getInt(0);
            c.close();
        }

        Log.d("테스트", "연" + year + "월" + month + "합계" + sum);
        return sum;
    }

    /* 데이터베이스 헬퍼 클래스 정의 */
    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context) {
            super(context, "testDB.db", null, 1);
        }

        // 데이터베이스가 생성될 때 호출
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("AccountBookDB", "DatabaseHelper 클래스의 onCreate() 메소드 호출");

            String createSpecSQL = "create table if not exists Spec("
                    + "spec_id INTEGER PRIMARY KEY autoincrement, "
                    + "type INTEGER NOT NULL, "
                    + "cat_main INTEGER NOT NULL, "
                    + "cat_sub INTEGER, "
                    + "price INTEGER NOT NULL, "
                    + "place TEXT NOT NULL, "
                    + "date date NOT NULL)";

            String createSpecDetailSQL = "create table if not exists SpecDetail("
                    + "spec_id   INTEGER NOT NULL, "
                    + "spec_detail_id INTEGER PRIMARY KEY autoincrement, "
                    + "cat_main INTEGER NOT NULL, "
                    + "cat_sub INTEGER, "
                    + "spec_name   TEXT NOT NULL, "
                    + "spec_price INTEGER NOT NULL, "
                    + "place TEXT NOT NULL, "
                    + "date date NOT NULL, "
                    + "FOREIGN KEY(spec_id) REFERENCES Spec(spec_id) ON UPDATE CASCADE)";

            db.execSQL(createSpecSQL);
            db.execSQL(createSpecDetailSQL);
        }

        // 데이터베이스가 업그레이드될 때 호출됨
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("AccountBookDB", "DatabaseHelper 클래스의 onUpgrade() 메소드 호출");

            if (newVersion > 1) {
                db.execSQL("DROP TABLE IF EXISTS Spec");
                db.execSQL("DROP TABLE IF EXISTS SpecDetail");
            }
        }

        // 데이터베이스가 열릴 때 호출됨
        @Override
        public void onOpen(SQLiteDatabase db) {
            Log.d("AccountBookDB", "DatabaseHelper 클래스의 onOpen() 메소드 호출");
            super.onOpen(db);
        }
    }
}
