package com.socket.util;

import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.socket.controller.CardRechargeInfo;
import com.socket.controller.WaterCardInfo;

/**
 * �����ӿͻ��˷��������ݰ�
 * @author ���ڷ�
 *
 */
public class UnpackUtil {
	public static JSONArray GetBody(String pack)
	{		JSONArray array=new JSONArray();
		try {
			if(pack!=null&&!pack.equals(""))
			{
				String type=pack.substring(4,6);
				System.out.println("type:"+type);
				String md5=pack.substring(6, 38);
				System.out.println("md5:"+md5);
				int endindex=pack.lastIndexOf("zfjy");
				System.out.println("endindex:"+endindex);
				String body=pack.substring(42,endindex);
				System.out.println("body:"+body);
				//����ƥ��ɹ�
				if(EncryData.GetMd5Str(body).equals(md5))
				{
					if(type.equals("00"))
					{
						array=GetFristBody(body);
					}
					else if(type.equals("01"))
					{
						array=GetSecondBody(body);
					}
					return array;
				}
				else
				{
					String back=String.format("{error_code:%d,msg:\"%s\"}", 101,"���ݸ�ʽ������");
					array.put(new JSONObject(back));
					array.put(new JSONObject(back));
				}
			}
			else
			{
				String back=String.format("{error_code:%d,msg:\"%s\"}", 101,"���ݸ�ʽ������");
				array.put(new JSONObject(back));
				array.put(new JSONObject(back));
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			String back=String.format("{error_code:%d,msg:\"%s\"}", 103,"�������ڲ�����");
			try {
				array.put(new JSONObject(back));
				array.put(new JSONObject(back));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Log.writeToError("���ʱ���ִ���"+e.getMessage());
		}
		return array;
	}
	private static JSONArray GetFristBody(String body)
	{
		String[] temp=body.split("\\|");
		System.out.println("�ָ����Ԫ������"+temp.length);
		String cardno=temp[0];//�Ϳ���
		String waterCardNo=temp[1];//ˮ����
		String schoolid=temp[2];//ѧУ��
		CardRechargeInfo cardRechargeInfo=new CardRechargeInfo();
		JSONObject object=cardRechargeInfo.GetFristRechargeInfo(cardno, schoolid);
		System.out.println(object.toString());
		WaterCardInfo waterCard=new WaterCardInfo();
		JSONObject object2=waterCard.GetFristRechargeInfo(waterCardNo, schoolid);
		System.out.println(object2.toString());
		JSONArray array=new JSONArray();
		array.put(object);
		array.put(object2);
		return array;
	}
	private static JSONArray GetSecondBody(String body)
	{
		//
		JSONArray back=new JSONArray();//�������ݵ��ַ���
		try {
			JSONArray array=new JSONArray(body);
			JSONObject object=array.getJSONObject(0);//�Ϳ�
			JSONObject object2=array.getJSONObject(1);//ˮ��
			int error_code1=object.getInt("error_code");//�Ϳ�
			int error_code2=object2.getInt("error_code");//ˮ��
			//�����������
			if(error_code1==0)
			{
				String bodyCanka=object.getString("msg");//��ȡ�Ϳ�����
				Log.writeToRecharge("��ȡ���ĲͿ����ݣ�"+bodyCanka);
				String[] temp=bodyCanka.split("\\|");//�ԡ�|���ָ��ַ���
				String cardno=temp[0];
				String schoolid=temp[1];
				String tradeno=temp[2];
				double money=Double.parseDouble(temp[3]);
				CardRechargeInfo cardRechargeInfo=new CardRechargeInfo();
				boolean flag=cardRechargeInfo.UpdateSatus(cardno, schoolid, money, tradeno);
				int code=404;
				String msg="failed";
				if(flag)
				{
					code=0;
					msg="OK";
				}
				JSONObject back1=new JSONObject(String.format("{error_code:%d,msg:\"%s\"}", code,msg));
				back.put(back1);
			}
			else
			{
				int code=404;
				String msg="failed";
				JSONObject back1=new JSONObject(String.format("{error_code:%d,msg:\"%s\"}", code,msg));
				back.put(back1);
			}
			if(error_code2==0)
			{
				String bodyShuika=object2.getString("msg");//��ȡ�Ϳ�����
				Log.writeToRecharge("��ȡ����ˮ�����ݣ�"+bodyShuika);
				String[] temp=bodyShuika.split("\\|");//�ԡ�|���ָ��ַ���
				String cardno=temp[0];
				String schoolid=temp[1];
				double money=Double.parseDouble(temp[2]);
				WaterCardInfo waterCardInfo=new WaterCardInfo();
				boolean flag=waterCardInfo.UpdateSatus(cardno, schoolid, money);
				int code=404;
				String msg="failed";
				if(flag)
				{
					code=0;
					msg="OK";
				}
				JSONObject back1=new JSONObject(String.format("{error_code:%d,msg:\"%s\"}", code,msg));
				back.put(back1);
			}
			else
			{
				int code=404;
				String msg="failed";
				JSONObject back1=new JSONObject(String.format("{error_code:%d,msg:\"%s\"}", code,msg));
				back.put(back1);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("����ڶ��η��͵�����ʱ�����˴���"+e.getMessage());
		}
		return back;
	}
}
