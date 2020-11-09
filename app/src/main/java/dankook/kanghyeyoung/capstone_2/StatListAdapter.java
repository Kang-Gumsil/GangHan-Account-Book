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

public class StatListAdapter extends RecyclerView.Adapter<StatListAdapter.ViewHolder> {
    static int mSelectedYear;
    static int mSelectedMonth;
    static ArrayList<SumOfCat> mItems=new ArrayList<>();

    public StatListAdapter(int year, int month) {
        super();

        mSelectedYear = year;
        mSelectedMonth = month;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.view_item_stat_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SumOfCat item = mItems.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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
            int catMain = item.getCat();
            textView_cat.setText(Spec.CAT_MAIN_CLASS[catMain]);

            // textView_percentage 설정
            int total = item.getSum();
            float percentage
                    = (float) total / AccountBookDB.sumAll(mSelectedYear, mSelectedMonth, Spec.TYPE_EXPENSE) * 100;
            textView_name.setText(Math.round(percentage * 10) / 10.0 + "%");
            Log.d("STAT", item.getCat()+", "+total+", "+AccountBookDB.sumAll(mSelectedYear, mSelectedMonth, Spec.TYPE_EXPENSE));

            // textView_total 설정
            textView_price.setText(DECIMAL_FORMAT.format(total) + "원");
        }
    }

    public void updateDate(int year, int month) {
        mItems.clear();
        mSelectedYear=year;
        mSelectedMonth=month;
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