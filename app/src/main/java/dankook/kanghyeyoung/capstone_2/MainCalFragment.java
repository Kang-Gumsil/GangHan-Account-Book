package dankook.kanghyeyoung.capstone_2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.getSumOfDay;

/*
    Calendar 객체,,,
    1. month
       0 : 1월
       1 : 2월
       2 : 3월 ...

    2. dayOfWeek (요일)
       1 : 일요일
       2 : 월요일
       3 : 화요일 ...
 */

public class MainCalFragment extends Fragment implements MainFragment {
    private static final String TAG="MainCalFragment";

    CalendarAdapter mCalendarAdapter;
    Calendar mCalendar;
    Context mContext;
    GridView mGridView;
    SummaryView mSummaryView;
    MainActivity activity;

    int mCurYear;
    int mCurMonth;
    int mSelectedYear;
    int mSelectedMonth;

    /* DatePicker 리스너 정의 */
    DatePickerDialog.OnDateSetListener mDialogListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
            activity.updateSelectedDate(i, i1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /* layout inflate 및 context, activity 얻기 */
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_cal, container, false);
        mContext=rootView.getContext();
        activity=(MainActivity) getActivity();;

        /* view 참조 */
        mGridView = rootView.findViewById(R.id.gridView);

        /* 현재 날짜 설정 */
        Calendar calendar = Calendar.getInstance();
        mCurYear = calendar.get(Calendar.YEAR);
        mCurMonth = calendar.get(Calendar.MONTH) + 1; // 캘린더의 month는 0이 1월, 1이 2월

        /* summaryView 설정  */
        mSummaryView = new SummaryView(mContext);
        FrameLayout frameLayout = rootView.findViewById(R.id.frameLayout);
        frameLayout.addView(mSummaryView);

        // 초기에는 선택된 날짜를 현재 날짜로 설정
        updateSelectedDate(mCurYear, mCurMonth);

        // summaryView의 날짜 설정 버튼에 onClickListener 등록
        mSummaryView.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 현재 선택된 년/월을 DatePicker 프래그먼트로 보내기 위한 번들 생성
                Bundle bundle = new Bundle();
                bundle.putInt("curYear", mCurYear);
                bundle.putInt("selectedYear", mSelectedYear);
                bundle.putInt("selectedMonth", mSelectedMonth);

                // datePickerDialog 생성 및 show
                YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog();
                yearMonthPickerDialog.setArguments(bundle);
                yearMonthPickerDialog.setDateSetListener(mDialogListener);
                yearMonthPickerDialog.show(getChildFragmentManager(), "datePicker");
            }
        });

        return rootView;
    }

    /* 해당 년/월의 캘린더 생성 */
    private void getCalendar(int year, int month) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(year, month-1, 1);
    }

    /* 밑의 메소드 호출 */
    public void updateSelectedDate() {
        updateSelectedDate(mSelectedYear, mSelectedMonth);
    }

    /* 설정된 년/월 변경 및 calendar 갱신 */
    public void updateSelectedDate(int year, int month) {
        Log.d(TAG, "The date has been selected, or data has been changed.");

        mSelectedYear = year;
        mSelectedMonth = month;

        mSummaryView.mTextViewYear.setText(mSelectedYear + "년");
        mSummaryView.mTextViewMonth.setText(mSelectedMonth + "월");
        mSummaryView.showSummary(mSelectedYear, mSelectedMonth);
        Log.d(TAG, "The summary view has been updated.");

        if (mGridView!=null) {

            /* 캘린더 생성 후 년/월 설정 */
            getCalendar(mSelectedYear, mSelectedMonth);
            int firstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK); // 해당 달 1일의 요일
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 달의 일 수
            int lastWeekOfMonth = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH); // 해당 달의 주 수
            int lastDayOfWeek=-1; // 해당 달 마지막 일의 요일

            /* 해당 달의 날짜 리스트 만들기 -> Day(날짜, 요일, 수입, 지출) */
            ArrayList<DayInfo> dayInfos = new ArrayList<DayInfo>();

            /* 달력 채우기 */
            // 1일 이전 칸 채우기
            for (int i = 1; i <= firstDayOfWeek-1; i++) { // 2가 월요일
                dayInfos.add(null);
            }

            // 1일 ~ 마지막날 칸 채우기
            for (int i = 1, j = firstDayOfWeek; i <= lastDayOfMonth; i++, j++) {
                DayInfo dayInfo = getSumOfDay(mSelectedYear, mSelectedMonth, i);
                dayInfos.add(dayInfo);
                lastDayOfWeek=(j-1) % 7; // 0 : 일요일, 1 : 월요일, 2 : 화요일,,,
            }

            // 마지막날 이후 칸 채우기
            for(int i=lastDayOfWeek+1;i<7;i++) {
                dayInfos.add(null);
            }

            // gridView의 열 개수를 해당 달의 주 수만큼으로 수정하기
            // 5주면 셀의 개수를 35개로, 6주면 42개로 설정하기
            float itemHeight=0;
            float gridViewHeight=8;
            if (lastWeekOfMonth == 5) {
                mGridView.setNumColumns(35);
                itemHeight=288;

            } else if (lastWeekOfMonth == 6) {
                mGridView.setNumColumns(42);
                itemHeight=240;
            }

            // gridView의 행 개수를 7로 설정
            mGridView.setNumColumns(7);

            /* 캘린더 어댑터에 해당 달의 날짜 리스트, 클릭 리스너 설정 */
            mCalendarAdapter = new CalendarAdapter(dayInfos, itemHeight);
            mCalendarAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    int day=((DayInfo)mCalendarAdapter.getItem(position)).getDay();
                    ShowCalDayDialog dialog =
                            new ShowCalDayDialog(getContext(), mSelectedYear, mSelectedMonth, day);
                    dialog.show();
                }
            });
            mGridView.setAdapter(mCalendarAdapter);
        }
        Log.d(TAG, "The data in the grid view has been updated.");
    }
}