## Spring Security 实战教程配套代码 

## 实现JWT认证

###判断请求中是否携带token
 
 
## 使用方式

使用了 h2 数据库内存模式  启动后`ddl.sql`和`dml.sql` 会初始化数据库。 数据库中会初始化一个用户`Felordcn` 密码为`12345` 的用户。


获取 jwt token 可启动后 调用 :

```
curl -X POST \
  'http://localhost:8080/process?login_type=1' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{username:"Felordcn",password:"12345"}'
```

怎么转换为 Postman 不用我多说了  都在 [Spring Security 实战干货系列](https://www.felord.cn/categories/spring-security/) 