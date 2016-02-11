import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration.DurationInt

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object HelloClusterApp extends App {

  // Create the actor system
  val root = ConfigFactory.load()

  val feConfig = ConfigFactory
    .parseString("akka.remote.netty.tcp.port=2551")
    .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
    .withFallback(root.getConfig("helloCluster"))
    .withFallback(root)

  val fe = ActorSystem("HelloRemoteSystem", feConfig)
  val helloFrontend = fe.actorOf(Props[HelloFrontEnd], "frontend")

  val ports = Seq(2552, 0, 0, 0)
  ports foreach { port =>
    val config = ConfigFactory
      .parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(root.getConfig("helloCluster"))
    val system = ActorSystem("HelloRemoteSystem", config)
    system.actorOf(Props[PingPongActor], "backend")
  }

  import scala.concurrent.ExecutionContext.Implicits.global
  val mb = akka.actor.Inbox.create(fe)
  fe.scheduler.schedule(3.seconds, 15.seconds) {
    mb.send(helloFrontend, "ping")
  }

}
