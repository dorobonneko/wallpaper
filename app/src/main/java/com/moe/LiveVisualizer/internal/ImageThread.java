package com.moe.LiveVisualizer.internal;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.graphics.Bitmap;
import com.bumptech.glide.gifdecoder.GifDecoder;
import java.io.File;
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.IOException;
import android.view.MotionEvent;
import java.util.Arrays;
import android.os.HandlerThread;

public class ImageThread extends HandlerThread implements Handler.Callback {
	private Bitmap image;
	private GifDecoder gifDecoder;
	private Handler handler;
	private LiveWallpaper live;
	private File file;
	private boolean isLoad,ripple;
	private int m_width,m_height;

	private short[] m_buf1, m_buf2;

	private int[] m_bitmap1, m_bitmap2;
	private int m_preX,m_preY;
	private animeThread anime;
	public ImageThread(LiveWallpaper live, File file) {
        super("解析图片线程");
		this.live = live;
		this.file = file;
		anime = new animeThread();
		anime.start();
	}

    @Override
    public void start() {
        super.start();
        handler = new Handler(getLooper(), this);
        handler.sendEmptyMessage(0);
    }

	public boolean isGif() {
		return gifDecoder != null;
	}
	public void reLoad() {
		isLoad = false;
        handler.sendEmptyMessage(0);
		//if ( handler != null )
		//	handler.obtainMessage(0).sendToTarget();
	}
	public void notifyVisiableChanged(boolean visiable) {
		if (visiable && isGif() && handler != null)
			handler.sendEmptyMessage(1);
        if (visiable)
            anime.resumeAnime();
        else
            anime.pauseAnime();
	}
	@Override
	public boolean handleMessage(Message p1) {
		switch (p1.what) {
			case 0:
				loadImage();
                synchronized (this) {
                    isLoad = true;
                    notifyAll();
                }
				handler.sendEmptyMessage(2);
				break;
			case 1:
                try {
                    gifDecoder.advance();
					image = gifDecoder.getNextFrame();
					if (handler.hasMessages(1))
						handler.removeMessages(1);
					handler.sendEmptyMessageDelayed(1, gifDecoder.getNextDelay());
                } catch (Exception e) {}
				break;
			case 2:
				if (!ripple) {
					m_buf1 = null;
					m_buf2 = null;
					m_bitmap1 = null;
					m_bitmap2 = null;
					System.gc();
				} else if (image != null && !isGif()) {
					m_width = image.getWidth();
                    m_height = image.getHeight();

                    // leave 1 extra up, low border for boundary condition
                    m_buf1 = new short[m_width * (m_height)];
                    m_buf2 = new short[m_width * (m_height)];

                    m_bitmap1 = new int[m_width * m_height];
                    //m_bitmap2 = new int[m_width * m_height];
                    image.getPixels(m_bitmap1, 0, m_width, 0, 0, m_width, m_height);
                    m_bitmap2 = Arrays.copyOf(m_bitmap1, m_bitmap1.length);
				}
                break;
		}
		return true;
	}
	public void destroy() {
		if (handler != null)
			handler.getLooper().quit();
        if (image != null)
            image.recycle();
        if (gifDecoder != null)
            gifDecoder.clear();
        if (anime != null)
            anime.interrupt();
	}
	public Bitmap getImage() {
        synchronized (this) {
            try {
                if (!isLoad) 
                    wait();
            } catch (InterruptedException e) {}
        }
		if (isGif() && handler != null && !handler.hasMessages(1))
			handler.sendEmptyMessage(1);
		return image;
	}
	public int[] getImageData() {
        synchronized (this) {
            try {
                if (!isLoad)
                    wait();
            } catch (InterruptedException e) {}
        }
        return m_bitmap2;
	}

