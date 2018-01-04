package com.moe.yaohuo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.support.design.widget.CollapsingToolbarLayout;
import com.moe.entity.UserItem;
import com.moe.utils.UserUtils;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.utils.ImageLoad;
import com.moe.utils.ImageCache;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import java.util.List;
import com.moe.adapter.ViewPagerAdapter;
import java.util.ArrayList;
import android.support.v7.widget.LinearLayoutManager;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import android.view.Menu;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.moe.adapter.FragmentAdapter;
import com.moe.fragment.BbsListFragment;
import com.moe.entity.BbsItem;
import com.moe.fragment.ReplyFragment;
import android.support.design.widget.AppBarLayout;
import com.moe.fragment.PictureAlbum;
import android.widget.Toast;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.animation.Animator;
import android.view.MotionEvent;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import com.moe.fragment.ActivedFragment;
import com.moe.fragment.MessageFragment;
import android.os.Build;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import android.renderscript.RenderScript;
import android.renderscript.Allocation;
import android.renderscript.ScriptIntrinsicBlur;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import android.graphics.Bitmap;
import android.renderscript.Element;
import android.view.Gravity;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.content.res.TypedArray;

public class UserSpaceActivity extends BaseActivity implements View.OnClickListener,UserUtils.Callback,AppBarLayout.OnOffsetChangedListener
{
//	private boolean isAnime;
//	private float oldx,oldy,X;
//	private int mode;
//	private View view,background;
//	private long time;
	
