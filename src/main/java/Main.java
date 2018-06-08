import controller.TaskController;
import services.impl.MemoryTaskScheduler;


public class Main {

  public static void main(String[] args) {
    new TaskController(new MemoryTaskScheduler());
  }

}
