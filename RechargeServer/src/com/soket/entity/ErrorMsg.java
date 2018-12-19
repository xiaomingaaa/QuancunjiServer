package com.soket.entity;

public class ErrorMsg {
	private int error_code;
	private String msg="-";
	public ErrorMsg(int code,String content)
	{
		error_code=code;
		msg=content;
	}
	@Override
	public String toString()
	{
		String json=String.format("{error_code:%d,msg:\"%s\"}", error_code,msg);
		return json;
	}
}
