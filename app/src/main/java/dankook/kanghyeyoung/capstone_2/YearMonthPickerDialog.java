package dankook.kanghyeyoung.capstone_2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class YearMonthPickerDialog extends Dialog {
    private DatePickerDialog.OnDateSetListener listener;
    private NumberPicker mYearPicker;
    private NumberPicker mMonthPicker;
    private int mCurYear;
    private int mSelectedYear;
    private int mSelectedMonth;

    public YearMonthPickerDialog(@NonNull Context context, int curYear, int year, int month) {
        super(context);

        mCurYear=curYear;
        mSelectedYear=year;
        mSelectedMonth=month;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* layout 적용 */
        setContentView(R.layout.dialog_year_month_picker);

        /* view 참조 */
        mYearPicker=findViewById(R.id.numberPicker_year);
        mMonthPicker=findViewById(R.id.numberPicker_month);
        Button button_ok=findViewById(R.id.button_ok);
        Button button_cancel=findViewById(R.id.button_cancel);

        /* 버튼에 clickListener 설정 */
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet( // 날짜 설정
                        null, mYearPicker.getValue(), mMonthPicker.getValue(), 0);
                YearMonthPickerDialog.this.dismiss(); // 다이얼로그 종료
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog.this.dismiss(); // 다이얼로그 종료
            }
        });

        // 2000년부터 내년까지 설정 가능
        mYearPicker.setMinValue(2000);
        mYearPicker.setMaxValue(mCurYear+1);
        mYearPicker.setValue(mSelectedYear);

        // 1월 ~ 12월 설정 가능
        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);
        mMonthPicker.setValue(mSelectedMonth);
    }

    /* dialog에 listener 설정 */
    public void setDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener=listener;
    }
}
