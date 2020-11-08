package dankook.kanghyeyoung.capstone_2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class MainSpecFragment extends Fragment implements MainFragment{
    private static final String TAG = "MainSpecFragment";
    private static final int REQUEST_CODE_FOR_INPUT = 100;

    SummaryView mSummaryView;
    SpecAdapter mSpecAdapter;
    Context mContext;
    PopupMenu popupMenu;
    MainActivity activity;
    RecyclerView mRecyclerView;

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

        /* specAdapter 생성 및 onItemClickListener 설정 */
        mSpecAdapter = new SpecAdapter();
        mSpecAdapter.setOnItemClickListener(new SpecAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Spec item = mSpecAdapter.getItem(position);
                Intent intent = new Intent(mContext, ShowSpecActivity.class);
                intent.putExtra("spec_item", item);
                activity.startActivityForResult(intent, REQUEST_CODE_FOR_INPUT);
            }
        });

        /* recyclerView 설정 및 adapter 설정 */
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSpecAdapter);

        /* recyclerView 아이템간 구분선 추가 */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(
                        mRecyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* 내역추가 버튼 설정 */
        Button inputButton = rootView.findViewById(R.id.button_input);
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Layout1", "ImageButton was clicked");
                popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.input_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_manual:
                                Intent intent = new Intent(getContext(), InputManualActivity.class);
                                activity.startActivityForResult(intent, REQUEST_CODE_FOR_INPUT);
                                break;

                            case R.id.item_auto:
                                intent = new Intent(getContext(), InputAutoActivity.class);
                                activity.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return rootView;
    }


    /* 밑의 메소드 호출 */
    public void updateSelectedDate() {
        updateSelectedDate(mSelectedYear, mSelectedMonth);
    }

    /* 설정된 년/월 변경 및 adapter 갱신 */
    public void updateSelectedDate(int year, int month) {
        mSelectedYear = year;
        mSelectedMonth = month;

        mSummaryView.mTextView_year.setText(mSelectedYear + "년");
        mSummaryView.mTextView_month.setText(mSelectedMonth + "월");
        mSummaryView.showSummary(mSelectedYear, mSelectedMonth);

        ArrayList<Spec> items = selectAllSpecs(mSelectedYear, mSelectedMonth);
        mSpecAdapter.clear();
        mSpecAdapter.addItems(items);
        mSpecAdapter.notifyDataSetChanged();
    }
}

