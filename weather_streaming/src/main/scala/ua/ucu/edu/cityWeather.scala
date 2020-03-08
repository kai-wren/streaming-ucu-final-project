package ua.ucu.edu

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
        val aqi = new WeatherAPI().getWeatherApi(24.023239,49.838261)
        //aqi.get_aqi("f84d40a229949596ed1d0748a0cb6e4314b0a74c", "lviv")
        this
    }
}
