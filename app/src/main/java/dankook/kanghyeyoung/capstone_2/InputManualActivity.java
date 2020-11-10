package dankook.kanghyeyoung.capstone_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.insert;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_DIGITAL;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_EDUCATION;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_ENTERTAIN;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_FASHION_BEAUTY;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_FINANCE;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_FOOD;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_HEALTH;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_HOBBY_TRAVEL;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_HOUSEHOLDITEM;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INCOME;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INTERIOR;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_LIVING_COMMUNI;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_MULTI;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_OTHER;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_RELATIONSHIP;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_TRANSPORT;
import static dankook.kanghyeyoung.capstone_2._COLOR.COLOR_PINK_GRAY;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_TIME_FORMAT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class InputManualActivity extends AppCompatActivity {
    String TAG="InputManualActivity";
    LinearLayout mLayoutCat;
    LinearLayout mLayoutSubCat;
    LinearLayout mLayoutSpecDetail;
    RecyclerView mRecyclerView;
    Button mButtonIncome;
    Button mButtonSpend;
    Button mButtonAdd;
    Button mButtonRegister;
    ImageButton mImageButtonClose;
    Spinner mSpinnerCat;
    Spinner mSpinnerSubCat;
    EditText mInputPrice;
    EditText mInputPlace;
    TextView mTextViewDate;

    int mTypeFlag;
    String mPriceResult =null;
    SpecDetailAdapter mSpecDetailAdapter;
    Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_manual);

        /* view 참조 */
        mLayoutCat=findViewById(R.id.layout_cat);
        mLayoutSubCat=findViewById(R.id.layout_subcat);
        mLayoutSpecDetail =findViewById(R.id.layout_specDetail);
        mRecyclerView=findViewById(R.id.recyclerView);
        mButtonIncome=findViewById(R.id.button_income);
        mButtonSpend=findViewById(R.id.button_spend);
        mButtonAdd=findViewById(R.id.button_add);
        mButtonRegister=findViewById(R.id.button_register);
        mImageButtonClose=findViewById(R.id.imageButton_close);
        mInputPrice=findViewById(R.id.editText_price);
        mInputPlace=findViewById(R.id.editText_place);
        mSpinnerCat=findViewById(R.id.spinner_cat);
        mSpinnerSubCat=findViewById(R.id.spinner_subcat);
        mTextViewDate=findViewById(R.id.textView_date);

        /* 닫기 버튼 */
        mImageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* 분류 버튼 */
        mTypeFlag=-1;
        // 분류 - 수입 버튼 클릭 시
        mButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTypeFlag==-1||mTypeFlag==Spec.TYPE_EXPENSE) {
                    mTypeFlag=0;
                    mButtonIncome.setBackgroundColor(Color.parseColor(COLOR_PINK_GRAY));
                    mButtonSpend.setBackgroundColor(Color.parseColor("#02FFFFFF"));
                    mLayoutCat.setVisibility(View.GONE);
                    Log.d(TAG, "수입 버튼 선택됨");
                }
            }
        });

        // 분류 - 지출 버튼 클릭 시
        mButtonSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTypeFlag==-1||mTypeFlag==Spec.TYPE_INCOME) {
                    mTypeFlag=1;
                    mButtonSpend.setBackgroundColor(Color.parseColor(COLOR_PINK_GRAY));
                    mButtonIncome.setBackgroundColor(Color.parseColor("#02FFFFFF"));
                    mLayoutCat.setVisibility(View.VISIBLE);
                    Log.d(TAG, "지출 버튼 선택됨");
                }
            }
        });

        /* mInputPrice에 입력할 때 1000단위 컴마 찍도록 리스너 설정 */
        mInputPrice.addTextChangedListener(textWatcher);

        /* mSpinnerCat에서 선택되는 항목에 따라 mSpinnerSubCat 표시 여부 다르게 설정 */
        mSpinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String name = mSpinnerCat.getSelectedItem().toString();
                if (position == 0) {
                    onNothingSelected(adapterView);
                }
                else {
                    if (position== CAT_MAIN_MULTI) {
                        mLayoutSpecDetail.setVisibility(View.VISIBLE);
                    } else {
                        mLayoutSpecDetail.setVisibility(View.INVISIBLE);
                    }
                    if (position!=CAT_MAIN_ENTERTAIN && position!=CAT_MAIN_INTERIOR
                            && position!=CAT_MAIN_OTHER && position!=CAT_MAIN_MULTI
                            && position!=CAT_MAIN_INCOME) {
                        mLayoutSubCat.setVisibility(View.VISIBLE);
                    } else {
                        mLayoutSubCat.setVisibility(View.GONE);
                    }
                    int subCatArrayId = getResources().getIdentifier(name, "array", getApplicationContext().getPackageName());

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                            (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                    getResources().getStringArray(subCatArrayId));
                    mSpinnerSubCat.setAdapter(spinnerArrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        /* 날짜 설정 */
        mCalendar = Calendar.getInstance();

        // 현재 시간 정보로 날짜 텍스트뷰 내용 설정
        DateTimePickerDialog.setDate(mCalendar, mTextViewDate);

        // 날짜 선택 다이얼로그를 호출하는 클릭 이벤트 정의
        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(InputManualActivity.this);

                // 커스텀 다이얼로그 호출 - 이때 결과를 출력할 TextView를 매개변수로 같이 넘겨줌
                dateTimePickerDialog.callFunction(mTextViewDate, mCalendar);
                try {
                    mCalendar.setTime(DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        /* 리싸이클러뷰 설정 및 어댑터 설정 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mSpecDetailAdapter = new SpecDetailAdapter(this);
        mRecyclerView.setAdapter(mSpecDetailAdapter);

        /* 리싸이클러뷰에서 아이템간 구분선 추가 */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(),new LinearLayoutManager(getApplicationContext()).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* SpecDetail 내역 추가 버튼을 누르면 다이얼로그를 통해 상세 내역 입력 */
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InputManualActivity.this);
                View view = LayoutInflater.from(InputManualActivity.this)
                        .inflate(R.layout.view_dialog_input_manual, null, false);
                builder.setView(view);

                Button buttonDetailRegister = view.findViewById(R.id.button_detail_register);
                Button buttonDetailCancel = view.findViewById(R.id.button_detail_cancel);
                final Spinner spinnerDetailCat = view.findViewById(R.id.spinner_detail_cat);
                final Spinner spinnerDetailSubcat = view.findViewById(R.id.spinner_detail_subcat);
                final EditText inputSpecName = view.findViewById(R.id.editText_specName);
                final EditText inputSpecPrice=view.findViewById(R.id.editText_specPrice);

                final LinearLayout layoutDetailSubcat = view.findViewById(R.id.layout_detail_subcat);

                final AlertDialog dialog = builder.create();

                /* inputSpecPrice 1000단위 컴마 찍기 */
                TextWatcher textWatcher2=new TextWatcher() {
                    String priceResult=null;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(priceResult)) {
                            priceResult = DECIMAL_FORMAT.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                            inputSpecPrice.setText(priceResult);
                            inputSpecPrice.setSelection(priceResult.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                };

                inputSpecPrice.addTextChangedListener(textWatcher2);

                spinnerDetailCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String name = spinnerDetailCat.getSelectedItem().toString();
                        if (position == 0) {
                            onNothingSelected(adapterView);
                        } else {
                            if (position!=CAT_MAIN_ENTERTAIN && position!=CAT_MAIN_INTERIOR
                                    && position!=CAT_MAIN_OTHER && position!=CAT_MAIN_MULTI
                                    && position!=CAT_MAIN_INCOME) {
                                layoutDetailSubcat.setVisibility(View.VISIBLE);
                            } else {
                                layoutDetailSubcat.setVisibility(View.GONE);
                            }
                            int detailSubCatArrayId = getResources().getIdentifier(name, "array", getApplicationContext().getPackageName());

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                            getResources().getStringArray(detailSubCatArrayId));
                            spinnerDetailSubcat.setAdapter(spinnerArrayAdapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });

                // 취소 버튼 누를 시 다이얼로그 사라짐
                buttonDetailCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // 등록 버튼 누를 시 입력 내용 리싸이클러뷰에 추가
                buttonDetailRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int detailCat = (int)(spinnerDetailCat.getSelectedItemId());


                        if(detailCat==0 || inputSpecName.getText().toString().equals("") ||
                                inputSpecPrice.getText().toString().equals("") ||
                                ((detailCat==CAT_MAIN_FOOD||detailCat==CAT_MAIN_FASHION_BEAUTY||
                                  detailCat==CAT_MAIN_HOUSEHOLDITEM||detailCat==CAT_MAIN_TRANSPORT||
                                  detailCat==CAT_MAIN_LIVING_COMMUNI||detailCat==CAT_MAIN_EDUCATION||
                                  detailCat==CAT_MAIN_HOBBY_TRAVEL||detailCat==CAT_MAIN_HEALTH||
                                  detailCat==CAT_MAIN_RELATIONSHIP||detailCat==CAT_MAIN_FINANCE||
                                  detailCat==CAT_MAIN_DIGITAL) && (spinnerDetailSubcat.getSelectedItemPosition()-1 == -1)))
                        {
                            Log.d(TAG, "detailCat="+detailCat);
                            Toast.makeText(getApplicationContext(), "항목을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String specName = inputSpecName.getText().toString();
                        int specPrice = Integer.parseInt(inputSpecPrice.getText().toString().replaceAll("\\,",""));

                        SpecDetail specDetailItem;

                        if(detailCat!=CAT_MAIN_ENTERTAIN && detailCat!=CAT_MAIN_INTERIOR
                                && detailCat!=CAT_MAIN_OTHER && detailCat!=CAT_MAIN_MULTI) {
                            int detailSubCat = (int)spinnerDetailSubcat.getSelectedItemId()-1;
                            specDetailItem = new SpecDetail(detailCat, detailSubCat, specName, specPrice);
                        } else {
                            specDetailItem = new SpecDetail(detailCat, specName, specPrice);
                        }

                        mSpecDetailAdapter.addItem(specDetailItem);
                        mSpecDetailAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        /* 내역 등록 버튼 */
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int catMain=0;
                if(mTypeFlag==-1) {
                    Toast.makeText(getApplicationContext(), "분류/지출 항목을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(mTypeFlag==1) {
                    catMain=mSpinnerCat.getSelectedItemPosition();
                    if (mInputPrice.getText().toString().equals("") || mSpinnerCat.getSelectedItemPosition() == 0
                            || mInputPlace.getText().toString().equals("")
                            || ((catMain == CAT_MAIN_FOOD || catMain == CAT_MAIN_FASHION_BEAUTY ||
                            catMain == CAT_MAIN_HOUSEHOLDITEM || catMain == CAT_MAIN_TRANSPORT ||
                            catMain == CAT_MAIN_LIVING_COMMUNI || catMain == CAT_MAIN_EDUCATION ||
                            catMain == CAT_MAIN_HOBBY_TRAVEL || catMain == CAT_MAIN_HEALTH ||
                            catMain == CAT_MAIN_RELATIONSHIP || catMain == CAT_MAIN_FINANCE ||
                            catMain == CAT_MAIN_DIGITAL) && (mSpinnerSubCat.getSelectedItemPosition() - 1 == -1))) {
                        Toast.makeText(getApplicationContext(), "항목을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Log.d(TAG, "\n분류:" + mTypeFlag + "금액:" + mInputPrice.getText() + " 거래처:" + mInputPlace.getText()
                        + "\n카테고리:" + mSpinnerCat.getSelectedItem().toString()
                        + "\n날짜:" + mTextViewDate.getText());

                int type = mTypeFlag;
                int price = Integer.parseInt(mInputPrice.getText().toString().replaceAll("\\,",""));
                String place = mInputPlace.getText().toString();
                int catSub = mSpinnerSubCat.getSelectedItemPosition()-1;
                Date date = new Date();
                try {
                    date = DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Spec spec=new Spec(type, price, place, catMain, catSub, date);

                // 세부 내역 존재하는 경우, 세부 내역을 Spec의 specDetails에 추가
                int detailSize = mSpecDetailAdapter.getItemCount();
                Log.d("InputManualActivity", "adapter에 등록된 item의 수: " + detailSize);
                for(int i=0; i < detailSize; i++) {
                    SpecDetail specDetailItem = mSpecDetailAdapter.getItem(i);
                    spec.addSpecDetail(specDetailItem);
                }

                try {
                    int insertKey = insert(spec);
                    Log.d(TAG, "insert() 호출함");
                    Log.d(TAG, "insert 결과:"+ insertKey);

                    setResult(RESULT_OK);
                    finish();

                } catch(SQLiteException e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(getApplicationContext(),
                            "거래처 및 내역명에는 ' 또는 \"가 들어갈 수 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /* mInputPrice 1000단위 컴마 찍기 */
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(mPriceResult)) {
                mPriceResult = DECIMAL_FORMAT.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                mInputPrice.setText(mPriceResult);
                mInputPrice.setSelection(mPriceResult.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

}