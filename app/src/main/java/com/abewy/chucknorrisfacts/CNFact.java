package com.abewy.chucknorrisfacts;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Jonathan on 04/03/2015.
 */
@JsonObject
public class CNFact implements Parcelable
{
    @JsonField
    public int id;

    @JsonField(name = "joke")
    public String fact;

    public CNFact()
    {

    }

    protected CNFact(Parcel in)
    {
        id = in.readInt();
        fact = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(fact);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CNFact> CREATOR = new Parcelable.Creator<CNFact>()
    {
        @Override
        public CNFact createFromParcel(Parcel in)
        {
            return new CNFact(in);
        }

        @Override
        public CNFact[] newArray(int size)
        {
            return new CNFact[size];
        }
    };
}
