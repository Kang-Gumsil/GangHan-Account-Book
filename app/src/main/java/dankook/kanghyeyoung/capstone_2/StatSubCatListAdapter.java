package dankook.kanghyeyoung.capstone_2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class StatSubCatListAdapter extends RecyclerView.Adapter<StatSubCatListAdapter.ViewHolder> {
    private static final String TAG = "MainStatFragment";
    int mMainCat;
    int mMainSum;
    ArrayList<SumOfCat> mItems = new ArrayList<>();

    public StatSubCatListAdapter(int mainCat, int mainSum) {
        mMainCat = mainCat;
        mMainSum = mainSum;
    }

    @NonNull
    @Override
    public StatSubCatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.view_item_stat_list, viewGroup, false);
        return new StatSubCatListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatSubCatListAdapter.ViewHolder viewHolder, int position) {
        SumOfCat item = mItems.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_cat;
        TextView textView_name;
        TextView textView_price;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_cat = itemView.findViewById(R.id.textView_cat);
            textView_name = itemView.findViewById(R.id.textView_percent);
            textView_price = itemView.findViewById(R.id.textView_total);
        }

        public void setItem(SumOfCat item) {
            // textView_cat 설정
            int subCat = item.getCat();
            textView_cat.setText(Spec.getCatSubName(mMainCat, subCat));

            // textView_percentage 설정
            int total = 0;
            float percentage = 0.0f;
            if (mMainSum != 0) {
                total = item.getSum();
                percentage = (float) total / (float) mMainSum * 100.0f;
            }
            textView_name.setText(Math.round(percentage * 10.0f) / 10.0f + "%");

            // textView_total 설정
            textView_price.setText(DECIMAL_FORMAT.format(total) + "원");
        }
    }

    public void addItem(SumOfCat item) {
        mItems.add(item);
    }

    public void setItems(ArrayList<SumOfCat> items) {
        this.mItems = items;
    }

    public SumOfCat getItem(int position) {
        return mItems.get(position);
    }

    public void setItem(int position, SumOfCat item) {
        mItems.set(position, item);
    }
}