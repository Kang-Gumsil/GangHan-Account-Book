package dankook.kanghyeyoung.capstone_2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class YearMonthPickerDialog extends DialogFragment {
    DatePickerDialog.OnDateSetListener listener;
    NumberPicker mYearPicker;
    NumberPicker mMonthPicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /* view inflate 및 view 참조 */
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View dialog=inflater.inflate(R.layout.dialog_year_month_picker, null);

        mYearPicker=dialog.findViewById(R.id.numberPicker_year);
        mMonthPicker=dialog.findViewById(R.id.numberPicker_month);

        /* 버튼에 clickListener 설정 */
        dialog.findViewById(R.id.button_ok)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet( // 날짜 설정
                        null, mYearPicker.getValue(), mMonthPicker.getValue(), 0);
                YearMonthPickerDialog.this.dismiss(); // 다이얼로그 종료
            }
        });
        dialog.findViewById(R.id.button_cancel)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YearMonthPickerDialog.this.dismiss(); // 다이얼로그 종료
            }
        });

        /* 년/월의 범위 설정 및 현재 선택된 년/월로 초기값 설정 */
        Bundle bundle=getArguments();
        int curYear=bundle.getInt("curYear");
        int selectedYear=bundle.getInt("selectedYear");
        int selectedMonth=bundle.getInt("selectedMonth");

        // 2000년부터 내년까지 설정 가능
        mYearPicker.setMinValue(2000);
        mYearPicker.setMaxValue(curYear+1);
        mYearPicker.setValue(selectedYear);

        // 1월 ~ 12월 설정 가능
        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);
        mMonthPicker.setValue(selectedMonth);

        /* alertDialogBuilder 생성 및 view 설정 */
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(dialog);

        /* 다이얼로그 생성 및 리턴 */
        return builder.create();
    }

    /* dialog에 listener 설정 */
    public void setDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener=listener;
    }
}
