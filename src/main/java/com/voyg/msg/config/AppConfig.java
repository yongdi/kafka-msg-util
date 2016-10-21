package com.voyg.msg.config;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by voyg.net on 2016/9/28.
 */
@Configuration
@ComponentScan("com.voyg.msg")
@PropertySource("classpath:config.properties")
@PropertySource("file:${data.config.dir.path}/kafka.properties")
public class AppConfig {

	@Autowired
	private Environment env;

	@Bean(name = "properties")
	public Properties factoryBean() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setFileEncoding("UTF-8");
		propertiesFactoryBean.setLocations(new ClassPathResource("/config.properties"),
				new FileSystemResource("file:${data.config.dir.path}/kafka.properties"));
		return propertiesFactoryBean.getObject();
	}

	/*@Bean
	@Resource(name = "properties")
	public AppContext appContext (PropertiesFactoryBean properties){
		AppContext ctx = null;
		try {
			ctx = new AppContext(properties.getObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ctx;
	}*/

	/*@Bean(name = "kafkaProducerConfig")
	public ProducerConfig producerConfig(){
		Properties properties = new Properties();
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("metadata.broker.list", brokerListStr);
		return new ProducerConfig(properties);
	}*/

	@Bean(name = "kafkaProducer", destroyMethod = "close")
//	@Resource(name = "kafkaProducerConfig")
	public Producer<String, String> producer(/*ProducerConfig producerConfig*/){
		Properties properties = new Properties();
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("metadata.broker.list", env.getProperty("kafka.broker.list"));
		ProducerConfig config = new ProducerConfig(properties);
		return new Producer<>(config);
	}

	@Bean(name = "kafkaConsumerConfig")
	public ConsumerConfig consumerConfig(){
		Properties properties = new Properties();
		properties.put("zookeeper.connect", env.getProperty("kafka.zookeeper"));
                /*pls note that if you use the same group.id with others, everybody will shares the same message queue. that means, one message can be only received by one consumer. use different group.id if you don't want to be affected by other consumer*/
		properties.put("group.id", env.getProperty("kafka.group.common"));
		properties.put("zookeeper.session.timeout.ms", "10000");
		properties.put("zookeeper.sync.time.ms", "200");
		properties.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(properties);
	}

	@Bean(name = "kafkaConsumerConnector", destroyMethod = "shutdown")
	@Resource(type = ConsumerConfig.class)
	public ConsumerConnector  consumerConnector(ConsumerConfig consumerConfig) {
		return Consumer.createJavaConsumerConnector(consumerConfig);
	}
}
