# MyBatis Generator
采用MyBatis Generator自动生成以下代码:

    mapper.xml
    mapper.java
    domain.java

运行如下命令:

    mvn mybatis-generator:generate -pl web  # 注意: 只需要执行子目录web的配置文件就可以了

功能说明如下:

    实现了增删改查和列表功能
    实现了翻页功能
    实现了过滤功能    

其他说明如下:
    
    还需要借助公司的Swagger2Code项目, 自动生成service和controller层的代码


参考文档:

    1. http://www.jianshu.com/p/e09d2370b796
    2. http://www.mybatis.org/generator/index.html
    3. http://mbg.cndocs.tk/index.html
    4. https://my.oschina.net/miemiedev/blog/135516
