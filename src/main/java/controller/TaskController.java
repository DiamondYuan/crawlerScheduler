package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.CrawlerTask;
import domain.Response;
import domain.ResponseError;
import services.TaskScheduler;

import java.io.IOException;

import static spark.Spark.*;


public class TaskController {

  private ObjectMapper objectMapper;

  public TaskController(final TaskScheduler taskScheduler) {
    this.objectMapper = new ObjectMapper();
    ThreadLocal<String> projectThreadLocal = new ThreadLocal<>();
    before((request, response) -> {
      String project = request.queryMap().get("project").value();
      if (project == null) {
        throw new IllegalArgumentException("error project can't be null");
      }
      projectThreadLocal.set(project);
    });
    get("/api/v1/tasks", (req, res) -> new Response<>(taskScheduler.pollTask(projectThreadLocal.get())), this::toJson);

    post("/api/v1/tasks/clear", (request, response) -> {
      taskScheduler.clearProject(projectThreadLocal.get());
      return new Response<>("success");
    }, this::toJson);

    post("/api/v1/tasks", (request, response) -> {
      CrawlerTask crawlerTask = objectMapper.readValue(request.body(), CrawlerTask.class);
      if (crawlerTask.getUrl() == null || crawlerTask.getWeight() == null) {
        throw new IllegalArgumentException("error url or weight can't be null");
      }
      taskScheduler.pushTask(projectThreadLocal.get(), crawlerTask);
      return new Response<>("success");
    }, this::toJson);

    post("/api/v1/tasks/complete", (request, response) -> {
      CrawlerTask crawlerTask = objectMapper.readValue(request.body(), CrawlerTask.class);
      if (crawlerTask.getWeight() == null || crawlerTask.getUrl() == null) {
        throw new IllegalArgumentException("error url or weight can't be null");
      }
      taskScheduler.completeTask(projectThreadLocal.get(), crawlerTask);
      return new Response<>("success");
    }, this::toJson);

    after((req, res) -> {
      projectThreadLocal.remove();
      res.type("application/json");
    });

    exception(IOException.class, (e, req, res) -> {
      res.status(500);
      res.body(toJson(new ResponseError(e)));
    });
    exception(IllegalArgumentException.class, (e, req, res) -> {
      res.status(400);
      res.body(toJson(new ResponseError(e)));
    });
  }

  private String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }
}
