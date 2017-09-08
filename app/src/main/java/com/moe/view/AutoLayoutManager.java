package com.moe.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.Recycler;
import android.view.View;
public class AutoLayoutManager extends RecyclerView.LayoutManager
{
	private int totalHeight;
	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams()
	{
		return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
	{
		//在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler);

        //定义竖直方向的偏移量
        int offsetY = 0;
		int offsetX=0;
        for (int i = 0; i < getItemCount(); i++) {
            //这里就是从缓存里面取出
            View view = recycler.getViewForPosition(i);
            //将View加入到RecyclerView中
            addView(view);
            //对子View进行测量
            measureChildWithMargins(view, 0, 0);
            //把宽高拿到，宽高都是包含ItemDecorate的尺寸
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            //最后，将View布局
			if(getWidth()-offsetX>=width){
				//行内有空间，继续摆放
				layoutDecorated(view, offsetX, offsetY, offsetX+width, offsetY + height);
				offsetX += width;
			}else{
				//行内无空间，换行摆放
				offsetX=0;
				offsetY += height;
				layoutDecorated(view, offsetX, offsetY, width, offsetY + height);
				offsetX=width;
			}
			if(i==getItemCount()-1)offsetY+=height;
            
        }
		totalHeight=offsetY;
	}

	@Override
	public boolean canScrollVertically()
	{
		return true;
	}
private int verticalScrollOffset;
	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
	{
	int travel = dy;

    //如果滑动到最顶部
    if (verticalScrollOffset + dy < 0) {
		//travel = -verticalScrollOffset;
		travel=0;
    } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
		//travel = totalHeight - getVerticalSpace() - verticalScrollOffset;
   travel=0; }

    //将竖直方向的偏移量+travel
    verticalScrollOffset += travel;

    // 调用该方法通知view在y方向上移动指定距离
    offsetChildrenVertical(-travel);

    return travel;
	}

	private int getVerticalSpace() {
		//计算RecyclerView的可用高度，除去上下Padding值
		return getHeight() - getPaddingBottom() - getPaddingTop();
	}

		
}
