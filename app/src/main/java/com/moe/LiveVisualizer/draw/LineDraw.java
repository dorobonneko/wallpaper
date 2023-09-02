package com.moe.LiveVisualizer.draw;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;

public abstract class LineDraw extends Draw {
	private int size;
	private float spaceWidth,borderHeight,borderWidth,drawHeight,sourceSpace,offsetX;
	private Paint paint;
	private boolean antialias;
	public LineDraw(ImageDraw draw) {
		super(draw);
		LiveWallpaper.WallpaperEngine engine=getEngine();
		paint = new Paint();
		//paint.setStyle(Paint.Style.FILL);
		//paint.setStrokeCap(getEngine().getPreference().getBoolean("round", true) ?Paint.Cap.ROUND: Paint.Cap.SQUARE);
		borderHeight = engine.getPreference().getInt("borderHeight", 100);
		sourceSpace = engine.getPreference().getInt("spaceWidth", 20);
		drawHeight = engine.getDisplayHeight() - engine.getPreference().getInt("height", 10) / 100.0f * engine.getDisplayHeight();
		borderWidth = engine.getPreference().getInt("borderWidth", 30);
        size = engine.getPreference().getInt("num", 50);
		paint.setStrokeWidth(borderWidth);
		notifySizeChanged();
	}

	@Override
	public void setAntialias(boolean antialias) {
		this.antialias = antialias;
		//paint.setAntiAlias(antialias);
	}
    @Override
    public void setNum(int num) {
        this.size = num;
        notifySizeChanged();
    }

	public Paint getPaint() {
		return paint;
	}
	public float getDrawHeight() {
		return drawHeight;
	}
	public float getSpaceWidth() {
		return spaceWidth;
	}
	public float getBorderWidth() {
		return borderWidth;
	}
	public float getBorderHeight() {
		return borderHeight;
	}
	@Override
	final public void onDrawHeightChanged(float height) {
		this.drawHeight = height;
	}

	@Override
	final public void onBorderHeightChanged(int height) {
		this.borderHeight = height;
		notifySizeChanged();
	}

	@Override
	public void draw(Canvas canvas) {
		paint.setStrokeWidth(borderWidth);
		paint.setStrokeCap(getRound());
		paint.setStyle(Paint.Style.FILL);
		super.draw(canvas);
	}

	@Override
	public void onDraw(Canvas canvas, int color_mode) {
		if (isFinalized())return;
		Paint paint=getPaint();
		paint.setStrokeCap(getRound());
		paint.setAntiAlias(antialias);
		paint.setDither(antialias);
		switch (color_mode) {
			case 0:
				switch (getEngine().getColorList().size()) {
					case 0:
						paint.setColor(0xff39c5bb);
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					case 1:
						paint.setColor(getEngine().getColorList().get(0));
						drawGraph(getFft(), canvas, color_mode, false);
						break;
					default:
						paint.setShader(getShader());
						drawGraph(getFft(), canvas, color_mode, false);								
						paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
						paint.setShader(null);
						break;
				}
				break;
			case 1:
			case 2:
				if (getEngine() != null)
					switch (getEngine().getColorList().size()) {
						case 0:
							paint.setColor(0xff39c5bb);
							break;
						default:
							paint.setColor(getEngine().getColorList().get(0));
							break;
					}
			case 4:
				drawGraph(getFft(), canvas, color_mode, true);
				break;
			case 3:
				int color=getColor();
				try {
                    paint.setColor(getEngine().getPreference().getBoolean("nenosync", false) ?color: 0xffffffff);
				} catch (NullPointerException e) {}
				paint.setShadowLayer(getBorderWidth(), 0, 0, color);
				drawGraph(getFft(), canvas, color_mode, false);
				paint.clearShadowLayer();
				break;
		}
		paint.reset();
	}





	@Override
	final public void onBorderWidthChanged(int width) {
		this.borderWidth = width;
		paint.setStrokeWidth(width);
		notifySizeChanged();
	}

	@Override
	final public void onSpaceWidthChanged(int space) {
		sourceSpace = space;
		notifySizeChanged();
	}
	@Override
	public int size() {
		return size;
	}

	@Override
	public void notifySizeChanged() {
		/*try
         {
         size = (int)(getEngine().getDisplayWidth()/sourceSpace);
         }
         catch (Exception e)
         {}
         try
         {
         size = size > getEngine().getFftSize() ?getEngine().getFftSize(): size;
         spaceWidth = (float)getEngine().getDisplayWidth()/(size-1);

         }
         catch (Exception e)
         {}*/
         spaceWidth=getEngine().getDisplayWidth()/(float)size;
        offsetX=(getSpaceWidth()-getBorderWidth())/2f;
	}

	@Override
	public void finalized() {
		// TODO: Implement this method
		super.finalized();
		paint.reset();
	}
    public float getStartOffset(){
        return offsetX;
    }
}
