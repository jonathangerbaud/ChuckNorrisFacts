package com.abewy.chucknorrisfacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jonathan on 04/03/2015.
 */
public class AboutFragment extends Fragment
{

    public static AboutFragment newInstance()
    {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

}
