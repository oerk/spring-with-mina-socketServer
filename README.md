# spring-with-mina-socketServer 

Spring Integration  Apache MINA Example

改写原org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory实现FixedStringCodecFactory  

实现报文格式 header+content的报文格式接收处理

例如 发送报文内容为 123456789

`  
nc  127.0.0.1  10200 <<EOF  

00000009123456789

EOF  

`

发长度9 00000009 内容 123456789

服务端decode 后最终收到 123456789

服务收到后返回 "在这里实现业务逻辑"
nc 收到的内容为 “00000010在这里实现业务逻辑“


application-mina.xml 配置
header 宽度  FixedStringCodecFactory  

` <constructor-arg index="1" value="8" />`  

编码配置  

`
<constructor-arg index="0" value="#{T(java.nio.charset.StandardCharsets).UTF_8}" />
`

日志如下

    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 新客户端连接, session open for /127.0.0.1:40886
    INFO 29644 --- [NioProcessor-98] o.a.mina.filter.logging.LoggingFilter    : OPENED
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 服务端, session open for /127.0.0.1:40886
    INFO 29644 --- [NioProcessor-98] o.a.mina.filter.logging.LoggingFilter    : RECEIVED: HeapBuffer[pos=0 lim=18 cap=2048: 30 30 30 30 30 30 30 39 31 32 33 34 35 36 37 38 39 0A...]
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 消息长度总长 len = 18
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 消息长度总长 len = 18
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 解析的请求报文长度为:9
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 丢弃剩余内容长 len = 1
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 丢弃剩余内容:
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 服务端接收到来自IP:/127.0.0.1:40886的消息:123456789
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringEncoder   : 要发送的最终内容:00000010在这里实现业务逻辑
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringEncoder   : 要发送的最终内容长度:18
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringEncoder   : 要发送的buff长度:36
    INFO 29644 --- [NioProcessor-98] o.a.mina.filter.logging.LoggingFilter    : SENT: 在这里实现业务逻辑
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 消息已发送给IP:/127.0.0.1:40886的消息:在这里实现业务逻辑
    INFO 29644 --- [NioProcessor-98] o.a.mina.filter.logging.LoggingFilter    : CLOSED
    DEBUG 29644 --- [NioProcessor-98] c.t.minasocket.impl.FixedStringDecoder   : 服务端, session closed from null




[Spring4 集成Mina](https://www.cnblogs.com/juepei/p/3940396.html)  
[spring-boot-mina](https://github.com/oncekey/spring-boot-mina.git)  
[Apache Mina物联网编程&网络通信框架研究与应用](https://mp.weixin.qq.com/s/YqGQDvi7vKyqoeSYAxSlCw)  
[Apache mina 入门(五) —— 断包，粘包问题解决](https://mp.weixin.qq.com/s/Easre973H1fbYntFi3m_AA)  