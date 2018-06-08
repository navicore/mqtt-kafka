package onextent.iot.mqtt.kafka

import akka.actor.{Actor, Props}
import com.sandinh.paho.akka._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.mqtt.kafka.Conf._

object SubActor extends LazyLogging {

  def apply(): Unit = {

    val psConfig =
      PSConfig(brokerUrl = mqttUrl, conOpt = ConnOptions(mqttUser, mqttPwd).get)

    val pubsub = actorSystem.actorOf(Props(classOf[MqttPubSub], psConfig))

    class SubscribeActor extends Actor {

      pubsub ! Subscribe(mqttTopic, self)

      def receive: Receive = {
        case SubscribeAck(Subscribe(`mqttTopic`, `self`, _), fail) =>
          if (fail.isEmpty) {
            context become ready
          } else
            logger.error(s"Can't subscribe to $mqttTopic")
      }

      def ready: Receive = {
        case msg: Message =>
          logger.debug(s"received ${new String(msg.payload)} from ${msg.topic}")
        //todo: write to kafka
        //todo: write to kafka
        //todo: write to kafka
        //todo: write to kafka
        //todo: write to kafka
        //todo: write to kafka
        //todo: write to kafka
        case x =>
          logger.warn(s"received unexpected type: $x")
      }
    }

    actorSystem.actorOf(Props(classOf[SubscribeActor]))
  }

}
