package ua.ucu.edu.aqiStreaming


import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
//import ua.ucu.edu.controllerAQI._

trait Command

object deviceAQI {
  def apply(city: String): Behavior[Command] =
    Behaviors.setup(context => new deviceAQI(context, city))

  case class ReadTemperature(city: String, token: String, replyTo: ActorRef[RespondTemperature]) extends Command
  case class RespondTemperature(city: String, value: Option[String])
}

class deviceAQI(context: ActorContext[Command], city: String)
  extends AbstractBehavior[Command] {
  import deviceAQI._

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case ReadTemperature(city, token, replyTo) =>
        while(true) {
          val response = getAQI.getAqi(token, city)
          replyTo ! RespondTemperature(city, Option(response))
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