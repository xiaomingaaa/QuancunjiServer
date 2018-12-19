package com.socket.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.socket.util.ConfigUtil;

class ConfigUtilTest {

	/**
	 * ≤‚ ‘config.json÷–µƒ≈‰÷√
	 */
	@Test
	void test() {
		ConfigUtil configUtil=new ConfigUtil();
		String str= configUtil.GetConfig().toString();
		assertEquals("35001,root,root", str);		
	}
	
}
