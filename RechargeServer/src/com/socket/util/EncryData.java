package com.socket.util;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
/**
 * 对数据进行加密
 * @author 马腾飞
 *
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class EncryData {
	/**
	 * md5加密
	 * @param content
	 * @return
	 */
	public static String GetMd5Str(String content)
	{
		String result="";
		try {
			MessageDigest mDigest=MessageDigest.getInstance("MD5");
			byte[] md5Bytes=mDigest.digest(content.getBytes(Charset.forName("utf-8")));
			StringBuilder buffer=new StringBuilder(32);
			for(byte b:md5Bytes)
			{
				int number=b&0xff;
				String str=Integer.toHexString(number);
				if(str.length()==1)
				{
					buffer.append("0");
				}
				buffer.append(str);
			}
			result=buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("加密时出现错误："+e.getMessage());
		}
		return result;
	}
	public static String GetBase64Str(String content)
	{
		String baseUrlStr=Base64.getEncoder().encodeToString(content.getBytes(Charset.forName("utf-8")));
		return baseUrlStr;
	}
	public static String Base64StrDecode(String content)
	{
		byte[]  strBytes=Base64.getDecoder().decode(content.getBytes(Charset.forName("utf-8")));
		String temp="";
		try {
			temp= new String(strBytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.writeToError("base64编解码时出现错误："+e.getMessage());
		}
		return temp;
	}
}
