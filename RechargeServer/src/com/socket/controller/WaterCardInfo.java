package com.socket.controller;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.socket.util.EncryData;
import com.socket.util.Log;
import com.socket.util.SQLHelper;
import com.soket.entity.ErrorMsg;

/**
 * ˮ�����ݴ���
 * @author ���ڷ�
 *
 */
public class WaterCardInfo {
	
	private  final String url="jdbc:mysql://localhost:3306/card_water"; 
	
	/**
	 * ��ѯ�жϴ��˿����Ƿ����
	 * @param cardno ����
	 * @param school ѧУ��
	 * @return
	 */
	private boolean QueryCardExist(String cardno,String schoolid)
	{
		String sql="select id from zf_card_info where card_no=? and school_id=?";
		Object[] params={cardno,schoolid};
		String[] colunms= {"id"};
		SQLHelper helper=new SQLHelper();
		helper.url=url;
		try {
			JSONArray set=helper.QueryResult(sql, params,colunms);
			if(set.length()>0)
			{
				
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError(String.format("��ѯ������Ϣ�Ƿ����ʱ���ִ���:%s,������룺%d", e.getMessage(),0));
			return false;
		}
		return false;
	}
	/**
	 * ��ȡ��һ�οͻ���ͨ�ŷ��ص�json����
	 * @param cardno
	 * @param schoolid
	 * @return
	 */
	public JSONObject GetFristRechargeInfo(String cardno,String schoolid)
	{
		int code=0;
		String result="";
		JSONObject jsonObject=new JSONObject();
		if(!QueryCardExist(cardno, schoolid))
		{
			try {
				jsonObject=new JSONObject((new ErrorMsg(404, "����Ϣ������")).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("����json���ݳ��ִ���"+e.getMessage());
			}
			return jsonObject;
		}
		String sql="select credit,type from zf_recharge_detail where card_no=? and is_active = 0 and school_id = ? order by id asc limit 1";
		Object[] params={cardno,Integer.parseInt(schoolid)};
		String[] colunms= {"credit","type"};
		SQLHelper helper=new SQLHelper();
		helper.url=url;
		JSONArray set=helper.QueryResult(sql, params,colunms);
		try {
			if(set!=null)
			{
				if(set.length()>0)
				{
					JSONObject object=set.getJSONObject(0);
					double money=object.getDouble("credit");
					String trade_no="0";
					String type=object.getString("type");
					result=EncryDataPackage(trade_no,type,money,cardno,schoolid);
					code=0;				
				}				
				else
				{
					code=102;
					result="û�п���ֵ��Ϣ";
				}
			}
			else
			{
				code=103;
				result="�������ڲ�����";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code=103;
			result="�������ڲ�����";
			Log.writeToError(String.format("��ѯzf_recharge_detail���ʱ(���ܳ���json�Ĵ���)���ִ���%s,������룺%d",e.getMessage(),0));
		}
		try {
			jsonObject=new JSONObject((new ErrorMsg(code, result)).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("��󷵻�json���ʱ�����˴���"+e.getMessage());
		}
		return jsonObject;
	}
	public boolean UpdateSatus(String cardno,String schoolid,double money)
	{
		boolean flag=false;
		if(QueryCardExist(cardno,schoolid))
		{
			String sqlText="select id from zf_recharge_detail where card_no=?  and school_id=? and is_active=0";
			Object[] params= {cardno,schoolid};
			String[] colunms= {"id"};
			SQLHelper helper=new SQLHelper();
			helper.url=url;
			JSONArray array=helper.QueryResult(sqlText, params, colunms);
			JSONObject object=new JSONObject();			
			try {
				if(array.length()>0)
				{
					object=array.getJSONObject(0);
					long time= System.currentTimeMillis()/1000;
					String updateSql="update zf_recharge_detail set is_active=1,balance=?,qctime=? where id=?";
					Object[] params1= {money,time,object.getInt("id")};
					if(helper.Update(updateSql, params1))
					{
						flag=true;
					}
					else
					{
						flag=false;
					}
				}
			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.writeToError("����״̬Ȧ��״̬ʱ���ִ���"+e.getMessage());
			}			
			
			
		}
		
		return flag;
	}
	/**
	 * �������ݰ�
	 * @param trado_no ������
	 * @param type ֧������
	 * @param money Ȧ����
	 * @param cardno ����
	 * @param schoolid ѧУ��
	 * @return
	 */
	private String EncryDataPackage(String trado_no,String type,double money,String cardno,String schoolid)
	{
		String body=cardno+"|"+schoolid+"|"+Double.toString(money)+"|"+trado_no+"|"+type;
		String miwen=EncryData.GetMd5Str(body);
		body=EncryData.GetBase64Str("AABB"+miwen+"hnzf"+body+"zfjy"+"BBAA");
		return body;
	}
}
