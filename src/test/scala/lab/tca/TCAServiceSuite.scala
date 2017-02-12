package lab.tca

import com.twitter.finagle.Service
import com.twitter.finagle.http.{FileElement, Request, RequestBuilder, Response}
import com.twitter.io.{Buf, Reader}
import com.twitter.util.{Duration, Await}
import io.finch.test.ServiceSuite
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec

trait TCAServiceSuite {
  this: FlatSpec with ServiceSuite with Matchers =>

  def createService(): Service[Request, Response] = {
    Endpoint.makeService()
  }

  "Twitter Count Application" should "return keywords" in { f =>
    val request = Request("/keywords")
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "respond to queries" in { f =>
    val request = Request("/count?keyword=women&start=2017-02-11T20:32&end=2017-02-11T20:35")
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "handle invalid keywords" in { f =>
    val request = Request("/count?keyword=deepmind&start=2017-01-01T00:00&end=2017-01-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 404
  }

  it should "handle invalid time formats" in { f =>
    val request = Request("/count?keyword=deepmind&start=2017-01-01T00:00:99&end=2017-Jan-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 404
  }

  it should "handle invalid queries" in { f =>
    val request = Request("/count?keyword=tech&foo=2017-01-01T00:00&bar=2017-01-01T00:30")
    val result: Response = f(request)
    result.statusCode shouldBe 400
  }

}

class TCAServiceSpec extends FlatSpec with ServiceSuite with TCAServiceSuite with Matchers
