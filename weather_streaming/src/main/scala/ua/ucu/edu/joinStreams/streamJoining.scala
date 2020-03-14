package ua.ucu.edu.joinStreams

import java.util.Properties

import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
//import scala.util.parsing.json.JSON
//import scala.collection.mutable._
//import scala.util.parsing.json.JSON
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}



class streamJoining{
  case class AirComponents(temp: String, pressure: String, humidity: String)
  case class AirWeatherClass(mainWeather: AirComponents)
  case class CityClass(name: String)
  case class JoinedAqiAirClass(city: String, aqi: String, WeatherData: AirComponents)
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
//
//
//    val processWeatherStream = weatherStream.map((k,v) => {
//      case class airClass(temp: String, pressure: String, humidity: String)
//      case class airWeather(mainWeather: airClass)
//      implicit val formats = DefaultFormats
//      val parsedJson = parse(v)
//      val parsed = parsedJson.extract[airWeather]
//      val airComponents = List(parsed.mainWeather.temp, parsed.mainWeather.pressure, parsed.mainWeather.humidity)
//      (k.toString.toLowerCase(), airComponents.mkString(","))
//    }).groupByKey.reduce((v1, v2)=> v2).toStream

//    val processedAqiStream = aqiStream.map((k,v) => {(k.toString.toLowerCase(), v.toString)}).groupByKey.reduce((v1, v2)=> v2).toStream



//    val joined = processedAqiStream.leftJoin(processWeatherStream)((lV: String, rV: String) => rV)(Joined.keySerde(Serdes.String).withValueSerde(Serdes.String) )
    val joinedAqiAir = aqiStream.join(airStream)((lV: String, rV: String) => {
      lV + " - " + rV
//      case class aqiValue(aqi: String)

      implicit val formats = DefaultFormats
      val parsedAir = parse(rV) // parse airStream data
      val parserAirJson = parsedAir.extract[AirWeatherClass] // extract Air components
      val parsedCity = parsedAir.extract[CityClass]           // extract
      val joinedData = JoinedAqiAirClass(parsedCity.name, lV, AirComponents( parserAirJson.mainWeather.temp, parserAirJson.mainWeather.pressure, parserAirJson.mainWeather.humidity))
      val joinedStringData = write(joinedData)
      joinedStringData
    },
      windows = JoinWindows.of(400)).to("aqi-weather-joined")

    val firstJoinedStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-joined")
    val joinedAqiAirWind = firstJoinedStream.join(windStream)((lV: String, rV: String) => {
      implicit val formats = DefaultFormats
      val parsedAqiAir = parse(lV)
      val parsedWind = parse(rV)
      val parsedAqiAirJson = parsedAqiAir.extract[JoinedAqiAirClass]
      val parsedWindJson = parsedWind.extract[WindWeatherClass]
      val parsedCity = parsedWind.extract[CityClass]
      val joinedData = JoinedAqiAirWindClass(parsedCity.name,parsedAqiAirJson.aqi,
        parsedAqiAirJson.WeatherData.temp, parsedAqiAirJson.WeatherData.pressure, parsedAqiAirJson.WeatherData.humidity,
        parsedWindJson.wind.speed, parsedWindJson.wind.deg)
      val joinedStringData = write(joinedData)
      println(joinedStringData)
      joinedStringData
    }, windows = JoinWindows.of(400)).to("aqi-weather-joined-final")



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