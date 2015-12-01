
cocamq

基于netty 的简单mq实现

     网络通讯层netty
     持久层NIO 顺序写
     broker的负载均衡基于zookeeper

1.写入测试

     启动zookeeper
     修改zkconfig.properties configure.properties
     gradle dist 打包
     解压缩zip包到任意目录
     运行bin/broker.sh 启动broker
     客户端运行MessageProducerTest.java 

硬件 

     mac os 5400 SATA硬盘 I5 2.4Ghz 8GB内存
     windows 7 5400 SATA硬盘 I5 2.6GHz 8GB内存
     centos 6 Linux 2.6.32 16G 8核
     
写入速度

     tcp_wmem min4096 default 16384
     tcp_rmem min4096 default 87380
     消息1k   10w循环写   tps 5w/s
     Linux broker 和 mac producer。
     jvm配置8G
     
2.读取速度

    tcp_wmem min4096 default 16384
    tcp_rmem min4096 default 87380
    初步完成
    运行junit MessageConsumerTest.java
    利用nio+netty zerocopy
    broker transferTo consumer
    消费者仿照kafka方式，根据offset向broker拉取固定长度消息 
    拉取1m数据<1ms

关于写入比较(利用nio Filechannel顺序写 和 MappedByteBuffer)

     
    在mac os 下MappedByteBuffer性略高nio Filechannel 顺序写 (与可利用内存有关)
    在windows 下mapedbuffer性能好于filechannel顺序写。快10-20%
    详见MsgFileStorageTest.java和UseMappedFile.java
