package services.impl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResponse {
  private final int code;
  private final String body;
}
