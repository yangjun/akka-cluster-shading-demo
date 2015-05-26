package sharding;

import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.contrib.pattern.ShardCoordinator;
import akka.contrib.pattern.ShardRegion;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.io.Serializable;

/**
 * Created by yangjungis@126.com on 2015/5/24.
 */
public class Counter extends UntypedPersistentActor {
  public static enum CounterOp {
    INCREMENT, DECREMENT
  }

  public static class Get implements Serializable {
    final public long counterId;

    public Get(long counterId) {
      this.counterId = counterId;
    }
  }

  public static class EntryEnvelope implements Serializable {
    final public long id;
    final public Object payload;

    public EntryEnvelope(long id, Object payload) {
      this.id = id;
      this.payload = payload;
    }
  }

  public static class CounterChanged implements Serializable {
    final public int delta;

    public CounterChanged(int delta) {
      this.delta = delta;
    }
  }

  public Counter() {
    System.out.println("create Counter ......");
  }

  public static final Props props = Props.create(Counter.class);

  public static final String name = "Counter";

  int count = 0;

  @Override
  public String persistenceId() {
    return getSelf().path().parent().name() + "-" + getSelf().path().name();
  }

  @Override
  public void preStart() throws Exception {
    super.preStart();
    System.out.println("preStart......");
    context().setReceiveTimeout(Duration.create(120, TimeUnit.SECONDS));
  }

  void updateState(CounterChanged event) {
    count += event.delta;
  }

  @Override
  public void onReceiveRecover(Object msg) {
    if (msg instanceof CounterChanged)
      updateState((CounterChanged) msg);
    else
      unhandled(msg);
  }

  @Override
  public void onReceiveCommand(Object msg) {
    System.out.println("count = " + count);
    if (msg instanceof Get) {
      getSender().tell(count, getSelf());
    } else if (msg == CounterOp.INCREMENT) {
      persist(new CounterChanged(+1), new Procedure<CounterChanged>() {
        public void apply(CounterChanged evt) {
          updateState(evt);
        }
      });
    } else if (msg == CounterOp.DECREMENT) {
      persist(new CounterChanged(-1), new Procedure<CounterChanged>() {
        public void apply(CounterChanged evt) {
          updateState(evt);
        }
      });
    } else if (msg.equals(ReceiveTimeout.getInstance())) {
      getContext().parent().tell(
              new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    } else {
      unhandled(msg);
    }
  }

  public static final ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {
    @Override
    public String entryId(Object message) {
      System.out.println("messageExtractor.entryId = " + message);
      if (message instanceof Counter.EntryEnvelope)
        return String.valueOf(((Counter.EntryEnvelope) message).id);
      else if (message instanceof Counter.Get)
        return String.valueOf(((Counter.Get) message).counterId);
      else
        return null;
    }

    @Override
    public Object entryMessage(Object message) {
      System.out.println("messageExtractor.entryMessage = " + message);
      if (message instanceof Counter.EntryEnvelope)
        return ((Counter.EntryEnvelope) message).payload;
      else
        return message;
    }

    @Override
    public String shardId(Object message) {
      System.out.println("messageExtractor.shardId = " + message);
      if (message instanceof Counter.EntryEnvelope) {
        long id = ((Counter.EntryEnvelope) message).id;
        return String.valueOf(id % 10);
      } else if (message instanceof Counter.Get) {
        long id = ((Counter.Get) message).counterId;
        return String.valueOf(id % 10);
      } else {
        return null;
      }
    }
  };
}
