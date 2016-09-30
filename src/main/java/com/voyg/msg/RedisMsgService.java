package com.voyg.msg;

import java.util.concurrent.Future;

/**
 * Created by voyg.net on 2016/9/28.
 */
public class RedisMsgService implements IMessageService {
	@Override
	public void readAndHandleAsync(String channel, String key) {
	}

	@Override
	public String readSync(String channel, String key) {
		return null;
	}

	@Override
	public Future<String> readAsync(String channle, String key) {
		return null;
	}

	@Override
	public void write(String channel, String key, String value) {
	}
}
