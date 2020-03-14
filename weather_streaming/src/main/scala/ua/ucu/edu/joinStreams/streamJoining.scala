package ua.ucu.edu.joinStreams

import java.util.Properties

import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}



class streamJoining{
  case class AirComponents( temp: String, pressure: String, humidity: String)
  case class AirWeatherClass(name: String, mainWeather: AirComponents)
  case class CityClass(name: String)
  case class JoinedAqiAir(city: String, aqi: String, temp: String, pressure: String, humidity: String)
  case class WindComponents(speed: String, deg: String)
  case class WindWeatherClass(wind:WindComponents)
  case class JoinedAqiAirWindClass(city: String, aqi: String, temp: String, pressure: String, humidity: String, windSpeed: String, windDeg: String)

  def consumeStreams(): Topology = {
    val builder = new StreamsBuilder
    // data from aqi-streaming topic
    val aqiStream: KStream[String, String] = builder.stream[String, String]("aqi-streaming")
    // data from aqi-weather-streaming topic
    val airStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-streaming")
    val windStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-wind-streaming")

    val joinedAqiAir = aqiStream.join(airStream)((lV: String, rV: String) => {

      implicit val formats = DefaultFormats
      val parsedAir = parse(rV) // parse airStream data

      val parserAirJson = parsedAir.extract[AirWeatherClass] // extract Air components (temp, pressure, humidity)
      val parsedCity = parsedAir.extract[CityClass]           // extract
      val joinedData = JoinedAqiAir(parsedCity.name, lV, parserAirJson.mainWeather.temp, parserAirJson.mainWeather.pressure, parserAirJson.mainWeather.humidity)
      val joinedStringData = write(joinedData)
      joinedStringData

    },
      windows = JoinWindows.of(20000))//.to("aqi-weather-joined")

//
//    val firstJoinedStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-joined")

    val joinedAqiAirWind = joinedAqiAir.join(windStream)((lV: String, rV: String) => {
      implicit val formats = DefaultFormats
      val parsedAqiAir = parse(lV)  // AQI + Air Weather
      val parsedWind = parse(rV)    // Wind Weather

      val parsedAqiAirJson = parsedAqiAir.extract[JoinedAqiAir]
      val parsedWindJson = parsedWind.extract[WindWeatherClass]
      val parsedCity = parsedWind.extract[CityClass]
      val joinedData = JoinedAqiAirWindClass(parsedCity.name,parsedAqiAirJson.aqi,
        parsedAqiAirJson.temp, parsedAqiAirJson.pressure, parsedAqiAirJson.humidity,
        parsedWindJson.wind.speed, parsedWindJson.wind.deg)
      val joinedStringData = write(joinedData)
      println(joinedStringData)
      joinedStringData
    }, windows = JoinWindows.of(20000)).to("aqi-weather-joined")



    builder.build()
  }
}

object streamJoining extends App{
  val props: Properties ={
    val rand = scala.util.Random
    val pr = new Properties()
    pr.put(StreamsConfig.APPLICATION_ID_CONFIG, "joined-streams1" + rand.nextInt.toString)
    pr.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    pr.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    pr
  }

  val app = new streamJoining
  val topology = app.consumeStreams()
  println(topology.describe)

  val streams: KafkaStreams = new KafkaStreams(topology, props)
  streams.cleanUp()
  streams.start()

  sys.ShutdownHookThread{
    streams.close()
  }
}