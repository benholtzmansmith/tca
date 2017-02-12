package lab.tca

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.immutable.TreeMap

class Storage {

  //Map[ Keyword -> TreeMap[ Timestamp(minute) -> Count ]]
  @volatile private var aggregates = Map[String, TreeMap[Long, Long]]()

  //1 record per minute per keyword => 300 minutes = 60 * 5 minutes = 5 hours of history
  val MAX_RECORDS_PER_KEYWORD = 300

  def query(keyword: String, start: Long, end: Long): Long = {
    println(s"QUERYING keyword: $keyword | start=${getReadableTime(start)} - end=${getReadableTime(end)}")
    val tMap = aggregates.getOrElse(keyword, new TreeMap[Long, Long]())
    tMap.from(start).to(end).values.sum
  }

  def insert(keyword: String, time: Long, count: Long): Unit = {
    println(s"INSERTING @ ${getReadableTime(time)}: keyword: $keyword | count=$count")
    val tMap = aggregates.getOrElse(keyword, new TreeMap[Long, Long]())
    val newTMap = tMap + (time -> count)
    val trimmedTMap = if (newTMap.size > MAX_RECORDS_PER_KEYWORD) newTMap.drop(newTMap.size - MAX_RECORDS_PER_KEYWORD) else newTMap
    aggregates = aggregates + (keyword -> trimmedTMap)
  }

  def purge(): Unit = aggregates = Map[String, TreeMap[Long, Long]]()

  private def getReadableTime(time: Long): String = {
    val date = new Date(time)
    val dateFormat = new SimpleDateFormat("dd/MM h:mm a")
    dateFormat.format(date)
  }

}

object Storage extends Storage
