package dankook.kanghyeyoung.capstone_2;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_ENTERTAIN;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INCOME;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_INTERIOR;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_MULTI;
import static dankook.kanghyeyoung.capstone_2.Spec.CAT_MAIN_OTHER;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class SpecDetailAdapter extends RecyclerView.Adapter<SpecDetailAdapter.CustomeViewHolder> {
    private ArrayList<SpecDetail> mItems = new ArrayList<SpecDetail>();
    private Context mContext;

    public SpecDetailAdapter(Context context){
        mContext=context;
    }

    @NonNull
    @Override
    public CustomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.view_item_spec_detail, viewGroup, false);

        return new CustomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomeViewHolder viewHolder, int position) {
        SpecDetail item = mItems.get(position);
        viewHolder.setItem(item);


    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public class CustomeViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewCat;
        protected EditText inputSpecName;
        protected EditText inputSpecPrice;

        public CustomeViewHolder(View view) {
            super(view);

            this.textViewCat = view.findViewById(R.id.textView_cat);
            this.inputSpecName = view.findViewById(R.id.editText_specName);
            this.inputSpecPrice = view.findViewById(R.id.editText_specPrice);

            inputSpecName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {  }

                @Override
                public void afterTextChanged(Editable s) {
                    String specName=inputSpecName.getText().toString();
                    mItems.get(getAdapterPosition()).setSpecName(specName);
                    Log.d("SpecDetailAdapter", mItems.get(getAdapterPosition()).getSpecName());

                }
            });

            inputSpecPrice.addTextChangedListener(new TextWatcher() {
                String priceResult;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(priceResult)) {
                        priceResult = DECIMAL_FORMAT.format(
                                Double.parseDouble(s.toString().replaceAll(",", "")));
                        inputSpecPrice.setText(priceResult);
                        inputSpecPrice.setSelection(priceResult.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!inputSpecPrice.getText().toString().isEmpty()) {
                        int specPrice = Integer.parseInt(inputSpecPrice.getText().toString().replaceAll("\\,", ""));
                        mItems.get(getAdapterPosition()).setSpecPrice(specPrice);
                        Log.d("SpecDetailAdapter", "specPrice:"+mItems.get(getAdapterPosition()).getSpecPrice());

                    } else {
                        mItems.get(getAdapterPosition()).setSpecPrice(-1);
                    }
                }
            });

            textViewCat.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.d("SpecDetailAdapter", "textViewCat Clicked");
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                    View view = LayoutInflater.from(mContext)
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
                                int subCatArrayId = mContext.getResources().getIdentifier(mainCatStr, "array", mContext.getPackageName());

                                // subCatArray에 대한 서브 카테고리 스피너
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                        (mContext, android.R.layout.simple_spinner_dropdown_item,
                                                mContext.getResources().getStringArray(subCatArrayId));
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

                            String specName = inputSpecName.getText().toString();
                            int specPrice = Integer.parseInt(inputSpecPrice.getText().toString().replaceAll("\\,", ""));
                            SpecDetail specDetail = new SpecDetail(catMain, catSub, specName, specPrice);

                            Log.d("SpecDetailAdapter", "adapter position:"+getAdapterPosition());
                            Log.d("SpecDetailAdapter", "specDetail:"+specDetail.getSpecName()+", catMain:"+specDetail.getCatMain());
                            mItems.set(getAdapterPosition(), specDetail);

                            notifyItemChanged(getAdapterPosition());

                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }

        public void setItem(SpecDetail item) {
            // inputCat 설정
            Log.d("DetailAdapter","item"+ item.getSpecName()+", cat_main : "+item.getCatMain()+", cat_sub : "+item.getCatSub());

            textViewCat.setText(item.getCatStr());

            // inputSpecName 설정
            inputSpecName.setText(item.getSpecName());

            // inputSpecPrice 설정
            inputSpecPrice.setText(DECIMAL_FORMAT.format(item.getSpecPrice()));
        }
    }

    /* 리싸이클러뷰 어댑터 초기화 */
    public void clear() {
        mItems.clear();
    }

    public void addItem(SpecDetail item) {
        mItems.add(item);
    }

    public void setItems(ArrayList<SpecDetail> items) {
        this.mItems = items;
    }

    public SpecDetail getItem(int position) {
        return mItems.get(position);
    }

    public void setItem(int position, SpecDetail item) {
        mItems.set(position, item);
    }

    public ArrayList<SpecDetail> getItems() {
        return mItems;
    }
}
