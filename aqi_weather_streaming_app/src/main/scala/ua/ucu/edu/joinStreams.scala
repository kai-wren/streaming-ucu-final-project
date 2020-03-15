package ua.ucu.edu


import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse}
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream



class streamJoining {

  case class AirComponents(name: String, temp: String, pressure: String, humidity: String)

  case class JoinedAqiAir(city: String, aqi: String, temp: String, pressure: String, humidity: String)

  case class WindComponents(windSpeed: String, windDeg: String)

  case class WindWeatherClass(wind: WindComponents)

  case class JoinedAqiAirWindClass(city: String, aqi: String, temp: String, pressure: String, humidity: String, windSpeed: String, windDeg: String)

  def consumeStreams(): Topology = {
    val builder = new StreamsBuilder
    val aqiStream: KStream[String, String] = builder.stream[String, String]("aqi-streaming")
    val airStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-streaming")
    val windStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-wind-streaming")

    val joinedAqiAir = aqiStream.join(airStream)((lV: String, rV: String) => {

      implicit val formats = DefaultFormats
      val parsedAir = parse(rV) // parse airStream data
      val parserAirJson = parsedAir.extract[AirComponents] // extract Air components (temp, pressure, humidity)
      val joinedData = JoinedAqiAir(parserAirJson.name, lV, parserAirJson.temp, parserAirJson.pressure, parserAirJson.humidity)
      val joinedStringData = write(joinedData)
      joinedStringData
    },
      windows = JoinWindows.of(10000))

    val joinedAqiAirWind = joinedAqiAir.join(windStream)((lV: String, rV: String) => {
      implicit val formats = DefaultFormats
      val parsedAqiAir = parse(lV) // AQI + Air Weather
      val parsedWind = parse(rV) // Wind Weather

      val parsedAqiAirJson = parsedAqiAir.extract[JoinedAqiAir]
      val parsedWindJson = parsedWind.extract[WindComponents]
      val joinedData = JoinedAqiAirWindClass(parsedAqiAirJson.city, parsedAqiAirJson.aqi,
        parsedAqiAirJson.temp, parsedAqiAirJson.pressure, parsedAqiAirJson.humidity,
        parsedWindJson.windSpeed, parsedWindJson.windDeg)
      val joinedStringData = write(joinedData)
      println(joinedStringData)
      joinedStringData
    }, windows = JoinWindows.of(10000)).to("aqi-weather-joined")

    builder.build()
  }
}
