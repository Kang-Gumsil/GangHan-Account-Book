package dankook.kanghyeyoung.capstone_2;

import androidx.annotation.Nullable;

public class DayInfo {
    int mDay;
    int mIncome;
    int mExpense;

    public DayInfo(@Nullable int day, @Nullable int income, @Nullable int expense) {
        this.mDay = day;
        this.mIncome = income;
        this.mExpense = expense;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }

    public int getIncome() {
        return mIncome;
    }

    public void setIncome(int mIncome) {
        this.mIncome = mIncome;
    }

    public int getExpense() {
        return mExpense;
    }

    public void setExpense(int mExpense) {
        this.mExpense = mExpense;
    }
}
