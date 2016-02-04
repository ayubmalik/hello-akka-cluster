import org.scalatest.{ FlatSpecLike, Matchers }
import akka.actor.{ Actor, Props, ActorSystem }
import scala.concurrent.duration._
import org.scalatest.FlatSpec
import scala.concurrent.Promise
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class FuturesSpike extends FlatSpec {

  "A Promise" should "do stuff" in {
    val p = Promise[String]
    val f = p.future
    Future {
      val ip =  scala.io.Source.fromURL("http://www.telize.com/ip").mkString
      println(ip)
      p.success(ip)
    }

    println ("waiting...")
    f map { response =>
      println(s"response: $response")
    }
  }

}
