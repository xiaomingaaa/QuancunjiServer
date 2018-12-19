package com.socket.util;
/**
 * mysql数据库的访问类
 * @author 马腾飞
 *
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.soket.entity.Config;

import java.sql.PreparedStatement;

public class SQLHelper {
	private static final String driverName="com.mysql.jdbc.Driver";
	public   String url="jdbc:mysql://localhost:3306/card";
	private String userName;
	private String password;
	private Connection connection;//连接对象
	private PreparedStatement pStatement;//执行sql语句
	private ResultSet resultSet;//查询结果集
	public SQLHelper() {
		Config config=ConfigUtil.GetConfig();
		userName=config.getDbuser();
		password=config.getDbpwd();
	}
	/**
	 * 获取数据库连接，对与外界不可见
	 * @return true获得成功 false获得失败
	 */
	private boolean GetConnection() {
		try {
			Class.forName(driverName);
			connection=DriverManager.getConnection(url, userName, password);
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError(String.format("餐卡加载驱动失败：%s", e.getMessage()));
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("获取餐卡连接数据库对象错误："+e.getMessage());
			return false;
		}
	}
	/**
	 * 释放内存
	 */
	private void Release() {
		try {
			if(connection!=null) {
				connection.close();
			}
			if(resultSet!=null)
			{
				resultSet.close();
			}
			if(pStatement!=null)
			{
				pStatement.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("释放连接内存出现错误："+e.getMessage());
		}
	}
	/**
	 * 单语句查询结果
	 * @param sql 查询sql语句
	 * @param params 预定义参数
	 * @param colunms 使用预定义的列明，将获取到的数据转换成Json格式转发
	 * @return
	 */
	public JSONArray QueryResult(String sql,Object params[],String[] colunms)
	{
		JSONArray array=new JSONArray();
		 if(GetConnection())
		 {
			 try {
				pStatement=connection.prepareStatement(sql);
				for(int i=0;i<params.length;i++)
				{
					pStatement.setObject(i+1, params[i]);
				}
				resultSet=pStatement.executeQuery();
				while(resultSet.next())
				{
					JSONObject object=new JSONObject();
					for(int i=0;i<colunms.length;i++)
					{
						object.put(colunms[i], resultSet.getObject(colunms[i]));
					}
					array.put(object);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("查询结果集出错："+e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("查询结果集时json转换出现错误："+e.getMessage());
			}
			
		 }	
		 if(array.length()>0)
		 {
			 try {
				System.out.println(array.getJSONObject(0).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 Release();
		 return array;
	}
	/**
	 * 执行更新删除操作语句
	 * @param sql 要执行的sql语句
	 * @param params sql语句中的参数
	 * @return 是否执行成功的标记 true成功，false失败
	 */
	public boolean Update(String sql,Object[] params)
	{
		if(GetConnection())
		{
			try {
				pStatement=connection.prepareStatement(sql);
				for(int i=0;i<params.length;i++)
				{
					pStatement.setObject(i+1, params[i]);
				}
				int flag=pStatement.executeUpdate();
				if(flag>0)
				{
					Release();
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("执行sql语句的更新操作时出现错误："+e.getMessage());
				Release();
				return false;
			}
			
		}
		Release();
		return false;
	}
	/**
	 * 批量插入数据到数据库
	 * @param sql 要执行的sql语句模板
	 * @param array 要执行的参数集合
	 * @param colunms 模板中列的名称
	 * @return true执行成功，false执行失败
	 */
	public boolean UpdateAllSql(String sql,JSONArray array,String[] colunms)
	{
		if(GetConnection())
		{
			try {
				connection.setAutoCommit(false);
				pStatement=connection.prepareStatement(sql);
				for(int i=0;i<array.length();i++)
				{
					JSONObject jsonObject=array.getJSONObject(i);
					for(int j=0;j<colunms.length;j++)
					{
						pStatement.setObject(j+1,jsonObject.get(colunms[j]));
						
					}
					pStatement.addBatch();
				}
				pStatement.executeBatch();
				Release();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("批量更新语句查询时出现错误："+e.getMessage());
				Release();
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("批量查询的时候json数据读写错误："+e.getMessage());
				Release();
				return false;
			}
			
		}
		Release();
		return false;
	}
}
