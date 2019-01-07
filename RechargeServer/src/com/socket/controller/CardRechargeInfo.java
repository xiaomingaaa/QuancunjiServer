package com.socket.controller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.socket.util.*;
import com.soket.entity.ErrorMsg;
public class CardRechargeInfo {
	/**
	 * 查询判断此人卡号是否存在
	 * @param cardno 卡号
	 * @param school 学校号
	 * @return
	 */
	private boolean QueryCardExist(String cardno,String schoolid)
	{
		String sql="select id from zf_card_info where card_no=? and school_id=?";
		Object[] params={cardno,schoolid};
		String[] colunms= {"id"};
		SQLHelper helper=new SQLHelper();
		try {
			JSONArray set=helper.QueryResult(sql, params,colunms);
			if(set.length()>0)
			{
				
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError(String.format("查询卡号信息是否存在时出现错误:%s,错误代码：%d", e.getMessage(),0));
			return false;
		}
		return false;
	}
	/**
	 * 获取第一次客户端通信返回的json数据
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
				jsonObject=new JSONObject((new ErrorMsg(404, "卡信息不存在")).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.writeToError("返回json数据出现错误："+e.getMessage());
			}
			return jsonObject;
		}
		String sql="select credit,trade_no,type from zf_recharge_detail where card_no=? and is_active = 0 and school_id = ? order by id asc limit 1";
		Object[] params={cardno,schoolid};
		String[] colunms= {"credit","trade_no","type"};
		SQLHelper helper=new SQLHelper();
		JSONArray set=helper.QueryResult(sql, params,colunms);
		try {
			if(set!=null)
			{
				if(set.length()>0)
				{
					JSONObject object=set.getJSONObject(0);
					double money=object.getDouble("credit");
					String trade_no=object.getString("trade_no");
					String type=object.getString("type");
					result=EncryDataPackage(trade_no,type,money,cardno,schoolid);
					code=0;				
				}				
				else
				{
					code=102;
					result="没有卡充值信息";
				}
			}
			else
			{
				code=103;
				result="服务器内部错误";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code=103;
			result="服务器内部错误";
			Log.writeToError(String.format("查询zf_recharge_detail语句时(可能出现json的错误)出现错误：%s,错误代码：%d",e.getMessage(),0));
		}
		try {
			jsonObject=new JSONObject((new ErrorMsg(code, result)).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("最后返回json结果时出现了错误："+e.getMessage());
		}
		return jsonObject;
	}
	public boolean UpdateSatus(String cardno,String schoolid,double money,String tradeno)
	{
		boolean flag=false;
		if(QueryCardExist(cardno,schoolid))
		{
			String sqlText="select id from zf_recharge_detail where trade_no=? and card_no=?  and school_id=? and is_active=0";
			Object[] params= {tradeno,cardno,schoolid};
			String[] colunms= {"id"};
			SQLHelper helper=new SQLHelper();
			JSONArray array=helper.QueryResult(sqlText, params, colunms);
			JSONObject object=new JSONObject();			
			try {
				if(array.length()>0)
				{
					object=array.getJSONObject(0);
					long time= System.currentTimeMillis()/1000;
					int id=object.getInt("id");
					String updateSql="update zf_recharge_detail set is_active=1,balance=?,qctime=? where id=?";
					String updateBalance="update zf_card_info set balance=? where card_no=?";
					Object[] params2= {money,cardno};
					Object[] params1= {money,time,id};
					if(helper.Update(updateSql, params1)&&helper.Update(updateBalance, params2))
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
					Log.writeToError("更改状态圈存状态时出现错误："+e.getMessage());
			}			
			
			
		}
		
		return flag;
	}
	/**
	 * 构造数据包
	 * @param trado_no 订单号
	 * @param type 支付类型
	 * @param money 圈存金额
	 * @param cardno 卡号
	 * @param schoolid 学校号
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
