package com.moe.adapter;
import com.moe.entity.FloorItem;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.moe.yaohuo.R;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.entity.UserItem;
import android.view.View;
import com.moe.utils.ImageCache;
import android.text.Html;
import android.graphics.drawable.Drawable;
import com.moe.internal.ImageGetter;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import com.moe.entity.ListItem;
import android.content.Intent;
import com.moe.yaohuo.BbsActivity;
import com.moe.utils.UrlUtils;
import com.moe.yaohuo.WebViewActivity;
import android.view.MotionEvent;
import android.text.Layout;
import android.text.Spannable;
import com.moe.internal.TextViewClickMode;
import com.moe.yaohuo.UserInfoActivity;
import android.widget.PopupWindow;
import android.graphics.drawable.BitmapDrawable;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.util.TypedValue;
import android.support.v4.view.ViewCompat;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import java.io.IOException;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import org.jsoup.nodes.Document;
import android.text.InputType;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.os.Build;
import android.widget.FrameLayout;
import com.moe.utils.UserUtils;
import java.util.ArrayList;
public class FloorAdapter extends RecyclerView.Adapter
{
	private ListItem li;
	private List<FloorItem> list;
	private ArrayList<View> headers,foors; 
	public FloorAdapter(List<FloorItem> list,ListItem bi){
		this.list=list;
		this.li=bi;
		headers=new ArrayList<>();
		foors=new ArrayList<>();
	}
	public void addHeaderView(View v){
		headers.add(v);
		notifyItemInserted(headers.size()-1);
	}
	public void addHeaderView(View v,RecyclerView.LayoutParams rl){
		v.setLayoutParams(rl);
		addHeaderView(v);
	}
	public View getHeaderView(int index){
		return headers.get(index);
	}
	public int getHeaderCount(){
		return headers.size();
	}
	public void addFoorView(View v){
		foors.add(v);
		notifyItemInserted(headers.size()+list.size()+foors.size()-1);
	}
	public View getFoorView(int index){
		return foors.get(index);
	}
	public int getFoorCount(){
		return foors.size();
	}
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		if(p2==-1)
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.floor_item,p1,false));
		else
		if(p2<headers.size())
			return new ViewHolderView(headers.get(p2));
			else
			return new ViewHolderView(foors.get(p2-headers.size()-list.size()));
		
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder rvh, int p2)
	{
		if(rvh.getItemViewType()!=-1)return;
		final ViewHolder vh=(FloorAdapter.ViewHolder) rvh;
		final FloorItem bi=list.get(p2-headers.size());
		UserItem ui=bi.getUser();
		//已弃用
		//UserUtils.getInstance(vh.title.getContext()).getUserItem(bi,this,p2);
		if(ui!=null){
			vh.title.setText(Html.fromHtml(ui.getName()+(ui.getLevel()==-1?"":"<font color='#aaaa00'> Lv"+ui.getLevel()+"</font>")));
			if(ui.getLogo()!=null)
			ImageCache.load(ui.getLogo(),vh.logo);
			else
			vh.logo.setImageDrawable(null);
		}else{
			vh.title.setText(Html.fromHtml(bi.getName()));
			vh.logo.setImageBitmap(null);
			UserUtils.loadUserItem(vh.title.getContext(), bi.getUid(), new UserUtils.Callback(){

					@Override
					public void onLoad(UserItem ui)
					{
						bi.setUser(ui);
						setUserItem(ui,vh);
					}
				});
		}
		vh.summary.setText((bi.getFloor()<1?"":(bi.getFloor()+"# "))+bi.getTime());
		vh.content.setText(Html.fromHtml(bi.getContent(), new ImageGetter(vh.content,true), null));
		vh.money.setText(bi.getMoney()==0?null:(bi.getMoney()+""));
		if(bi.isDelete()||bi.isSendmoney())
			vh.more.setVisibility(View.VISIBLE);
			else
			vh.more.setVisibility(View.GONE);
		if(bi.getFloor()==-1)
			vh.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
			else
			vh.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
	}
	private void setUserItem(UserItem ui,ViewHolder vh){
		if(ui!=null){
			vh.title.setText(Html.fromHtml(ui.getName()+(ui.getLevel()==-1?"":"<font color='#aaaa00'> Lv"+ui.getLevel()+"</font>")));
			if(ui.getLogo()!=null)
				ImageCache.load(ui.getLogo(),vh.logo);
			else
				vh.logo.setImageDrawable(null);
		}
	}
	@Override
	public int getItemViewType(int position)
	{
		return position>=headers.size()&&position<headers.size()+list.size()?-1:position;
	}

	@Override
	public int getItemCount()
	{
		return list.size()+headers.size()+foors.size();
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 0:
					notifyItemChanged(msg.obj);
					break;
			}
		}
		
	};
	public class ViewHolderView extends RecyclerView.ViewHolder{
		public ViewHolderView(View v){
			super(v);
			//v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
		}
	}
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		View more;
		ImageView logo;
		TextView title,summary,content,money;
		@SuppressLint("NewApi")
		public ViewHolder(View v){
			super(v);
			more=v.findViewById(R.id.more);
			logo=(ImageView)v.findViewById(R.id.icon);
			title=(TextView)v.findViewById(R.id.title);
			summary=(TextView)v.findViewById(R.id.summary);
			content=(TextView)v.findViewById(R.id.floor_content);
			LinearLayout.LayoutParams ll=(LinearLayout.LayoutParams)content.getLayoutParams();
			if(Build.VERSION.SDK_INT>16)
			ll.setMarginStart(ll.getMarginStart()+(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,content.getResources().getDisplayMetrics()));
			content.setLayoutParams(ll);
			money=(TextView)v.findViewById(R.id.money);
			v.setOnClickListener(this);
			/**MovementMethod mm=new MovementMethod();
			mm.setOnClickListener(this);
			content.setMovementMethod(mm);*/
			new TextViewClickMode(content);
			//content.setFocusable(false);
			logo.setOnClickListener(this);
			more.setOnClickListener(this);
			}
		@SuppressLint("NewApi")
		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case R.id.icon:
					UserItem ui=list.get(getAdapterPosition()-headers.size()).getUser();
					if(ui!=null&&ui.getUid()>999){
					p1.getContext().startActivity(new Intent(p1.getContext(),UserInfoActivity.class).putExtra("uid",ui.getUid()));
					}
					break;
				case R.id.more:
					final FloorItem fi=list.get(getAdapterPosition()-headers.size());
					final PopupWindow pw=new PopupWindow(p1.getContext(),null,android.support.v7.appcompat.R.style.AlertDialog_AppCompat_Light);
					pw.setBackgroundDrawable(new BitmapDrawable());
					pw.setOutsideTouchable(true);
					pw.setTouchable(true);
					pw.setFocusable(true);
					CardView card=new CardView(p1.getContext());
					LinearLayout ll=new LinearLayout(p1.getContext());
					ll.setOrientation(ll.VERTICAL);
					card.addView(ll,new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.MATCH_PARENT));
					if(fi.isSendmoney()){
						TextView money=new TextView(p1.getContext());
						money.setText("丢钱");
						money.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						money.setPadding(30,0,0,0);
						TypedArray ta=p1.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
						money.setForeground(ta.getDrawable(0));
						ta.recycle();
						money.setOnClickListener(new View.OnClickListener(){

								@Override
								public void onClick(final View p1)
								{
									pw.dismiss();
									final EditText value=(EditText)LayoutInflater.from(p1.getContext()).inflate(R.layout.send_money, null);
									value.setInputType(InputType.TYPE_CLASS_NUMBER);
									new AlertDialog.Builder(p1.getContext()).setTitle("输入金额").setView(value).setPositiveButton("确定", new DialogInterface.OnClickListener(){

											@Override
											public void onClick(DialogInterface d, int p2)
											{
												new Thread(){
													public void run(){
														try
														{
															Document doc=Jsoup.connect(PreferenceUtils.getHost(p1.getContext()) + "/bbs/sendmoney.aspx")
																.data("sendmoney", value.getText().toString())
																.data("action", "gomod")
																.data("reid",fi.getReid()+"")
																.data("classid", li.getClassid()+"")
																.data("siteid", "1000")
																.data("id", li.getId()+"")
																.data("sid", PreferenceUtils.getCookie(p1.getContext()))
																.userAgent(PreferenceUtils.getUserAgent())
																.cookie(PreferenceUtils.getCookieName(p1.getContext()), PreferenceUtils.getCookie(p1.getContext())).post();
																if(doc.getElementsByClass("tip").get(0).child(0).text().indexOf("成功")!=-1){
																fi.setMoney(fi.getMoney()+Integer.parseInt(value.getText().toString()));
																handler.obtainMessage(0,getAdapterPosition()).sendToTarget();
																}
														}
														catch (IOException e)
														{}
													}
												}.start();
											}
										}).setNegativeButton("取消", null).show();
									
									
								}
							});
						ll.addView(money,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
					}
					if(fi.isDelete()){
						TextView delete=new TextView(p1.getContext());
						delete.setText("删除");
						delete.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						delete.setPadding(30,0,0,0);
						ll.addView(delete,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
					}
					card.setCardElevation(6);
					card.setRadius(10);
					ll=new LinearLayout(p1.getContext());
					LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
					param.setMargins(15,5,15,15);
					ll.addView(card,param);
					pw.setContentView(ll);
					pw.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,180,p1.getResources().getDisplayMetrics()));
					pw.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50*((ViewGroup)card.getChildAt(0)).getChildCount(),p1.getResources().getDisplayMetrics()));
					if(Build.VERSION.SDK_INT>22){
					pw.setEnterTransition(new android.transition.Explode());
					pw.setExitTransition(new android.transition.Fade());
					}
					pw.showAsDropDown(p1,0,0);
					break;
					default:
				if(oicl!=null)oicl.onItemClick(FloorAdapter.this,this);
					break;
			}
		}
	}
	private OnItemClickListener oicl;
	public void setOnItemClickListener(OnItemClickListener l){
		oicl=l;
	}
	public abstract interface OnItemClickListener{
		void onItemClick(RecyclerView.Adapter ra,RecyclerView.ViewHolder vh);
	}
}
