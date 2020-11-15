package dankook.kanghyeyoung.capstone_2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.getDaySpec;

public class MainSpecFragment extends Fragment implements MainFragment {
    private static final String TAG = "MainSpecFragment";
    private static final int REQUEST_CODE_FOR_UPDATE = 100;

    SummaryView mSummaryView;
//    SpecAdapter mSpecAdapter;
    Context mContext;
    MainActivity activity;
//    RecyclerView mRecyclerView;
    LinearLayout mLinearLayout;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_spec, container, false);
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

        /* view 참조 */
//        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mLinearLayout=rootView.findViewById(R.id.linearLayout);
        Button buttonManualInput = rootView.findViewById(R.id.button_manual_input);
        Button buttonAutoInput = rootView.findViewById(R.id.button_auto_input);

        /* specAdapter, onItemClickListener 설정 */
//        mSpecAdapter = new SpecAdapter();
//        mSpecAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Spec item = mSpecAdapter.getItem(position);
//                Intent intent = new Intent(mContext, ShowSpecActivity.class);
//                intent.putExtra("spec_item", item);
//                activity.startActivity(intent);
//            }
//        });
//        mRecyclerView.setAdapter(mSpecAdapter);
//
//        /* recyclerView에 레이아웃 추가 */
//        LinearLayoutManager layoutManager =
//                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//
//        /* recyclerView 아이템간 구분선 추가 */
//        DividerItemDecoration dividerItemDecoration =
//                new DividerItemDecoration(
//                        mRecyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* 내역추가 버튼 설정 */
        buttonManualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InputManualActivity.class);
                activity.startActivityForResult(intent, REQUEST_CODE_FOR_UPDATE);
            }
        });
        buttonAutoInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InputAutoActivity.class);
                activity.startActivityForResult(intent, REQUEST_CODE_FOR_UPDATE);
            }
        });

        /* 데이터 추가 */
        updateSelectedDate(mCurYear, mCurMonth);

        return rootView;
    }

    /* 밑의 메소드 호출 */
    public void updateSelectedDate() {
        updateSelectedDate(mSelectedYear, mSelectedMonth);
    }

    /* 설정된 년/월 변경 및 adapter 갱신 */
    public void updateSelectedDate(int year, int month) {
        Log.d(TAG, "The date has been selected, or data has been changed.");

        mSelectedYear = year;
        mSelectedMonth = month;

        mSummaryView.mTextViewYear.setText(mSelectedYear + "년");
        mSummaryView.mTextViewMonth.setText(mSelectedMonth + "월");
        mSummaryView.showSummary(mSelectedYear, mSelectedMonth);
        Log.d(TAG, "The summary view has been updated.");

        mLinearLayout.removeAllViews();

        for (int day=1; day<=31; day++) {
            ArrayList<Spec> items=getDaySpec(mSelectedYear, mSelectedMonth, day);
            if(items.size()!=0) {
                addRecyclerView(items, day);
            }
        }
//        if (mSpecAdapter != null) {
//            ArrayList<Spec> items = selectAllSpecs(mSelectedYear, mSelectedMonth);
//            mSpecAdapter.clear();
//            mSpecAdapter.addItems(items);
//            mSpecAdapter.notifyDataSetChanged();
//        }
        Log.d(TAG, "The data in the recycler view has been updated.");
    }

    public void addRecyclerView(ArrayList<Spec> items, int day) {

        /* 날짜 및 리사이클러뷰 화면에 추가 */
        ViewGroup rootView= (ViewGroup) getLayoutInflater().inflate(R.layout.view_spec_day, null, false);
        TextView textView=rootView.findViewById(R.id.textView_day);
        textView.setText(day+"일 내역");

        /* specAdapter 생성 */
        final SpecAdapter specAdapter = new SpecAdapter();
        specAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Spec item = specAdapter.getItem(position);
                Intent intent = new Intent(mContext, ShowSpecActivity.class);
                intent.putExtra("spec_item", item);
                activity.startActivity(intent);
            }
        });

        /* recyclerView에 아이템 추가, adapter 설정 */
        RecyclerView recyclerView=rootView.findViewById(R.id.recyclerView);
        specAdapter.addItems(items);
        recyclerView.setAdapter(specAdapter);

        /* recyclerView에 레이아웃 추가 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        /* recyclerView 아이템간 구분선 추가 */
//        DividerItemDecoration dividerItemDecoration =
//                new DividerItemDecoration(
//                        recyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);

        /* 화면에 추가 */
        mLinearLayout.addView(rootView);
        Log.d(TAG, "레이아웃 추가됨");
    }
}

