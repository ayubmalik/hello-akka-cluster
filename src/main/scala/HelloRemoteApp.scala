import scala.concurrent.ExecutionContext.Implicits

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props

object HelloRemoteApp extends App {

  import scala.concurrent.ExecutionContext.Implicits.global
  val root = ConfigFactory.load()

  val config = ConfigFactory
    .parseString(s"akka.remote.netty.tcp.port=2522")
    .withFallback(root.getConfig("helloRemote"))

  val system = ActorSystem("HelloRemoteSystem", config)
 

}
