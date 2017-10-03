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
import com.moe.fragment.ListFragment;
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

public class UserInfoActivity extends BaseActivity implements View.OnClickListener
{
	private boolean isAnime;
	private float oldx,oldy,X;
	private int mode;
	private View view,background;
	private long time;
	
	private int id;
	private List<Fragment> list;
	private ImageView logo,sex;
	private TextView username,summary;
	private UserItem ui;
	private ViewPager vp;
	private View info;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		view=findViewById(R.id.coordinatorlayout);
		background=getWindow().getDecorView();
		ViewCompat.setElevation(view,60);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(0xffffffff);
		toolbar.setTitle("");
		CollapsingToolbarLayout.LayoutParams param=(CollapsingToolbarLayout.LayoutParams)toolbar.getLayoutParams();
		param.topMargin=getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
		//toolbar.setLayoutParams(param);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		info=findViewById(R.id.user_information);
		logo=(ImageView)findViewById(android.R.id.icon);
		logo.setOnClickListener(this);
		username=(TextView)findViewById(android.R.id.title);
		sex=(ImageView)findViewById(R.id.user_info_sex);
		summary=(TextView)findViewById(android.R.id.summary);
		TabLayout tl=(TabLayout)findViewById(R.id.tablayout);
		vp=(ViewPager)findViewById(R.id.viewpager);
		list=new ArrayList<>();
		vp.setAdapter(new FragmentAdapter(getSupportFragmentManager(),list));
		vp.setOverScrollMode(vp.OVER_SCROLL_NEVER);
		tl.setTabMode(tl.MODE_SCROLLABLE);
		tl.setupWithViewPager(vp,true);
		if(savedInstanceState!=null){
			id=savedInstanceState.getInt("id");
			ui=savedInstanceState.getParcelable("ui");
			handler.sendEmptyMessage(0);
			list.addAll(getSupportFragmentManager().getFragments());
			vp.getAdapter().notifyDataSetChanged();
		}else{
			id=getIntent().getIntExtra("uid",id);
			loadInfo();
		}
		
		info.setVisibility(info.VISIBLE);
		info.setOnClickListener(UserInfoActivity.this);
		
		
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
		getMenuInflater().inflate(R.menu.user_info,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
			case R.id.send:
				if(ui==null)return true;
				startActivity(new Intent(this,SendMessageActivity.class).putExtra("uid",ui.getUid()));
				break;
			case R.id.friend:
				if(ui==null)return true;
				if(ui.getUid()!=PreferenceUtils.getUid(this))
					new Thread(){
						public void run(){
							try
							{
								Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/FriendList.aspx")
									.data("siteid", "1000")
									.data("action", "addfriend")
									.data("friendtype", "0")
									.data("touserid", ui.getUid() + "")
									.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
								handler.obtainMessage(1,"已加为好友").sendToTarget();
							}
							catch (IOException e)
							{handler.obtainMessage(1,"网络连接失败").sendToTarget();}
						}
					}.start();
				break;
			case R.id.black:
				if(ui==null)return true;
				if(ui.getUid()!=PreferenceUtils.getUid(this))
				new Thread(){
					public void run(){
						try
						{
							Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "/bbs/FriendList.aspx")
								.data("siteid", "1000")
								.data("action", "addfriend")
								.data("friendtype", "1")
								.data("touserid", ui.getUid() + "")
								.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
								.userAgent(PreferenceUtils.getUserAgent())
								.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
								handler.obtainMessage(1,"已拉黑").sendToTarget();
						}
						catch (IOException e)
						{handler.obtainMessage(1,"网络连接失败").sendToTarget();}
					}
				}.start();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putInt("id",id);
		outState.putParcelable("ui",ui);
		super.onSaveInstanceState(outState);
	}

	
	
	private void loadInfo(){
		new Thread(){
			public void run(){
				/*try
				{
					Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + "bbs/userinfo.aspx")
						.data("touserid", id + "")
						.userAgent(PreferenceUtils.getUserAgent())
						.cookie("sidyaohuo", PreferenceUtils.getCookie(getApplicationContext()))
						.get();
				}
				catch (IOException e)
				{}*/
				ui=UserUtils.getUserInfo(getApplicationContext(),id);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					if(ui!=null){
						Glide.with(UserInfoActivity.this).load(ui.getLogo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);
						//ImageCache.load(ui.getLogo(),logo);
						username.setText(ui.getName());
						summary.setText(ui.getSign());
						if(ui.getSex()==1)
							sex.setImageResource(R.drawable.ic_gender_female);
							else
							sex.setImageResource(R.drawable.ic_gender_male);
						//if(ui.getUid()==PreferenceUtils.getUid(getApplicationContext())){
							//logo.setOnClickListener(UserInfoActivity.this);

						//}
						if(list.size()==0){
						ListFragment send_bbs=new ListFragment();
							Bundle b=new Bundle();
						BbsItem bi=new BbsItem();
						bi.setAction("search");
						bi.setKey(ui.getUid()+"");
						bi.setType("pub");
						b.putParcelable("bbs",bi);
						send_bbs.setArguments(b);
						list.add(send_bbs);
						ReplyFragment reply=new ReplyFragment();
							b=new Bundle();
							b.putInt("uid",ui.getUid());
							reply.setArguments(b);
							list.add(reply);
						vp.getAdapter().notifyDataSetChanged();
						}
						if(list.size()==2){
						PictureAlbum pa=new PictureAlbum();
						Bundle b=new Bundle();
						b.putInt("uid",ui.getUid());
						pa.setArguments(b);
						list.add(pa);
						vp.getAdapter().notifyDataSetChanged();
						}
					}
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
			case R.id.user_information:
				startActivity(new Intent(this,UserDataActivity.class).putExtra("ui",ui));
				break;
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
	private void animeResume()
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
	}
}
