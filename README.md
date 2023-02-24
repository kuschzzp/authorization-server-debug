# authorization-server-debug

基于spring-authorization-server 1.0.1 的功能调试

1. 自定义登录页面、授权选择scope页面
2. 使用jdbcTemplate接入MySQL、Oracle数据库
3. 增加客户端注册的接口：/oauth2/client/register
4. 自定义往 access_token、id_token的claims体中放置数据
5. 自定义token生成规则，可以让其生成符合其他系统规则的token
6. 用户登录使用数据库中的用户信息
7. 自定义密码加密方式、主要是用在注册客户端的secret和用户的密码
8. 调试OIDC的自带注册接口 /connect/register