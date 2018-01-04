package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import com.moe.fragment.BbsListFragment;
import com.moe.entity.BbsItem;
import android.widget.Toast;
import android.content.Intent;
import com.moe.utils.PreferenceUtils;
import java.lang.reflect.Field;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

public class ListActivity extends EventActivity implements View.OnClickListener
{
	private BbsItem bbs;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
		try
		{
			Field title=Toolbar.class.getDeclaredField("mTitleTextView");
			title.setAccessible(true);
			TextView o=(TextView) title.get(toolbar);
			o.setId(android.R.id.title);
			o.setOnClickListener(this);
		}catch(Exception e){}
		View v=findViewById(R.id.edit);
		v.setVisibility(v.VISIBLE);
		v.setOnClickListener(this);
		if(savedInstanceState==null){
			BbsListFragment list=new BbsListFragment();
			bbs=getIntent().getParcelableExtra("bbs");
			Bundle b=new Bundle();
			b.putParcelable("bbs",bbs);
			list.setArguments(b);
			getSupportFragmentManager().beginTransaction().add(R.id.main_index,list).commit();
		}else{
			bbs=savedInstanceState.getParcelable("bbs");
		}
		setTitle(bbs.getTitle());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("bbs",bbs);
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.edit:
				if(!PreferenceUtils.isLogin(this))
					Toast.makeText(getApplicationContext(),"请先登录",Toast.LENGTH_SHORT).show();
					else
					startActivity(new Intent(this,AddBbsActivity.class).putExtra("bbs",bbs));
				break;
			case android.R.id.title:
				RecyclerView rv=(RecyclerView)findViewById(R.id.list);
				if(rv!=null){
					rv.smoothScrollToPosition(0);
				}
				break;
		}
	}

	
}
