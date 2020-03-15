package ua.ucu.edu.weatherStreaming

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}


object weatherKafkaProducer {
  val BrokerList: String = System.getenv("KAFKA_BROKERS")
  val props: Properties = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BrokerList)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "weather_provider")
  props.put("application.id", "weather_streaming")
  val producer = new KafkaProducer[String,String](props)


  def produceRecord(topic: String, key: String, value: String): Unit ={
    val record = new ProducerRecord[String, String](topic, key, value)
    producer.send(record, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = if(exception != null){
        exception.printStackTrace();
      } else {
        println("The offset of the record we just sent is: " + metadata.offset())
      }
    })
  }

}
