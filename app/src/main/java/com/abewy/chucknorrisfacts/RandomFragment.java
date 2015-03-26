package com.abewy.chucknorrisfacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.ActivityChooserModel;
import android.text.AndroidCharacter;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.cocosw.bottomsheet.BottomSheet;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import fr.castorflex.android.flipimageview.library.FlipImageView;

/**
 * Created by Jonathan on 04/03/2015.
 */
public class RandomFragment extends Fragment
{
    private static ArrayList<CNFact> mFacts;
    private static ArrayList<CNFact> mRemainingFacts;

    private CNFact mFact;
    private TextSwitcher mFactText;
    private TextSwitcher mId;
    private ScrollView mScrollView;
    private int accentColor;
    private FlipImageView favoriteMenuItem;

    public static RandomFragment newInstance()
    {
        return new RandomFragment();
    }

    public RandomFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_random, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ImageView header = (ImageView) view.findViewById(R.id.header);
        final FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.random_button);

        mFacts = FactManager.getFacts();

        mFactText = (TextSwitcher) view.findViewById(R.id.fact);
        mId = (TextSwitcher) view.findViewById(R.id.number);

        mFactText.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                View view = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_random_fact, null, false);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                view.setLayoutParams(params);
                return view;
            }
        });

        mId.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                return getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_random_id, null, false);
            }
        });

        Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);

        mFactText.setInAnimation(in);
        mFactText.setOutAnimation(out);

        mId.setInAnimation(in);
        mId.setOutAnimation(out);

        ((TextView) mFactText.getCurrentView()).setMovementMethod(new ScrollingMovementMethod());
        ((TextView) mFactText.getNextView()).setMovementMethod(new ScrollingMovementMethod());

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displayRandom();
            }
        });

        displayRandom();
    }

    private void displayRandom()
    {
        mFact = getRandomFact();

        String lowerCaseFact = Html.fromHtml(mFact.fact).toString().toLowerCase();
        int chuck = -1, norris = -1;

        SpannableString ss = new SpannableString(Html.fromHtml(mFact.fact));
        int color = getResources().getColor(R.color.colorAccent);

        while ((chuck = lowerCaseFact.indexOf("chuck", chuck + 1)) != -1)
        {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
            ss.setSpan(styleSpan, chuck, chuck + 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(span, chuck, chuck + 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        while ((norris = lowerCaseFact.indexOf("norris", norris + 1)) != -1)
        {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
            ss.setSpan(styleSpan, norris, norris + 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            ss.setSpan(span, norris, norris + 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        ((TextView) mFactText.getNextView()).setScrollY(0);

        mFactText.setText(ss);
        mId.setText("# " + mFact.id);

        if (favoriteMenuItem != null)
        {
            favoriteMenuItem.setFlipped(FactManager.isFavorite(mFact) ? true : false, true);
        }
        // getActivity().supportInvalidateOptionsMenu();
    }

    private CNFact getRandomFact()
    {
        if (mRemainingFacts == null)
        {
            mRemainingFacts = new ArrayList<>();
        }

        if (mRemainingFacts.size() == 0)
        {
            mRemainingFacts.addAll(mFacts);
        }

        int index = (int) Math.floor(Math.random() * mRemainingFacts.size());
        CNFact fact = mRemainingFacts.get(index);

        mRemainingFacts.remove(index);

        return fact;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(Menu.NONE, R.id.menu_share, Menu.NONE, R.string.action_share).setIcon(R.drawable.ic_share_variant_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem item = menu.add(Menu.NONE, R.id.menu_favorite, Menu.NONE, FactManager.isFavorite(mFact) ? R.string.action_remove_favorite : R.string.action_add_favorite);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setActionView(R.layout.ab_favorite);

        favoriteMenuItem = (FlipImageView) item.getActionView();
        favoriteMenuItem.setFlipped(FactManager.isFavorite(mFact) ? true : false, true);
        favoriteMenuItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (FactManager.isFavorite(mFact))
                {
                    FactManager.removeFromFavorites(mFact);
                    favoriteMenuItem.setFlipped(false, true);
                }
                else
                {
                    FactManager.addToFavorites(mFact);
                    favoriteMenuItem.setFlipped(true, true);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_share)
        {
            getShareActions(new BottomSheet.Builder(getActivity()).grid().title("Share With"), mFact.fact).limit(R.integer.bs_initial_grid_row).build().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomSheet.Builder getShareActions(BottomSheet.Builder builder, String text)
    {
        PackageManager pm = getActivity().getPackageManager();

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "\"" + text + "\"\nvia Chuck Norris Facts app (https://play.google.com/store/apps/details?id=com.abewy.lrpa&hl=en)");
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, "\"" + text + "\"\nvia <a href=\"https://play.google.com/store/apps/details?id=com.abewy.lrpa&hl=en\">Chuck Norris Facts app</a>");

        final ActivityChooserModel dataModel = ActivityChooserModel.get(getActivity(), "share_history.xml");
        dataModel.setIntent(shareIntent);

        final int n = dataModel.getActivityCount();

        // Populate the sub-menu with a sub set of the activities.
        for (int i = 0; i < n; i++)
        {
            ResolveInfo activity = dataModel.getActivity(i);
            builder.sheet(i, activity.loadIcon(pm), activity.loadLabel(pm));
        }

        builder.listener(new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent launchIntent = dataModel.chooseActivity(which);
                if (launchIntent != null)
                {
                    if (Build.VERSION.SDK_INT >= 21)
                    {
                        final String action = launchIntent.getAction();

                        if (Intent.ACTION_SEND.equals(action) ||
                                Intent.ACTION_SEND_MULTIPLE.equals(action))
                        {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        }
                    }
                    else
                    {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    }

                    startActivity(launchIntent);
                }
            }
        });
        return builder;
    }
}
