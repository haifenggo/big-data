import configparser
from confluent_kafka import Producer, Consumer, KafkaException

# 读取配置文件
config = configparser.ConfigParser()
config.read('config.ini')

# 获取生产者配置
producer_conf = dict(config['producer'])

# 创建生产者实例
producer = Producer(producer_conf)

# 发送消息的回调函数
def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

# 发送消息
producer.produce('my_topic', key='key', value='hello world', callback=delivery_report)
producer.flush()

# 获取消费者配置
consumer_conf = dict(config['consumer'])

# 创建消费者实例
consumer = Consumer(consumer_conf)

# 订阅主题
consumer.subscribe(['my_topic'])

# 消费消息
try:
    while True:
        msg = consumer.poll(timeout=1.0)
        if msg is None:
            continue
        if msg.error():
            if msg.error().code() == KafkaError._PARTITION_EOF:
                continue
            else:
                raise KafkaException(msg.error())
        print('Received message: {}'.format(msg.value().decode('utf-8')))
except KeyboardInterrupt:
    pass
finally:
    consumer.close()
