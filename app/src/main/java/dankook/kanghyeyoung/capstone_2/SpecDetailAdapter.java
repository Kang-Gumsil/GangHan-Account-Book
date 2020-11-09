package dankook.kanghyeyoung.capstone_2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class SpecDetailAdapter extends RecyclerView.Adapter<SpecDetailAdapter.ViewHolder> {
    ArrayList<SpecDetail> mItems = new ArrayList<SpecDetail>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.view_item_spec_detail, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SpecDetail item = mItems.get(position);
        viewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText inputCat;
        EditText inputSpecName;
        EditText inputSpecPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            inputCat = itemView.findViewById(R.id.editText_cat);
            inputSpecName = itemView.findViewById(R.id.editText_specName);
            inputSpecPrice = itemView.findViewById(R.id.editText_specPrice);
        }

        public void setItem(SpecDetail item) {
            // inputCat 설정
            Log.d("DetailAdapter","item"+ item.getSpecName()+", cat_main : "+item.getCatMain()+", cat_sub : "+item.getCatSub());

            inputCat.setText(item.getCatStr());

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
}
