package com.abewy.chucknorrisfacts;

import android.content.Context;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.skocken.efficientadapter.lib.adapter.SimpleAdapter;
import com.skocken.efficientadapter.lib.viewholder.AbsViewHolder;

import java.io.InputStream;
import java.util.ArrayList;

import fr.castorflex.android.flipimageview.library.FlipImageView;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

/**
 * Created by Jonathan on 04/03/2015.
 */
public class ListFragment extends Fragment
{
    private RecyclerView mRecyclerView;

    public static ListFragment newInstance()
    {
        return new ListFragment();
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        InputStream is = getResources().openRawResource(R.raw.facts);

        ArrayList<CNFact> facts = FactManager.getFacts();

        SimpleAdapter adapter = new SimpleAdapter<CNFact>(
                R.layout.item_fact, FactViewHolder.class,
                facts);
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
    }

    public static class FactViewHolder extends AbsViewHolder<CNFact>
    {
        public FactViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override
        protected void updateView(Context context, final CNFact object)
        {
            TextView fact = (TextView) findViewByIdEfficient(R.id.fact);

            String lowerCaseFact = Html.fromHtml(object.fact).toString().toLowerCase();
            int chuck = -1, norris = -1;

            SpannableString ss = new SpannableString(Html.fromHtml(object.fact));
            int color = context.getResources().getColor(R.color.colorAccent);

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

            fact.setText(ss);

            TextView id = (TextView) findViewByIdEfficient(R.id.number);
            id.setText("# " + object.id);

            final FlipImageView favorite = (FlipImageView) findViewByIdEfficient(R.id.favorite);

            if (FactManager.isFavorite(object))
                favorite.setFlipped(true, false);
            else
                favorite.setFlipped(false, false);

            favorite.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (FactManager.isFavorite(object))
                    {
                        FactManager.removeFromFavorites(object);
                        favorite.setFlipped(false, true);
                    }
                    else
                    {
                        FactManager.addToFavorites(object);
                        favorite.setFlipped(true, true);
                    }
                }
            });

            final ImageButton share = (ImageButton) findViewByIdEfficient(R.id.share);

            share.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d("FavoritesAdapter", "click");
                    getShareActions(share.getContext(), new BottomSheet.Builder(share.getContext(), com.cocosw.bottomsheet.R.style.BottomSheet_Dialog).grid().title("Share With"), object.fact).limit(R.integer.bs_initial_grid_row).build().show();
                }
            });
        }

        private BottomSheet.Builder getShareActions(final Context context, BottomSheet.Builder builder, String text)
        {
            PackageManager pm = context.getPackageManager();

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "\"" + text + "\"\nvia Chuck Norris Facts app (https://play.google.com/store/apps/details?id=com.abewy.lrpa&hl=en)");
            shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, "\"" + text + "\"\nvia <a href=\"https://play.google.com/store/apps/details?id=com.abewy.lrpa&hl=en\">Chuck Norris Facts app</a>");

            final ActivityChooserModel dataModel = ActivityChooserModel.get(context, "share_history.xml");
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

                        context.startActivity(launchIntent);
                    }
                }
            });
            return builder;
        }
    }
}
