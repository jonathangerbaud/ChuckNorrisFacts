package com.abewy.chucknorrisfacts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Created by Jonathan on 12/03/2015.
 */
public class FactManager
{
    private static final String FACTS_ID = "factsId";
    private static ArrayList<CNFact> facts;
    private static CNFact[] factsArray;
    private static ArrayList<CNFact> favorites;
    private static SharedPreferences prefs;

    public static void init(Activity activity)
    {
        InputStream is = activity.getResources().openRawResource(R.raw.facts);

        facts = new ArrayList<>();

        try
        {
            facts = (ArrayList<CNFact>) LoganSquare.parseList(is, CNFact.class);
            factsArray = facts.toArray(new CNFact[facts.size()]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        prefs = activity.getSharedPreferences(activity.getPackageName() + activity.getResources().getConfiguration().locale.getCountry(), Context.MODE_PRIVATE);

        favorites = get();
        sort();
    }

    public static void addToFavorites(CNFact fact)
    {
        favorites.add(fact);

        sort();

        save();
    }

    public static void removeFromFavorites(CNFact fact)
    {
        favorites.remove(fact);

        save();
    }

    public static ArrayList<CNFact> getFacts()
    {
        return facts;
    }

    public static ArrayList<CNFact> getFavorites()
    {
        return favorites;
    }

    private static void sort()
    {
        Collections.sort(favorites, new Comparator<CNFact>()
        {
            @Override
            public int compare(CNFact fact1, CNFact fact2)
            {
                if (fact1.id > fact2.id)
                    return 1;

                if (fact1.id < fact2.id)
                    return -1;

                return 0;
            }
        });
    }

    private static void save()
    {
        HashSet<String> set = new HashSet<String>();
        for (CNFact f : favorites)
        {
            set.add(String.valueOf(f.id));
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(FACTS_ID, set);
        editor.commit();
    }

    private static ArrayList<CNFact> get()
    {
        ArrayList<CNFact> favorites = new ArrayList<CNFact>();

        Set<String> ids = prefs.getStringSet(FACTS_ID, null);

        if (ids != null)
        {
            for (String id : ids)
            {
                int value = Integer.parseInt(id);

                CNFact fact = getFact(value);
                if (fact != null)
                    favorites.add(fact);
            }
        }

        return favorites;
    }

    public static boolean isFavorite(CNFact fact)
    {
        for (CNFact f : favorites)
        {
            if (f.id == fact.id)
                return true;
        }

        return false;
    }

    private static CNFact getFact(final int id)
    {
        CNFact search = new CNFact();
        search.id = id;

        int index = Arrays.binarySearch(factsArray, search, new Comparator<CNFact>()
        {
            @Override
            public int compare(CNFact lhs, CNFact rhs)
            {
                if (lhs.id > rhs.id)
                    return 1;
                else if (lhs.id < rhs.id)
                    return -1;

                return 0;
            }
        });

        CNFact found = facts.get(index);

        return found;
    }
}
