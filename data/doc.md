
##### 1 sandbox-message
```
rocketmq 消息实践
rabbitmq 消息实践
zxing 二维码生成和条形码生成


```

##### 2 sandbox-flex
```
mybatis-flex orm 框架实践


```

##### 3 product-server
```


```


##### 5 sandbox-stock

```mysql

select id, name, amount, current, eps, float_market_capital,pb_ttm, pe_ttm, percent, high_year, low_year, dividend_yield, update_time  
from tb_stock_info where name in ("中国银行","建设银行","工商银行","农业银行")

```


```
用于编译项目中的Java源代码。它使用Java编译器将Java源代码编译成字节码，以便在Java虚拟机上运行。该插件可以在项目的pom.xml文件中配置，以指定编译器版本、编译选项等。在配置完成后，可以通过运行Maven的compile命令来执行编译操作

verbose  
这个参数会让编译器输出更多的信息，通常用于调试。当使用这个参数时，编译器会提供有关正在执行的每个步骤的详细信息 
-Xlint:all
这个参数用于启用所有编译器警告。Xlint 是一个非标准的选项，但许多 Java 编译器（如 Oracle 和 OpenJDK）都支持它。使用 -Xlint:all 会启用所有可用的编译器警告，这有助于发现代码中的潜在问题。
deprecation
这个参数会启用对 Java 代码中使用的已弃用的 API 的警告。当一个类、方法或字段被标记为 @Deprecated 时，使用这个参数可以确保代码不会意外地使用这些已弃用的部分
parameters
这个参数让编译器为方法参数生成名称。当使用这个参数时，方法参数的名称会被存储在字节码中，这样在反射时可以获取这些名称。这对于某些框架和库（如 Hibernate 和 JPA）特别有用，因为它们需要知道参数的名称



```
