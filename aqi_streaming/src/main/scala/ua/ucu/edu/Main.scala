package ua.ucu.edu

import akka.actor.typed.ActorSystem

object Main extends App {
  val system = ActorSystem(controllerAQI(), "system")
  system ! controllerAQI.Start()
//  system ! producerAQI.Stop()


}
