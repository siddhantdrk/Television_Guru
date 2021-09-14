package com.app.televisionguru.ui.home;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.televisionguru.MainActivity;
import com.app.televisionguru.dao.Task;
import com.app.televisionguru.databinding.FragmentHomeBinding;
import com.app.televisionguru.room.AppDatabase;
import com.app.televisionguru.room.AppExecutors;
import com.app.televisionguru.room.DatabaseClient;
import com.app.televisionguru.ui.AnimInterface;
import com.app.televisionguru.ui.HomeItemListner;
import com.app.televisionguru.ui.adapter.HomeListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnimsFragment extends Fragment implements HomeItemListner {

    private FragmentHomeBinding binding;
    private AppDatabase mAppDatabase;
    List<Task> mTasks = new ArrayList<>();
    HomeListAdapter homeListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((MainActivity) getActivity()).passAnimVal(() -> DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                .taskDao()
                .getAllByAscendingOrder("Anime").observe(getViewLifecycleOwner(), tasks -> {
                    mTasks.clear();
                    mTasks.addAll(tasks);
                    homeListAdapter.notifyDataSetChanged();
                }));

        binding.rvAnims.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeListAdapter(getActivity(), mTasks, this);
        binding.rvAnims.setAdapter(homeListAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                                .taskDao().delete(mTasks.get(position));
                    }
                });
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((HomeListAdapter.MyViewHolder) viewHolder).viewForeground;

                    getDefaultUIUtil().onSelected(foregroundView);
                }
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

        }).attachToRecyclerView(binding.rvAnims);

        getData(false, getActivity());
        return root;
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

    public void getData(boolean sort, Activity activity) {
        DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                .taskDao()
                .getAll("Anime").observe(getViewLifecycleOwner(), tasks -> {
            mTasks.clear();
            mTasks.addAll(tasks);
            homeListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemDeleteClickListner(int index) {

    }
}