package dankook.kanghyeyoung.capstone_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.YEAR_MONTH_FORMAT;
import static dankook.kanghyeyoung.capstone_2._IMAGE.CAT_IMAGE;


public class SpecAdapter extends RecyclerView.Adapter<SpecAdapter.ViewHolder> {
    Context mContext;
    View mItemView;
    ArrayList<Spec> mItems=new ArrayList<Spec>();

    /* 리스너 객체 참조를 저장하는 변수 */
    private OnItemClickListener mItemClickListener=null;

    /* onItemClickListener 객체 참조를 어댑터에 전달하는 메서드 */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener=listener;
    }

    @NonNull
    @Override
    public SpecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        /* view inflate 및 context 저장 */
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        mItemView=inflater.inflate(R.layout.view_item_spec, parent, false);
        mContext=parent.getContext();

        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Spec item=mItems.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(Spec item) {
        if(!mItems.contains(item)) {
            mItems.add(item);
        }
    }

    public void addItems(ArrayList<Spec> items) {
        int size = items.size();
        for(int i=0;i<size;i++){
            mItems.add(items.get(i));
        }
    }

    public void clear() {
        mItems = new ArrayList<>();
    }

    public Spec getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewPlace;
        TextView mTextViewCat;
        TextView mTextViewPrice;
        ImageView mImageViewCat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewPlace=itemView.findViewById(R.id.textView_place);
            mTextViewCat=itemView.findViewById(R.id.textView_cat);
            mTextViewPrice=itemView.findViewById(R.id.textView_price);
            mImageViewCat=itemView.findViewById(R.id.imageView_cat);

            /* itemView에 onClickListener 설정 (내역 상세보기창 띄우기) */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION) {
                        if(mItemClickListener!=null) {
                            mItemClickListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }

        public void setItem(Spec item) {
            mTextViewPlace.setText(item.getPlace()); // 거래처

            // 가격 -> 지출이면 가격 앞에 '-' 붙이기
            if (item.getType()==Spec.TYPE_EXPENSE) {
                mTextViewPrice.setText("-"+DECIMAL_FORMAT.format(item.getPrice())+"원");
            } else {
                mTextViewPrice.setText(DECIMAL_FORMAT.format(item.getPrice())+"원");
            }

            // 카테고리 이름 및 아이콘 넣기
            int catMain=item.getCatMain();
            mTextViewCat.setText(item.getCatStr());
            mImageViewCat.setImageResource(CAT_IMAGE[catMain]);
        }
    }
}
