package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.LiveWallpaper;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.graphics.Matrix;

public class WallpaperThread extends Thread
{

	private LiveWallpaper.MoeEngine engine;
	private Handler handler;
	private ImageDraw imageDraw;
	public WallpaperThread(LiveWallpaper.MoeEngine engine)
	{

		this.engine = engine;
		imageDraw = ImageDrawCompat.getInstance(engine);
	}

	public void close()
	{
		imageDraw = null;
	}

	public void onUpdate(byte[] p2)
	{
		if ( engine.isVisible() && handler != null )
		{
			handler.removeMessages(0);
			handler.sendMessageDelayed(handler.obtainMessage(0, p2), 33);
		}
	}


	@Override
	public void run()
	{

		Looper.prepare();
		handler = new Handler(){
			public void handleMessage(Message msg)
			{
				SurfaceHolder sh=engine.getSurfaceHolder();
				if ( sh != null )
				{
					synchronized ( sh )
					{
						final Canvas canvas=sh.lockCanvas();
						if ( canvas != null )
						{
							if ( engine.getSharedPreferences().getBoolean("artwork", false) && engine.getArtwork() != null )
							{
								Bitmap buffer=engine.getArtwork();
								Matrix matrix=new Matrix();
								float scale=Math.max(((float)canvas.getWidth() / buffer.getWidth()), ((float)canvas.getHeight() / buffer.getHeight()));
								matrix.setScale(scale, scale);
								float offsetX=(buffer.getWidth() * scale - canvas.getWidth()) / 2;
								float offsetY=(buffer.getHeight() * scale - canvas.getHeight()) / 2;
								matrix.postTranslate(-offsetX, -offsetY);
								canvas.drawBitmap(engine.getArtwork(), matrix, null);
							}
							else
							if ( engine.getWallpaper() != null )
								canvas.drawBitmap(engine.getWallpaper(), 0, 0, null);
							else
								canvas.drawColor(0xff000000);
							if ( imageDraw != null )
							{
								ImageDraw draw=imageDraw.lockData((byte[])msg.obj);
								if ( draw != null )
								//try{
									draw.draw(canvas);
								//}catch(Exception e){}
							}
							sh.unlockCanvasAndPost(canvas);
						}
					}
				}

			}
		};
		Looper.loop();


	}
	/*


	 */
}
