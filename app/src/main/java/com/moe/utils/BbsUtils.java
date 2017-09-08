package com.moe.utils;
import java.util.List;
import com.moe.entity.BbsItem;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import android.content.Context;
import java.util.ArrayList;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class BbsUtils
{
	public static List<BbsItem> getBbs(Context context){
		Document doc=null;
		try
		{
			doc=Jsoup.connect(PreferenceUtils.getHost(context) + "/bbs.htm")
				.userAgent(PreferenceUtils.getUserAgent())
				//.header("Cookie",moe.getString("cookie","))
				//.cookie("ASP.NET_SessionId","eblkqa45lstya2fqedir2h45")
				//.cookie("GUID","5e31fe031954")
				.cookie(PreferenceUtils.getCookieName(context),PreferenceUtils.getCookie(context))
				.get();
		}
		catch (IOException e)
		{return null;}
		Elements supelements=doc.getElementsByClass("bbslist");
		if(supelements.size()==0)return null;
		List<BbsItem> lbi=new ArrayList<>();
		for(Element sub:supelements){
			Elements elements=sub.children();
			for(int i=0;i<elements.size();i++){
				try{
					BbsItem bi=new BbsItem();
					Element element=elements.get(i);
					String url=element.getElementsByTag("a").get(0).absUrl("href");
					bi.setClassid(Integer.parseInt(url.substring(url.lastIndexOf("=")+1)));
					bi.setTitle(element.getElementsByTag("h2").get(0).childNode(0).toString());
					bi.setProgress(element.getElementsByTag("h3").get(0).childNode(0).toString());
					bi.setImgurl(element.getElementsByTag("img").get(0).absUrl("src"));
					bi.setType("days");
					bi.setKey("365");
					bi.setAction("search");
					lbi.add(bi);
				}catch(Exception e){}
			}
		}
		return lbi;
	}
}
