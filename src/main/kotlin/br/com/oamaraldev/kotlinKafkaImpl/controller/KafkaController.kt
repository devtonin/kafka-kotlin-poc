package br.com.oamaraldev.kotlinKafkaImpl.controller

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Future

@RestController
class KafkaController @Autowired constructor(kafkaTemplate: KafkaTemplate<String, String>) {

    var kafkaTemplate: KafkaTemplate<String, String>? = kafkaTemplate;
    val topic:String = "test_topic"

    @GetMapping("/send")
    fun sendMessage(@RequestParam("message") message : String) : ResponseEntity<String> {

        val lf : ListenableFuture<SendResult<String, String>> = kafkaTemplate?.send(topic, message)!!
        val sendResult: SendResult<String, String> = lf.get()

        return ResponseEntity.ok(sendResult.producerRecord.value() + " sent to topic")
    }


    @GetMapping("/produce")
    fun produceMessage(@RequestParam("message") message : String) : ResponseEntity<String> {

        val producerRecord : ProducerRecord<String, String> = ProducerRecord(topic, message)
        val map = mutableMapOf<String, String>()

        map["key.serializer"]   = "org.apache.kafka.common.serialization.StringSerializer"
        map["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        map["bootstrap.servers"] = "localhost:9092"

        val producer = KafkaProducer<String, String>(map as Map<String, Any>?)
        val future: Future<RecordMetadata> = producer.send(producerRecord)!!

        return ResponseEntity.ok(" message sent to " + future.get().topic());
    }
}