package com.abewy.chucknorrisfacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * Created by Jonathan on 04/03/2015.
 */
public class FavoritesFragment extends Fragment
{
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    public static FavoritesFragment newInstance()
    {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mEmptyView = view.findViewById(R.id.empty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new NotifyMeAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        ArrayList<CNFact> facts = FactManager.getFavorites();

        FavoritesAdapter adapter = new FavoritesAdapter(facts);

        mRecyclerView.setAdapter(adapter);
        mEmptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private class NotifyMeAnimator extends LandingAnimator
    {
        @Override
        public void onRemoveFinished(RecyclerView.ViewHolder item)
        {
            super.onRemoveFinished(item);

            mEmptyView.setVisibility(mRecyclerView.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }
}
