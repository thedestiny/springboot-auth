
```

引入依赖
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.24.0</version>
</dependency>

<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java-util</artifactId>
    <version>3.24.0</version>
</dependency>

# 生成 java 文件
protoc --java_out=. student.proto 

java_out
python_out 
cpp_out 
go_out 


```