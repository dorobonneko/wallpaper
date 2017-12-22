package com.moe.internal;
import android.text.style.ClickableSpan;
import android.widget.TextView;
import android.view.View;
import android.view.MotionEvent;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.URLSpan;
import com.moe.entity.ListItem;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.content.Intent;
import com.moe.yaohuo.BbsActivity;
import com.moe.entity.BbsItem;
import com.moe.yaohuo.ListActivity;
import com.moe.yaohuo.WebViewActivity;
import com.moe.utils.UrlUtils;
import android.net.Uri;
import android.text.style.ImageSpan;
import android.text.style.CharacterStyle;
import android.widget.Toast;
import com.moe.yaohuo.ViewImageActivity;
import android.text.Selection;
import com.moe.yaohuo.UserSpaceActivity;

public class TextViewClickMode implements TextView.OnTouchListener
{
	private CharacterStyle[] link;
	private TextView widget;
	public TextViewClickMode(TextView tv){
		tv.setOnTouchListener(this);
		tv.setTextIsSelectable(true);
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
			case event.ACTION_CANCEL:
				break;
			case event.ACTION_UP:
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
		if(link.length>0){
			return true;
		}else return false;
		//return true;

	}
	private void onClick(CharacterStyle span){
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
}
