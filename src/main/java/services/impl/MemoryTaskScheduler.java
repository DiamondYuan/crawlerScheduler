package services.impl;

import domain.CrawlerTask;
import domain.Status;
import services.TaskScheduler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author diamondyuan
 */
public class MemoryTaskScheduler implements TaskScheduler {


  private Map<String, Set<CrawlerTask>> tasksFilter;
  private Map<String, AtomicInteger> taskCountMap;
  private Map<String, AtomicInteger> completeTaskCountMap;
  private Map<String, PriorityBlockingQueue<CrawlerTask>> task;
  private Map<String, Set<CrawlerTask>> doingTaskMap;

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock r = lock.readLock();
  private final Lock w = lock.writeLock();


  public MemoryTaskScheduler() {
    this.tasksFilter = new ConcurrentHashMap<>();
    taskCountMap = new ConcurrentHashMap<>();
    completeTaskCountMap = new ConcurrentHashMap<>();
    doingTaskMap = new ConcurrentHashMap<>();
    task = new ConcurrentHashMap<>();
  }

  @Override
  public void pushTask(String project, CrawlerTask crawlerTask) {
    r.lock();
    r.unlock();
    if (tasksFilter.containsKey(project) || (tasksFilter.putIfAbsent(project, crawlerTaskSet(crawlerTask)) != null)) {
      if (!tasksFilter.get(project).add(crawlerTask)) {
        return;
      }
    }

    if (taskCountMap.containsKey(project) || (taskCountMap.putIfAbsent(project, new AtomicInteger(1)) != null)) {
      taskCountMap.get(project).incrementAndGet();
    }

    //如果不存在 则初始化 存入
    if (task.containsKey(project) || task.putIfAbsent(project, createPriorityBlockingQueue(crawlerTask)) != null) {
      task.get(project).add(crawlerTask);
    }
  }

  private PriorityBlockingQueue<CrawlerTask> createPriorityBlockingQueue(CrawlerTask crawlerTask) {
    return new PriorityBlockingQueue<CrawlerTask>(100,
      (o1, o2) -> o2.getWeight() - o1.getWeight()) {{
      add(crawlerTask);
    }};
  }


  private Set<CrawlerTask> crawlerTaskSet(CrawlerTask crawlerTask) {
    Set<CrawlerTask> crawlerTasks = Collections.newSetFromMap(new ConcurrentHashMap<CrawlerTask, Boolean>());
    crawlerTasks.add(crawlerTask);
    return crawlerTasks;
  }


  @Override
  public CrawlerTask pollTask(String project) {
    r.lock();
    r.unlock();
    PriorityBlockingQueue<CrawlerTask> priorityBlockingQueue = task.get(project);
    CrawlerTask crawlerTask;
    if (priorityBlockingQueue == null || (crawlerTask = priorityBlockingQueue.poll()) == null) {
      return null;
    }
    if (doingTaskMap.containsKey(project) || (doingTaskMap.putIfAbsent(project, crawlerTaskSet(crawlerTask)) != null)) {
      doingTaskMap.get(project).add(crawlerTask);
    }
    return crawlerTask;
  }

  @Override
  public void completeTask(String project, CrawlerTask crawlerTask) {
    r.lock();
    r.unlock();
    if (!doingTaskMap.containsKey(project) || !doingTaskMap.get(project).remove(crawlerTask)) {
      return;
    }
    if (completeTaskCountMap.containsKey(project) || (completeTaskCountMap.putIfAbsent(project, new AtomicInteger(1)) != null)) {
      completeTaskCountMap.get(project).incrementAndGet();
    }
  }

  @Override
  public void clearProject(String project) {
    w.lock();
    doingTaskMap.remove(project);
    taskCountMap.remove(project);
    tasksFilter.remove(project);
    completeTaskCountMap.remove(project);
    task.remove(project);
    w.unlock();
  }

  @Override
  public Status status(String project) {
    int doing = 0;
    int all = 0;
    int complete = 0;
    if (taskCountMap.containsKey(project)) {
      all = taskCountMap.get(project).get();
    }
    if (doingTaskMap.containsKey(project)) {
      doing = doingTaskMap.get(project).size();
    }
    if (completeTaskCountMap.containsKey(project)) {
      complete = completeTaskCountMap.get(project).get();
    }
    Status status = new Status();
    status.setAll(all);
    status.setDoing(doing);
    status.setComplete(complete);
    return status;
  }


}
