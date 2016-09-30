package com.voyg.msg;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
* <strong>kafka message service</strong><br>
* @author voyg.net
* @since 2016/9/28
*/
@Service
public class KafkaMsgService implements IMessageService {
	private static Logger log = LoggerFactory.getLogger(KafkaMsgService.class);
	@Qualifier("kafkaProducer")
	@Autowired
	Producer<String, String> producer;

	@Qualifier("kafkaConsumerConnector")
	@Autowired
	ConsumerConnector consumer;

	//always on
	private static final ExecutorService reader = Executors.newSingleThreadExecutor();
	private static final ExecutorService woker = Executors.newSingleThreadExecutor();

	public void write(String topic, String key, String value) {
		try {
			KeyedMessage<String, String> data = new KeyedMessage<>(topic, key, value);
			producer.send(data);
			log.info("send kafka msg success: topic={}, msg={}", key, value);
		} catch (Exception e) {
			log.error("=====================kafka msg error=====================\n" +
					"send kafka msg failure: topic={}, msg={}", key, value);
			e.printStackTrace();
		}
	}

	public void readAndHandleAsync(String topic, String key) {
		//specify thread count for topic
		Map<String, Integer> topicCountMap = new HashMap<>();
		topicCountMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
				consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> msgStreamList = consumerMap.get(topic);
		for (final KafkaStream<byte[], byte[]> kafkaStream : msgStreamList) {
			reader.execute(() -> {
				ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
				while (it.hasNext()) {
					MessageAndMetadata<byte[], byte[]> messageAndMetadata = it.next();
					try {
						String msgStr = null;
						//notice ! DONOT fetch key directly
						String msgKey = messageAndMetadata.key() == null ? null : new String(messageAndMetadata.key(), "UTF-8");
						msgStr = new String(messageAndMetadata.message(), "UTF-8");
						if (Objects.equals(key, msgKey) || StringUtils.isBlank(key)) {
							//your business
							log.info("kafka message received: {}-{}-{}-{}: {}", topic, messageAndMetadata.partition(), messageAndMetadata.offset(),
									msgKey, msgStr);
							Future<String> future = woker.submit(new BussinessHandler(topic, key, msgStr));
							try {
								log.info("kafka message handle completed within 1 second: "+future.get(1, TimeUnit.SECONDS));
							} catch (TimeoutException ignored){}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public String readSync(String topic, String key) {
		Map<String, Integer> topicCountMap = new HashMap<>();
		topicCountMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
				consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> msgStreamList = consumerMap.get(topic);
		for (final KafkaStream<byte[], byte[]> kafkaStream : msgStreamList) {
			for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : kafkaStream) {
				try {
					String msgStr = null;
					//notice! DONOT fetch key() directly
					String msgKey = messageAndMetadata.key() == null ? null : new String(messageAndMetadata.key(), "UTF-8");
					msgStr = new String(messageAndMetadata.message(), "UTF-8");
					if (Objects.equals(key, msgKey) || StringUtils.isBlank(key)) {
						log.info("kafka message received: {}-{}-{}-{}: {}", topic, messageAndMetadata.partition(), messageAndMetadata.offset(),
								msgKey, msgStr);
						return msgStr;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Future<String> readAsync(String topic, String key) {
		return reader.submit(() -> readSync(topic, key));
	}
}
