package com.app.televisionguru.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.televisionguru.R;
import com.app.televisionguru.dao.Task;
import com.app.televisionguru.ui.HomeItemListner;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.MyViewHolder> {
    private final HomeItemListner mHomeItemListner;
    private Context context;
    private List<Task> mTaskList;

    public HomeListAdapter(Context context, List<Task> mTaskList, HomeItemListner mHomeItemListner) {
        this.context = context;
        this.mTaskList = mTaskList;
        this.mHomeItemListner = mHomeItemListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_list_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeListAdapter.MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {
        myViewHolder.name.setText(mTaskList.get(i).getName());
        myViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHomeItemListner.onItemClickListner(i);
            }
        });

        myViewHolder.itemView.findViewById(R.id.viewIcon).setVisibility(mTaskList.get(i).isVisible()
                ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if (mTaskList == null) {
            return 0;
        }
        return mTaskList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public LinearLayout viewForeground;
        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvHomeList);
            viewForeground = itemView.findViewById(R.id.viewForeground);

        }
    }
}
