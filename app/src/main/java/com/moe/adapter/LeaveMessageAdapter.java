package com.moe.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.moe.entity.FloorItem;
import java.util.List;
import android.view.LayoutInflater;
import com.moe.yaohuo.R;
import android.widget.TextView;
import com.moe.internal.TextViewTouch;
import com.moe.utils.UserUtils;
import com.moe.entity.UserItem;
import android.widget.ImageView;
import com.moe.utils.ImageLoad;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.PopupWindow;
import android.support.v7.widget.CardView;
import android.widget.LinearLayout;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.os.Build;
import com.moe.utils.ImageCache;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import org.jsoup.Jsoup;
import com.moe.thread.ServerService;
import com.moe.internal.ImageGetter;
import android.support.v4.app.ActivityCompat;
import android.app.Activity;
import android.content.Intent;
import com.moe.yaohuo.LeaveMessageActivity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.content.Context;
import com.moe.utils.PreferenceUtils;
import android.content.res.Resources.NotFoundException;
import java.io.IOException;
import org.jsoup.nodes.Document;
import com.moe.yaohuo.UserInfoActivity;
import android.support.v4.view.ViewCompat;

public class LeaveMessageAdapter extends RecyclerView.Adapter<LeaveMessageAdapter.ViewHolder>
{
	private List<? extends FloorItem> list;
	private boolean owner;
	public LeaveMessageAdapter(List<? extends FloorItem> list, boolean owner)
	{
		this.list = list;
		this.owner = owner;
	}
	@Override
	public LeaveMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.floor_item_view, p1, false));
	}

	@Override
	public void onBindViewHolder(final LeaveMessageAdapter.ViewHolder p1, int p2)
	{
		FloorItem fi=list.get(p2);
		if (fi.getUser() == null)
		{
			p1.name.setText(Html.fromHtml(fi.getName()));
			p1.logo.setImageDrawable(null);
			UserUtils.loadUserItem(p1.itemView.getContext(), fi.getUid(), new UserUtils.Callback(){

					@Override
					public void onLoad(UserItem ui)
					{
						if (ui != null)
						{
							/*new ImageLoad.Builder(ui.getLogo()).callback(new ImageLoad.Callback(){

							 @Override
							 public void onLoad(String url, Drawable b, Object o)
							 {
							 p1.logo.setImageDrawable(b);
							 }
							 }).get().execute();*/
							ImageCache.load(ui.getLogo(), p1.logo);
							p1.name.setText(Html.fromHtml(ui.getName()));

						}
					}
				});
		}
		else
		{
//				new ImageLoad.Builder(fi.getUser().getLogo()).callback(new ImageLoad.Callback(){
//
//						@Override
//						public void onLoad(String url, Drawable b, Object o)
//						{
//							p1.logo.setImageDrawable(b);
//						}
//					}).get().execute();
			ImageCache.load(fi.getUser().getLogo(), p1.logo);

			p1.name.setText(Html.fromHtml(fi.getUser().getName()));
		}
		p1.content.setText(Html.fromHtml(fi.getContent(), new ImageGetter(p1.content, true), null));
		if (owner)
			p1.more.setVisibility(View.VISIBLE);
		else
			p1.more.setVisibility(View.GONE);
		p1.summary.setText("#" + fi.getFloor() + " " + fi.getTime());

	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView name,content,summary;
		private ImageView logo;
		private View more;
		public ViewHolder(View v)
		{
			super(v);
			more = v.findViewById(R.id.more);
			more.setOnClickListener(this);
			name = (TextView) v.findViewById(R.id.title);
			content = (TextView) v.findViewById(R.id.content);
			summary = (TextView) v.findViewById(R.id.summary);
			content.setOnTouchListener(new TextViewTouch(content));
			logo = (ImageView) v.findViewById(R.id.icon);
			logo.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			switch(p1.getId()){
				case R.id.icon:
					((Activity)p1.getContext()).startActivity(new Intent(p1.getContext(),UserInfoActivity.class).putExtra("uid",list.get(getAdapterPosition()).getUid()));
					break;
				case R.id.more:
			//final FloorItem fi=list.get(getAdapterPosition());
			final PopupWindow pw=new PopupWindow(p1.getContext(), null, android.support.v7.appcompat.R.style.AlertDialog_AppCompat_Light);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.setOutsideTouchable(true);
			pw.setTouchable(true);
			pw.setFocusable(true);
			CardView card=new CardView(p1.getContext());
			LinearLayout ll=new LinearLayout(p1.getContext());
			ll.setOrientation(ll.VERTICAL);
			card.addView(ll, new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT));
			TextView money=new TextView(p1.getContext());
			money.setText("回复");
			money.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			money.setPadding(30, 0, 0, 0);
			TypedArray ta=p1.getContext().obtainStyledAttributes(new int[]{android.support.v7.appcompat.R.attr.selectableItemBackground});
			ViewCompat.setBackground(money,ta.getDrawable(0));
			money.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(final View p1)
					{
						pw.dismiss();
						//回复消息
						((Activity)p1.getContext()).startActivityForResult(new Intent(p1.getContext(), LeaveMessageActivity.class).putExtra("floor", list.get(getAdapterPosition())), 392);
					}
				});
			ll.addView(money, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

			TextView delete=new TextView(p1.getContext());
			ViewCompat.setBackground(delete,ta.getDrawable(0));
			ta.recycle();

			delete.setText("删除");
			delete.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			delete.setPadding(30, 0, 0, 0);
			delete.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(final View p1)
					{
						pw.dismiss();
						new AlertDialog.Builder(p1.getContext()).setTitle("确认删除？").setPositiveButton("取消", null).setNegativeButton("确认", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p0, int p2)
								{
									new Thread(){
										public void run()
										{
											FloorItem fi=list.get(getAdapterPosition());
											try
											{
												Document doc=Jsoup.connect(PreferenceUtils.getHost(p1.getContext()) + p1.getResources().getString(R.string.leaveMessage_del, new Integer[]{new Integer((int)fi.getReid()),new Integer(fi.getUid())}))
													.userAgent(PreferenceUtils.getUserAgent())
													.cookie(PreferenceUtils.getCookieName(p1.getContext()), PreferenceUtils.getCookie(p1.getContext())).get();
												if (doc.text().contains("删除成功"))
													handler.obtainMessage(0, getAdapterPosition()).sendToTarget();
												else
													throw new Exception("删除失败");
											}
											catch (Exception e)
											{handler.obtainMessage(1, p1.getContext()).sendToTarget();}

										}
									}.start();
								}
							}).show();
					}
				});
			ll.addView(delete, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

			card.setCardElevation(6);
			card.setRadius(10);
			ll = new LinearLayout(p1.getContext());
			LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			param.setMargins(15, 5, 15, 15);
			ll.addView(card, param);
			pw.setContentView(ll);
			pw.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, p1.getResources().getDisplayMetrics()));
			pw.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * ((ViewGroup)card.getChildAt(0)).getChildCount(), p1.getResources().getDisplayMetrics()));
			if (Build.VERSION.SDK_INT > 22)
			{
				pw.setEnterTransition(new android.transition.Explode());
				pw.setExitTransition(new android.transition.Fade());
			}
			pw.showAsDropDown(p1, 0, 0);
			break;
			}
		}
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					list.remove(((Integer)msg.obj).intValue());
					notifyItemRemoved(msg.obj);
					break;
				case 1:
					Toast.makeText((Context)msg.obj, "删除失败", Toast.LENGTH_SHORT).show();
					break;
			}
		}

	};
}
