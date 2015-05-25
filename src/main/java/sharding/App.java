package sharding;

/**
 * Created by yangjungis@126.com on 2015/5/25.
 */
public class App {
  public static void main(String[] args) {
    String[] port = {"2551"};
    AppBachend.main(port);

    String[] empty = {};
    AppBachend.main(empty);

    AppFront.main(empty);
  }
}
