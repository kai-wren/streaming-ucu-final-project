package ua.ucu.edu.joinStreams

import java.util.Properties

import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes
import org.apache.kafka.streams.scala.Serdes._

import scala.util.parsing.json.JSON
//import scala.collection.mutable._
//import scala.util.parsing.json.JSON
import net.liftweb.json._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}




class streamJoining{
  def consumeStreams(): Topology = {
    val builder = new StreamsBuilder
    // data from aqi-streaming topic
    val aqiStream: KStream[String, String] = builder.stream[String, String]("aqi-streaming")
    // data from aqi-weather-streaming topic
    val weatherStream: KStream[String, String] = builder.stream[String, String]("aqi-weather-streaming")


    case class aqiData(city: String, aqi: String)
    case class weatherData(city: String, temp: String, pressure: String, humidity: String)
    implicit val formats = DefaultFormats


    val processedAqiStream = aqiStream.map((k,v) => {
      //      val resultAqi = aqiData(k,v)
      //      val jsonAqi = write(resultAqi)
      val result = JSON.parseFull(v)
      result match {
        case Some(map: Map[String, Any]) => (map("name").toString, //assign city as key
          Option(map("value")) match { //assign value based on "value" attribute of input JSON
            case Some(v) => map("value").toString
            case None => "null"
          })
        case None => ("null", "null")
      }}).groupByKey.reduce((v1, v2)=> v2).toStream

    val processWeatherStream = weatherStream.map((k,v) => {
      //      val Array(temp, pressure, humidity, _ @ _*) = v.split(",")
      //      val resultWeather = weatherData(k, temp.toString(), pressure.toString(), humidity.toString())
      //    val jsonWeather = write(resultWeather)
      val result = JSON.parseFull(v)
      result match {
        case Some(map: Map[String, Any]) => (map("name").toString, map("temp").toString) // creating new key as user ID and value as page ID
        case None => ("null", "null")
      }
    }).groupByKey.reduce((v1, v2)=> v2)

        val joined = processedAqiStream.leftJoin(processWeatherStream)((lV: String, rV: String) => rV)(Joined.keySerde(Serdes.String).withValueSerde(Serdes.String))
        joined.to("aqi-weather-joined")
    builder.build()
  }
}

object streamJoining extends App{
  val props: Properties ={
    val rand = scala.util.Random
    val pr = new Properties()
    pr.put(StreamsConfig.APPLICATION_ID_CONFIG, "joined-streams" + rand.nextInt.toString)
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