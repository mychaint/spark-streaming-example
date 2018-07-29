# Spark Streaming Example
This is an example project to make a streaming data pipeline using Spark Structured Streaming, Kafka and MySQL.

### Architecture
##### Producer
Python mock producer will generate data randomly and push the data into a kafka cluster. These data are mock orders including itemid, orderid, shopid, region, count of items sold.

##### ETL
ETL pipeline of orders is a structured streaming based real-time processor. It consumer data from kafka and aggregate data within a window period and output it with update mode.

##### Persistence
In this example, persistence is simply a mysql database. With a unique rowkey provided by ETL, data persistence is done by `REPLACE` command in order to implement de-duplication.

In enterprise production environment, persistence layer could consider using HBase or similar NoSQL database, such as Cassendra, to facilitate low latency writing and reading operations, HA and data distribution (load balancing). 

It is not recommanded a streaming job to directly output data as parquet files in HDFS. There are several reasons:
* parquet does not support UPDATE.
* disk WRITE is a high latency operation.
* there will be significant number of parquet files overtime which troubles performance of file scan.
* Hive metastore need to be updated once new parquet file generated. 
* if schema of data changed, structured streaming is not working in original directory.

If choose APPEND mode to output parquet files, watermark is needed, which cause the final dataset is not completed. This scenario is only working for data less important, such user clicks, page views, search trend.

##### SQL Support
There are many SQL engines on top of HBase, such as Hive, Phoenix, Drill to support SQL queries. 

##### Data Visualization
Based on Hive, Phoenix, Drill like SQL engines or pure HBase client, it is possible to build good data visualization with many analytics tools, such Apache Superset.

### Code Style & QA
In spark streaming project, data sources, data output mode are different in order to conduct testing and deployment separately. According to my best practices to develope a spark project, I used dependency injection over my code, so that core streaming processing code would be fixed, and method of capturing source data, transforming data and sinking data are changing according to different runtime environment.

In this example code, I use `Google-Guice` to handle method injection as runtime.

In class `OrderStreamingProcessor`, core processing flow is in method `execute`
```scala
def execute(): Unit = {
    this.getDataSource()
        .proceedTransformation
        .proceedSink
        .awaitTermination
    }
```

Methods `getDataSource`, `proceedTransformation` and `proceedSink` are injected by `Google-Guice`. Injection rule is defined in package `com.mychaint.order.inject`. 

When I want to test the main logic, just execute main function with following parameter `test`
```bash
spark-submit --class com.mychaint.order.AppContext my.jar test <dev email>
```

In production, it just changes to 
```bash
spark-submit --class com.mychaint.order.AppContext my.jar prod <dev email>
```

All injected methods are defined in `DataUtils`. They are able to be easily changed in the same place.

Besides core logic test, just make sure unit tests of IO sides passed, we could make a relatively high quality of our project before production deployment.

