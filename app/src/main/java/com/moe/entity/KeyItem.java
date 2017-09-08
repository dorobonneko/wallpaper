package com.moe.entity;
import android.os.Parcel;
import android.os.Parcelable;

public class KeyItem extends BbsItem
{
	public KeyItem(Parcel p1){
		super(p1);
	}
	public KeyItem(){}
	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		// TODO: Implement this method
		super.writeToParcel(p1, p2);
	}
	public static Parcelable.Creator<KeyItem> CREATOR=new Parcelable.Creator<KeyItem>(){

		@Override
		public KeyItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new KeyItem(p1);
		}

		@Override
		public KeyItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new KeyItem[p1];
		}


	};
}
