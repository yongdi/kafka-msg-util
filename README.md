## 读写消息服务，目前支持kafka

#### 使用

```xml
<dependency>
  <groupId>com.jkys.msg</groupId>
  <artifactId>msg-util</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

实例：Example
测试：MyTest

但不建议直接用jar包，里面的业务处理类必须实际去实现
请直接copy代码到自己的项目中去，重写业务实现类


#### kafka

- write(topic, key, message);//发消息

- read(topic, key); //如果key为null或空白，则收取全部消息