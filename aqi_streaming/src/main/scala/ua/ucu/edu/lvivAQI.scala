package ua.ucu.edu

import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

object lvivAQI {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new lvivAQI(context))
}

class lvivAQI(context: ActorContext[String]) extends AbstractBehavior[String] {

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "getdata" =>
        val aqi = new getAQI
        aqi.get_aqi("f84d40a229949596ed1d0748a0cb6e4314b0a74c", "lviv")
        this
    }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("second stopped")
      this
  }
}