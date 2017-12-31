package com.moe.download;
import android.database.sqlite.SQLiteStatement;
import java.lang.reflect.Field;

public class FieldUtils
{
	public static void bind(SQLiteStatement state,Field field,Object p0,int index){
		try
		{
			field.setAccessible(true);
			switch (field.getType().getSimpleName())
			{
				case "String":
					state.bindString(index,field.get(p0)==null?"":field.get(p0).toString());
					break;
				case "long":
					state.bindLong(index,field.getLong(p0));
					break;
				case "int":
					state.bindLong(index,field.getInt(p0));
					break;
				case "char":
					state.bindLong(index,field.getChar(p0));
					break;
				case "boolean":
					state.bindLong(index,field.getBoolean(p0)?1:0);
					break;
				case "byte":
					state.bindLong(index,field.getByte(p0));
					break;
				case "short":
					state.bindLong(index,field.getShort(p0));
					break;
				case "float":
					state.bindDouble(index,field.getFloat(p0));
					break;
				case "double":
					state.bindDouble(index,field.getDouble(p0));
					break;
			}
		}
		catch (IllegalAccessException e)
		{}
		catch (IllegalArgumentException e)
		{}
	}
	public static Field getField(Class class_,String name){
		if(class_.getSimpleName().equals(Object.class.getSimpleName()))return null;
		try
		{
			return class_.getDeclaredField(name);
		}
		catch (NoSuchFieldException e)
		{ return getField(class_.getSuperclass(),name);}
	}
}
