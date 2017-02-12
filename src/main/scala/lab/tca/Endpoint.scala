package lab.tca

import java.text.SimpleDateFormat

import _root_.argonaut._
import argonaut.Argonaut._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.finch._
import io.finch.argonaut._
import io.finch.items._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.util.Try

object Endpoint {
  val config = ConfigFactory.load()
  val keywords = config.getStringList("tca.twitter.keywords").asScala.toList
  val queryDateFormat = config.getString("tca.twitter.queryDateFormat")

  implicit val encodeException: EncodeJson[Exception] = EncodeJson {
    case Error.NotPresent(ParamItem(p)) => Json.obj(
      "error" -> jString("param_not_present"), "param" -> jString(p)
    )
    case Error.NotValid(ParamItem(p), rule) => Json.obj(
      "error" -> jString("param_not_valid"), "param" -> jString(p), "rule" -> jString(rule)
    )
    case Error.RequestErrors(errors) => Json.obj(
      "error" -> jString("request_errors"), "errors" -> jString(errors.map(_.getMessage).mkString(", "))
    )
    // Domain errors
    case error: TCAError => Json.obj(
      "error" -> jString(error.message)
    )
  }

  def makeService(): Service[Request, Response] = (getKeywords() :+: query())
    .handle({
      case e: TCAError => NotFound(e)
    }).toService

  def getKeywords(): Endpoint[List[String]] =
    get("keywords") { () => Ok(keywords) }

  def query(): Endpoint[Long] =
    get("count" :: param("keyword") :: param("start") :: param("end")) {
      (keyword: String, start: String, end: String) =>
        if (!keywords.contains(keyword)) throw InvalidInput(s"Invalid keyword: $keyword")

        val startMillis = parseQueryTime(start)
        val endMillis = parseQueryTime(end)

        Ok(Storage.query(keyword, startMillis, endMillis))
    }

  def parseQueryTime(time: String): Long = {
    val format = new SimpleDateFormat(queryDateFormat)
    Try {
      format.parse(time).getTime
    }.getOrElse(throw InvalidInput(s"Invalid time format: $time"))
  }
}