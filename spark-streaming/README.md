# Structured Streaming
This is a streaming ETL pipeline to load data statistics into a database.
In example code, it uses mysql as persistence layer.

### Create table
```sql
CREATE TABLE `agg_order_minute_tab` (
  `rowkey` varchar(32) NOT NULL,
  `region` varchar(10) DEFAULT NULL,
  `shopid` int(16) DEFAULT NULL,
  `itemid` int(16) DEFAULT NULL,
  `timestamp` int(11) DEFAULT NULL,
  `total_expenditure` int(11) DEFAULT NULL,
  `total_items_sold` int(11) DEFAULT NULL,
  `total_orders` int(11) DEFAULT NULL,
  `min_processing_time` int(11) DEFAULT NULL,
  `max_processing_time` int(11) DEFAULT NULL,
  `utime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rowkey`),
  KEY `region` (`region`),
  KEY `shopid` (`shopid`),
  KEY `itemid` (`itemid`),
  KEY `timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
```
Data volume increases quickly in streaming quickly, therefore mysql is not a 
properly persistence platform in most of scenarios. However you still
able to sharding the table in order to handle a great volume of data.
