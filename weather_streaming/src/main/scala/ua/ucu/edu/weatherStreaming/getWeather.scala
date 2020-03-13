package ua.ucu.edu.weatherStreaming

import net.liftweb.json.{DefaultFormats, parse}



    /*val client = HttpClientBuilder.create.build
    val url = "https://climacell-microweather-v1.p.rapidapi.com/weather/realtime?" +
      "unit_system=si&fields=temp,wind_speed,humidity,cloud_cover,surface_shortwave_radiation,fire_index,no2,o3,co,so2,pm25,pm10" +
      "&lat=" + lat + "&lon=" + log
    val httpGet = new HttpGet(url)
    httpGet.setHeader("x-rapidapi-host", "climacell-microweather-v1.p.rapidapi.com")
    httpGet.setHeader("x-rapidapi-key", "acdf4fcdb9mshcc0efc0676e25b0p1458a7jsnb3d5654d7882")
    //val response = client.execute[String](httpGet,)
    val response = client.execute(httpGet)
    val inputStream = response.getEntity.getContent
    val inputBuffer = new BufferedReader(new InputStreamReader(inputStream))
    val responseString = inputBuffer.readLine()
    println(responseString)
    client.close()*/
//    val url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=04c7561284bb760addde892b62840442"
//    val result = scala.io.Source.fromURL(url).mkString.toString()
//      .replace("main","mainWeather")
//    println(result)
//    result
//    val parsedResult = parseJson(result)
//    println(parsedResult)
//  }
object WeatherAPI {
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
  case class mainWeather(temp: String, feels_like: String, temp_min: String, temp_max: String,  pressure: String, humidity: String)
  case class wind(speed: String, deg: String)
  case class Weather(mainWeather: mainWeather, wind: wind)


  val parsedJson = parse(json)
  //    println(parsedJson)
  val parsed = parsedJson.extract[Weather]
//    println(weatherItems.temp)
  val airItems = List(parsed.mainWeather.temp, parsed.mainWeather.pressure, parsed.mainWeather.humidity)
//  val windItems = List(parsed.wind.speed, parsed.wind.deg)
  airItems.mkString(",")
//  airItems.mkString(",")
//    println(s"Temperature = ${weatherItems.air.temp}, pressure = ${weatherItems.air.pressure}" +
//      s", humidity = ${weatherItems.air.humidity}, wind speed = ${weatherItems.wind.speed}")
//    List(weatherItems.air.temp,weatherItems.air.pressure,weatherItems.air.humidity,weatherItems.wind.speed)
//    val airElements = (json \\ "main").children
//    for (el <- airElements){
//      val airItems = el.extract[WeatherAir]
//      println(s"Temperature: ${airItems.temp}, pressure: ${airItems.pressure}, humidity: ${airItems.humidity}")
//    }
//
//    val windElements = (json \\ "wind").children
//    for (el <- windElements){
//      val windItems = el.extract[WeatherWind]
//      println(s"wind speed: ${windItems.windSpeed}")
//    }
  }
}
