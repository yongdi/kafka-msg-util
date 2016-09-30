import com.voyg.msg.KafkaMsgService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by voyg.net on 2016/9/28.
 */
public class MyTest extends BaseTest {
	@Autowired
	private KafkaMsgService kms;
	@Autowired
	private Environment env;

	@org.junit.Test
	public void test(){

		String topic = env.getProperty("kafka.topic.custom");
		String key = "beijing";
		System.out.println("hello kafka");

		kms.write(topic, key, "hello from beijing");

		//kms.readAndHandleAsync(env.getProperty("kafka.topic.custom"), null);
	}
}
