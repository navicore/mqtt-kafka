package onextent.iot.mqtt.kafka

import akka.stream.alpakka.mqtt._
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.mqtt.kafka.Conf._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object SubStream extends LazyLogging {

  def apply(): Unit = {

    val settings = MqttSourceSettings(
      MqttConnectionSettings(
        mqttUrl,
        "test-client",
        new MemoryPersistence
      ).withAuth(mqttUser, mqttPwd),
      Map(mqttTopic -> MqttQoS.AtMostOnce)
    )

    val mqttSource = MqttSource.atMostOnce(settings, bufferSize = 8)
    mqttSource.runForeach(m => {

      val str = new String(m.payload.toArray)

      println(s"got one:\n$str")

    })

    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
    //todo: sink to kafka
  }

}
