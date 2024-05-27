from configparser import ConfigParser
from confluent_kafka import Producer, Consumer, KafkaException
import json

def connectKafka():
    try:
        # 读取配置文件
        config = ConfigParser()
        config.read('config.ini')

        # 获取生产者配置
        producer_conf = dict(config['producer'])
        # 创建生产者实例
        producer = Producer(producer_conf)
        return producer
    except KafkaException as e:
        print(f"Failed to connect to Kafka: {e}")
        return None

# 发送消息的回调函数
def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

def send_to_kafka(data, topic, producer):
    try:
        for item in data:
            # 将字典转换为 JSON 格式的字符串
            message = json.dumps(item, ensure_ascii=False)#.encode('utf-8')  # 编码为 UTF-8 字节串
            # 发送消息
            producer.produce(topic, value=message)
        producer.flush()
        print("Messages sent to Kafka successfully")
    except Exception as e:
        print(f"Failed to send message to Kafka: {e}")