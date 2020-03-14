package ua.ucu.edu

import akka.actor.typed.{Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.AbstractBehavior

object controllerAQI { //creating object for controller actor
  def apply(): Behavior[Command] =
    Behaviors.setup(context => new controllerAQI(context))

  case class Start() extends Command //list commands which controller actor has
  case class Stop() extends Command
  case class Response(response: deviceAQI.RespondAQI) extends Command //command to receive responses from devices actors
}

class controllerAQI(context: ActorContext[Command]) extends AbstractBehavior[Command] { //class for controller actor
  import controllerAQI._
// setting some global values
  private val respondAQIAdapter = context.messageAdapter(Response.apply) //message adapter to receive responses from device actors
  private val token = "f84d40a229949596ed1d0748a0cb6e4314b0a74c" // token used to authorize against API
  private val topic = "aqi-streaming" //topic to which data is written
  private val cities = List("lviv", "kyiv", "odesa", "zaporizhzhya", "ivano-frankivsk", "dnipro", "rivne", "ternopil",
  "chernivci", "mariupol") // list of ukrainian cities for which we collect data

  override def onMessage(msg: Command): Behavior[Command] = //overwriting method which reacts to messages send
    msg match {
      case Start() => // if command start sent
        for (city <- cities) { //loop through all cities
          val device = context.spawn(deviceAQI(city), city + "AQI") // spawn device actor to read API data
          device ! deviceAQI.ReadAQI(city, token, respondAQIAdapter) // send read data command
        }
        this
      case Stop() => Behaviors.stopped // command to stop all actors - currently not in use since app runs indefinitely
      case Response(response) => onRespondAQI(response) //controller actor command to receive response from devices actors
      this
    }

 def onRespondAQI(response: deviceAQI.RespondAQI): Behavior[Command] = { //method to process responses from device actors
   val reading: String = response.value match {
     case Some(value) => value
     case None        => "None"
   }
   val aqiIndex = helperAQI.parseJSON(reading) // parse JSON data which returned by API using helper class method
   aqiKafkaProducer.produceRecord(topic, response.city, aqiIndex.toString) // call method of AQI kafka producer to send processed record to topic
   this
 }



  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case PostStop =>
      println("AQI controller stopped!")
      this
  }

}
