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
			//��һ��server socket channel
			serverSocketChannel=ServerSocketChannel.open();
			//����socketΪ������ģʽ
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(ConfigUtil.GetConfig().getPort()));
			//System.out.print(ConfigUtil.GetConfig().getPort());
			selector=Selector.open();//��ѡ����
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("��������ʼ������������");
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		while (true) {
			try {
				selector.select();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.writeToError("socketѡ����ע����ִ���"+e.getMessage());
				break;
			}
			//�����Ѿ������˵��¼�
			Set<SelectionKey> readKeys=selector.selectedKeys();
			for(Iterator<SelectionKey> iterator=readKeys.iterator();iterator.hasNext();) {
				//System.out.println("����ͻ�����ѯ");
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
					System.out.println("�ͻ��˽�������");
				}
				
			}
			else if(key.isReadable()) {
				//System.out.println("��ʼ�����ݡ�������");
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
				Log.writeToError("���ܵ�������Ϊ��"+receiveMsg);
			}
		}catch (Exception e) {
			
		}
	}
	//�����û�������Ϣ
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
      			else
      			{
      				sc.close();
      			}
    		}catch (IOException e) {
      			try {
					sc.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("�ͻ��˼����ر��쳣��"+e1.getMessage());
					Log.writeToError("�ͻ��˼����ر��쳣��"+e1.getMessage());
				}
    			System.out.println("�����û�������Ϣʱ�쳣��ԭ��  -------->  " + e.getMessage());
      			Log.writeToError("�����û�������Ϣʱ�쳣��ԭ��  -------->  " + e.getMessage());
      			
   		}
    		return result;
  	}
	
}
