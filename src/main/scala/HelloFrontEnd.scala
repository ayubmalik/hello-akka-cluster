import akka.actor.Actor
import akka.actor.ActorLogging
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberEvent
import akka.actor.Terminated
import akka.actor.ActorRef

class HelloFrontEnd extends Actor with ActorLogging {

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0
  def receive = {
    case "BackendRegistration" =>
      context watch sender()
      backends = backends :+ sender()
      log.info("BACKENDS: {}", backends)
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
    case "ping" if backends.isEmpty => 
      log.info("got message from: " + sender + " but EMPTY")
    case "ping" =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward "ping"
  }
}
