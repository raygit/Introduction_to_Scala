package sample.persistence

//#eventsourced-example
import akka.actor._
import akka.persistence._

final case class Cmd(data: String)
final case class Evt(data: String)

final case class ExampleState(events: List[String] = Nil) {
  def update(evt: Evt) = copy(evt.data :: events)
  def size = events.length
  override def toString: String = events.reverse.toString
}

class ExampleProcessor extends EventsourcedProcessor with ActorLogging {
  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.update(event)

  def numEvents =
    state.size

  val receiveRecover: Receive = {
    case evt: Evt                                 => { log.info(s"Recovering $evt"); updateState(evt)}
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)
        //context.system.eventStream.publish(event)
      }
    case "snap"  => saveSnapshot(state)
    case "print" => println(state)
  }

}
//#eventsourced-example

object EventsourcedExample extends App {

  val system = ActorSystem("example")
  val processor = system.actorOf(Props[ExampleProcessor], "processor-4-scala")

  processor ! Cmd("foo")
  processor ! Cmd("baz")
  processor ! Cmd("bar")
  processor ! "snap"
  processor ! Cmd("buzz")
  processor ! "print"

  Thread.sleep(1000)
  system.shutdown()
}
