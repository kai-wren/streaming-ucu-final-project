package ua.ucu.edu

import java.io.{BufferedReader, InputStreamReader}

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder


class WeatherAPI {
  def getWeatherApi(city: String): Unit = {
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
    val url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=04c7561284bb760addde892b62840442"
    val result = scala.io.Source.fromURL(url).mkString
    println(result)
  }
}
