package com.moe.fragment;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import com.moe.yaohuo.R;
import android.support.design.widget.TabLayout;
import java.util.List;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import android.support.v4.view.ViewPager;
import com.moe.adapter.ViewPagerAdapter;
import com.moe.entity.DownloadItem;
import com.moe.adapter.DownloadAdapter;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.services.DownloadService;
import android.os.Handler;
import android.os.Message;
import com.moe.download.Download;
import com.moe.view.Divider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.MimeTypeMap;
import java.io.File;
import android.net.Uri;
import com.moe.download.DownloadQuery;
import com.moe.download.*;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.graphics.drawable.VectorDrawableCompat;
import com.moe.yaohuo.PreferenceActivity;
import android.view.Menu;
public class DownloadFragment extends AnimeFragment implements View.OnClickListener,
DownloadAdapter.OnItemClickListener,
DownloadAdapter.OnItemLongClickListener,
DownloadQuery.Listener<DownloadItem>
{
	private List<DownloadItem> loading_selected,success_selected;
	private List<DownloadItem> loading,success;
	private DownloadAdapter loading_adapter,success_adapter;
	private List<RecyclerView> list;
	private View delete,cancel;
	private ViewPager vp;
	private RefreshBroadcast broadcast;
	private Animation enter,exit;
	private DownloadQuery<DownloadItem> query=null;
	private QuerySql<DownloadItem> loading_sql,success_sql;
	public DownloadFragment(){
		query=new DownloadQuery<DownloadItem>(DownloadItem.class);
		loading_sql=new QuerySql<>();
		success_sql=new QuerySql<>();
		loading_sql.setArgs(null,"state!=?",new String[]{DownloadService.State.SUCCESS+""},null,null,"time");
		success_sql.setArgs(null,"state=?",new String[]{DownloadService.State.SUCCESS+""},null,null,"time desc");
		loading_sql.setListener(this);
		success_sql.setListener(this);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		list = new ArrayList<>();
		list.add(new RecyclerView(container.getContext()));
		list.add(new RecyclerView(container.getContext()));
		list.get(0).setTag("下载中");
		list.get(1).setTag("已完成");
		//list.get(0).addItemDecoration(new Divider());
		//list.get(1).addItemDecoration(new Divider());
		list.get(0).setLayoutManager(new LinearLayoutManager(getActivity()));
		list.get(1).setLayoutManager(new LinearLayoutManager(getActivity()));
		list.get(0).setAdapter(loading_adapter = new DownloadAdapter(loading = new ArrayList<>(), loading_selected = new ArrayList<>()));
		list.get(1).setAdapter(success_adapter = new DownloadAdapter(success = new ArrayList<>(), success_selected = new ArrayList<>()));
		list.get(0).setNestedScrollingEnabled(false);
		list.get(1).setNestedScrollingEnabled(false);
		list.get(0).addItemDecoration(new Divider(getResources().getDimensionPixelSize(R.dimen.list_padding)));
		list.get(1).addItemDecoration(new Divider(getResources().getDimensionPixelSize(R.dimen.list_padding)));
		
		((DefaultItemAnimator)list.get(0).getItemAnimator()).setSupportsChangeAnimations(false);
		//list.get(1).setItemAnimator(null);
		return inflater.inflate(R.layout.download_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		TabLayout tab=(TabLayout)view.findViewById(R.id.tablayout);
		vp = (ViewPager)view.findViewById(R.id.viewpager);
		tab.setupWithViewPager(vp);
		vp.setAdapter(new ViewPagerAdapter(list));
		delete = view.findViewById(R.id.download_view_delete);
		delete.setOnClickListener(this);
		cancel = view.findViewById(R.id.download_view_cancel);
		cancel.setOnClickListener(this);
		loading_adapter.setOnItemClickListener(this);
		loading_adapter.setOnItemLongClickListener(this);
		success_adapter.setOnItemClickListener(this);
		success_adapter.setOnItemLongClickListener(this);
		enter = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		enter.setDuration(300);
		exit = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		exit.setDuration(300);
		exit.setAnimationListener(new Animation.AnimationListener(){

				@Override
				public void onAnimationStart(Animation p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onAnimationEnd(Animation p1)
				{
					delete.setVisibility(delete.INVISIBLE);
					cancel.setVisibility(cancel.INVISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation p1)
				{
					// TODO: Implement this method
				}
			});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		query.doQuery(loading_sql);
		query.doQuery(success_sql);
		getContext().registerReceiver(broadcast = new RefreshBroadcast(), new IntentFilter(DownloadService.ACTION_REFRESH));
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.add(0,0,0,"设置");
		menu.getItem(0).setIntent(new Intent(getActivity(),PreferenceActivity.class).setAction("download")).setIcon(VectorDrawableCompat.create(getResources(),R.drawable.settings,getActivity().getTheme())).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		if(!hidden){
			query.doQuery(loading_sql);
			query.doQuery(success_sql);
			
		}
	}

	
	@Override
	public void done(QuerySql<DownloadItem> qs, List<DownloadItem> t)
	{
		if(qs==loading_sql){
			loading.clear();
			if(t!=null)
				loading.addAll(t);
			loading_adapter.notifyDataSetChanged();
			
		}else{
			success.clear();
			if(t!=null)
				success.addAll(t);
			success_adapter.notifyDataSetChanged();
		
		}
	}





	
	
	@Override
	public void onClick(View p1)
	{
		switch (p1.getId())
		{
			case R.id.download_view_delete:
				switch (vp.getCurrentItem())
				{
					case 0:
						if (loading_selected.size() == 0)
						{
							Toast.makeText(getActivity(), "没有任务被选中", Toast.LENGTH_SHORT).show();
							return;
						}
						break;
					case 1:
						if (success_selected.size() == 0)
						{
							Toast.makeText(getActivity(), "没有任务被选中", Toast.LENGTH_SHORT).show();
							return;
						}
						break;
				}
				new AlertDialog.Builder(getActivity()).setMessage("确定删除已选任务？")
					.setNeutralButton("确定", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							switch (vp.getCurrentItem())
							{
								case 0:
									for (DownloadItem url:loading_selected)
									{
										int index=loading.indexOf(url);
										if (index != -1)
										{
											//dd.delete(loading.remove(index), false);
											loading_adapter.notifyItemRemoved(index);
										}
									}

									break;
								case 1:
									for (DownloadItem url:success_selected)
									{
										int index=success.indexOf(url);
										if (index != -1)
										{
											//dd.delete(success.remove(index), false);
											success_adapter.notifyItemRemoved(index);
										}
									}
									break;
							}
						}
					})
					.setNegativeButton("和文件一起", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							switch (vp.getCurrentItem())
							{
								case 0:
									for (DownloadItem url:loading_selected)
									{
										int index=loading.indexOf(url);
										if (index != -1)
										{
											url.delete();
											loading.remove(url);
											//dd.delete(loading.remove(index), true);
											loading_adapter.notifyItemRemoved(index);
										}
									}

									break;
								case 1:
									for (DownloadItem url:success_selected)
									{
										int index=success.indexOf(url);
										if (index != -1)
										{
											url.delete();
											success.remove(url);
											File file=new File(url.getDir(),url.getTitle());
											if(file.exists())file.delete();
											//dd.delete(success.remove(index), true);
											success_adapter.notifyItemRemoved(index);
										}
									}
									break;
							}
						}
					})
					.setPositiveButton("取消", null).show();
				break;
			case R.id.download_view_cancel:

				onBackPressed();
				break;
		}
	}

	@Override
	public boolean onItemLongClick(DownloadAdapter adapter, RecyclerView.ViewHolder vh)
	{
		if (delete.getVisibility() == delete.INVISIBLE)
		{
			delete.setVisibility(delete.VISIBLE);
			cancel.setVisibility(delete.VISIBLE);
			delete.startAnimation(enter);
			cancel.startAnimation(enter);
			switch (vh.getItemViewType())
			{
				case 0:
					loading_selected.add(loading.get(vh.getAdapterPosition()));
					loading_adapter.notifyItemChanged(vh.getAdapterPosition());
					break;
				case 1:
					success_selected.add(success.get(vh.getAdapterPosition()));
					success_adapter.notifyItemChanged(vh.getAdapterPosition());
					break;
			}
		}
		return true;
	}

	@Override
	public void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder vh)
	{
		if(vh.getAdapterPosition()==-1)return;
		if (delete.getVisibility() == delete.VISIBLE)
		{
			DownloadItem url=null;
			switch (vh.getItemViewType())
			{
				case 0:
					url = loading.get(vh.getAdapterPosition());
					if (loading_selected.contains(url))
						loading_selected.remove(url);
					else
						loading_selected.add(url);
					loading_adapter.notifyItemChanged(vh.getAdapterPosition());
					break;
				case 1:
					url = success.get(vh.getAdapterPosition());
					if (success_selected.contains(url))
						success_selected.remove(url);
					else
						success_selected.add(url);
					success_adapter.notifyItemChanged(vh.getAdapterPosition());
					break;
			}
		}
		else
		{
			switch (vh.getItemViewType())
			{
				case 0:
					getContext().startService(new Intent(getContext(), DownloadService.class).putExtra("down", loading.get(vh.getAdapterPosition())));
					break;
				case 1:
					String title=success.get(vh.getAdapterPosition()).getTitle();
					int index=title.lastIndexOf(".");
					if(index!=-1){
					String type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(title.substring(index+1));
					try{getActivity().startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(new File(success.get(vh.getAdapterPosition()).getDir(),success.get(vh.getAdapterPosition()).getTitle())),type));}catch(Exception e){
						Toast.makeText(getActivity(),"没有可用程序",Toast.LENGTH_SHORT).show();
					}
					}
					break;
			}
		}
	}

	@Override
	public boolean onBackPressed()
	{
		if (delete.getVisibility() == delete.VISIBLE)
		{
			delete.startAnimation(exit);
			cancel.startAnimation(exit);
			loading_selected.clear();
			success_selected.clear();
			loading_adapter.notifyItemRangeChanged(0,loading.size());
			success_adapter.notifyItemRangeChanged(0,success.size());
			return true;
		}
		return false;
	}



	private class RefreshBroadcast extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			List<DownloadItem> ldi=p2.getParcelableArrayListExtra("data");
			if(ldi==null)
				refresh((DownloadItem)p2.getParcelableExtra("data"));
				else
			for (DownloadItem di:ldi)
			refresh(di);
			
		}
		public <T> void refresh(DownloadItem di){
			if(di==null)return;
			if (di.getState() != DownloadService.State.SUCCESS)
			{
				int index=loading.indexOf(di);
				if(index==-1){
					loading.add(0,di);
					loading_adapter.notifyItemInserted(0);
				}else{
					loading.set(index,di);
					loading_adapter.notifyItemChanged(index);
				}
			}else{
				int index=loading.indexOf(di);
				if(index==-1){
					index=success.indexOf(di);
					if(index==-1){
						success.add(0,di);
						success_adapter.notifyItemInserted(0);
					}
				}else{
					loading.remove(index);
					loading_adapter.notifyItemRemoved(index);
					success.add(0, di);
					success_adapter.notifyItemInserted(0);
				}
			}
			/*int index=loading.indexOf(di);
			if (index != -1)
			{
				if (di.getState() != DownloadService.State.SUCCESS)
				{
					loading.set(index, di);
					loading_adapter.notifyItemChanged(index);
				}else if(success.contains(di)){
					loading.remove(index);
					loading_adapter.notifyItemRemoved(index);
				}else{
					loading.remove(index);
					loading_adapter.notifyItemRemoved(index);
					success.add(0, di);
					success_adapter.notifyItemInserted(0);
				}
			}else{
				
			}*/
		}

	}


}
