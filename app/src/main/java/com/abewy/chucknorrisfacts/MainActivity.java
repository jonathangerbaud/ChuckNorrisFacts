package com.abewy.chucknorrisfacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.presage.Presage;
import io.presage.utils.IADHandler;

public class MainActivity extends ActionBarActivity
{
    private Drawer.Result mDrawer;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Presage.getInstance().setContext(this.getBaseContext());
        Presage.getInstance().start();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null)
        {
            setSupportActionBar(mToolbar);
        }

        //Create the mDrawer
        mDrawer = new Drawer()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(false)
                .withHeader(R.layout.drawer_header)
                        //.withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_section1).withIcon(R.drawable.ic_shuffle_grey600_48dp).withSelectedIcon(R.drawable.ic_shuffle_cyana700_48dp),
                        new PrimaryDrawerItem().withName(R.string.title_section2).withIcon(R.drawable.ic_view_list_grey600_48dp).withSelectedIcon(R.drawable.ic_view_list_cyana700_48dp),
                        new PrimaryDrawerItem().withName(R.string.title_section3).withIcon(R.drawable.ic_star_grey600_48dp).withSelectedIcon(R.drawable.ic_star_cyana700_48dp),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.title_section4).withIcon(R.drawable.ic_information_outline_grey600_48dp).withSelectedIcon(R.drawable.ic_information_outline_cyana700_48dp)
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem)
                    {
                        openFragment(position);
                        //mDrawer.setSelection(position);
                    }
                })
                .build();

        FactManager.init(this);

        mDrawer.setSelection(0, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //handle the click on the back arrow click
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!mDrawer.isDrawerOpen())
                {
                    mDrawer.openDrawer();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        //handle the back press :D close the mDrawer first and if the mDrawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen())
        {
            mDrawer.closeDrawer();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void openFragment(int position)
    {
        Log.d("openFragment", "position = " + position);
        Fragment fragment = null;

        switch (position)
        {
            case 0:
            case 1:
            {
                fragment = RandomFragment.newInstance();
                setTitle("");
                mToolbar.setBackgroundResource(R.color.transparent);
                break;
            }
            case 2:
            {
                fragment = ListFragment.newInstance();
                setTitle(R.string.title_section2);
                mToolbar.setBackgroundResource(R.color.colorPrimary);
                break;
            }
            case 3:
            {
                fragment = FavoritesFragment.newInstance();
                setTitle(R.string.title_section3);
                mToolbar.setBackgroundResource(R.color.colorPrimary);
                break;
            }
            case 5:
            {
                fragment = AboutFragment.newInstance();
                setTitle(R.string.title_section4);
                mToolbar.setBackgroundResource(R.color.colorPrimary);
                break;
            }

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Presage.getInstance().adToServe("interstitial", new IADHandler() {

            @Override
            public void onAdNotFound() {
                Log.i("PRESAGE", "ad not found");
            }

            @Override
            public void onAdFound() {
                Log.i("PRESAGE", "ad found");
            }

            @Override
            public void onAdClosed() {
                Log.i("PRESAGE", "ad closed");
            }
        });
    }
}
