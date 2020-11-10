package dankook.kanghyeyoung.capstone_2;

import java.io.Serializable;
import java.util.Date;

import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_ENTERTAIN;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INCOME;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INTERIOR;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_MULTI;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_OTHER;

public class SpecDetail implements Serializable {

    private int mSpecId;
    private int mSpecDetailId;
    private int mCatMain;
    private int mCatSub;
    private String mSpecName;
    private int mSpecPrice;
    private String mPlace;
    private Date mDate;

    public SpecDetail(int specId, int specDetailId, int catMain, int catSub,
                      int specPrice, String specName, String place, Date date) {
        this.mSpecId = specId;
        this.mSpecDetailId = specDetailId;
        this.mCatMain = catMain;
        this.mCatSub = catSub;
        this.mSpecPrice = specPrice;
        this.mSpecName = specName;
        this.mPlace = place;
        this.mDate = date;
    }

    public SpecDetail(int catMain, int catSub, String specName, int specPrice) {
        this.mCatMain = catMain;
        this.mCatSub = catSub;
        this.mSpecName = specName;
        this.mSpecPrice = specPrice;
    }

    public SpecDetail(int catMain, String specName, int specPrice) {
        this.mCatMain = catMain;
        this.mSpecName = specName;
        this.mSpecPrice = specPrice;
    }

    /* 메인 카테고리와 서브 카테고리를 문자열로 반환 (ex: 식비-장보기, 술/유흥 ) */
    public String getCatStr() {
        if(mCatMain==-1) {
            return "미분류";
        }
        if(mCatMain!=CAT_MAIN_INCOME && mCatMain!=CAT_MAIN_ENTERTAIN &&
                mCatMain!=CAT_MAIN_INTERIOR && mCatMain!=CAT_MAIN_OTHER &&
                mCatMain!=CAT_MAIN_MULTI) {
            return Spec.CAT_MAIN_CLASS[mCatMain]+"-"+Spec.CAT_SUB_CLASS[mCatMain][mCatSub];
        } else {
            return Spec.CAT_MAIN_CLASS[mCatMain];
        }
    }

    /* getter */
    public int getSpecDetailId() {
        return mSpecDetailId;
    }

    public int getCatMain() {
        return mCatMain;
    }

    public int getCatSub() {
        return mCatSub;
    }

    public String getSpecName() {
        return mSpecName;
    }

    public int getSpecPrice() {
        return mSpecPrice;
    }

    /* setter */
    public void setSpecId(int specId) {
        this.mSpecId = specId;
    }

    public void setCatMain(int catMain) {
        this.mCatMain = catMain;
    }

    public void setCatSub(int catSub) {
        this.mCatSub = catSub;
    }

    public void setSpecName(String specName) {
        this.mSpecName = specName;
    }

    public void setSpecPrice(int specPrice) {
        this.mSpecPrice = specPrice;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}
