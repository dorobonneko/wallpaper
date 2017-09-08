package com.moe.entity;
import android.os.Parcelable;
import android.os.Parcel;

public class UserItem implements Parcelable
{
	private String name,logo;
	private int state,sex;
	private long exper,money;
	private int uid;
	private int msg;
	private int level;
	private String nickName,area,registerTime,time,star;
	private String sign;
	private String height,weight,age;
	public void setSign(String sign)
	{
		this.sign = sign;
	}

	public String getSign()
	{
		return sign;
	}
	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getLevel()
	{
		return level;
	}

	public void setHeight(String height)
	{
		this.height = height;
	}

	public String getHeight()
	{
		return height;
	}

	public void setWeight(String weight)
	{
		this.weight = weight;
	}

	public String getWeight()
	{
		return weight;
	}

	public void setAge(String old)
	{
		this.age = old;
	}

	public String getAge()
	{
		return age;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	public String getArea()
	{
		return area;
	}

	public void setRegisterTime(String registerTime)
	{
		this.registerTime = registerTime;
	}

	public String getRegisterTime()
	{
		return registerTime;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getTime()
	{
		return time;
	}

	public void setStar(String star)
	{
		this.star = star;
	}

	public String getStar()
	{
		return star;
	}
	public void setMsg(int msg)
	{
		this.msg = msg > 99 ?99: msg;
	}

	public int getMsg()
	{
		return msg;
	}

	public void setLogo(String logo)
	{
		this.logo = logo;
	}

	public String getLogo()
	{
		return logo;
	}

	public void setName(String author)
	{
		this.name = author;
	}

	public String getName()
	{
		return name;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public int getState()
	{
		return state;
	}

	public void setSex(int sex)
	{
		this.sex = sex;
	}

	public int getSex()
	{
		return sex;
	}

	public void setExper(long exper)
	{
		this.exper = exper;
	}

	public long getExper()
	{
		return exper;
	}

	public void setMoney(long money)
	{
		this.money = money;
	}

	public long getMoney()
	{
		return money;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getUid()
	{
		return uid;
	}
	@Override
	public int describeContents()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		p1.writeString(name);
		p1.writeInt(state);
		p1.writeInt(sex);
		p1.writeLong(exper);
		p1.writeLong(money);
		p1.writeInt(uid);
		p1.writeString(logo);
		p1.writeInt(msg);
		p1.writeInt(level);
		p1.writeString(height);
		p1.writeString(weight);
		p1.writeString(age);
		p1.writeString(nickName);
		p1.writeString(area);
		p1.writeString(registerTime);
		p1.writeString(time);
		p1.writeString(star);
		p1.writeString(sign);
	}
	public static Parcelable.Creator<UserItem> CREATOR=new Parcelable.Creator<UserItem>(){

		@Override
		public UserItem createFromParcel(Parcel p1)
		{
			// TODO: Implement this method
			return new UserItem(p1);
		}

		@Override
		public UserItem[] newArray(int p1)
		{
			// TODO: Implement this method
			return new UserItem[p1];
		}

	};
	public UserItem()
	{}
	public UserItem(Parcel p1)
	{
		setName(p1.readString());
		setState(p1.readInt());
		setSex(p1.readInt());
		setExper(p1.readLong());
		setMoney(p1.readLong());
		setUid(p1.readInt());
		setLogo(p1.readString());
		setMsg(p1.readInt());
		level = p1.readInt();
		height = p1.readString();
		weight = p1.readString();
		age = p1.readString();
		nickName = p1.readString();
		area = p1.readString();
		registerTime = p1.readString();
		time = p1.readString();
		star = p1.readString();
		sign=p1.readString();
	}
}
