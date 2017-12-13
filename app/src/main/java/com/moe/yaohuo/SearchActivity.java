package com.moe.yaohuo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.MenuItem;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Menu;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatImageButton;
import com.moe.entity.BbsItem;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Intent;
import com.moe.fragment.ListFragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import java.util.List;
import java.util.ArrayList;
import com.moe.adapter.SearchHistoryAdapter;
import com.moe.database.SearchHistoryDatabase;
import android.view.View;
import com.moe.adapter.SearchHistoryAdapter.ViewHolder;
import android.view.inputmethod.InputMethodManager;

public class SearchActivity extends EventActivity implements TextWatcher,SearchHistoryAdapter.OnItemClickListener,SearchHistoryAdapter.OnAddListener
{
	private EditText key;
	private BbsItem bi;
	private ListFragment list;
	private ArrayList<String> history;
	private SearchHistoryAdapter sha;
	private SearchHistoryDatabase shd;
	private RecyclerView history_view;
	private boolean canload=true;
	private InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		shd=SearchHistoryDatabase.getInstance(this);
		super.onCreate(savedInstanceState);
		key=new EditText(this);
		key.setTextColor(getResources().getColor(R.color.icons));
		key.setSingleLine();
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setCustomView(key,new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.MATCH_PARENT));
		key.addTextChangedListener(this);
		if(savedInstanceState!=null){
			key.setText(savedInstanceState.getCharSequence("key"));
			list=(ListFragment)getSupportFragmentManager().findFragmentByTag("list");
			bi=savedInstanceState.getParcelable("bbs");
			this.history=savedInstanceState.getStringArrayList("history");
		}
		LayoutInflater.from(this).inflate(R.layout.search_view,(ViewGroup)findViewById(R.id.main_index),true);
		history_view=(RecyclerView)findViewById(R.id.search_history);
		history_view.setLayoutManager(new LinearLayoutManager(this));
		if(this.history==null)this.history=new ArrayList<>();
		history_view.setAdapter(sha=new SearchHistoryAdapter(this.history));
		history_view.setItemAnimator(null);
		sha.setOnItemClickListener(this);
		sha.setOnAddListener(this);
		if(savedInstanceState==null){
			bi=new BbsItem();
			bi.setAction("search");
			bi.setType("title");
			list=new ListFragment();
			Bundle b=new Bundle();
			b.putParcelable("bbs",bi);
			list.setArguments(b);
			getSupportFragmentManager().beginTransaction().add(R.id.content,list,"list").commit();
		afterTextChanged(key.getText());
		}
	}

	@Override
	public void onItemClick(SearchHistoryAdapter sha, SearchHistoryAdapter.ViewHolder vh)
	{
		if(vh.getAdapterPosition()==history.size()){
			shd.clear();
			history.clear();
			sha.notifyDataSetChanged();
			}else{
		onAdd(sha,vh);
		search(history.get(vh.getAdapterPosition()));
		}
	}

	@Override
	public void onAdd(SearchHistoryAdapter sha, SearchHistoryAdapter.ViewHolder vh)
	{
		canload=false;
		key.setText(history.get(vh.getAdapterPosition()));
	}
	private void search(String key){
		history_view.setVisibility(history_view.INVISIBLE);
		shd.insert(key);
		bi.setKey(key);
		list.load(bi);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,0,0,"搜索");
		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.magnify)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			
			case 0:
				String key=this.key.getText().toString().trim();
				if(key.length()>0){
					imm.hideSoftInputFromWindow(this.key.getWindowToken(),imm.HIDE_NOT_ALWAYS);
				search(key);
				}
				break;
				default:
		return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		if(!canload){canload=true;return;}
		history_view.setVisibility(history_view.VISIBLE);
		if(history.size()>0){
		history.clear();
		sha.notifyDataSetChanged();
		}
		history.addAll(shd.query(p1.toString()));
		sha.notifyItemRangeInserted(0,history.size());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("bbs",bi);
		outState.putCharSequence("key",key.getText());
		outState.putStringArrayList("history",history);
		super.onSaveInstanceState(outState);
	}
	
}
