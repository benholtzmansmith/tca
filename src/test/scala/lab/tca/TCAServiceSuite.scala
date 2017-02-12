package lab.tca

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.typesafe.config.ConfigFactory
import io.finch.test.ServiceSuite
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec
import scala.collection.JavaConverters._

trait TCAServiceSuite {
  this: FlatSpec with ServiceSuite with Matchers =>

  def createService(): Service[Request, Response] = {
    Endpoint.makeService()
  }

  val config = ConfigFactory.load()
  val keywords = config.getStringList("tca.twitter.keywords").asScala.toList

  "Twitter Count Application" should "return keywords" in { f =>
    val request = Request("/keywords")
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "respond to queries" in { f =>
    val request = Request(s"/count?keyword=${keywords.head}&start=2017-02-11T20:32&end=2017-02-11T20:35")
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "handle invalid keywords" in { f =>
    val request = Request("/count?keyword=porsche&start=2017-01-01T00:00&end=2017-01-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 404
  }

  it should "handle invalid time formats" in { f =>
    val request = Request("/count?keyword=roybot&start=2017-01-01T00:00:99&end=2017-Jan-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 404
  }

  it should "handle invalid queries" in { f =>
    val request = Request("/count?keyword=roybot&foo=2017-01-01T00:00&bar=2017-01-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 400
  }

}

class TCAServiceSpec extends FlatSpec with ServiceSuite with TCAServiceSuite with Matchers
