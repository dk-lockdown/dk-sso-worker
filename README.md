# dk-sso-worker

本服务v2版本，不依赖于spring security oauth2，实现了oauth2协议和openid协议，客户端可以通过oauth2协议进行token的获取和token的存储.



## 测试使用步骤

1. #### 执行membership.sql脚本

   ```shell
   $ mysql -uroot -p您的密码 < script/membership.sql
   ```

2. #### 修改连接字符串

 修改`dk-sso-worker/src/main/resources/META-INF/application.yaml`

```yaml
spring:
  profiles: test
#配置存放于Apollo中,请在此地址中修改
apollo:
  meta: http://172.16.76.127:8080
  bootstrap:
    enabled: true
```

#### 在测试服务时,可以通过以下两种方式进行测试:

- **密码模式**

```shell
curl -i -X POST -d "grant_type=password&client_id=simida&client_secret=123456&username=scott&password=123456&scope=all" http://localhost:3002/sso/oauth/token 
```

- **短信验证码模式**

```shell
curl -i -X POST -d "grant_type=sms&client_id=simida&client_secret=123456&mobile_number=18728868675&verify_code=123456&scope=all" http://localhost:3002/sso/oauth/token 
```

- **授权码模式**

```shell
$ curl http://localhost:3002/sso/login?client_id=simida&response_type=code&redirect_uri=http://www.baidu.com&scope=all
```

通过获取到的token,进行下一步的请求

```shell
curl -i -d "grant_type=authorization_code&code=获取到的token&client_id=simida&client_secret=123456&redirect_uri=http://www.baidu.com&scope=all" -X POST http://localhost:3002/sso/oauth/token
```

## 参照

[OAuth2](https://oauth.net/2/ )

[一张图搞定OAuth2](https://www.cnblogs.com/flashsun/p/7424071.html )