	public int getWidth() {
		return m_width;
	}
	public int getHeight() {
		return m_height;
	}
	private synchronized void loadImage() {
		if (this.image != null) {
			this.image.recycle();
			image = null;
		}
		if (gifDecoder != null) {
			this.gifDecoder.clear();
			this.gifDecoder = null;
		}
		if (file.exists() && file.isFile()) {
			FileInputStream fis=null;
			try {
				gifDecoder = new GifDecoder(new GifDecoder.BitmapProvider(){

						@Override
						public Bitmap obtain(int p1, int p2, Bitmap.Config p3) {
							return Bitmap.createBitmap(p1, p2, p3);
						}

						@Override
						public void release(Bitmap p1) {
							p1.recycle();
						}
					});

				fis = new FileInputStream(file);
				gifDecoder.read(fis, (int)file.length());
				if (gifDecoder.getFrameCount() == 0) {
					throw new NullPointerException("it's not gif");
				} else if (handler != null)
					handler.sendEmptyMessage(1);

			} catch (Exception e) {
				if (gifDecoder != null)gifDecoder.clear();
				gifDecoder = null;
				image = BitmapFactory.decodeFile(file.getAbsolutePath());

            } finally {
				try {
					if (fis != null)fis.close();
				} catch (IOException e) {}
			}
		} 
	}
	public void setRipple(boolean ripple) {
        if (isGif())return;
		if (this.ripple != ripple) {
            this.ripple = ripple;
            handler.sendEmptyMessage(2);
		}
	}
	public void onTouchEvent(MotionEvent event) {
		if (isGif())return;
		int action = event.getAction();
		int x = (int)(event.getX());
		int y = (int)(event.getY());

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				dropStone(x, y, 5, 50);
				m_preX = x;
				m_preY = y;
				break;

			case MotionEvent.ACTION_MOVE:
				bresenhamDrop(m_preX, m_preY, x, y, 5, 50);
				m_preX = x;
				m_preY = y;
				break;
		}
	}
	// 某点下一时刻的波幅算法为：上下左右四点的波幅和的一半减去当前波幅，即
	// 		X0' =（X1 + X2 + X3 + X4）/ 2 - X0
	//  +----x3----+
	//  +    |     +
	//  +    |     +
	//	x1---x0----x2
	//  +    |     +
	//  +    |     +
	//  +----x4----+
	//
    void rippleSpread() {
    	int pixels = m_width * (m_height - 1);
    	for (int i = m_width; i < pixels; ++i) {
    		// 波能扩散:上下左右四点的波幅和的一半减去当前波幅
	        // X0' =（X1 + X2 + X3 + X4）/ 2 - X0
	        //
    		m_buf2[i] = (short)(((m_buf1[i - 1] + m_buf1[i + 1] +
				m_buf1[i - m_width] + m_buf1[i + m_width]) >> 1) - m_buf2[i]);

    		// 波能衰减 1/32
			//
    		m_buf2[i] -= m_buf2[i] >> 8;
    	}

    	//交换波能数据缓冲区
    	short[] temp = m_buf1;
    	m_buf1 = m_buf2;
    	m_buf2 = temp;
    }

    void rippleRender() {
    	int offset;
    	int i = m_width;
    	int length = m_width * m_height;
    	for (int y = 1; y < m_height - 1; ++y) {
    		for (int x = 0; x < m_width; ++x, ++i) {
    			// 计算出偏移象素和原始象素的内存地址偏移量 : offset = width * yoffset + xoffset
    			offset = (m_width * (m_buf1[i - m_width] - m_buf1[i + m_width]))
					+ (m_buf1[i - 1] - m_buf1[i + 1]);

    			// 判断坐标是否在窗口范围内
    			if (i + offset > 0 && i + offset < length) {
    				m_bitmap2[i] = m_bitmap1[i + offset];
    			} else {
    				m_bitmap2[i] = m_bitmap1[i];
    			}
    		}
    	}
    }

	// 为了形成水波，我们必须在水池中加入波源，你可以想象成向水中投入石头，
	// 形成的波源的大小和能量与石头的半径和扔石头的力量都有关系。
	// 我们只要修改波能数据缓冲区 buf，让它在石头入水的地点来一个负的"尖脉冲"，
	// 即让  buf[x, y] = -n。经过实验，n 的范围在（32 ~ 128）之间比较合适。
	// stoneSize 	: 波源半径
	// stoneWeight 	: 波源能量
	//
    void dropStone(int x, int y, int stoneSize, int stoneWeight) {
    	// 判断坐标是否在屏幕范围内
    	if ((x + stoneSize) > m_width || (y + stoneSize) > m_height
			|| (x - stoneSize) < 0 || (y - stoneSize) < 0) {
    		return;
    	}

    	int value = stoneSize * stoneSize;
    	short weight = (short)-stoneWeight;
		for (int posx = x - stoneSize; posx < x + stoneSize; ++posx) {
		    for (int posy = y - stoneSize; posy < y + stoneSize; ++posy) {
		        if ((posx - x) * (posx - x) + (posy - y) * (posy - y) < value) {
		            m_buf1[m_width * posy + posx] = weight;
		        }
		    }
		}
	}

    void dropStoneLine(int x, int y, int stoneSize, int stoneWeight) {
	    // 判断坐标是否在屏幕范围内
	    if ((x + stoneSize) > m_width || (y + stoneSize) > m_height
	        || (x - stoneSize) < 0 || (y - stoneSize) < 0) {
	        return;
	    }

	    for (int posx = x - stoneSize; posx < x + stoneSize; ++posx) {
		    for (int posy = y - stoneSize; posy < y + stoneSize; ++posy) {
	            m_buf1[m_width * posy + posx] = -20;
	        }
	    }
	}

    // xs, ys : 起始点，xe, ye : 终止点，size : 波源半径，weight : 波源能量
    void bresenhamDrop(int xs, int ys, int xe, int ye, int size, int weight) {
	    int dx = xe - xs;
	    int dy = ye - ys;
	    dx = (dx >= 0) ? dx : -dx;
	    dy = (dy >= 0) ? dy : -dy;

	    if (dx == 0 && dy == 0) {
	    	dropStoneLine(xs, ys, size, weight);
	    } else if (dx == 0) {
	    	int yinc = (ye - ys != 0) ? 1 : -1;
	        for (int i = 0; i < dy; ++i) {
	        	dropStoneLine(xs, ys, size, weight);
	            ys += yinc;
	        }
	    } else if (dy == 0) {
	    	int xinc = (xe - xs != 0) ? 1 : -1;
	        for (int i = 0; i < dx; ++i) {
	        	dropStoneLine(xs, ys, size, weight);
	            xs += xinc;
	        }
	    } else if (dx > dy) {
	        int p = (dy << 1) - dx;
	        int inc1 = (dy << 1);
	        int inc2 = ((dy - dx) << 1);
	        int xinc = (xe - xs != 0) ? 1 : -1;
	        int yinc = (ye - ys != 0) ? 1 : -1;

	        for (int i = 0; i < dx; ++i) {
	        	dropStoneLine(xs, ys, size, weight);
	            xs += xinc;
	            if (p < 0) {
	                p += inc1;
	            } else {
	                ys += yinc;
	                p += inc2;
	            }
	        }
	    } else {
	        int p = (dx << 1) - dy;
	        int inc1 = (dx << 1);
	        int inc2 = ((dx - dy) << 1);
	        int xinc = (xe - xs != 0) ? 1 : -1;
	        int yinc = (ye - ys != 0) ? 1 : -1;

	        for (int i = 0; i < dy; ++i) {
	        	dropStoneLine(xs, ys, size, weight);
	            ys += yinc;
	            if (p < 0) {
	                p += inc1;
	            } else {
	                xs += xinc;
	                p += inc2;
	            }
	        }
	    }
	}
	class animeThread extends Thread {
		private Object locked=new Object();
		private boolean anime;
		public void resumeAnime() {
			synchronized (locked) {
				anime = true;
				locked.notify();
			}
		}
		public void pauseAnime() {
			anime = false;
		}
		@Override
		public void run() {
			while (!interrupted()) {
				synchronized (locked) {
					try {
						if (!anime || (image != null && isGif()))
							locked.wait();
					} catch (InterruptedException e) {}
				}
				try {
                    if (m_bitmap1 != null && m_bitmap2 != null && m_buf1 != null && m_buf2 != null) {
                        rippleSpread();
                        rippleRender();
                    }
				} catch (Exception e) {}
				try {
					sleep(16);
				} catch (InterruptedException e) {}
			}
		}

	}
}
