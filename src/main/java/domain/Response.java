package domain;

import lombok.Data;

@Data
public class Response<T> {
  private T data;

  public Response(T data) {
    this.data = data;
  }
}
