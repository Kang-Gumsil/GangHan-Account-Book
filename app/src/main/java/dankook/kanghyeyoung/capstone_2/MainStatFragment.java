package dankook.kanghyeyoung.capstone_2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import static dankook.kanghyeyoung.capstone_2.Spec.COUNT_EXPENSE_CAT_MAIN;
import static dankook.kanghyeyoung.capstone_2._COLOR.COLOR_TEXT_INT;

public class MainStatFragment extends Fragment implements MainFragment {
    private static final String TAG = "MainStatFragment";

    PieChart mChart;
    Context mContext;
    MainActivity activity;
    SummaryView mSummaryView;
    RecyclerView mRecyclerView;
    StatMainCatListAdapter mStatMainCatListAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /* layout inflate 및 context, activity 얻기 */
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_stat, container, false);
        mContext = rootView.getContext();
        activity = (MainActivity) getActivity();

        /* 현재 날짜 설정 */
        Calendar calendar = Calendar.getInstance();
        mCurYear = calendar.get(Calendar.YEAR);
        mCurMonth = calendar.get(Calendar.MONTH) + 1; // 캘린더의 month는 0이 1월, 1이 2월

        /* summaryView 설정  */
        mSummaryView = new SummaryView(mContext);
        FrameLayout frameLayout = rootView.findViewById(R.id.frameLayout);
        frameLayout.addView(mSummaryView);

        // summaryView의 날짜 설정 버튼에 onClickListener 등록
        mSummaryView.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // datePickerDialog 생성 및 show
                YearMonthPickerDialog dialog =
                        new YearMonthPickerDialog(getContext(), mCurYear, mSelectedYear, mSelectedMonth);
                dialog.setDateSetListener(mDialogListener);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        /* view 참조 */
        mChart = rootView.findViewById(R.id.pieChart);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        /* 파이차트 모양 설정 */
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 5, 5, 5);
        mChart.setDrawHoleEnabled(true);
        mChart.getLegend().setEnabled(false);

        mChart.setEntryLabelColor(COLOR_TEXT_INT);
        mChart.setEntryLabelTextSize(16f);
        mChart.setEntryLabelTypeface(getResources().getFont(R.font.nanum_square_ac_r));

        /* StatListAdapter, onItemClickListener 설정 */
        mStatMainCatListAdapter = new StatMainCatListAdapter(mSelectedYear, mSelectedMonth);
        mStatMainCatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int catMain = mStatMainCatListAdapter.getItem(position).getCat();
                Log.d(TAG, catMain + "(" + Spec.getCatMainName(catMain) + ")" + " 카테고리 선택, 총 금액 : "
                        + AccountBookDB.SumForCat(mSelectedYear, mSelectedMonth, catMain));

                if (Spec.getCatSubCount(catMain) != 0) {
                    ShowStatSubCatDialog dialog =
                            new ShowStatSubCatDialog(getContext(), catMain, mSelectedYear, mSelectedMonth);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                } else {
                    showToast(Spec.getCatMainName(catMain) + " 카테고리는 세부 카테고리가 없습니다.");
                }
            }
        });
        mRecyclerView.setAdapter(mStatMainCatListAdapter);

        /* recyclerView에 레이아웃 설정 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /* recyclerView 아이템간 구분선 추가 */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(),
                        new LinearLayoutManager(getContext()).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* 데이터 추가 */
        updateSelectedDate(mCurYear, mCurMonth);

        return rootView;
    }

    /* 밑의 메소드 호출 */
    public void updateSelectedDate() {
        updateSelectedDate(mSelectedYear, mSelectedMonth);
    }

    /* 설정된 년/월 변경 및 pieChart, adapter 갱신 */
    public void updateSelectedDate(int year, int month) {
        Log.d(TAG, "The date has been selected, or data has been changed.");

        mSelectedYear = year;
        mSelectedMonth = month;
        mSummaryView.mTextViewYear.setText(mSelectedYear + "년");
        mSummaryView.mTextViewMonth.setText(mSelectedMonth + "월");
        mSummaryView.showSummary(mSelectedYear, mSelectedMonth);
        Log.d(TAG, "The summary view has been updated.");

        /* 변동사항 업데이트 */
        ArrayList<Integer> sumOfCats = new ArrayList();

        // 다중 카테고리, 수입 카테고리 제외
        for (int i = 0; i < COUNT_EXPENSE_CAT_MAIN - 1; i++) {
            sumOfCats.add(AccountBookDB.SumForCat(mSelectedYear, mSelectedMonth, i + 1));
        }

        /* pieChart 데이터 업데이트 */
        if (mChart != null) {

            // pieEntry 추가
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
            for (int i = 0; i < COUNT_EXPENSE_CAT_MAIN - 1; i++) {
                int tempTotal = sumOfCats.get(i);

                // 합계금액이 0이 아닐때만 넣기
                if (tempTotal != 0) {
                    entries.add(new PieEntry(tempTotal, Spec.CAT_MAIN_CLASS[i + 1]));
                }
            }

            // 파이차트 데이터셋 정의 -> 파이엔트리, 색, 여백 등 설정
            PieDataSet dataSet = new PieDataSet(entries, "카테고리별 소비 통계");
            dataSet.setValueTypeface(getResources().getFont(R.font.nanum_square_ac_r));
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(30f);
            int chartColors[]={
                    Color.rgb(239, 136, 118),
                    Color.rgb(204, 86, 86),
                    Color.rgb(140, 65, 96),
                    Color.rgb(237, 123, 140),
                    Color.rgb(255,169,164),
                    Color.rgb(235,108,110),
                    Color.rgb(139,119,238),
                    Color.rgb(93,87,203),
                    Color.rgb(76,93,128),
                    Color.rgb(111,131,175),
                    Color.rgb(124,135,236),
                    Color.rgb(174,165,254),
                    Color.rgb(113,109,234),
                    Color.rgb(178,176,244),
            };
            dataSet.setColors(chartColors);

            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            // 파이차트에 데이터 추가
            PieData data = new PieData(dataSet);
            data.setValueTextSize(22f);
            data.setValueTextColor(COLOR_TEXT_INT);
            data.setValueFormatter(new PercentFormatter(mChart));
            mChart.setData(data);
            mChart.invalidate();
        }
        Log.d(TAG, "The data in the pie chart has been updated.");

        /* gridView 데이터 업데이트 */
        if (mStatMainCatListAdapter != null) {

            // 카테고리별 합계금액 갖는 hashMap 만들고 업데이트
            mStatMainCatListAdapter.updateDate(mSelectedYear, mSelectedMonth);
            for (int i = 0; i < COUNT_EXPENSE_CAT_MAIN - 1; i++) {
                mStatMainCatListAdapter.addItem(new SumOfCat(i + 1, sumOfCats.get(i)));
            }
            mStatMainCatListAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "The data in the grid view has been updated.");
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
