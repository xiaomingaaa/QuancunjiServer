package com.soket.entity;
/**
 * 配置文件的序列化实体类
 * @author 马腾飞
 *
 */
public class Config {
	private int port;
	private String dbuser;
	private String dbpwd;
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the dbuser
	 */
	public String getDbuser() {
		return dbuser;
	}
	/**
	 * @param dbuser the dbuser to set
	 */
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	/**
	 * @return the dbpwd
	 */
	public String getDbpwd() {
		return dbpwd;
	}
	/**
	 * @param dbpwd the dbpwd to set
	 */
	public void setDbpwd(String dbpwd) {
		this.dbpwd = dbpwd;
	}
	public Config(int port,String dbuser,String dbpwd) {
		setDbpwd(dbpwd);
		setDbuser(dbuser);
		setPort(port);
	}
	public String toString() {
		String string=String.format("%s,%s,%s", getPort(),getDbuser(),getDbpwd());
		return string;
	}
}
