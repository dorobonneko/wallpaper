package com.moe.LiveVisualizer.service;
import android.service.quicksettings.TileService;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;

public class CircleSwitch extends TileService
{
	private SharedPreferences moe;

	@Override
	public void onCreate()
	{
		super.onCreate();
		moe=getSharedPreferences("moe",0);
	}
	
	@Override
	public void onTileAdded()
	{
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setLabel(moe.getBoolean("circleSwitch",true)?"旋转开启":"旋转关闭");
			tile.setState(Tile.STATE_ACTIVE);
			tile.updateTile();
		}
	}

	@Override
	public void onTileRemoved()
	{
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setLabel(moe.getBoolean("circleSwitch",true)?"旋转开启":"旋转关闭");
			tile.setState(Tile.STATE_INACTIVE);
			tile.updateTile();
		}
	}

	@Override
	public void onClick()
	{
		moe.edit().putBoolean("circleSwitch",!moe.getBoolean("circleSwitch",true)).commit();
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setLabel(moe.getBoolean("circleSwitch",true)?"旋转开启":"旋转关闭");
			tile.setState(Tile.STATE_ACTIVE);
			tile.updateTile();
		}
	}

	@Override
	public void onStartListening()
	{
		onTileAdded();
	}

	@Override
	public void onStopListening()
	{
		// TODO: Implement this method
		super.onStopListening();
	}
	
}
