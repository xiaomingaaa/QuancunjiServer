package com.socket.test;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.socket.util.EncryData;
import com.soket.entity.ErrorMsg;

class EncryDataTest {

	@Test
	void test() {
		String aString="AABBsui|shdui|djsa|shdaj|sjdbBBAA";
		String s1=EncryData.GetBase64Str(aString);
		String decodeStr=EncryData.Base64StrDecode(s1);
		System.out.println(String.format("加密前：%s,加密后：%s", decodeStr,s1));
		assertEquals(aString, decodeStr);
	}
	@Test
	void TestErrorMsgJson()
	{
		String excepted=(new ErrorMsg(102, "卡信息不存在")).toString();
		JSONObject object;
		try {
			object = new JSONObject(excepted);
			String json=object.toString();
			assertEquals(excepted, json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	@Test
	void TestMd5Str()
	{
		String mingwen="AABBsui|shdui|djsa|shdaj|sjdbBBAA";
		String miwen=EncryData.GetMd5Str(mingwen);
		assertEquals(mingwen, miwen);
	}
}
