package ua.ucu.edu

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior

object producerAQI {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new producerAQI(context))
}

class producerAQI(context: ActorContext[String]) extends AbstractBehavior[String] {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "start" =>
        val firstRef = context.spawn(lvivAQI(), "first-actor")
        firstRef ! "getdata"
        this
    }
}
