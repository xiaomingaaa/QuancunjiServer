package com.socket.server;


/**
 * 创建用于管理socket连接的类
 * @author 马腾飞
 *
 */
public class Connect {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server server=new Server();
		server.StartListen();//开启服务器监听程序
	}
}
