package ua.ucu.edu

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

trait Command // adding trait for commands

object deviceAQI { // defining object for device actor
  def apply(city: String): Behavior[Command] =
    Behaviors.setup(context => new deviceAQI(context, city))

  case class ReadAQI(city: String, token: String, replyTo: ActorRef[RespondAQI]) extends Command //command to read AQI data
  case class RespondAQI(city: String, value: Option[String]) //command to respond AQI data back
}

class deviceAQI(context: ActorContext[Command], city: String) // class for device actor
  extends AbstractBehavior[Command] {
  import deviceAQI._

  override def onMessage(msg: Command): Behavior[Command] = { // define reaction of actor on different messages
    msg match {
      case ReadAQI(city, token, replyTo) => //read AQI data fromm API for given city
        while(true) { // executing reading indefinitely until actor stopped
          val response = helperAQI.getAqi(token, city) // read data fromm API via helper method
          replyTo ! RespondAQI(city, Option(response)) // reply data read back
          Thread.sleep(10000) // to avoid to many reads putting 10s delay tyo simulate reading once per 10 seconds
        }
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = { // signal to stop device actor when not required anymore
    case PostStop =>
      println("AQI device in " + city + " stopped!")
      this
  }
}