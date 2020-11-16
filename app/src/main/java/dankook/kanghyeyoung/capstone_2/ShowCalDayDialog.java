package dankook.kanghyeyoung.capstone_2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.getDaySpec;
import static dankook.kanghyeyoung.capstone_2._FORMAT.YEAR_MONTH_FORMAT;

public class ShowCalDayDialog extends Dialog {
    private final static String TAG = "ShowCalDayDialog";

    private Context mContext;
    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;
    private RecyclerView mRecyclerView;
    private SpecAdapter mSpecAdapter;

    public ShowCalDayDialog(@NonNull Context context, int year, int month, int day) {
        super(context);
        mContext=context;
        mSelectedYear=year;
        mSelectedMonth=month;
        mSelectedDay=day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ShowCalDayDialog 생성");

        /* layout 적용 */
        setContentView(R.layout.dialog_show_cal_day);

        /* view 참조 */
        TextView textViewCatMain = findViewById(R.id.textView_date);
        mRecyclerView = findViewById(R.id.recyclerView);

        /* main_cat 텍스트뷰 설정 */
        Calendar calendar=Calendar.getInstance();
        calendar.set(mSelectedYear, mSelectedMonth-1, mSelectedDay);
        textViewCatMain.setText(YEAR_MONTH_FORMAT.format(new Date(calendar.getTimeInMillis())));

        /* specAdapter, onItemClickListener 설정 */
        mSpecAdapter = new SpecAdapter(mContext, true);
        mSpecAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Spec item = mSpecAdapter.getItem(position);
                Intent intent = new Intent(mContext, ShowSpecActivity.class);
                intent.putExtra("spec_item", item);
                getContext().startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mSpecAdapter);

        /* recyclerView에 레이아웃 추가 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /* recyclerView에 아이템 추가 */
        for(Spec item : getDaySpec(mSelectedYear, mSelectedMonth, mSelectedDay)) {
            mSpecAdapter.addItem(item);
        }
        mSpecAdapter.notifyDataSetChanged();

        /* 확인 버튼 설정 */
        Button button = findViewById(R.id.button_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
