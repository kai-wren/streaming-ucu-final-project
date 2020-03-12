package ua.ucu.edu.weatherStreaming

import akka.actor.typed.ActorSystem

object main extends App {
  val system = ActorSystem(producerWeather(), "system")
  system ! "start"
  system ! "stop"


}
