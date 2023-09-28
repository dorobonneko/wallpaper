package com.moe.LiveVisualizer.draw.line;
import android.graphics.Paint;
import com.moe.LiveVisualizer.service.LiveWallpaper;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.LinearGradient;
import android.util.TypedValue;
import android.graphics.Shader;
import com.moe.LiveVisualizer.internal.ImageDraw;
import com.moe.LiveVisualizer.draw.LineDraw;
import com.moe.LiveVisualizer.utils.ColorList;
import android.graphics.RectF;
import android.graphics.Path;

public class LineChartDraw extends LineDraw
{
	private float[] tmpData=new float[8];
    private float[] pointX;
    private float[] points;
	public LineChartDraw(ImageDraw draw)
	{
		super(draw);
    }

    @Override
    public void notifySizeChanged() {
        super.notifySizeChanged();
        if(pointX==null||pointX.length!=size()){
            pointX=new float[size()];
            float offsetX=getStartOffset();
            for(int i=0;i<pointX.length;i++){
                pointX[i]=offsetX+i*getSpaceWidth();
            }
        }
        if(points==null||points.length!=size())
            points=new float[size()];
    }

	/*@Override
	public void onDraw(Canvas canvas, int color_mode)
	{
		Paint paint=getPaint();
		switch(color_mode){
			case 0://色带
			switch(getEngine().getColorList().size()){
				case 0:
					paint.setColor(0xff39c5bb);
					drawGraph(getFft(), canvas,color_mode,false);
					break;
				case 1:
					paint.setColor(getEngine().getColorList().get(0));
					drawGraph(getFft(), canvas,color_mode,false);
					break;
				default:
					paint.setShader(getShader());
					drawGraph(getFft(), canvas,color_mode,false);
					break;
			}
				break;
			case 1://间隔
			case 2:
			case 4:
				drawGraph(getFft(),canvas,color_mode,true);
				break;
			case 3://霓虹灯
			int color=getColor();
				paint.setColor(getEngine().getPreference().getBoolean("nenosync",false)?color:0xffffffff);
				paint.setShadowLayer(paint.getStrokeWidth(),0,0,color);
				drawGraph(getFft(),canvas,color_mode,false);
				break;
		}
		paint.reset();
	}*/

	/*@Override
	public double[] getFft()
	{
		return getWave();
	}*/
	/*@Override
	public void drawGraph(double[] buffer, Canvas canvas,final int color_mode, boolean useMode)
	{
		Paint paint=getPaint();
		float offsetX=getStartOffset();
		for ( int i=0;i < buffer.length-2;i+=2 )
		{
			float height=((byte)(buffer[i]+128))*getBorderHeight()/256;
				tmpData[0] = offsetX;
				tmpData[1] = getDrawHeight() -height ;
			height=((byte)(buffer[i+1]+128))*getBorderHeight()/256;
			tmpData[2] = (offsetX += getSpaceWidth()+getBorderWidth());
			tmpData[3] = getDrawHeight() -height;
			System.arraycopy(tmpData,2,tmpData,4,2);
			height=((byte)(buffer[i+2]+128))*getBorderHeight()/256;
			tmpData[6]=(offsetX+=getSpaceWidth()+getBorderWidth());
			tmpData[7]=getDrawHeight()-height;
			if(useMode)
				checkMode(color_mode,paint);
			
			canvas.drawLines(tmpData, paint);
		}
	}*/
    @Override
    public void drawGraph(double[] buffer, Canvas canvas,final int color_mode, boolean useMode)
    {
        Paint paint=getPaint();
        paint.setStyle(Paint.Style.STROKE);
        float offsetX=getStartOffset();
        for(int i=0;i<size();i++){
            float height=getDrawHeight()-(float)buffer[i]/127*getBorderHeight();
            if ( height < points[i] )
                points[i]=Math.max(0,points[i]-(points[i]-height)*getInterpolator((points[i]-height)/points[i]*1f));
            else if(height>points[i])
                points[i]=points[i]+(height-points[i])*getInterpolator((height-points[i])/height*0.6f);
        }
        Path path = new Path();
        path.moveTo(offsetX,getDrawHeight());
            for (int i = 0; i < size(); i ++) {
                float startX=pointX[i];
                float startY=points[i];
                float endX,endY;
                if(i+1==size()){
                    endX=canvas.getWidth();
                    endY=getDrawHeight();
                }else{
                     endX=pointX[i+1];
                    endY=points[i+1];
                }
                float centerX=startX+(endX-startX)/2;
                float centerY=startY+(endY-startY)/2;
                path.quadTo(startX,startY,centerX,centerY);
            }
            path.lineTo(canvas.getWidth(),getDrawHeight());
        if (useMode)
            checkMode(color_mode, paint);

        canvas.drawPath(path, paint);
    }
}
