package sharding;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by yangjungis@126.com on 2015/5/24.
 */
public class AppFront {

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

    // proxy
    ActorRef startedCounterRegion = ClusterSharding.get(system).start(Counter.name, null,
            Counter.messageExtractor);

    ActorRef counterRegion  = ClusterSharding.get(system).shardRegion(Counter.name);


    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    counterRegion.tell(new Counter.Get(100), counterRegion);
    counterRegion.tell(new Counter.EntryEnvelope(100, Counter.CounterOp.INCREMENT), counterRegion);
    counterRegion.tell(new Counter.Get(100), counterRegion);

  }
}


