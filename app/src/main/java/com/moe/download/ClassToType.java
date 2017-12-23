package com.moe.download;
import java.lang.reflect.*;

class ClassToType
{
	public static String getType(Field class_){
		switch(class_.getType().getSimpleName()){
			case "String":
				return "TEXT";
			case "long":
			case "int":
			case "char":
			case "short":
			case "boolean":
			case "byte":
				return "INTEGER";
			case "float":
			case "double":
				return "REAL";
				default:
				return "NULL";
		}
	}
	public static boolean isAccess(Field field){
		switch(field.getType().getSimpleName()){
			case "String":
			case "int":
			case "char":
			case "short":
			case "long":
			case "boolean":
			case "float":
			case "double":
			case "byte":
				return true;
		}
		return false;
	}
}
