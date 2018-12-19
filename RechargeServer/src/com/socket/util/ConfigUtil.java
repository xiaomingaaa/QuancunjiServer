package com.socket.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.soket.entity.Config;

public class ConfigUtil {
	public static Config GetConfig() {
		String path=System.getProperty("user.dir")+"/config.json";
		Config config=null;
		BufferedReader bufferedReader=null;
		try {
			FileReader reader=new FileReader(new File(path));
			bufferedReader=new BufferedReader(reader);
			String json="";
			String tempLine=bufferedReader.readLine();
			while(tempLine!=null&&!tempLine.equals("")) {
				json=json+tempLine;
				tempLine=bufferedReader.readLine();
			}
			bufferedReader.close();
			JSONObject jsonObject=new JSONObject(json);
			config=new Config(jsonObject.getInt("port"),jsonObject.getString("dbuser"),jsonObject.getString("dbpwd"));
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return config;
	}
}
