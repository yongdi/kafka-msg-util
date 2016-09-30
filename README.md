## message service

#### use

```xml
<dependency>
  <groupId>com.voyg.msg</groupId>
  <artifactId>msg-util</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

example：Example
test：MyTest

please re-write your own BusinessHandler

#### kafka

- write(topic, key, message);//send

- read(topic, key); //receive all messages if key is blank

#### kafka.properties

kafka.broker.list=ip:9092 
kafka.zookeeper=ip:2181 
kafka.topic.custom=test 
kafka.topic.common=topicCommon 
kafka.group.common=groupCommon 
