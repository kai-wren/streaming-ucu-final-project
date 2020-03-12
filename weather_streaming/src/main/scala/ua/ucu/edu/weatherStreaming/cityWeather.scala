package ua.ucu.edu.weatherStreaming

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}


object cityWeather {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new cityWeather(context))
}

class cityWeather(context: ActorContext[String]) extends AbstractBehavior[String] {

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "getdata" =>
        val weather = new WeatherAPI().getWeatherApi("Lviv")
        this
    }
}
