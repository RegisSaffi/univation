package com.university.univation;

import com.google.android.gms.maps.model.LatLng;

public class building
{
	
	public String name="ggghj";
	public String data="gghhhj";
	public String desc="ggghyu";
	public String id="gdhhdh";
	public String image="ggghyu";

	private LatLng location;

	public void setLocation(LatLng location){this.location=location;}
	public LatLng getLocation(){return location;}

	public String getName()
	{

		return name;
	}
	public void setName(String name)
	{
		this.name=name;
	}

	public String getDesc()
	{

		return desc;
	}
	public void setDesc(String desc)
	{
		this.desc=desc;
	}

	public String getId()
	{

		return id;
	}
	public void setId(String id)
	{
		this.id=id;
	}
	
	public String getData()
	{

		return data;
	}

	public void setData(String data)
	{
		this.data=data;
	}

	public String getImage()
	{

		return image;
	}


	public void setImage(String image)
	{
		this.image=image;
	}


}
