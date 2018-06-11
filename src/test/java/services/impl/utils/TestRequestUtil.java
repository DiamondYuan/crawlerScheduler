package services.impl.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import services.impl.domain.TestResponse;

import java.io.IOException;

public class TestRequestUtil {

  private OkHttpClient okHttpClient;
  private ObjectMapper objectMapper;

  private static final String Host = "http://127.0.0.1:4567/";
  private static final MediaType JSON
    = MediaType.parse("application/json; charset=utf-8");

  public TestRequestUtil() {
    okHttpClient = new OkHttpClient();
    objectMapper = new ObjectMapper();
  }

  public TestResponse get(String path) throws IOException {
    Request request = new Request.Builder()
      .url(String.format("%s%s", Host, path))
      .build();
    Response response = okHttpClient.newCall(request).execute();
    return new TestResponse(response.code(), response.body().string());
  }


  public TestResponse post(String path, Object object) throws IOException {
    RequestBody body = RequestBody.create(JSON, objectMapper.writeValueAsString(object));
    Request request = new Request.Builder()
      .url(String.format("%s%s", Host, path))
      .post(body)
      .build();

    Response response = okHttpClient.newCall(request).execute();
    return new TestResponse(response.code(), response.body().string());
  }
}
