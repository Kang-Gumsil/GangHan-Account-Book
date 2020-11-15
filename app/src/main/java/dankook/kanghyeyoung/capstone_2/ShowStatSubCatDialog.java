package dankook.kanghyeyoung.capstone_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static dankook.kanghyeyoung.capstone_2.Spec.COUNT_EXPENSE_CAT_MAIN;

public class ShowStatSubCatDialog extends Dialog {
    private final static String TAG = "ShowStatSubCatDialog";

    private int mMainCat;
    private int mSelectedYear;
    private int mSelectedMonth;
    private RecyclerView mRecyclerView;
    private StatSubCatListAdapter mStatSubCatListAdapter;

    public ShowStatSubCatDialog(@NonNull Context context, int mainCat, int year, int month) {
        super(context);
        mMainCat = mainCat;
        mSelectedYear = year;
        mSelectedMonth = month;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ShowStatusCatDialog 생성");

        /* layout 적용 */
        setContentView(R.layout.dialog_show_stat_sub_cat);

        /* view 참조 */
        TextView textViewCatMain = findViewById(R.id.textView_cat_main);
        mRecyclerView = findViewById(R.id.recyclerView);

        /* main_cat 텍스트뷰 설정 */
        textViewCatMain.setText(Spec.getCatMainName(mMainCat) + " 카테고리");

        /* recyclerView에 Adapter 및 레이아웃 설정 */
        mStatSubCatListAdapter = new StatSubCatListAdapter(
                mMainCat, AccountBookDB.SumForCat(mSelectedYear, mSelectedMonth, mMainCat));
        mRecyclerView.setAdapter(mStatSubCatListAdapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        /* recyclerView에 아이템 추가 */
        for (int i = 0; i < Spec.getCatSubCount(mMainCat); i++) {
            mStatSubCatListAdapter.addItem(
                    new SumOfCat(i, AccountBookDB.SumForSubCat(mSelectedYear, mSelectedMonth, mMainCat, i)));
        }
        mStatSubCatListAdapter.notifyDataSetChanged();

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
