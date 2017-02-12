package lab.tca

import java.util.concurrent.Executors

import com.twitter.finagle.Http
import com.twitter.util.Future
import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class Server {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))
  val config = ConfigFactory.load()
  val timeScale = config.getLong("tca.twitter.timeScale")
  val keywords = config.getStringList("tca.twitter.keywords").asScala.toList

  Future {
    val service = Endpoint.makeService()
    Http.serve(":8080", service)
  }

  List("consumerKey", "consumerSecret", "accessToken", "accessTokenSecret").foreach {
    twitterParam => System.setProperty(s"twitter4j.oauth.$twitterParam", config.getString(s"tca.twitter.$twitterParam"))
  }
  val streamingContext = new StreamingContext("local[*]", "TCA", Seconds(timeScale))
  val rootLogger = Logger.getRootLogger
  rootLogger.setLevel(Level.ERROR)
  val tweets = TwitterUtils.createStream(streamingContext, None)
}

object Server extends Server with App {
  val statuses = tweets.map { status =>
    keywords.filter(k => status.getText.toUpperCase.contains(k.toUpperCase))
  }.filter(_.nonEmpty).flatMap(identity).countByValue()

  statuses.foreachRDD { (vals, time) =>
    println(time)
    vals.collect().foreach { case (k, v) =>
      Storage.insert(k, time.milliseconds, v)
    }
  }

  streamingContext.start()
  streamingContext.awaitTermination()
}
