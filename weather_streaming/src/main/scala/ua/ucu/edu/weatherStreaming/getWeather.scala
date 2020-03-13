package ua.ucu.edu.weatherStreaming

import net.liftweb.json.{DefaultFormats, parse}
import net.liftweb.json.Serialization.write

object WeatherAPI {
  case class airWeather(temp: String,  pressure: String, humidity: String)
  case class windWeather(speed: String, deg: String)
  case class Weather(name: String, mainWeather: airWeather, wind: windWeather)

  def getWeatherApi(city: String): String = {
    // https://openweathermap.org/current#name
    val url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=04c7561284bb760addde892b62840442"
    val result = scala.io.Source.fromURL(url).mkString
      .replace("main", "mainWeather")
    //    println(result)
    result
  }

  def parseJson(json: String): String ={
    implicit val formats = DefaultFormats
    //    val weather: String = new WeatherAPI().getWeatherApi("Lviv").toString()
    val parsedJson = parse(json)    //get json from openweathermap API
    val parsed = parsedJson.extract[Weather]    // get Weather class with air and wind data
//    val airItems = List(parsed.mainWeather.temp, parsed.mainWeather.pressure, parsed.mainWeather.humidity)
    val jsonString = write(parsed)        // convert Weather class to json string
  //  airItems.mkString(",")

    jsonString
    }

  def parseAirJson(jsonWeatherString: String): String ={
    implicit val formats = DefaultFormats
    case class AirComponents(name: String, mainWeather: airWeather)
//    val jsonWeatherString = parseJson(json)
    val parsedJson = parse(jsonWeatherString)
    val parsed = parsedJson.extract[AirComponents]
//    val airItems = List(parsed.temp, parsed.pressure, parsed.humidity)
    val jsonAirString = write(parsed)
    jsonAirString
  }

  def parseWindJson(jsonWeatherString: String): String ={
    implicit val formats = DefaultFormats
    case class WindComponents(name: String, wind: windWeather)
//    val jsonWeatherString = parseJson(json)
    val parsedJson = parse(jsonWeatherString)
    val parsed = parsedJson.extract[WindComponents]
//        val airItems = List(parsed.speed, parsed.deg)
    val jsonWindString = write(parsed)
    jsonWindString
  }
}
