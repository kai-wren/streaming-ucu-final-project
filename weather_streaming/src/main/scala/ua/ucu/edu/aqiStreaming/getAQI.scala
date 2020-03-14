package ua.ucu.edu.aqiStreaming

import net.liftweb.json._

object getAQI {
  def getAqi(token: String, city: String): String ={
    val url = "https://api.waqi.info/feed/" + city + "/?token=" + token
    val result = scala.io.Source.fromURL(url).mkString
    println(result)
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
//    val jsonAirString = write(m)
//    jsonAirString

//    List(m.data.city.name, m.data.aqi)

    m.data.aqi

  }
}