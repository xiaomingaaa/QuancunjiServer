package com.socket.util;
/**
 * mysql���ݿ�ķ�����
 * @author ���ڷ�
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
	private Connection connection;//���Ӷ���
	private PreparedStatement pStatement;//ִ��sql���
	private ResultSet resultSet;//��ѯ�����
	public SQLHelper() {
		Config config=ConfigUtil.GetConfig();
		userName=config.getDbuser();
		password=config.getDbpwd();
	}
	/**
	 * ��ȡ���ݿ����ӣ�������粻�ɼ�
	 * @return true��óɹ� false���ʧ��
	 */
	private boolean GetConnection() {
		try {
			Class.forName(driverName);
			connection=DriverManager.getConnection(url, userName, password);
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError(String.format("�Ϳ���������ʧ�ܣ�%s", e.getMessage()));
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("��ȡ�Ϳ��������ݿ�������"+e.getMessage());
			return false;
		}
	}
	/**
	 * �ͷ��ڴ�
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
			Log.writeToError("�ͷ������ڴ���ִ���"+e.getMessage());
		}
	}
	/**
	 * ������ѯ���
	 * @param sql ��ѯsql���
	 * @param params Ԥ�������
	 * @param colunms ʹ��Ԥ���������������ȡ��������ת����Json��ʽת��
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
				Log.writeToError("��ѯ���������"+e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("��ѯ�����ʱjsonת�����ִ���"+e.getMessage());
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
	 * ִ�и���ɾ���������
	 * @param sql Ҫִ�е�sql���
	 * @param params sql����еĲ���
	 * @return �Ƿ�ִ�гɹ��ı�� true�ɹ���falseʧ��
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
				Log.writeToError("ִ��sql���ĸ��²���ʱ���ִ���"+e.getMessage());
				Release();
				return false;
			}
			
		}
		Release();
		return false;
	}
	/**
	 * �����������ݵ����ݿ�
	 * @param sql Ҫִ�е�sql���ģ��
	 * @param array Ҫִ�еĲ�������
	 * @param colunms ģ�����е�����
	 * @return trueִ�гɹ���falseִ��ʧ��
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
				Log.writeToError("������������ѯʱ���ִ���"+e.getMessage());
				Release();
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("������ѯ��ʱ��json���ݶ�д����"+e.getMessage());
				Release();
				return false;
			}
			
		}
		Release();
		return false;
	}
}
