package ua.ucu.edu.weatherStreaming

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import ua.ucu.edu.weatherStreaming.cityWeather._
trait Command

object cityWeather {
  def apply(city: String): Behavior[Command] =
    Behaviors.setup(context => new cityWeather(context, city))

  case class ReadTemp(city: String, reply: ActorRef[RespondTemp]) extends Command
  case class  RespondTemp(city: String, value: Option[String])
}

class cityWeather(context: ActorContext[Command], city: String) extends AbstractBehavior[Command] {

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case  ReadTemp(city,reply) =>
        while(true){
//          val weather = new WeatherAPI().getWeatherApi(city)
          val weather = WeatherAPI.getWeatherApi(city)
          reply ! RespondTemp(city, Option(weather))
          Thread.sleep(60000)
        }
        this

    }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop => println("Weather API stopped")
      this
  }
}
