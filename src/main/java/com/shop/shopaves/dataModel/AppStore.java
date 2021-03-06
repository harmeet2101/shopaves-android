package com.shop.shopaves.dataModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.shop.shopaves.Util.MyApp;

import java.util.ArrayList;

public class AppStore{
	private SharedPreferences sp;
	public AppStore(Context ctx) {
		sp = MyApp.getInstance().getPreferences();
	}
	
	public void setData(String key,String value){
		sp.edit().putString(key, value).commit();
	}

/*


	public void setArrayData(String key,ArrayList<String> value){
		sp.edit().putStringSet(key, value).commit();
	}*/
	
	public String getData(String key){
		return sp.getString(key, null);
	}
	
	public void clearData(){
		sp.edit().clear().commit();
	}
	
	public void setInt(String key,int value){
		sp.edit().putInt(key, value).commit();
	}
	
	public int getInt(String key){
		return sp.getInt(key,Integer.MIN_VALUE);
	}

	public void clearDataByKey(String key){
		sp.edit().remove(key).commit();
	}

}
