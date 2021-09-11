package com.app.televisionguru.ui.gallery;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.televisionguru.dao.Task;
import com.app.televisionguru.databinding.FragmentGalleryBinding;
import com.app.televisionguru.room.AppExecutors;
import com.app.televisionguru.room.DatabaseClient;
import com.app.televisionguru.ui.HomeItemListner;
import com.app.televisionguru.ui.adapter.HomeListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TelevisionFragment extends Fragment implements HomeItemListner {

    private FragmentGalleryBinding binding;
    private List<Task> mTasks = new ArrayList<>();
    HomeListAdapter homeListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.rvTelevision.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeListAdapter(getActivity(), mTasks, this);
        binding.rvTelevision.setAdapter(homeListAdapter);
        getData(false, getActivity());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(() -> {
                    int position = viewHolder.getAdapterPosition();
                    DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                            .taskDao().delete(mTasks.get(position));
                });
            }

            private void drawText(String text, Canvas c, RectF button, Paint p) {
                float textSize = 30;
                p.setColor(Color.WHITE);
                p.setAntiAlias(true);
                p.setTextSize(textSize);

                float textWidth = p.measureText(text);
                c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((HomeListAdapter.MyViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((HomeListAdapter.MyViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((HomeListAdapter.MyViewHolder) viewHolder).viewForeground;

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(binding.rvTelevision);
        return root;
    }

    public void getData(boolean sort, Activity mainActivity) {
        DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                .taskDao()
                .getAll("Television").observe(getViewLifecycleOwner(), tasks -> {
            mTasks.clear();
            mTasks.addAll(tasks);
            homeListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClickListner(int index) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                        .taskDao().updateVisibility(!mTasks.get(index).isVisible(),
                        mTasks.get(index).getId(), mTasks.get(index).getType());
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        getData(false, getActivity());
                                                                    }
                                                                }
                );
            }
        });
    }

    @Override
    public void onItemDeleteClickListner(int index) {

    }
}