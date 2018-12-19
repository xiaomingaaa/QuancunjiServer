package com.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import com.socket.util.*;

public class Server {
	ServerSocketChannel serverSocketChannel;
	Selector selector=null;
	ByteBuffer readBuffer=ByteBuffer.allocate(1024);
	ByteBuffer writeBuffer=ByteBuffer.allocate(1024);
	CharsetDecoder decode = Charset.forName("UTF-8").newDecoder();
	public void StartListen() {
		try {
			//打开一个server socket channel
			serverSocketChannel=ServerSocketChannel.open();
			//设置socket为非阻塞模式
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(3501));
			selector=Selector.open();//打开选择器
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("服务器开始监听。。。。");
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		while (true) {
			try {
				selector.select();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.writeToError("socket选择器注册出现错误："+e.getMessage());
				break;
			}
			//返回已经就绪了的事件
			Set<SelectionKey> readKeys=selector.selectedKeys();
			for(Iterator<SelectionKey> iterator=readKeys.iterator();iterator.hasNext();) {
				System.out.println("进入客户端轮询");
				SelectionKey key=iterator.next();
				iterator.remove();
				HandleMsg(key);
				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void HandleMsg(SelectionKey key)
	{
		try {
			if(key.isAcceptable()) {
				serverSocketChannel=(ServerSocketChannel)key.channel();
				SocketChannel client=serverSocketChannel.accept();
				if(client!=null) {
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
					System.out.println("客户端建立连接");
				}
				
			}
			else if(key.isReadable()) {
				System.out.println("开始读数据。。。。");
				SocketChannel client=(SocketChannel)key.channel();
				readBuffer.clear();
				String receiveMsg= ReadMessage(client);
				System.out.println(receiveMsg);
				receiveMsg=EncryData.Base64StrDecode(receiveMsg);
				System.out.println(receiveMsg);
				JSONArray array= UnpackUtil.GetBody(receiveMsg);
				String sendStr=array.toString();
				System.out.println(sendStr);
				writeBuffer.clear();
				writeBuffer.put(sendStr.getBytes("utf8"));
				writeBuffer.flip();
				client.write(writeBuffer);
				Log.writeToError("接受到的数据为："+receiveMsg);
			}
		}catch (Exception e) {
			
		}
	}
	//接收用户发言信息
  	private String ReadMessage(SocketChannel sc) {
    		String result = null;
    		int n = 0;
    		try {
      			n = sc.read(readBuffer);
      			if(n>0) {
      				readBuffer.flip();
          			Charset charset = Charset.forName("UTF-8");
          			CharsetDecoder decoder = charset.newDecoder();
          			CharBuffer charBuffer = decoder.decode(readBuffer);
          			result = charBuffer.toString();
      			}
    		}catch (IOException e) {
      			System.out.println("接收用户发言信息时异常，原因  -------->  " + e.getMessage());
      			Log.writeToError("接收用户发送信息时异常，原因  -------->  " + e.getMessage());
   		}
    		return result;
  	}
  	private void SendMsg(String msg)
  	{
  		
  	}
  	private void Display(String[] temp)
  	{
  		for(int i=0;i<temp.length;i++)
  		{
  			System.out.print(temp[i]+",");
  		}
  	}
	
}
