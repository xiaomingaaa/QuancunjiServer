package com.soket.entity;

public class RechargeInfo {
	private int cardno;
	private String schoolid;
	private String trade_no;
	private String type;
	private double rechargeMoney;
	/**
	 * @return the cardno
	 */
	public int getCardno() {
		return cardno;
	}
	/**
	 * @param cardno the cardno to set
	 */
	public void setCardno(int cardno) {
		this.cardno = cardno;
	}
	
	/**
	 * @return the rechargeMoney
	 */
	public double getRechargeMoney() {
		return rechargeMoney;
	}
	/**
	 * @param rechargeMoney the rechargeMoney to set
	 */
	public void setRechargeMoney(double rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	/**
	 *  重写toString方法，反回格式化的字符串
	 */
	
	@Override
	public java.lang.String toString() {
		// TODO Auto-generated method stub
		//构造要发送到客户端的消息体
		String string =getCardno()+"|"+getSchoolid()+"|"+getRechargeMoney()+"|"+getTrade_no()+"|"+getType();
		return string;
	}
	/**
	 * @return the trade_no
	 */
	public String getTrade_no() {
		return trade_no;
	}
	/**
	 * @param trade_no the trade_no to set
	 */
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	/**
	 * @return the schoolid
	 */
	public String getSchoolid() {
		return schoolid;
	}
	/**
	 * @param schoolid the schoolid to set
	 */
	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
