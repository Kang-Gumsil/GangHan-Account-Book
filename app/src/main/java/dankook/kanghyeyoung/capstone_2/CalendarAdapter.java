package dankook.kanghyeyoung.capstone_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class CalendarAdapter extends BaseAdapter {
    ArrayList<DayInfo> mDayInfos;

    /* 리스너 객체 참조를 저장하는 변수 */
    private OnItemClickListener mItemClickListener=null;

    /* onItemClickListener 객체 참조를 어댑터에 전달하는 메서드 */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener=listener;
    }

    public CalendarAdapter(ArrayList<DayInfo> dayInfos) {
        mDayInfos = dayInfos;
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
        final int position=i;
        DayInfo dayInfo = mDayInfos.get(position);

        /* 해당 그리드가 비었으면 view_item_cal 인플레이트 */
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.view_item_cal, viewGroup, false);
        }

        /* textView에 날짜와 수입/지출 설정 */
        TextView textView_day = view.findViewById(R.id.textView_day);
        TextView textView_income = view.findViewById(R.id.textView_income);
        TextView textView_expense = view.findViewById(R.id.textView_expense);

        if (dayInfo == null) {
            textView_day.setText(" ");
            textView_expense.setText(" ");
            textView_income.setText(" ");
            view.setClickable(false);

        } else {
            textView_day.setText(Integer.toString(dayInfo.getDay()));
            textView_expense.setText(DECIMAL_FORMAT.format(dayInfo.getExpense()));
            textView_income.setText(DECIMAL_FORMAT.format(dayInfo.getIncome()));

            /* click listener 설정 */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null) {
                        mItemClickListener.onItemClick(v, position);
                    }
                }
            });
        }

        return view;
    }
}
