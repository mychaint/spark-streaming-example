import datetime
import json
import time

from random import random, randint
from kafka import KafkaProducer

def produce_mock_orders_json(brokers, topic):
    producer = KafkaProducer(bootstrap_servers=brokers.split(","), retries=5)
    shopid = [id for id in xrange(100000, 101200)]
    itemid = [(id, round(random() * randint(2, 100), 2), shopid[id % 1200]) for id in xrange(100000, 130000)]
    orderid = 1000000000000
    region = ['SG', 'VN', 'TW','ID', 'TH','TW','ID','ID', 'TW','TW','TW','ID','TW', 'TH', 'MY', 'PH', 'ID']
    print "Start produce data into kafka %s, topic %s ..."  % (brokers, topic)
    while True:
        item = itemid[randint(0, len(itemid)-1)]
        record = {
            "itemid": item[0],
            "price": item[1],
            "shopid": item[2],
            "count": randint(1, 100),
            "timestamp": int(time.mktime(datetime.datetime.now().timetuple())),
            "region": region[randint(0,len(region)-1)],
            "orderid": orderid
        }
        print record
        orderid += 1
        producer.send(topic, json.dumps(record))
        time.sleep(randint(50, 600) / 1000.0)

if __name__ == '__main__':
    conf = {} 
    with open('conf/kafka.conf', 'r') as f:
       lines = f.readlines() 
    for line in lines:
        key = line.split("=")[0]
        value = line.split("=")[1]
        conf.update({key: value})
    produce_mock_orders_json(conf.get('brokers', ''), conf.get('topic', ''))