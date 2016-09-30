package com.voyg.msg;

import java.util.concurrent.Callable;

/**
* <strong>business handler</strong><br>
* @author voyg.net
* @since 2016/9/29
*/
public class BussinessHandler implements IBussinessHandler, Callable<String> {
	private String topic;   //received kafka topic
	private String key;     //received kafka key
	private String msg;     //received kafka message

	public BussinessHandler(String topic, String key, String msg) {
		this.topic = topic;
		this.key = key;
		this.msg = msg;
	}

	@Override
	public String call(){
		//TODO your code here
		System.out.println("I get kafka message, and start to do business logics: " + topic + "->" + key + ":" + msg);
		return "success";
	}
}
