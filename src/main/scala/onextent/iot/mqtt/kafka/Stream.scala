package onextent.iot.mqtt.kafka

import java.util.concurrent.CompletionStage

import akka.kafka.ConsumerMessage.Committable
import akka.kafka.ProducerMessage.Message
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.mqtt._
import akka.stream.alpakka.mqtt.scaladsl.{MqttCommittableMessage, MqttSource}
import akka.stream.scaladsl.{Flow, Source}
import akka.{Done, NotUsed}
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.mqtt.kafka.Conf._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{
  ByteArraySerializer,
  StringSerializer
}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.Future

object Stream extends LazyLogging {

  def getKey(text: String): String = {
    import onextent.data.navipath.dsl.NaviPathSyntax._
    text
      .query[String]("$.DevAddr")
      .getOrElse(text.hashCode().toString)
  }

  val mqttConsumerettings = MqttSourceSettings(
    MqttConnectionSettings(
      mqttUrl,
      "test-client",
      new MemoryPersistence
    ).withAuth(mqttUser, mqttPwd).withAutomaticReconnect(true),
    Map(mqttTopic -> MqttQoS.AtLeastOnce)
  )

  val kafkaProducerSettings: ProducerSettings[Array[Byte], String] =
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrap)

  def committable: Flow[MqttCommittableMessage,
                        Message[Array[Byte], String, Committable],
                        NotUsed] =
    Flow[MqttCommittableMessage]
      .map((msg: MqttCommittableMessage) => {
        val payloadStr = new String(msg.message.payload.toArray, "UTF8")
        val key = getKey(payloadStr)
        val record = new ProducerRecord[Array[Byte], String](
          mqttTopic,
          key.getBytes("UTF8"),
          payloadStr
        )
        val c = new Committable {
          override def commitScaladsl(): Future[Done] = {
            logger.debug(
              s"wrote $key to kafka topic $kafkaTopic. mqtt ack done.")
            msg.messageArrivedComplete()
          }
          override def commitJavadsl(): CompletionStage[Done] =
            throw new java.lang.UnsupportedOperationException()
        }
        Message(record, c)
      })

  def apply(): Unit = {

    val mqttSource: Source[MqttCommittableMessage, Future[Done]] =
      MqttSource.atLeastOnce(mqttConsumerettings, bufferSize = 8)

    mqttSource
      .via(committable)
      .runWith(Producer.commitableSink(kafkaProducerSettings))

  }

}
