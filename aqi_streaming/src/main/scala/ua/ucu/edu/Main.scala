package ua.ucu.edu

import akka.actor.typed.ActorSystem

object Main extends App {
  val system = ActorSystem(producerAQI(), "system")
  system ! "start"
  system ! "stop"

}
