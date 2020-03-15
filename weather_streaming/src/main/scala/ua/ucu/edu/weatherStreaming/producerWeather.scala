package ua.ucu.edu.weatherStreaming

import akka.actor.typed.{Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import ua.ucu.edu.weatherStreaming.producerWeather.{Response, Start, Stop}

object producerWeather {
  def apply(): Behavior[Command] =
    Behaviors.setup(context => new producerWeather(context))

  case class Start() extends Command
  case class Stop() extends Command
  case class Response(response: cityWeather.RespondTemp) extends Command
}

class producerWeather(context: ActorContext[Command]) extends AbstractBehavior[Command] {

  private val respondTemperatureAdapter = context.messageAdapter(Response.apply)
  private val airDataTopic = "aqi-weather-streaming"
  private val windDataTopic = "aqi-weather-wind-streaming"
  private val cities = List("lviv", "kyiv", "odesa", "zaporizhzhya", "ivano-frankivsk", "dnipro", "rivne", "ternopil",
    "chernivci", "mariupol")

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Start() =>
        for (city <-  cities){
          val citySignal = context.spawn(cityWeather(city), s"${city}-weather")
          citySignal ! cityWeather.ReadTemp(city, respondTemperatureAdapter)
        }
        this
      case Stop() => Behaviors.stopped
      case Response(response) => onRespondTemperature(response)
        this
    }

  def onRespondTemperature(response: cityWeather.RespondTemp): Behavior[Command] = {
    val reading: String = response.value match {
      case Some(value) => value
      case None     => "None"
    }

    val weatherData: String = WeatherAPI.parseJson(reading)
    val temperatureValue: String = WeatherAPI.parseAirJson(weatherData)
    weatherKafkaProducer.produceRecord(airDataTopic, response.city, temperatureValue.toString)


    val windValue: String = WeatherAPI.parseWindJson(weatherData)
    weatherKafkaProducer.produceRecord(windDataTopic, response.city, windValue.toString)

    this
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      println("AQI controller stopped!")
      this
  }

}