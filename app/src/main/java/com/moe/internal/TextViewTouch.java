package com.moe.internal;
import android.text.style.*;
import android.view.*;
import com.moe.yaohuo.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.Spannable;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;
import com.moe.entity.BbsItem;
import com.moe.entity.ListItem;
import com.moe.utils.UrlUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.graphics.Rect;

public class TextViewTouch implements TextView.OnTouchListener,View.OnLongClickListener
{
	private AlertDialog popup_copy;
	private CharacterStyle[] link;
	private TextView widget,message;
	//private Rect src=new Rect(),dst=new Rect();已使用新的判断方法
	public TextViewTouch(TextView tv){
		this.widget=tv;
		}
	@Override
	public boolean onTouch(View p1, MotionEvent event)
	{
		switch(event.getAction()){
			case event.ACTION_DOWN:
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);
				link = Spannable.Factory.getInstance().newSpannable(widget.getText()).getSpans(
					off, off, CharacterStyle.class);
				/*if(link.length>0){
					return true;
					}else return false;
				//break;*/
				//widget.getLocalVisibleRect(src);
				handler.sendEmptyMessageDelayed(0,300);
				break;
			case event.ACTION_MOVE:
				if(handler.hasMessages(0))handler.removeMessages(0);
				break;
			case event.ACTION_CANCEL:
				handler.removeMessages(0);
				break;
			case event.ACTION_UP:
				handler.removeMessages(0);
				if(link!=null&&link.length>0){
					int i=0;
					boolean flag=false;
					for(i=0;i<link.length;i++){
						if(link[i] instanceof ImageSpan){
							flag=true;
							break;
						}
					}
					if(ocl!=null)
						ocl.onClick(link[flag?i:0]);
						else
						onClick(link[flag?i:0]);
					/*for(CharacterStyle cs:link)
						ocl.onClick(cs);
					else
						for(CharacterStyle cs:link)
						onClick(cs);*/
				}
				//link=null;
				break;
		}
		//if(link==null)return true;
		/*if(link.length>0){
			return true;
		}else return false;*/
		return true;

	}
	private void onClick(CharacterStyle span){
		if(popup_copy!=null&&popup_copy.isShowing())return;
		if (span instanceof URLSpan)
		{
			URLSpan us=(URLSpan)span;
			Matcher m=Pattern.compile("/bbs(-|/book_view.aspx|/view.aspx)(\\?.*?&(amp;|)id=|\\?id=|)(\\d+)").matcher(us.getURL());
			if(m.find()){
				ListItem bbs=new ListItem();
				bbs.setId(Integer.parseInt(m.group(4)));
				try{
				widget.getContext().startActivity(new Intent(widget.getContext(), BbsActivity.class).putExtra("bbs", bbs));
				}catch(Exception e){
					Toast.makeText(widget.getContext(),"打开失败",Toast.LENGTH_SHORT).show();
				}
			}else{
				m=Pattern.compile("/bbs/list.aspx.*?classid=(\\d+)").matcher(us.getURL());
				if(m.find()){
					BbsItem bi=new BbsItem();
					bi.setClassid(Integer.parseInt(m.group(1)));
					widget.getContext().startActivity(new Intent(widget.getContext(),ListActivity.class).putExtra("bbs",bi));
				}
				else{
					m=Pattern.compile("/bbs/userinfo.aspx.*?touserid=([\\d]*)").matcher(us.getURL());
					if(m.find()){
						widget.getContext().startActivity(new Intent(widget.getContext(),UserSpaceActivity.class).putExtra("uid",Integer.parseInt(m.group(1))));
					}else{
					String url=UrlUtils.getAbsUrl(widget.getContext(), us.getURL());
					if(url.startsWith("http"))
					widget.getContext().startActivity(new Intent(widget.getContext(), WebViewActivity.class).setData(Uri.parse(url)));
					else
						try{widget.getContext().startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));}catch(Exception e){}
						}
				}
			}
		}else if(span instanceof ImageSpan){
			ImageSpan img=(ImageSpan) span;
			widget.getContext().startActivity(new Intent(widget.getContext(),ViewImageActivity.class).setData(Uri.parse(UrlUtils.getAbsUrl(widget.getContext(),img.getSource()))));
		}
	}
	public void setOnClickListener(OnClickListener l){
		ocl=l;
	}
	private OnClickListener ocl;
	public abstract interface OnClickListener{
		void onClick(CharacterStyle span);
	}
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			//widget.getLocalVisibleRect(dst);
			//if(src.equals(dst))
			onLongClick(widget);
		}
		
	};
	@Override
	public boolean onLongClick(View p1)
	{
		if(popup_copy==null){
			message=(TextView) LayoutInflater.from(p1.getContext()).inflate(R.layout.textview,null);
			message.setFocusable(true);
			message.setFocusableInTouchMode(true);
			message.setClickable(true);
			message.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
			message.setTextIsSelectable(true);
			int padding=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,p1.getResources().getDisplayMetrics());
			message.setPadding(padding,padding,padding,padding);
			popup_copy=new AlertDialog.Builder(p1.getContext()).setView(message).setCancelable(true).create();
			}
		message.setText(widget.getText().toString());
		popup_copy.show();
		return true;
	}

	
}
