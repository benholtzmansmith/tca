package lab.tca

import io.finch.test.ServiceIntegrationSuite
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec

class TCAIntegrationTest extends FlatSpec with Matchers
  with ServiceIntegrationSuite with TCAServiceSuite
