package com.moe.yaohuo;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.view.Gravity;
import com.moe.app.EmojiDialog;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.text.TextUtils;
import org.jsoup.Jsoup;
import com.moe.utils.PreferenceUtils;
import com.moe.utils.StringUtils;
import java.io.IOException;
import org.jsoup.nodes.Document;
import com.moe.entity.FloorItem;

public class LeaveMessageActivity extends EventActivity
{
	private int uid;
	private EditText content;
	private EmojiDialog emoji;
	private ProgressBar sending;
	private FloorItem floor;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		content = new EditText(this);
		content.setMinLines(5);
		content.setGravity(Gravity.TOP | Gravity.START);
		sending = new ProgressBar(this);
		FrameLayout.LayoutParams center_h=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		center_h.gravity = Gravity.CENTER_HORIZONTAL;
		sending.setLayoutParams(center_h);
		sending.setVisibility(View.INVISIBLE);
		FrameLayout parent=(FrameLayout)findViewById(R.id.main_index);
		parent.addView(content, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		parent.addView(sending);
		if (savedInstanceState == null)
		{
			uid = getIntent().getIntExtra("uid", 0);
			floor = getIntent().getParcelableExtra("floor");
		}
		else
		{
			uid = savedInstanceState.getInt("uid");
			floor = savedInstanceState.getParcelable("floor");
			content.setText(savedInstanceState.getCharSequence("content"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.send, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("floor", floor);
		outState.putInt("uid", uid);
		outState.putCharSequence("content", content.getText());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.emoji:
				if (emoji == null)
					emoji = new EmojiDialog(this);
				emoji.show(getWindow());
				break;
			case R.id.send:
				if (sending.getVisibility() == View.VISIBLE)
					handler.obtainMessage(0, "正在发送，请勿重复点击").sendToTarget();
				else if (!TextUtils.isEmpty(content.getText()))
				{
					sending.setVisibility(View.VISIBLE);
					new Thread(){
						public void run()
						{
							try
							{
								Document doc=Jsoup.connect(PreferenceUtils.getHost(getApplicationContext()) + (floor == null ?getString(R.string.leaveMessage_pic): getString(R.string.leaveMessage_re)))
									.data("action", floor == null ?"add": "gomod")
									.data("content", StringUtils.replaceRtlf(content.getText().toString()))
									.data("touserid", uid + "")
									.data("siteid", "1000")
									.data("classid", "0")
									.data("face", "face")
									.data("reid", floor == null ?"": floor.getReid() + "")
									.data("sid", PreferenceUtils.getCookie(getApplicationContext()))
									.userAgent(PreferenceUtils.getUserAgent())
									.cookie(PreferenceUtils.getCookieName(getApplicationContext()), PreferenceUtils.getCookie(getApplicationContext())).post();
								if (doc.text().contains("留言成功"))
									handler.sendEmptyMessage(2);
								else
									handler.obtainMessage(1, "留言失败").sendToTarget();
							}
							catch (IOException e)
							{handler.obtainMessage(1, e.getMessage()).sendToTarget();}
						}
					}.start();
				}
				else
					handler.obtainMessage(0, "内容不能为空").sendToTarget();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				case 1:
					obtainMessage(0, msg.obj).sendToTarget();
					sending.setVisibility(View.INVISIBLE);

					break;
				case 2:
					setResult(RESULT_OK);
					finish();
					break;

			}
		}

	};
}
