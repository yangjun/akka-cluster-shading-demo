package sharding;

import akka.actor.*;
import akka.contrib.pattern.ClusterSharding;
import akka.pattern.Patterns;
import akka.persistence.journal.leveldb.SharedLeveldbStore;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.Function1;
import scala.None;
import scala.PartialFunction;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Created by yangjungis@126.com on 2015/5/24.
 */
public class AppBachend {

  /**
   * 主方法
   * @param args
   */
  public static void main(String[] args) {
    String port = "0";
    if (args.length > 0) {
      port = args[0];
    }

    // 设置端口
    Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
            .withFallback(ConfigFactory.load());

    // 创建 ActorSystem
    ActorSystem system = ActorSystem.create("ClusterSystem", config);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // 开始 CounterRegion
    ActorRef startedCounterRegion = ClusterSharding.get(system).start(Counter.name, Counter.props,
            Counter.messageExtractor);

  }


}