	private int uid;
	private List<Fragment> list;
	private ImageView logo_background,logo;
	private UserItem ui;
	private ViewPager vp;
	private CollapsingToolbarLayout ctl;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_space_view);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		//toolbar.setTitleTextColor(0xffffffff);
		//toolbar.setTitle(null);
		if(Build.VERSION.SDK_INT>=19)
		{
		CollapsingToolbarLayout.LayoutParams param=(CollapsingToolbarLayout.LayoutParams)toolbar.getLayoutParams();
		param.topMargin=getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
		}
		ctl=(CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout);
		TypedArray ta=obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.colorControlNormal});
		ctl.setCollapsedTitleTextColor(ta.getColor(0,0xffffffff));
		ta.recycle();
		((AppBarLayout)findViewById(R.id.appbarlayout)).addOnOffsetChangedListener(this);
		//toolbar.setLayoutParams(param);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(null);
		logo_background=(ImageView) findViewById(R.id.logo_background);
		logo=(ImageView)findViewById(android.R.id.icon);
		logo.setOnClickListener(this);
		TabLayout tl=(TabLayout)findViewById(R.id.tablayout);
		vp=(ViewPager)findViewById(R.id.viewpager);
		list=new ArrayList<>();
		vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(),list));
		vp.setOverScrollMode(vp.OVER_SCROLL_NEVER);
		tl.setTabMode(tl.MODE_SCROLLABLE);
		tl.setupWithViewPager(vp,true);
		if(savedInstanceState!=null){
			uid=savedInstanceState.getInt("id");
			ui=savedInstanceState.getParcelable("ui");
			handler.sendEmptyMessage(0);
			list.addAll(getSupportFragmentManager().getFragments());
			vp.getAdapter().notifyDataSetChanged();
		}else{
			uid=getIntent().getIntExtra("uid",uid);
		}
		if(ui==null)
			UserUtils.loadUserItem(this,uid,this);
			else
			onLoad(ui);
		if(list.size()==0){
			BbsListFragment send_bbs=new BbsListFragment();
			Bundle b=new Bundle();
			BbsItem bi=new BbsItem();
			bi.setAction("search");
			bi.setKey(uid+"");
			bi.setType("pub");
			b.putParcelable("bbs",bi);
			send_bbs.setArguments(b);
			list.add(send_bbs);
		}
		if(list.size()==1){
			ReplyFragment reply=new ReplyFragment();
			Bundle b=new Bundle();
			b.putInt("uid",uid);
			reply.setArguments(b);
			list.add(reply);
		}
		if(list.size()==2){
			ActivedFragment pa=new ActivedFragment();
			Bundle b=new Bundle();
			b.putInt("uid",uid);
			pa.setArguments(b);
			list.add(pa);
		}
		if(list.size()==3){
			MessageFragment mf=new MessageFragment();
			Bundle b=new Bundle();
			b.putInt("uid",uid);
			mf.setArguments(b);
			list.add(mf);
		}
		vp.getAdapter().notifyDataSetChanged();
		
		
	}

	@Override
	public void onOffsetChanged(AppBarLayout p1, int p2)
	{
		if(p2==-p1.getTotalScrollRange())
			ctl.setTitle(ui!=null?ui.getName():null);
			else
		ctl.setTitle(null);
	}


	@Override
	public void onLoad(UserItem ui)
	{
		if(ui!=null){
			this.ui=ui;
			setTitle(ui.getName());
			Glide.with(UserSpaceActivity.this).load(ui.getLogo()).error(getResources().getDrawable(R.drawable.yaohuo)).diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);

			Glide.with(UserSpaceActivity.this).load(ui.getLogo()).asBitmap().listener(new RequestListener<String,Bitmap>(){

					@Override
					public boolean onException(Exception p1, String p2, Target<Bitmap> p3, boolean p4)
					{
						// TODO: Implement this method
						return false;
					}

					@Override
					public boolean onResourceReady(Bitmap p1, String p2, Target<Bitmap> p3, boolean p4, boolean p5)
					{
						RenderScript rs=RenderScript.create(getApplicationContext());
						Bitmap blurredBitmap = p1.copy(Bitmap.Config.ARGB_8888, true);

						final Allocation input = Allocation.createFromBitmap(rs,blurredBitmap,Allocation.MipmapControl.MIPMAP_FULL,Allocation.USAGE_SHARED);
						final Allocation output = Allocation.createTyped(rs,input.getType());
						//(3)
						// Load up an instance of the specific script that we want to use.
						ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
						//(4)
						scriptIntrinsicBlur.setInput(input);
						//(5)
						// Set the blur radius
						scriptIntrinsicBlur.setRadius(25);
						//(6)
						// Start the ScriptIntrinisicBlur
						scriptIntrinsicBlur.forEach(output);
						//(7)
						// Copy the output to the blurred bitmap
						output.copyTo(blurredBitmap);
						//(8)
						rs.destroy();
						//blurredBitmap.recycle();
						//logo_background.setImageBitmap(blurredBitmap);
						int[] b=new int[blurredBitmap.getByteCount()];
						blurredBitmap.getPixels(b,0,blurredBitmap.getWidth(),0,0,blurredBitmap.getWidth(),blurredBitmap.getHeight());
						p1.setPixels(b,0,blurredBitmap.getWidth(),0,0,p1.getWidth(),p1.getHeight());
						blurredBitmap.recycle();
						return false;
					}
				}).diskCacheStrategy(DiskCacheStrategy.ALL).into(logo_background);
			
		}
	}


	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(0,0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//if(ui!=null&&ui.getUid()!=getSharedPreferences("moe",0).getInt("uid",-1))
		getMenuInflater().inflate(R.menu.user_space,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
			case R.id.leave_nessage:
				startActivityForResult(new Intent(this,LeaveMessageActivity.class).putExtra("uid",uid),521);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("id",uid);
		outState.putParcelable("ui",ui);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode){
			case 392:
			case 521:
				if(resultCode==RESULT_OK&&list.size()==4){
					MessageFragment msgf=(MessageFragment)list.get(3);
					try{msgf.onRefresh();}catch(Exception e){}
					}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					break;
				case 1:
					Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
	};

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case android.R.id.icon:
				if(ui!=null)
				startActivity(new Intent(this,ViewImageActivity.class).setData(Uri.parse(ui.getLogo())));
				break;
		}
	}

	@Override
	protected void onStop()
	{
		Glide.get(this).clearMemory();
		super.onStop();
	}

	/*@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if(isAnime)return true;
		switch(event.getAction()){
			case event.ACTION_DOWN:
				oldx=event.getRawX();
				if(oldx<30){
					oldy=event.getRawY();
					mode=0;
					time=System.currentTimeMillis();
					//super.dispatchTouchEvent(event);
					return true;
				}else{
					oldy=-1;
					break;
				}
			case event.ACTION_MOVE:
				if(oldy==-1)break;
				switch(mode){
					case -1://横向滚动
						float lx=X+event.getRawX()-oldx;
						view.setX(lx<0?0:lx);
						background.setBackgroundColor(Color.argb((int)((1-lx/background.getWidth())*255/2),0,0,0));
						return true;
					case 0:
						if(Math.abs(oldy-event.getRawY())<5&&event.getRawX()>oldx)
						{mode=-1;
							X=view.getX();
						}else{
							mode=-1;
						}
						dispatchTouchEvent(event);
						break;
					case 1:
						break;
				}
				break;
			case event.ACTION_UP:
				if(oldy==-1)break;
				if(mode==-1){
					if(System.currentTimeMillis()-time<200||view.getX()>view.getWidth()*0.25)
						animeFinish();
					else
						animeResume();
				}
				break;
		}

		return super.dispatchTouchEvent(event);
	}
*/
	/*private void animeResume()
	{
		float[] data=new float[]{view.getX(),0};
		Animator anim=ObjectAnimator.ofFloat(view,"X",data);
		anim.setDuration((int)(data[0]/view.getWidth()*500));
		anim.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					isAnime=true;
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					isAnime=false;
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					// TODO: Implement this method
				}
			});
		anim.start();
	}
	private void animeFinish(){
		float[] data=new float[]{view.getX(),view.getWidth()};
		AnimatorSet anim=new AnimatorSet();
		Animator trans=ObjectAnimator.ofFloat(view,"X",data);
		ValueAnimator alpha=ObjectAnimator.ofInt(new int[]{(int)((1-view.getX()/view.getWidth())*255/2),0});
		alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					background.setBackgroundColor(Color.argb(p1.getAnimatedValue(),0,0,0));
				}
			});
		anim.addListener(new Animator.AnimatorListener(){

				@Override
				public void onAnimationStart(Animator p1)
				{
					isAnime=true;
				}

				@Override
				public void onAnimationEnd(Animator p1)
				{
					finish();
				}

				@Override
				public void onAnimationCancel(Animator p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationRepeat(Animator p1)
				{
					// TODO: Implement this method
				}
			});
		anim.playTogether(new Animator[]{trans,alpha});
		anim.setDuration((int)((data[1]-data[0])/data[1]*500));
		anim.start();
	}*/
}
