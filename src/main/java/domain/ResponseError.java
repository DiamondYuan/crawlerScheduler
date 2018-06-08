package domain;


import lombok.Data;

@Data
public class ResponseError {

  private String message;

  public ResponseError(String message) {
    this.message = message;
  }

  public ResponseError(Exception e) {
    this.message = e.getMessage();
  }
}
