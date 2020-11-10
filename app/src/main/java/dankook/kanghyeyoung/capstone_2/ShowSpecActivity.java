package dankook.kanghyeyoung.capstone_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.delete;
import static dankook.kanghyeyoung.capstone_2.AccountBookDB.update;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_CLASS;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_ENTERTAIN;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INCOME;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INTERIOR;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_MULTI;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_OTHER;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_SUB_CLASS;
import static dankook.kanghyeyoung.capstone_2.Spec.TYPE_EXPENSE;
import static dankook.kanghyeyoung.capstone_2.Spec.TYPE_INCOME;
import static dankook.kanghyeyoung.capstone_2._COLOR.COLOR_PINK_GRAY;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_TIME_FORMAT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class ShowSpecActivity extends AppCompatActivity {
    String TAG="ShowSpecActivity";

    ImageButton mImageButtonClose;
    EditText mInputPrice;
    EditText mInputPlace;
    TextView mTextViewType;
    TextView mTextViewCat;
    TextView mTextViewDate;
    Button mButtonDate;
    Button mButtonDelete;
    Button mButtonModify;
    LinearLayout mLayoutCat;

    int mTypeFlag;
    Spec mItem;
    Calendar mCalendar;


    ArrayList<SpecDetail> mSpecDetails;
    SpecDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_spec);

        /* view 참조 */
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mImageButtonClose = findViewById(R.id.imageButton_close);
        mInputPrice = findViewById(R.id.editText_price);
        mInputPlace = findViewById(R.id.editText_place);
        mTextViewCat = findViewById(R.id.textView_cat);
        mTextViewDate = findViewById(R.id.textView_date);
        mButtonDate = findViewById(R.id.button_date);
        mButtonDelete = findViewById(R.id.button_delete);
        mButtonModify = findViewById(R.id.button_modify);
        mLayoutCat = findViewById(R.id.layout_cat);
        mTextViewType=findViewById(R.id.textView_type);

        /* 닫기 버튼 이벤트리스너 정의 */
        mImageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* 리사이클러뷰에 레이아웃 설정 및 데이터와 어댑터 추가 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SpecDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // 아이템 간 구분선 추가
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(),
                        new LinearLayoutManager(getApplicationContext()).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* Buldle로 받은 item 처리 */
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("spec_item")) {
            mItem = (Spec) intent.getSerializableExtra("spec_item");
            Log.d(TAG, "specId:" + mItem.getSpecId() + ", place:" + mItem.getPlace());
        }

        /* 분류 텍스트뷰 설정 */
        if (mItem.getType() == TYPE_INCOME) {
            mTextViewType.setText("수입");
            mTypeFlag = TYPE_INCOME;
            mLayoutCat.setVisibility(View.GONE);
        } else {
            mTextViewType.setText("지출");
            mTypeFlag = TYPE_EXPENSE;
        }

        /* 가격 설정 */
        mInputPrice.setText(DECIMAL_FORMAT.format(mItem.getPrice()));

        /* mInputPrice에 입력할 때 1000단위 컴마 찍도록 리스너 설정 */
        mInputPrice.addTextChangedListener(textWatcher);

        /* 장소 설정 */
        mInputPlace.setText(mItem.getPlace());

        /* 카테고리 설정 */
        mTextViewCat.setText(mItem.getCatStr());

        // 카테고리 - 버튼 클릭 시 재설정 위한 다이얼로그 띄움
        mTextViewCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowSpecActivity.this);
                View view = LayoutInflater.from(ShowSpecActivity.this)
                        .inflate(R.layout.dialog_category_select, null, false);
                builder.setView(view);

                // 다이얼로그 레이아웃의 view 참조
                final Button buttonRegister = view.findViewById(R.id.button_register);
                final Button buttonCancel = view.findViewById(R.id.button_cancel);
                final Spinner spinnerCat = view.findViewById(R.id.spinner_cat);
                final Spinner spinnerSubcat = view.findViewById(R.id.spinner_subcat);
                final LinearLayout layoutSubcat = view.findViewById(R.id.layout_subcat);

                final AlertDialog dialog = builder.create();

                // 서브 카테고리가 존재하는 메인 카테고리 선택 시 서브 카테고리 스피너도 나타나도록 함
                spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String mainCatStr = spinnerCat.getSelectedItem().toString();
                        if (position == 0) {
                            onNothingSelected(adapterView);
                        } else {
                            if (position!=CAT_MAIN_ENTERTAIN && position!=CAT_MAIN_INTERIOR
                                    && position!=CAT_MAIN_OTHER && position!=CAT_MAIN_MULTI
                                    && position!=CAT_MAIN_INCOME) {
                                layoutSubcat.setVisibility(View.VISIBLE);
                            } else {
                                layoutSubcat.setVisibility(View.GONE);
                            }
                            // mainCatStr에 해당하는 카테고리의 서브 카테고리 array id값을 가져옴
                            int subCatArrayId = getResources().getIdentifier(mainCatStr, "array", getApplicationContext().getPackageName());

                            // subCatArray에 대한 서브 카테고리 스피너
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                                            getResources().getStringArray(subCatArrayId));
                            spinnerSubcat.setAdapter(spinnerArrayAdapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // 취소 버튼 누를 시 다이얼로그 사라짐
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // 카테고리 텍스트 재설정
                buttonRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int catMain = spinnerCat.getSelectedItemPosition();
                        int catSub = spinnerSubcat.getSelectedItemPosition() - 1;
                        mItem.setCatMain(catMain);
                        mItem.setCatSub(catSub);
                        mTextViewCat.setText(mItem.getCatStr());

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        /* 날짜 설정 */
        mTextViewDate.setText(DATE_TIME_FORMAT.format(mItem.getDate()));

        // 날짜 영역 클릭 시 날짜, 시간 재설정할 수 있는 다이얼로그 띄움
        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(ShowSpecActivity.this);
                // 다이얼로그 띄웠을 때 현재 텍스트뷰 내용이 기본 값으로 설정돼있도록 Calendar 객체 값 설정
                mCalendar = Calendar.getInstance();
                try {
                    mCalendar.setTime(DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 커스텀 다이얼로그 호출, 이때 결과를 출력할 TextView를 매개변수로 같이 넘겨줌
                dateTimePickerDialog.callFunction(mTextViewDate, mCalendar);
                try {
                    mCalendar.setTime(DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        /* SpecDetail에 대한 데이터를 어댑터에 추가 */
        mSpecDetails = mItem.getSpecDetails();
        if (mSpecDetails.size() != 0) {
            for (SpecDetail specDetail : mSpecDetails) {
                mAdapter.addItem(specDetail);
            }
            mAdapter.notifyDataSetChanged();

        } else {
            findViewById(R.id.layout_detail).setVisibility(View.GONE);
        }

        /* 내역 삭제 */
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int specId = mItem.getSpecId();
                Log.d("ShowSpecActivity", "specId:" + specId);
                delete(specId);
                setResult(RESULT_OK);
                finish();
            }
        });

        /* 내역 수정 */
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spec_id = mItem.getSpecId();
                mItem.setType(mTypeFlag);
                mItem.setPrice(Integer.parseInt(mInputPrice.getText().toString().replaceAll("\\,","")));
                mItem.setPlace(mInputPlace.getText().toString());
                Date date = new Date();
                try {
                    date = DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mItem.setDate(date);

                if(mAdapter.getItemCount()>0) {
                    for(int i=0;i<mAdapter.getItemCount();i++){
                        mItem.setSpecDetail(i, mAdapter.getItem(i));
                        Log.d("Update Test", "mAdapter.getItemCount() item mainCat:"+mAdapter.getItem(i).getCatMain());
                    }
                }

                try {
                    update(spec_id, mItem);
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
        String priceResult;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(priceResult)) {
                priceResult = DECIMAL_FORMAT.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                mInputPrice.setText(priceResult);
                mInputPrice.setSelection(priceResult.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

}