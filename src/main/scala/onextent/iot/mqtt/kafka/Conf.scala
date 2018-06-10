package onextent.iot.mqtt.kafka

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

object Conf extends Conf with LazyLogging {

  implicit val actorSystem: ActorSystem = ActorSystem("MqttKafka")
  implicit val m: Materializer = ActorMaterializer(ActorMaterializerSettings(actorSystem))

}

trait Conf {

  val conf: Config = ConfigFactory.load()

  val bootstrap: String = conf.getString("kafka.bootstrap")

  val mqttUrl: String = conf.getString("mqtt.url")
  val mqttUser: String = conf.getString("mqtt.user")
  val mqttPwd: String = conf.getString("mqtt.pwd")
  val mqttTopic: String = conf.getString("mqtt.topic")

}
