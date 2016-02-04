import scala.concurrent.ExecutionContext.Implicits

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object HelloRemoteApp extends App {

  val hostname = if (!args.isEmpty) args(0) else "127.0.0.1"

  val root = ConfigFactory.load()

  val config = ConfigFactory
    .parseString(s"akka.remote.netty.tcp.hostname='$hostname'")
    .withFallback(root.getConfig("helloRemote"))

  val system = ActorSystem("HelloRemoteSystem", config)
  val hello = system.actorOf(Props[HelloRemoteActor], "helloRemoteActor")

}
