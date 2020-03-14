package ua.ucu.edu

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

trait Command

object deviceAQI {
  def apply(city: String): Behavior[Command] =
    Behaviors.setup(context => new deviceAQI(context, city))

  case class ReadAQI(city: String, token: String, replyTo: ActorRef[RespondAQI]) extends Command
  case class RespondAQI(city: String, value: Option[String])
}

class deviceAQI(context: ActorContext[Command], city: String)
  extends AbstractBehavior[Command] {
  import deviceAQI._

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case ReadAQI(city, token, replyTo) =>
        while(true) {
          val response = helperAQI.getAqi(token, city)
          replyTo ! RespondAQI(city, Option(response))
          Thread.sleep(10000)
        }
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      println("AQI device in " + city + " stopped!")
      this
  }
}