package ua.ucu.edu

import net.liftweb.json._

object helperAQI {
  def getAqi(token: String, city: String): String ={
    val url = "https://api.waqi.info/feed/" + city + "/?token=" + token
    val result = scala.io.Source.fromURL(url).mkString
    result
  }

  def parseJSON(json: String): Int = {
    implicit val formats = DefaultFormats
    case class city(geo: List[String], name: String)
    case class time(v: Int)
    case class data(aqi: Int, city: city, time: time)
    case class row(status: String, data: data)

    val parsed = parse(json)
    println(parsed)
    val m = parsed.extract[row]

    m.data.aqi

  }
}
