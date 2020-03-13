package ua.ucu.edu.aqiStreaming


import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{Behavior, PostStop, Signal}

//import ua.ucu.edu.deviceAQI._

object controllerAQI {
  def apply(): Behavior[Command] =
    Behaviors.setup(context => new controllerAQI(context))

  case class Start() extends Command
  case class Stop() extends Command
  case class Response(response: deviceAQI.RespondTemperature) extends Command
}

class controllerAQI(context: ActorContext[Command]) extends AbstractBehavior[Command] {
  import controllerAQI._

  private val respondTemperatureAdapter = context.messageAdapter(Response.apply)
  private val token = "f84d40a229949596ed1d0748a0cb6e4314b0a74c"
  private val topic = "aqi-streaming"
  private val cities = List("lviv", "kyiv", "odesa", "zaporizhzhya", "ivano-frankivsk", "dnipro", "rivne", "ternopil",
    "chernivci", "mariupol")

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Start() =>
//        val device = context.spawn(deviceAQI("lviv"), "lvivAQI")
//        device ! deviceAQI.ReadTemperature("lviv", token, respondTemperatureAdapter)
        for (city <- cities) {
          val device = context.spawn(deviceAQI(city), city + "AQI")
          device ! deviceAQI.ReadTemperature(city, token, respondTemperatureAdapter)
        }
        this
      case Stop() => Behaviors.stopped
      case Response(response) => onRespondTemperature(response)
        this
    }

  def onRespondTemperature(response: deviceAQI.RespondTemperature): Behavior[Command] = {
    val reading: String = response.value match {
      case Some(value) => value
      case None        => "None"
    }
    val aqiIndex = getAQI.parseJSON(reading)
    aqiKafkaProducer.produceRecord(topic, response.city, List(response.city, aqiIndex.toString).mkString(", "))
    this
  }



  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      println("AQI controller stopped!")
      this
  }

}