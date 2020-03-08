package ua.ucu.edu

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object producerWeather {
  def apply(): Behavior[String] = Behaviors.setup(context => new producerWeather(context))
}

class producerWeather(context: ActorContext[String]) extends AbstractBehavior [String]{

  override def onMessage(msg: String): Behavior[String] = {
    msg match{
      case "start" =>
        val firstRef = context.spawn(cityWeather(), "first-actor1")
        firstRef ! "getdata"
        this
    }
  }
}