package com.moe.download;
import java.util.*;
import java.lang.reflect.*;

public class Filter
{
	public static<T extends Object> T[] filter(T[] array,Class<T> c,Filter filter){
		ArrayList<T> list=new ArrayList<>();
		for(T t:array){
			if(filter.isAccept(t))
				list.add(t);
		}
		return list.toArray((T[])Array.newInstance(c,0));
	}
	public static abstract class Filter<T>{
		public abstract boolean isAccept(T item);
	}
}
