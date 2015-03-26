package com.abewy.chucknorrisfacts;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.internal.widget.ActivityChooserModel;
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

import java.util.List;

import fr.castorflex.android.flipimageview.library.FlipImageView;
import jp.wasabeef.recyclerview.animators.internal.ViewHelper;

/**
 * Created by Jonathan on 12/03/2015.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FactViewHolder> {

    private List<CNFact> facts;
    private int mDuration = 300;
    private int mLastPosition = -1;

    public FavoritesAdapter(List<CNFact> facts) {
        this.facts = facts;
    }

    @Override
    public int getItemCount() {
        return facts.size();
    }

    @Override
    public void onBindViewHolder(FactViewHolder viewHolder, int position) {
        final CNFact object = facts.get(position);

        String lowerCaseFact = Html.fromHtml(object.fact).toString().toLowerCase();
        int chuck = -1, norris = -1;

        SpannableString ss = new SpannableString(Html.fromHtml(object.fact));
        int color = viewHolder.fact.getResources().getColor(R.color.colorAccent);

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

        viewHolder.fact.setText(ss);
        viewHolder.id.setText("# " + object.id);

        final FlipImageView favorite = viewHolder.favorite;

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
                    remove(facts.indexOf(object));
                    FactManager.removeFromFavorites(object);
                }
                else
                {
                    FactManager.addToFavorites(object);
                    favorite.setFlipped(true, true);
                }
            }
        });

        final ImageButton share = viewHolder.share;

        share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("FavoritesAdapter", "click");
                getShareActions(share.getContext(), new BottomSheet.Builder(share.getContext(), com.cocosw.bottomsheet.R.style.BottomSheet_Dialog).grid().title("Share With"), object.fact).limit(R.integer.bs_initial_grid_row).build().show();
            }
        });

        if (position > mLastPosition) {
            for (Animator anim : getAnimators(viewHolder.itemView)) {
                anim.setDuration(mDuration).start();
            }
            mLastPosition = position;
        } else {
            ViewHelper.clear(viewHolder.itemView);
        }
    }

    protected Animator[] getAnimators(View view) {
        return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)};
    }

    public void remove(int position) {
        facts.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public FactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_fact, viewGroup, false);

        return new FactViewHolder(itemView);
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

    public static class FactViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView fact;
        protected TextView id;
        protected FlipImageView favorite;
        protected ImageButton share;

        public FactViewHolder(View v)
        {
            super(v);
            fact = (TextView) v.findViewById(R.id.fact);
            id = (TextView) v.findViewById(R.id.number);
            favorite = (FlipImageView) v.findViewById(R.id.favorite);
            share = (ImageButton) v.findViewById(R.id.share);
        }
    }
}
