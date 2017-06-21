package com.didispace;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import xiaopeng666.top.Application;
import xiaopeng666.top.mq.Sender;


public class HelloApplicationTests {

	@Autowired
	private Sender sender;

	@Test
	public void hello() throws Exception {

	}

}
