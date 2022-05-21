## Spring Security 实战教程配套代码 

##动态权限配置
 
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


## 测试动态权限

DEMO 用户 `Felordcn` 自带 `ROLE_ADMIN`、`ROLE_APP` 角色。

通过修改 `queryRoleByPattern` 方法硬编码的 角色来模拟动态权限

你也可以自行实现为数据库进行模拟。


## 注意

凡是 DEMO 中 带 `//todo` 注释的都是非常重要的知识点和关键点。