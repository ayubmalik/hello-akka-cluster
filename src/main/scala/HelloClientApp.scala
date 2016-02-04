import akka.actor.Actor
import akka.actor.ActorIdentity
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorSystem
import akka.actor.Inbox
import akka.actor.Identify
import akka.actor.Props
import akka.actor.ReceiveTimeout
import akka.actor.Terminated
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.io.StdIn

class HelloClientActor(val remote: ActorSelection) extends Actor with ActorLogging {
  def receive = waiting

  def inquire() = remote ! Identify(self)

  override def preStart() {
    inquire()
    context.setReceiveTimeout(10.seconds)
  }

  def waiting: Receive = {
    case ActorIdentity(`self`, Some(ref)) => {
      println("becoming active")
      context.watch(ref)
      context.setReceiveTimeout(Duration.Undefined)
      context.become(active(ref))
    }
    case ActorIdentity(`self`, None) => // ignore
    case ReceiveTimeout => inquire()
  }

  def active(ref: ActorRef): Receive = {
    case Terminated(`ref`) => context.become(waiting)
    case ActorIdentity(`self`, _) => // ignore
    case Hello(message) => println("Hello response: " + message)
    case msg: String => ref ! msg
  }

}

object HelloClientApp extends App {
  val ip = if (!args.isEmpty) args(0) else "127.0.0.1"
  val remoteUrl = s"akka.tcp://HelloRemoteApp@${ip}:8000/user/helloRemoteActor"
  println("using url:" + remoteUrl)
  val root = ConfigFactory.load()
  val config = root.getConfig("helloClient")
  val system = ActorSystem("helloClient", config)
  val remote = system.actorSelection(remoteUrl)
  val client = system.actorOf(Props(classOf[HelloClientActor], remote))
  val inbox = Inbox.create(system)
  println("Start typing messages: q to quit")
  Iterator.continually(StdIn.readLine()).takeWhile(_ != "q").foreach(msg => inbox.send(client, msg))
  println("Exiting")
  system.terminate
}
