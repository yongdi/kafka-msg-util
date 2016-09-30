package com.voyg.msg;

import java.util.concurrent.Future;

/**
 * Created by voyg.net on 2016/9/28.
 */
public interface IMessageService {
	void readAndHandleAsync(String channel, String key); 	//read and handle async
	String readSync(String channel, String key);			//read with sync
	Future<String> readAsync(String channle, String key);	//read with async
	void write(String channel, String key, String value); 	//send
}
