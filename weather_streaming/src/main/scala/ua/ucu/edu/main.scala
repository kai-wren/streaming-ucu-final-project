package ua.ucu.edu

import akka.actor.typed.ActorSystem

object main extends App {
  val system = ActorSystem(producerWeather(), "system")
  system ! "start"

}