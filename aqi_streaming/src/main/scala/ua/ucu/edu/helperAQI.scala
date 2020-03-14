package ua.ucu.edu

import net.liftweb.json._

object helperAQI { // defining helper AQI object which collects auxiliary methods
  def getAqi(token: String, city: String): String ={ //method to read AQI data from web API
    val url = "https://api.waqi.info/feed/" + city + "/?token=" + token //constructing URL
    val result = scala.io.Source.fromURL(url).mkString //parsing result to string
    result
  }

  def parseJSON(json: String): Int = { // method to parse data returned by API
    implicit val formats = DefaultFormats
    case class city(geo: List[String], name: String) //creating case classes which corresponds to API JSON structure
    case class time(v: Int)
    case class data(aqi: Int, city: city, time: time)
    case class row(status: String, data: data)

    val parsed = parse(json) // parse JSON data based on case classes
//    println(parsed)
    val m = parsed.extract[row]

    m.data.aqi // extract AQI data from parsed API data
  }
}
