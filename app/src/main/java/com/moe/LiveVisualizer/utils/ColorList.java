package com.moe.LiveVisualizer.utils;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Arrays;

public class ColorList
{
	private int[] buffer;
	private int capacity=7;
	private int size;
	public ColorList(){
		buffer=new int[capacity];
	}

	public int getRandom()
	{
		return get((int)(Math.random()*size));
	}
	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size==0;
	}

	public int[] toArray()
	{
		return Arrays.copyOf(buffer,size);
	}
	public int get(int index){
		return buffer[index];
	}
	public boolean add(int p1)
	{
		buffer[size]=p1;
		size++;
		trim();
		return true;
	}

	public boolean remove(int index)
	{
		for(;index<size;)
		buffer[index]=buffer[++index];
		size--;
		return true;
	}

	public void clear()
	{
		size=0;
	}
	
	
	private void trim(){
		if(size>=buffer.length-1){
			int[] src=buffer;
			buffer=new int[src.length+(int)(capacity*0.5f)];
			System.arraycopy(src,0,buffer,0,size);
		}
	}
}
