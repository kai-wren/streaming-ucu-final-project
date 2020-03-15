package ua.ucu.edu

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}

object aqiKafkaProducer{ //defining kafka producer object for AQI data

  val props:Properties = new Properties() //defining producer properties and configurations
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092") //broker host and port
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, // key serializer
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, // value serializer
    "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props) // creating new producer


  def produceRecord(topic: String, key: String, value: String) { // method to produce one record for given topic and key
    val record = new ProducerRecord[String, String](topic, key, value) // creating new producer record
    producer.send(record, new Callback { // sending record to topic
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = if(exception != null) {
        exception.printStackTrace();
      } else {
        println("The offset of the record we just sent is: " + metadata.offset()); // printing offset of record sent
      }
    })
  }

//  producer.close() // do not close producer since app running indefinitely

}
