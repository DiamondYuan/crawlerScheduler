package services;

import domain.CrawlerTask;
import domain.Status;

public interface TaskScheduler {

  /**
   * 任务名称
   *
   * @param project  项目名称
   *
   * @param crawlerTask 待提交的任务
   */
  void pushTask(String project, CrawlerTask crawlerTask);

  /**
   * 拉取任务
   *
   * @param project 项目名称
   *
   * @return 新的任务
   */
  CrawlerTask pollTask(String project);


  /**
   * 完成任务
   *
   * @param project  项目名称
   * @param crawlerTask 任务
   *
   */
  void completeTask(String project, CrawlerTask crawlerTask);


  /**
   * 清理项目的全部任务状态
   *
   * @param project 项目名称
   *
   * 清理项目的全部任务
   */
  void clearProject(String project);

  /**
   * 返回项目的状态
   *
   * @param project 项目名称
   * @return 状态
   */
  Status status(String project);


}
