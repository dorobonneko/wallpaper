package com.moe.view;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import java.util.HashMap;
import android.support.v4.view.ViewCompat;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import java.util.Collections;
import java.util.Comparator;
import android.view.animation.AlphaAnimation;
import android.os.Handler;
import android.os.Message;

public class ItemAnimation extends SimpleItemAnimator implements Comparator<RecyclerView.ViewHolder>
{
	private ArrayList<RecyclerView.ViewHolder> add=new ArrayList<>();
	private HashMap<RecyclerView.ViewHolder,Animator> addAnime=new HashMap<>();
	private ArrayList<RecyclerView.ViewHolder> remove=new ArrayList<>();
	private HashMap<RecyclerView.ViewHolder,Animation> removeAnime=new HashMap<>();
	
	private RecyclerView rv;
	public ItemAnimation(RecyclerView rv){
		this.rv=rv;
		setAddDuration(150);
	}

	@Override
	public int compare(RecyclerView.ViewHolder p1, RecyclerView.ViewHolder p2)
	{
		return p1.itemView.getId()>p2.itemView.getId()?0:-1;
		//return p1.getAdapterPosition()>p2.getAdapterPosition()?0:-1;
	}

	
	@Override
	public void runPendingAnimations()
	{
		Collections.sort(remove,this);
		Collections.reverse(remove);
		Collections.sort(add,this);
		check();
		
	}

	private void check()
	{
		if(!remove.isEmpty()){
			animeRemove(remove.remove(0));
		}else if(!add.isEmpty()){
			animeAdd(add.remove(0));
		}
	}
	private void animeRemove(final RecyclerView.ViewHolder vh){
		/*TranslateAnimation ta=new TranslateAnimation(0,rv.getWidth(),0,0);
		ta.setDuration(getRemoveDuration());
		ta.setAnimationListener(new Animation.AnimationListener(){

				@Override
				public void onAnimationStart(Animation p1)
				{
					dispatchRemoveStarting(vh);
				}

				@Override
				public void onAnimationEnd(Animation p1)
				{
					removeAnime.remove(vh);
					dispatchRemoveFinished(vh);
					check();
				}

				@Override
				public void onAnimationRepeat(Animation p1)
				{
					// TODO: Implement this method
				}
			});
			removeAnime.put(vh,ta);
			vh.itemView.startAnimation(ta);*/
			AlphaAnimation aa=new AlphaAnimation(1,0);
			aa.setDuration(getRemoveDuration());
			aa.setAnimationListener(new Animation.AnimationListener(){
				@Override
				public void onAnimationStart(Animation p1)
				{
					removeAnime.remove(vh);
					dispatchRemoveStarting(vh);
					handler.sendEmptyMessageDelayed(0,16);
				}

				@Override
				public void onAnimationEnd(Animation p1)
				{
					dispatchRemoveFinished(vh);
					check();
				}

				@Override
				public void onAnimationRepeat(Animation p1)
				{
					// TODO: Implement this method
				}
			});
			vh.itemView.startAnimation(aa);
			removeAnime.put(vh,aa);
	}
	private void animeAdd(final RecyclerView.ViewHolder vh){
		final float[] value=new float[]{0,1};
		Animator anime=ObjectAnimator.ofFloat(vh.itemView,"Alpha",value);
		anime.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					addAnime.remove(vh);
					dispatchAddStarting(vh);
					handler.sendEmptyMessageDelayed(0,16);
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					ViewCompat.setAlpha(vh.itemView,value[1]);
					dispatchAddFinished(vh);
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					ViewCompat.setAlpha(vh.itemView,value[1]);
					dispatchAddFinished(vh);
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					
				}
			});
			anime.setDuration(getAddDuration());
			anime.start();
			addAnime.put(vh,anime);
	}
	@Override
	public void endAnimation(RecyclerView.ViewHolder p1)
	{
		Animator a=addAnime.remove(p1);
		if(a!=null)a.cancel();
		add.remove(p1);
		Animation anime=removeAnime.remove(p1);
		if(anime!=null)anime.cancel();
		remove.remove(p1);
	}

	@Override
	public void endAnimations()
	{
		add.clear();
		for(Animator a:addAnime.values())
		a.cancel();
		remove.clear();
		for(Animation a:removeAnime.values())
		a.cancel();
	}

	@Override
	public boolean isRunning()
	{
		return !add.isEmpty()||!remove.isEmpty();
	}

	@Override
	public boolean animateRemove(RecyclerView.ViewHolder p1)
	{
		Animation anime=removeAnime.remove(p1);
		if(anime!=null)anime.cancel();
		remove.remove(p1);
		remove.add(p1);
		return true;
	}

	@Override
	public boolean animateAdd(RecyclerView.ViewHolder p1)
	{
		Animator anime=addAnime.remove(p1);
		if(anime!=null)anime.cancel();
		add.remove(p1);
		//p1.itemView.setTag(p1.itemView.getTranslationY());
		//ViewCompat.setTranslationY(p1.itemView,rv.getHeight());
		ViewCompat.setAlpha(p1.itemView,0);
		add.add(p1);
		return true;
	}

	@Override
	public boolean animateMove(RecyclerView.ViewHolder p1, int p2, int p3, int p4, int p5)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean animateChange(RecyclerView.ViewHolder p1, RecyclerView.ViewHolder p2, int p3, int p4, int p5, int p6)
	{
		// TODO: Implement this method
		return false;
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			check();
		}
		
	};
	
}
