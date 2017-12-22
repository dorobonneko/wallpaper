package com.moe.widget;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import com.moe.yaohuo.R;
import android.content.res.TypedArray;
import android.support.v7.graphics.Palette;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.os.Build;
import android.graphics.Color;
import com.moe.utils.ColorUtils;
import com.moe.utils.DrawableUtils;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.app.ActivityCompat;

public class BigLogoView extends AppCompatImageView implements Palette.PaletteAsyncListener
{
	private Path path=null;
	private int direction;
	private float height;
	//private Paint p=new Paint();
	private Parse parse;
	public BigLogoView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.logo_background);
		direction = ta.getInt(R.styleable.logo_background_direction, -1);
		height = ta.getFraction(R.styleable.logo_background_height_slide, 1, 1, 0.75f);
		ta.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (path == null && direction != -1)
		{
			path = new Path();
			path.moveTo(0, 0);
			path.lineTo(getMeasuredWidth(), 0);
			switch (direction)
			{
				case 0:
					path.lineTo(getMeasuredWidth(), getMeasuredHeight() * height);
					path.lineTo(0, getMeasuredHeight());
					break;
				case 1:
					path.lineTo(getMeasuredWidth(), getMeasuredHeight());
					path.lineTo(0, getMeasuredHeight() * height);
					break;
			}
			path.close();
			//path.,Path.Direction.CW);
		}
		if (path != null)
			canvas.clipPath(path);
		super.onDraw(canvas);
		//if(Build.VERSION.SDK_INT>18&&getDrawable()!=null)
		//	Palette.generateAsync(DrawableUtils.drawableToBitmap(getDrawable()),this);

	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		setImageDrawable(new BitmapDrawable(bm));
	}

	@Override
	public void setImageDrawable(final Drawable drawable)
	{
		super.setImageDrawable(drawable);
		if (Build.VERSION.SDK_INT > 22 && drawable != null && (!(drawable instanceof VectorDrawable)))
		{
			if (parse != null)
			{
				parse.close();
			}
			else
			{
				parse = new Parse(drawable);
				parse.start();
			}
		}
	}

	@Override
	public void onGenerated(Palette p1)
	{
		View root=getRootView();
		int vi=root.getSystemUiVisibility();
		if (ColorUtils.isLight(p1.getVibrantColor(0xff000000)))
			vi |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
		else
			vi &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

		root.setSystemUiVisibility(vi);
	}

	class Parse extends Thread
	{
		private Drawable drawable;
		public Parse(Drawable drawable)
		{
			this.drawable = drawable;
		}
		public void close()
		{
			interrupt();
		}
		public void run()
		{
			int light=0,dark=9;
			Bitmap b=DrawableUtils.drawableToBitmap(drawable);
			for (int i=0;i < b.getWidth();i++)
				for (int j=3;j < 6;j++)
				{
					if (ColorUtils.isLight(b.getPixel(i, j)))
						light++;
					else
						dark++;
					if (isInterrupted())
						return;
				}
			final boolean islight=light > dark;
			post(new Runnable(){

					@Override
					public void run()
					{View root=getRootView();
						int vi=root.getSystemUiVisibility();
						if (islight)
							vi |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
						else
							vi &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

						root.setSystemUiVisibility(vi);
					}
				});
		}
	}
}
