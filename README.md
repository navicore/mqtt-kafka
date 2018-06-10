Working Scala example of listening to an MQTT broker via TLS and writing to Kafka with an MQTT QOS callback acknowledging each "AtLeastOnce" MQTT msg as it is written to Kafka.


```console
docker run -d --name my-kafka -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=`ifconfig | sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'` --env ADVERTISED_PORT=9092 spotify/kafka
```

```console
sbt assembly && MQTT_URL=ssl://YOUR_HOST:8883 MQTT_TOPIC=test MQTT_USER=YOUR_USER MQTT_PWD=YOUR_PWD java -jar target/scala-2.12/MqttKafka.jar
```

```console
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
```
