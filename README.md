项目名称：myfriend

此项目适合java新手，所有使用到的工具均为基础功能，前后分离项目。大神请提建议。

模块说明：

    1.myfriend-api：
        主要用于存放公共实体类，常用方法工具，数据库操作相关

    2.myfriend-http：
        基于轻量级dropwizard框架实现webserver接口

    3.myfriend-websocket：
        基于tomcat 的 websocket，用Spring Boot快速搭建。

所需环境：
        jdk1.8：这个不用说了。
        mongodb：主力数据库存储信息。
        redis： 缓存在线状态及部分token。
        rabbitMQ：模块之间的通讯工具。
        其他，，待补充。

构建工具:
        gradle
IDE:
        Intelij IDEA 2016以上

功能介绍：
    前期目标功能：完成基础的公共聊天室的实现，点对点对话，在线状态实时统计。好友状态以公共方式主动推送显示。

    第一阶段：目前正在搭建基础构架，以测试实现模块间rabbitMQ的通讯以及websocket基础页面主推呈现。

    未来目标功能：利用websocket实现好友对战游戏。例如对打乒乓球，连机飞机大战等。

    目前演示地址：http://www.maomilaoshi.cn/myprojects/chat/    目前前端页面已经舍弃PHP  这是老版。





