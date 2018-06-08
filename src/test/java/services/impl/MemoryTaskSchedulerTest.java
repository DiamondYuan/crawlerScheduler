package services.impl;

import domain.CrawlerTask;
import domain.Status;
import org.junit.Test;
import services.TaskScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemoryTaskSchedulerTest {


  public static final String PROJECT_NAME = "test";

  @Test
  public void test() {
    TaskScheduler taskScheduler = new MemoryTaskScheduler();

    // 初始化状态全部任务为 0
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(0);
      setComplete(0);
      setAll(0);
    }});


    // 提交任务 预期任务数 1
    CrawlerTask task1 = new CrawlerTask() {{
      setUrl("http://www.google.com");
      setWeight(1);
    }};
    taskScheduler.pushTask(PROJECT_NAME, task1);
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(0);
      setComplete(0);
      setAll(1);
    }});


    //拉取任务 预期和提交的相同 doing 为 1
    assertEquals(task1, taskScheduler.pollTask(PROJECT_NAME));
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(1);
      setComplete(0);
      setAll(1);
    }});


    //再次拉取 预期任务为 1
    assertNull(taskScheduler.pollTask(PROJECT_NAME));
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(1);
      setComplete(0);
      setAll(1);
    }});

    //再次提交任务 预期状态不变
    taskScheduler.pushTask(PROJECT_NAME, task1);
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(1);
      setComplete(0);
      setAll(1);
    }});


    //再次拉取 预期状态不变
    assertNull(taskScheduler.pollTask(PROJECT_NAME));
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(1);
      setComplete(0);
      setAll(1);
    }});

    //完成任务 状态变为变化
    taskScheduler.completeTask(PROJECT_NAME, task1);
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(0);
      setComplete(1);
      setAll(1);
    }});

    taskScheduler.clearProject(PROJECT_NAME);
    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(0);
      setComplete(0);
      setAll(0);
    }});
    taskScheduler.pushTask(PROJECT_NAME, task1);
    assertEquals(task1, taskScheduler.pollTask(PROJECT_NAME));
  }


  @Test
  public void testWeight() {
    // 提交任务 预期任务数 1
    CrawlerTask task1 = new CrawlerTask() {{
      setUrl("http://www.google.com");
      setWeight(1);
    }};

    CrawlerTask task2 = new CrawlerTask() {{
      setUrl("http://www.baidu.com");
      setWeight(2);
    }};

    CrawlerTask task3 = new CrawlerTask() {{
      setUrl("http://www.qq.com");
      setWeight(1);
    }};

    TaskScheduler taskScheduler = new MemoryTaskScheduler();
    taskScheduler.pushTask(PROJECT_NAME, task1);
    taskScheduler.pushTask(PROJECT_NAME, task2);
    taskScheduler.pushTask(PROJECT_NAME, task3);

    assertEquals(taskScheduler.status(PROJECT_NAME), new Status() {{
      setDoing(0);
      setComplete(0);
      setAll(3);
    }});

    assertEquals(task2, taskScheduler.pollTask(PROJECT_NAME));
  }

}
