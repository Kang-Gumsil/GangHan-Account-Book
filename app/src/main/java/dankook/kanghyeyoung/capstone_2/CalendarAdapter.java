package dankook.kanghyeyoung.capstone_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
    ArrayList<DayInfo> mDayInfos;
    float mItemHeight;

    public CalendarAdapter(ArrayList<DayInfo> dayInfos, float itemHeight) {
        mDayInfos = dayInfos;
        mItemHeight = itemHeight;
    }

    @Override
    public int getCount() {
        return mDayInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mDayInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DayInfo dayInfo = mDayInfos.get(i);

        /* 해당 그리드가 비었으면 view_item_cal 인플레이트 */
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.view_item_cal, viewGroup, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) mItemHeight;
            view.setLayoutParams(params);
        }

        /* textView에 날짜와 수입/지출 설정 */
        TextView textView_day = view.findViewById(R.id.textView_day);
        TextView textView_income = view.findViewById(R.id.textView_income);
        TextView textView_expense = view.findViewById(R.id.textView_expense);

        if (dayInfo == null) {
            textView_day.setText(" ");
            textView_expense.setText(" ");
            textView_income.setText(" ");

        } else {
            textView_day.setText(Integer.toString(dayInfo.getDay()));
            textView_expense.setText(Integer.toString(dayInfo.getExpense()));
            textView_income.setText(Integer.toString(dayInfo.getIncome()));
        }

        return view;
    }
}
