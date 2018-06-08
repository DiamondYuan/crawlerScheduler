package domain;

import lombok.Data;

import java.util.Map;

@Data
public class CrawlerTask {
  private String url;
  private Map<String, String> info;
  private Integer weight;
}
