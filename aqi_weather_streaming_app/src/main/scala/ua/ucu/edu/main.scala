package ua.ucu.edu

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object main extends App{
  val props: Properties ={
    val rand = scala.util.Random
    val pr = new Properties()
    pr.put(StreamsConfig.APPLICATION_ID_CONFIG, "joined-streams1" + rand.nextInt.toString)
    pr.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    pr.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    pr
  }

  val app = new streamJoining
  val topology = app.consumeStreams()
  println(topology.describe)

  val streams: KafkaStreams = new KafkaStreams(topology, props)
  streams.cleanUp()
  streams.start()

  sys.ShutdownHookThread{
    streams.close()
  }
}
