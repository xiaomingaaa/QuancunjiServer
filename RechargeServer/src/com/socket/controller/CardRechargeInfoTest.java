package com.socket.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.socket.util.EncryData;

class CardRechargeInfoTest {

	CardRechargeInfo card=new CardRechargeInfo();
	@Test
	void test() throws JSONException {
		JSONObject jsonObject= card.GetFristRechargeInfo("124932", "56758");
		String sendStr=jsonObject.toString();
		System.out.println(sendStr);
		String mingwen=EncryData.Base64StrDecode(jsonObject.getString("msg"));
		System.out.println(mingwen);
	}
	WaterCardInfo waterCardInfo=new WaterCardInfo();
	@Test
	void testWaterCard() throws JSONException
	{
		JSONObject jsonObject= waterCardInfo.GetFristRechargeInfo("124932", "56777");
		String sendStr=jsonObject.toString();
		System.out.println(sendStr);
		String mingwen=EncryData.Base64StrDecode(jsonObject.getString("msg"));
		System.out.println(mingwen);
	}
	@Test
	void testUpdateStatus()
	{
		boolean a=false;
		a=waterCardInfo.UpdateSatus("124932", "56758", 23.58);
		System.out.println(a);
	}
}
