package ua.ucu.edu

import akka.actor.typed.ActorSystem

object Main extends App {
  val system = ActorSystem(controllerAQI(), "system") //creating actor system
  system ! controllerAQI.Start() // launching controller actor which suppose to call other specific actors
//  system ! producerAQI.Stop() // currently app running infinitely hence sop command commented out


}
