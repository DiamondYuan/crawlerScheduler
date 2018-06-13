package services.impl;

import controller.TaskController;
import domain.CrawlerTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.impl.domain.TestResponse;
import services.impl.utils.TestRequestUtil;

import static org.junit.Assert.assertEquals;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class TestController {

  private TestRequestUtil testRequestUtil;

  @Before
  public void setUp() {
    new TaskController(new MemoryTaskScheduler());
    testRequestUtil = new TestRequestUtil();
    awaitInitialization();

  }

  @After
  public void tearDown() {
    stop();
  }


  @Test
  public void testModelObjectsPOST() throws Exception {


    CrawlerTask crawlerTask = new CrawlerTask() {{
      setUrl("http://baidu.com");
      setWeight(1);
    }};

    {
      TestResponse testResponse = testRequestUtil.post("/tasks", crawlerTask);
      assertEquals(400, testResponse.getCode());
    }

    {
      TestResponse testResponse = testRequestUtil.post("/tasks?project=test", new CrawlerTask() {{
        setWeight(1);
      }});
      assertEquals(400, testResponse.getCode());
    }

    {
      TestResponse testResponse = testRequestUtil.post("/tasks?project=test", new CrawlerTask() {{
        setUrl("http://baidu.com");
      }});
      assertEquals(400, testResponse.getCode());
    }


    TestResponse testResponse = testRequestUtil.post("/tasks?project=test", crawlerTask);
    assertEquals(200, testResponse.getCode());


  }

}
