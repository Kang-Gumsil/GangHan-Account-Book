package dankook.kanghyeyoung.capstone_2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Spec implements Serializable {

    public static String TYPE_CLASS[]={"수입", "지출"};
    public static final int TYPE_INCOME=0;
    public static final int TYPE_EXPENSE=1;

    public static final String CAT_MAIN_CLASS[]={"수입", "식비", "패션미용", "생활용품", "교통차량",
                                        "주거통신", "교육", "취미여행", "건강", "관계",
                                        "유흥", "금융", "가전디지털", "인테리어", "기타", "다중"};
    public static final int CAT_MAIN_INCOME=0;
    public static final int CAT_MAIN_FOOD=1;
    public static final int CAT_MAIN_FASHION_BEAUTY=2;
    public static final int CAT_MAIN_HOUSEHOLDITEM=3;
    public static final int CAT_MAIN_TRANSPORT =4;
    public static final int CAT_MAIN_LIVING_COMMUNI=5;
    public static final int CAT_MAIN_EDUCATION=6;
    public static final int CAT_MAIN_HOBBY_TRAVEL=7;
    public static final int CAT_MAIN_HEALTH=8;
    public static final int CAT_MAIN_RELATIONSHIP=9;
    public static final int CAT_MAIN_ENTERTAIN=10;
    public static final int CAT_MAIN_FINANCE=11;
    public static final int CAT_MAIN_DIGITAL=12;
    public static final int CAT_MAIN_INTERIOR=13;
    public static final int CAT_MAIN_OTHER=14;
    public static final int CAT_MAIN_MULTI=15;
    public static final int COUNT_EXPENSE_CAT_MAIN=15;

    public static final String CAT_SUB_CLASS[][]={
            {},
            {"장보기", "외식", "카페/베이커리"},
            {"패션", "미용"},
            {"욕실용품", "세탁용품", "위생용품", "주방용품", "문구", "기타"},
            {"차량", "지하철/버스", "택시", "기차", "비행기", "기타"},
            {"통신비", "관리비", "공과금", "월세/이자", "기타"},
            {"등록금", "교재", "기타"},
            {"여행", "문화", "레저", "게임", "체험", "기타"},
            {"약국", "병원", "건강식품", "기타"},
            {"선물", "경조사비", "기타"},
            {},
            {"보험", "저축", "투자", "기타"},
            {"가전/디지털", "렌탈", "기타"},
            {},
            {},
            {},
    };
    public static final int CAT_SUB_FOOD_SHOPPING=0;
    public static final int CAT_SUB_FOOD_EATOUT=1;
    public static final int CAT_SUB_FOOD_CAFE_BAKERY=2;
    public static final int COUNT_CAT_SUB_FOOD=3;

    public static final int CAT_SUB_FASHION_BEAUTY_FASHION=0;
    public static final int CAT_SUB_FASHION_BEAUTY_BEAUTY=1;
    public static final int COUNT_CAT_SUB_FASHION_BEAUTY=2;

    public static final int CAT_SUB_HOUSEHOLDITEM_BATH=0;
    public static final int CAT_SUB_HOUSEHOLDITEM_LAUNDRY=1;
    public static final int CAT_SUB_HOUSEHOLDITEM_HYGIENE=2;
    public static final int CAT_SUB_HOUSEHOLDITEM_KITCHEN=3;
    public static final int CAT_SUB_HOUSEHOLDITEM_STATIONERY=4;
    public static final int CAT_SUB_HOUSEHOLDITEM_OTHER=5;
    public static final int COUNT_CAT_SUB_HOUSEHOLDITEM=6;

    public static final int CAT_SUB_TRANSPORT_CAR=0;
    public static final int CAT_SUB_TRANSPORT_SUBWAY_BUS=1;
    public static final int CAT_SUB_TRANSPORT_TAXI=2;
    public static final int CAT_SUB_TRANSPORT_TRAIN=3;
    public static final int CAT_SUB_TRANSPORT_PLAIN=4;
    public static final int CAT_SUB_TRANSPORT_OTHER=5;
    public static final int COUNT_CAT_SUB_TRANSPORT=6;

    public static final int CAT_SUB_LIVING_COMMUNI_COMMUNI=0;
    public static final int CAT_SUB_LIVING_COMMUNI_MAINTENANCE=1;
    public static final int CAT_SUB_LIVING_COMMUNI_UTILITY_BILL=2;
    public static final int CAT_SUB_LIVING_COMMUNI_RENT_INTEREST=3;
    public static final int CAT_SUB_LIVING_COMMUNI_OTHER=4;
    public static final int COUNT_CAT_SUB_LIVING_COMMUNI=5;

    public static final int CAT_SUB_EDUCATION_TUITION=0;
    public static final int CAT_SUB_EDUCATION_TEXTBOOK=1;
    public static final int CAT_SUB_EDUCATION_OTHER=2;
    public static final int COUNT_CAT_SUB_EDUCATION=3;

    public static final int CAT_SUB_HOBBY_TRAVEL_TRAVEL=0;
    public static final int CAT_SUB_HOBBY_TRAVEL_CULTURE=1;
    public static final int CAT_SUB_HOBBY_TRAVEL_LEISURE=2;
    public static final int CAT_SUB_HOBBY_TRAVEL_GAME=3;
    public static final int CAT_SUB_HOBBY_TRAVEL_EXPERIENCE=4;
    public static final int CAT_SUB_HOBBY_TRAVEL_OTHER=5;
    public static final int COUNT_CAT_SUB_HOBBY_TRAVEL=6;

    public static final int CAT_SUB_HEALTH_PHARMACY=0;
    public static final int CAT_SUB_HEALTH_HOSPITAL=1;
    public static final int CAT_SUB_HEALTH_FOOD=2;
    public static final int CAT_SUB_HEALTH_OTHER=3;
    public static final int COUNT_CAT_SUB_HEALTH=4;

    public static final int CAT_SUB_RELATIONSHIP_GIFT=0;
    public static final int CAT_SUB_RELATIONSHIP_EVENT=1;
    public static final int CAT_SUB_RELATIONSHIP_OTHER=2;
    public static final int COUNT_CAT_SUB_RELATIONSHIP=3;

    public static final int CAT_SUB_FINANCE_INSURANCE=0;
    public static final int CAT_SUB_FINANCE_SAVING=1;
    public static final int CAT_SUB_FINANCE_INVEST=2;
    public static final int CAT_SUB_FINANCE_OTHER=3;
    public static final int COUNT_CAT_SUB_FINANCE=4;

    public static final int CAT_SUB_DIGITAL_APPLIANCE_DIGITAL=0;
    public static final int CAT_SUB_DIGITAL_RENTAL=1;
    public static final int CAT_SUB_DIGITAL_OTHER=2;
    public static final int COUNT_CAT_SUB_DIGITAL=3;

    public static final int COUNT_CAT_SUB_ENTERTAIN=0;
    public static final int COUNT_CAT_SUB_INTERIOR=0;
    public static final int COUNT_CAT_SUB_OTHER=0;

    public static final int CAT_SUB_ENTERTAIN=-1;
    public static final int CAT_SUB_INTERIOR=-1;
    public static final int CAT_SUB_OTHER=-1;
    public static final int CAT_SUB_MULTI=-1;
    public static final int CAT_SUB_INCOME=0;



    ArrayList<SpecDetail> mSpecDetails=new ArrayList<>();

    private int mSpecId;
    private int mType;
    private int mPrice;
    private String mPlace;
    private int mCatMain;
    private int mCatSub;
    private Date mDate;

    public Spec(int type, int price, String place, int catMain, int catSub, Date date) {
        this.mType = type;
        this.mPrice = price;
        this.mPlace = place;
        this.mCatMain = catMain;
        this.mCatSub = catSub;
        this.mDate = date;
    }

    public Spec(int specId, int type, int price, String place, int catMain, int catSub, Date date) {
        this.mSpecId = specId;
        this.mType = type;
        this.mPrice = price;
        this.mPlace = place;
        this.mCatMain = catMain;
        this.mCatSub = catSub;
        this.mDate = date;
    }

    /* mSpecDetails에 SpecDetail 객체를 추가 */
    public void addSpecDetail(SpecDetail specDetail) {
        mSpecDetails.add(specDetail);
    }

    /* 메인 카테고리와 서브 카테고리를 문자열로 반환 (ex: 식비-장보기, 술/유흥 ) */
    public String getCatStr() {
        if (mCatMain != CAT_MAIN_INCOME && mCatMain != CAT_MAIN_ENTERTAIN &&
                mCatMain != CAT_MAIN_INTERIOR && mCatMain != CAT_MAIN_OTHER &&
                mCatMain != CAT_MAIN_MULTI) {
            return Spec.CAT_MAIN_CLASS[mCatMain] + "-" + Spec.CAT_SUB_CLASS[mCatMain][mCatSub];
        } else {
            return Spec.CAT_MAIN_CLASS[mCatMain];
        }
    }

    /* getter */
    public int getSpecId() {
        return mSpecId;
    }

    public int getType() {
        return mType;
    }

    public int getPrice() {
        return mPrice;
    }

    public String getPlace() {
        return mPlace;
    }

    public int getCatMain() {
        return mCatMain;
    }

    public int getCatSub() {
        return mCatSub;
    }

    public Date getDate() {
        return mDate;
    }

    public ArrayList<SpecDetail> getSpecDetails() {
        return mSpecDetails;
    }

    public void setSpecDetail(int index, SpecDetail specDetail) {
        this.mSpecDetails.set(index,specDetail);
    }

    public void setSpecDetails(ArrayList<SpecDetail> mSpecDetails) {
        this.mSpecDetails = mSpecDetails;
    }

    /* setter */
    public void setType(int type) {
        this.mType = type;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public void setCatMain(int catMain) {
        this.mCatMain = catMain;
    }

    public void setCatSub(int catSub) {
        this.mCatSub = catSub;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    static public String getCatMainName(int catMain) {
        return CAT_MAIN_CLASS[catMain];
    }
    static public String getCatSubName(int catMain, int catSub) {
        return CAT_SUB_CLASS[catMain][catSub];
    }
    static public int getCatSubCount(int catMain) {

        switch (catMain) {
            case CAT_MAIN_FOOD :
                return COUNT_CAT_SUB_FOOD;

            case CAT_MAIN_FASHION_BEAUTY :
                return COUNT_CAT_SUB_FASHION_BEAUTY;

            case CAT_MAIN_HOUSEHOLDITEM :
                return COUNT_CAT_SUB_HOUSEHOLDITEM;

            case CAT_MAIN_TRANSPORT :
                return COUNT_CAT_SUB_TRANSPORT;

            case CAT_MAIN_LIVING_COMMUNI :
                return COUNT_CAT_SUB_LIVING_COMMUNI;

            case CAT_MAIN_EDUCATION :
                return COUNT_CAT_SUB_EDUCATION;

            case CAT_MAIN_HOBBY_TRAVEL :
                return COUNT_CAT_SUB_HOBBY_TRAVEL;

            case CAT_MAIN_HEALTH :
                return COUNT_CAT_SUB_HEALTH;

            case CAT_MAIN_RELATIONSHIP :
                return COUNT_CAT_SUB_RELATIONSHIP;

            case CAT_MAIN_FINANCE :
                return COUNT_CAT_SUB_FINANCE;

            case CAT_MAIN_DIGITAL :
                return COUNT_CAT_SUB_DIGITAL;

            default :
                return 0;
        }
    }

}
