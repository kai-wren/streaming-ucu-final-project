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
    result
  }

  def parseJson(json: String): String ={
    implicit val formats = DefaultFormats
    val parsedJson = parse(json)    //get json from openweathermap API
    val parsed = parsedJson.extract[Weather]    // get Weather class with air and wind data
    val jsonString = write(parsed)        // convert Weather class to json string
    jsonString
    }

  def parseAirJson(jsonWeatherString: String): String ={
    implicit val formats = DefaultFormats
    case class AirComponents(name: String, mainWeather: airWeather)
    case class AirWeatherFlatten(name: String, temp: String,  pressure: String, humidity: String)
    val parsedJson = parse(jsonWeatherString)
    val parsed = parsedJson.extract[AirComponents]
    val flattenParsed = AirWeatherFlatten(parsed.name, parsed.mainWeather.temp, parsed.mainWeather.pressure, parsed.mainWeather.humidity)
    val jsonAirString = write(flattenParsed)
    jsonAirString
  }

  def parseWindJson(jsonWeatherString: String): String ={
    implicit val formats = DefaultFormats
    case class WindComponents(name: String, wind: windWeather)
    case class WindWeatherFlatten(name: String, windSpeed: String, windDeg: String)
    val parsedJson = parse(jsonWeatherString)
    val parsed = parsedJson.extract[WindComponents]
    val flattenParsed = WindWeatherFlatten(parsed.name, parsed.wind.speed, parsed.wind.deg)
    val jsonWindString = write(flattenParsed)
    jsonWindString
  }
}
