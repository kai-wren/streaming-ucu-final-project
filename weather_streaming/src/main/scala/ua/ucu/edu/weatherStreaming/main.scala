package ua.ucu.edu.weatherStreaming

import akka.actor.typed.ActorSystem

object main extends App {
  val system = ActorSystem(producerWeather(), "system")
  system ! producerWeather.Start()
//  system ! producerWeather.Stop()

//  val responds = WeatherAPI.getWeatherApi("Lviv")
//  println(responds)
//
//  val parced = WeatherAPI.parseJson(responds)
//  println(parced)



}
