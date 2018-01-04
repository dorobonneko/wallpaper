package com.moe.view;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;

public class GridDivider extends RecyclerView.ItemDecoration
{
	private int height;
	public GridDivider(int height){
		this.height=height;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		GridLayoutManager glm=(GridLayoutManager)parent.getLayoutManager();
		if(glm.getPosition(view)%glm.getSpanCount()==0){
			outRect.set(height,height,height,0);
		}else{
			outRect.set(0,height,height,0);
		}
		if(glm.getItemCount()%glm.getSpanCount()==0){
			//正好对齐
			if(glm.getPosition(view)>=(glm.getItemCount()/glm.getSpanCount()-1)*glm.getSpanCount()){
				outRect.bottom=height;
			}
		}else if(glm.getPosition(view)>=(glm.getItemCount()/glm.getSpanCount())*glm.getSpanCount()){
			outRect.bottom=height;
		}
	}
	
}
