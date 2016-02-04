import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.InitialStateAsEvents
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.UnreachableMember

case class Hello(msg: String)
case class UpdateMessage(msg: String)

class HelloRemoteActor extends Actor with ActorLogging {

  var message = "Hello from Akka"

  def receive = {
    case UpdateMessage(msg) => message = msg
    case msg: String => {
      log.info("*** msg received: " + msg + "***")
      sender ! Hello(message + ": " + msg)
    }
  }
}
