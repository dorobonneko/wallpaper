package com.moe.download;

public class QuerySql<T extends DownloadObject>
{
	private String sql;
	private DownloadQuery.Listener<T> listener;
	private String table,where,groupby,having,order;
	private String[] keys,whereArgs;
	public QuerySql(String sql){
		this.sql=sql;
	}
	public QuerySql()
	{}

	protected void setTable(String table)
	{
		this.table=table;
	}
	public void setListener(DownloadQuery.Listener<T> listener)
	{
		this.listener = listener;
	}

	public DownloadQuery.Listener<T> getListener()
	{
		return listener;
	}
	public String getSql(){
		if(sql==null){
			StringBuffer sql=new StringBuffer("select ");
			if(keys==null)
				sql.append("*");
				else{
					for(String key:keys){
						sql.append(key).append(",");
					}
					sql.deleteCharAt(sql.length()-1);
				}
				sql.append(" from ").append(table);
			if(where!=null){
				sql.append(" where ").append(where).append(" ");
				if(whereArgs!=null)
				for(String args:whereArgs){
					int index=sql.indexOf("?");
					sql.replace(index,index+1,args);
				}
			}
			sql.append(groupby==null?"":(" group by "+groupby));
			sql.append(having==null?"":(" having "+having));
			sql.append(order==null?"":(" order by "+order));
			this.sql=sql.toString();
		}
		return sql;
	}
	public void setArgs(String[] keys,String where,String[] whereArgs,String groupby,String having,String order){
		this.keys=keys;
		this.where=where;
		this.whereArgs=whereArgs;
		this.groupby=groupby;
		this.having=having;
		this.order=order;
	}
}
