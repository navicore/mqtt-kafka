package onextent.iot.mqtt.kafka

import akka.actor.{Actor, Props}
import com.sandinh.paho.akka._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.mqtt.kafka.Conf._

object Main extends App with LazyLogging {

  val psConfig =
    PSConfig(brokerUrl = mqttUrl, conOpt = ConnOptions(mqttUser, mqttPwd).get)

  val pubsub = actorSystem.actorOf(Props(classOf[MqttPubSub], psConfig))

  class SubscribeActor extends Actor {

    pubsub ! Subscribe(mqttTopic, self)

    def receive: PartialFunction[Any, Unit] = {
      case SubscribeAck(Subscribe(`mqttTopic`, `self`, _), fail) =>
        if (fail.isEmpty) {
          context become ready
        } else
          logger.error(s"Can't subscribe to $mqttTopic")
    }

    def ready: Receive = {
      case msg: Message =>
        logger.info(s"ejs got ${new String(msg.payload)} from ${msg.topic}")
      case x =>
        logger.warn(s"received unexpected type: $x")
    }
  }

  actorSystem.actorOf(Props(classOf[SubscribeActor]))

}
