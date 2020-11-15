package dankook.kanghyeyoung.capstone_2;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_TIME_FORMAT;

public class DateTimePickerDialog {
    private final static String TAG = "DateTimePickerDialog";

    private Context mContext;
    private Calendar mCalendar;
    private int mMaxDay;

    public DateTimePickerDialog(Context context) {
        this.mContext = context;
    }

    /* 선택한 날짜로부터 요일 구함 */
    private static String getDayOfWeek(Calendar cal){
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr;
        switch (dayOfWeek) {
            case 1:
                dayOfWeekStr = "(일)";
                break;
            case 2:
                dayOfWeekStr = "(월)";
                break;
            case 3:
                dayOfWeekStr = "(화)";
                break;
            case 4:
                dayOfWeekStr = "(수)";
                break;
            case 5:
                dayOfWeekStr = "(목)";
                break;
            case 6:
                dayOfWeekStr = "(금)";
                break;
            case 7:
                dayOfWeekStr = "(토)";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dayOfWeek);
        }
        return dayOfWeekStr;
    }

    /* 인자로 받은 Calendar 객체의 정보로 날짜 텍스트뷰 설정 */
    public static void setDate(Calendar cal, TextView textViewDate) {
        String date = DATE_TIME_FORMAT.format(cal.getTime());
        textViewDate.setText(date);
    }

    /* 다이얼로그 호출 함수를 정의 */
    public void callFunction(final TextView textViewDate, Calendar cal) {

        this.mCalendar = cal;
        // 커스텀 다이얼로그 정의하기 위해 Dialog 클래스 생성
        final Dialog dialog = new Dialog(mContext);

        // 커스텀 다이얼로그의 레이아웃을 설정
        dialog.setContentView(R.layout.dialog_date_time_picker);

        // 커스텀 다이얼로그의 각 위젯들을 참조
        final NumberPicker PickerYear = dialog.findViewById(R.id.picker_year);
        final NumberPicker PickerMonth = dialog.findViewById(R.id.picker_month);
        final NumberPicker PickerDay = dialog.findViewById(R.id.picker_day);
        final NumberPicker PickerHour = dialog.findViewById(R.id.picker_hour);
        final NumberPicker PickerMinute = dialog.findViewById(R.id.picker_minute);
        final TextView TextViewDayOfWeek = dialog.findViewById(R.id.textView_dayofweek);
        final Button ButtonConfirm = dialog.findViewById(R.id.button_confirm);
        final Button ButtonCancel = dialog.findViewById(R.id.button_cancel);

        // 인자로 받은 Calendar 객체의 날짜 정보 얻기
        int curYear = mCalendar.get(Calendar.YEAR);
        int curMonth = mCalendar.get(Calendar.MONTH) + 1;
        int curDay = mCalendar.get(Calendar.DATE);
        int curHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = mCalendar.get(Calendar.MINUTE);

        // 2000년도부터 내년까지 선택할 수 있음, 처음 기본값은 올해
        PickerYear.setMinValue(2000);
        PickerYear.setMaxValue(curYear + 1);
        PickerYear.setValue(curYear);

        // 1월 ~ 12월, 처음 기본값은 이번 달
        PickerMonth.setMinValue(1);
        PickerMonth.setMaxValue(12);
        PickerMonth.setValue(curMonth);

        // 1일 ~ mCal 상의 달의 마지막 날, 기본값은 오늘
        mMaxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        PickerDay.setMinValue(1);
        PickerDay.setMaxValue(mMaxDay);
        PickerDay.setValue(curDay);

        // 0시 ~ 23시, 기본값은 현재 시간
        PickerHour.setMinValue(0);
        PickerHour.setMaxValue(23);
        PickerHour.setValue(curHour);

        // 0분 ~ 59분
        PickerMinute.setMinValue(0);
        PickerMinute.setMaxValue(59);
        PickerMinute.setValue(curMinute);

        // mCalendar 객체의 년, 월, 일을 NumberPicker에서 선택한 값으로 설정
        mCalendar.set(PickerYear.getValue(), PickerMonth.getValue() - 1, PickerDay.getValue());
        TextViewDayOfWeek.setText(getDayOfWeek(mCalendar));

        // picker_year 값이 바뀌면 DayOfMonth 최대값, 요일도 변하도록 설정
        PickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, Integer.toString(PickerYear.getValue()));
                // cal객체의 년, 월, 일을 NumberPicker로 선택한 값으로 설정 (Calendar 클래스에서 월은 0부터 시작)
                mCalendar.set(PickerYear.getValue(), PickerMonth.getValue() - 1, PickerDay.getValue());
                mMaxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                PickerDay.setMaxValue(mMaxDay);
                TextViewDayOfWeek.setText(getDayOfWeek(mCalendar));
            }
        });

        // picker_month 값 바뀌면 DayOfMonth 최대값, 요일도 변하도록 설정
        PickerMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, Integer.toString(PickerMonth.getValue()));
                // cal객체의 년, 월, 일을 NumberPicker로 선택한 값으로 설정
                mCalendar.set(PickerYear.getValue(), PickerMonth.getValue() - 1, PickerDay.getValue());
                mMaxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                PickerDay.setMaxValue(mMaxDay);
                TextViewDayOfWeek.setText(getDayOfWeek(mCalendar));
            }
        });

        // picker_day 값 바뀌면 요일 변하도록 설정
        PickerDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, Integer.toString(PickerDay.getValue()));
                mCalendar.set(PickerYear.getValue(), PickerMonth.getValue() - 1, PickerDay.getValue()); // cal객체의 년, 월, 일을 NumberPicker로 선택한 값으로 설정
                TextViewDayOfWeek.setText(getDayOfWeek(mCalendar));
            }
        });

        // 확인 버튼 선택 시
        ButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "확인 버튼 클릭");
                // 직접 입력 화면의 날짜 텍스트 뷰의 내용을 선택한 날짜로 설정
                mCalendar.set(PickerYear.getValue(), PickerMonth.getValue() - 1, PickerDay.getValue(),
                        PickerHour.getValue(), PickerMinute.getValue());
                setDate(mCalendar, textViewDate);
                // 커스텀 다이얼로그 종료
                dialog.dismiss();
            }
        });

        // 취소 버튼 선택 시
        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "취소 버튼 클릭");
                // 커스텀 다이얼로그 종료
                dialog.dismiss();
            }
        });

        // 커스텀 다이얼로그를 노출
        dialog.show();
    }
}
