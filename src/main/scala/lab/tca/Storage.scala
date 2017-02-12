package lab.tca

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.immutable.TreeMap

object Storage {

  //[Keyword -> [Time -> Count]]
  @volatile private var aggregates = Map[String, TreeMap[Long, Long]]()

  def query(keyword: String, start: Long, end: Long): Long = {
    println(s"QUERYING keyword: $keyword | start=${getReadableTime(start)} - end=${getReadableTime(end)}")
    val tMap = aggregates.getOrElse(keyword, new TreeMap[Long, Long]())
    tMap.from(start).to(end).values.sum
  }

  def insert(keyword: String, time: Long, count: Long): Unit = {
    println(s"INSERTING @ ${getReadableTime(time)}: keyword: $keyword | count=$count")
    val tMap = aggregates.getOrElse(keyword, new TreeMap[Long, Long]())
    aggregates = aggregates + (keyword -> (tMap + (time -> count)))
  }

  def purge(): Unit = aggregates = Map[String, TreeMap[Long, Long]]()

  def getReadableTime(time: Long): String = {
    val date = new Date(time)
    val dateFormat = new SimpleDateFormat("dd/MM h:mm a")
    dateFormat.format(date)
  }

}
