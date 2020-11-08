package dankook.kanghyeyoung.capstone_2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import static dankook.kanghyeyoung.capstone_2._COLOR.COLOR_TEXT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class SummaryView extends LinearLayout {
    Context mContext;
    Button mButton;
    TextView mTextViewYear;
    TextView mTextViewMonth;
    TextView mTextViewIncome;
    TextView mTextViewExpense;
    TextView mTextViewBalance;

    public SummaryView(Context context) {
        super(context);
        init(context);
    }

    public SummaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        /* context 저장 */
        mContext=context;

        /* view inflate 및 참조 */
        LayoutInflater inflater=LayoutInflater.from(mContext);
        inflater.inflate(R.layout.view_summary, this, true);

        mButton=findViewById(R.id.button);
        mTextViewYear=findViewById(R.id.textView_year);
        mTextViewMonth=findViewById(R.id.textView_month);
        mTextViewIncome=findViewById(R.id.textView_income2);
        mTextViewExpense=findViewById(R.id.textView_expense2);
        mTextViewBalance=findViewById(R.id.textView_balance2);

        /* down 화살표 그림 설정 */
        ImageView imageView=findViewById(R.id.imageView_button);
        imageView.setColorFilter(Color.parseColor(COLOR_TEXT), PorterDuff.Mode.SRC_IN);
    }

    /* summaryView 조회 설정 */
    public void showSummary(int year, int month) {
        int sum_expense = sumAll(year, month, TYPE_EXPENSE);
        int sum_income = sumAll(year, month, TYPE_INCOME);
        int sum_balance = sum_income - sum_expense;

        mTextViewIncome.setText(DECIMAL_FORMAT.format(sum_income));
        mTextViewExpense.setText(DECIMAL_FORMAT.format(sum_expense));
        mTextViewBalance.setText(DECIMAL_FORMAT.format(sum_balance));
    }
}
