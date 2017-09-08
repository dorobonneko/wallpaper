package com.moe.utils;
import android.support.v4.provider.DocumentFile;
import java.io.File;
import android.net.Uri;

public class DocumentFileUtils
{
	//直接返回URI
	public static DocumentFile getDocumentFilePath(DocumentFile doc,File file) {
		String[] parts = file.getAbsolutePath().split("/");
		for (int i = 3; i < parts.length; i++) {
			DocumentFile tmp= doc.findFile(parts[i]);
			if(tmp==null){
				if(i==parts.length-1){
					doc=doc.createFile("audio/*",parts[i]);
				}else{
					doc=doc.createDirectory(parts[i]);
				}
			}else{
					doc=tmp;
			}
			
				
		}
		return doc;
	}
}
