package lab.tca

import org.scalatest.{FlatSpec, Matchers}

class StorageSpec extends FlatSpec with Matchers {

  "Storage" should "handle inserts and queries" in {
    Storage.purge()
    Storage.insert("key1", 1486862180000L, 1)
    Storage.insert("key2", 1486862180000L, 2)
    Storage.insert("key1", 1486862280000L, 10)
    Storage.insert("key2", 1486862280000L, 20)
    Storage.query("key1", 1486862180000L, 1486862480000L) shouldBe 11
    Storage.query("key2", 1486862180000L, 1486862480000L) shouldBe 22
  }

  it should "handle order of time" in {
    Storage.purge()
    Storage.insert("key1", 1486862180000L, 1)
    Storage.insert("key1", 1486862280000L, 10)
    Storage.insert("key1", 1486862380000L, 100)
    Storage.insert("key1", 1486862480000L, 1000)
    Storage.insert("key2", 1486862480000L, 10000)
    Storage.query("key1", 1486862180000L, 1486862280000L) shouldBe 11
    Storage.query("key1", 1486862180000L, 1486862380000L) shouldBe 111
    Storage.query("key1", 1486862280000L, 1486862480000L) shouldBe 1110
    Storage.query("key2", 1486862180000L, 1486862480000L) shouldBe 10000
  }

  it should "handle empty storage" in {
    Storage.purge()
    Storage.query("key1", 1486862180000L, 1486862280000L) shouldBe 0
  }

  it should "act like a cyclic buffer within each keyword" in {
    Storage.purge()

    val depth = Storage.MAX_RECORDS_PER_KEYWORD

    (1 to depth).foreach(i => Storage.insert("key1", 1486862180000L+i, i))
    Storage.insert("key1", 1486862180000L+depth+1, depth+1)
    Storage.query("key1", 0, 1486862180000L+depth+1) shouldBe (2 to depth+1).sum

    Storage.insert("key1", 1486862180000L+depth+2, depth+2)
    Storage.query("key1", 0, 1486862180000L+depth+2) shouldBe (3 to depth+2).sum
  }

}
