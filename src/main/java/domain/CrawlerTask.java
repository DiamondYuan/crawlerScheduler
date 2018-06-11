package domain;

import lombok.Data;

import java.util.List;

@Data
public class CrawlerTask {
  private String url;
  private List<Info> info;
  private Integer weight;
}
