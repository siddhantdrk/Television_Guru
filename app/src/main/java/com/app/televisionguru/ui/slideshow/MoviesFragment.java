package com.app.televisionguru.ui.slideshow;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
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
import com.app.televisionguru.databinding.FragmentSlideshowBinding;
import com.app.televisionguru.room.AppExecutors;
import com.app.televisionguru.room.DatabaseClient;
import com.app.televisionguru.ui.HomeItemListner;
import com.app.televisionguru.ui.adapter.HomeListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment implements HomeItemListner {

    private FragmentSlideshowBinding binding;
    private List<Task> mTasks = new ArrayList<>();
    HomeListAdapter homeListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.rvMovies.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeListAdapter(getActivity(), mTasks, this);
        binding.rvMovies.setAdapter(homeListAdapter);
        getData(false, getActivity());

        ((MainActivity) getActivity()).passMoviesVal(() -> DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                .taskDao()
                .getAllByAscendingOrder("Movies").observe(getViewLifecycleOwner(), tasks -> {
                    mTasks.clear();
                    mTasks.addAll(tasks);
                    homeListAdapter.notifyDataSetChanged();
                }));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

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
        }).attachToRecyclerView(binding.rvMovies);
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

    public void getData(boolean sort, Activity mainActivity) {
        DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                .taskDao()
                .getAll("Movies").observe(getViewLifecycleOwner(), tasks -> {
            mTasks.clear();
            mTasks.addAll(tasks);
            homeListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemDeleteClickListner(int index) {

    }
}