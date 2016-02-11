import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.InitialStateAsEvents
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.protobuf.msg.ClusterMessages.Join
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.MemberStatus
import akka.actor.RootActorPath
import akka.cluster.Member

class PingPongActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case "ping" => {
   
      log.info (self + " ping from: " + sender)
      sender ! "pong"
    }
    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit = {
    if (member.hasRole("frontend")) {
      log.info("registering with FrontEnd: " + member)
      val sel = context.actorSelection(RootActorPath(member.address) / "user" / "frontend")
      log.info("sending to: " + sel)
      sel ! "BackendRegistration"
    }
  }

}
