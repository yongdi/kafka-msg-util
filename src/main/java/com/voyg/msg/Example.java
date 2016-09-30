package com.jkys.msg;

import com.jkys.msg.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
* <strong>for example</strong><br>
 *     to test: run on server<pre>
 *     /opt/kafka/bin$ ./kafka-console-consumer.sh --zookeeper localhost:2181 --topic test 
 *     /opt/kafka/bin$ ./kafka-console-producer.sh --broker-list localhost:9092 --topic test 
 *     </pre>
 *     if key is specified，the get message of that key only. otherwise, receive all message<br>
 *     <br>
 *     <B>one line for each read</B>
* @author voyg.net
* @since 2016/9/28
*/
public class Example {

	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("hello kafka " + Thread.currentThread().getName());

		KafkaMsgService service = ctx.getBean(KafkaMsgService.class);
		Environment env = ctx.getEnvironment();
		String topic = env.getProperty("kafka.topic.custom");
		String key = "beijing";

		//send
		//service.write(topic, key, "haha");

		//async read and handle
		service.readAndHandleAsync(topic, key);

		//read only, sync
//		System.out.println(service.readSync(topic, key));
//		System.out.println("I cannot do other thing");

		//read only, async
//		Future<String> future = service.readAsync(topic, key);
//		System.out.println("I alse can do something else");
//		System.out.println(future.get());
	}
}
