import datetime
import json
import time

from random import random, randint
from kafka import KafkaProducer

def produce_mock_orders_json(brokers, topic):
    producer = KafkaProducer(bootstrap_servers=[brokers])
    itemid = [(id, round(random() * randint(2, 100), 2)) for id in xrange(100000, 130000)]
    orderid = 1000000000
    print "Start produce data into kafka %s, topic %s ..."  % (brokers, topic)
    while True:
        item = itemid[randint(0, len(itemid))]
        record = {
            "itemid": str(item[0]),
            "price": str(item[1]),
            "count": randint(1, 100),
            "timestamp": "%s" % datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "region": "SG",
            "orderid": orderid
        }
        orderid += 1
        producer.send(topic, json.dumps(record))
        time.sleep(randint(0, 1000) / 1000.0)

if __name__ == '__main__':
    conf = {} 
    with open('conf/kafka.conf', 'r') as f:
       lines = f.readlines() 
    for line in lines:
        key = line.split("=")[0]
        value = line.split("=")[1]
        conf.update({key: value})
    produce_mock_orders_json(conf['brokers'], conf['topic'])