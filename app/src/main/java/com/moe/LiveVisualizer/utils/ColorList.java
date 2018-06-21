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

	public synchronized int getRandom()
	{
		return get((int)(Math.random()*size));
	}
	public synchronized int size()
	{
		return size;
	}

	public synchronized boolean isEmpty()
	{
		return size==0;
	}

	public synchronized int[] toArray()
	{
		return Arrays.copyOf(buffer,size);
	}
	public synchronized int get(int index){
		return buffer[index];
	}
	public synchronized boolean add(int p1)
	{
		buffer[size]=p1;
		size++;
		trim();
		return true;
	}

	public synchronized boolean remove(int index)
	{
		for(;index<size;)
		buffer[index]=buffer[++index];
		size--;
		return true;
	}

	public synchronized void clear()
	{
		size=0;
	}
	
	
	private synchronized void trim(){
		if(size>=buffer.length-1){
			int[] src=buffer;
			buffer=new int[src.length+(int)(capacity*0.5f)];
			System.arraycopy(src,0,buffer,0,size);
		}
	}
}
