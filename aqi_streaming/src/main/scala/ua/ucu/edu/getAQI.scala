package ua.ucu.edu

class getAQI {
  def get_aqi(token: String, city: String): Unit ={
    val url = "https://api.waqi.info/feed/" + city + "/?token=" + token
    val result = scala.io.Source.fromURL(url).mkString


    println(result)
  }
}
