<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>登录页面</title>
    <link rel="stylesheet" type="text/css" href="static/css/login.css">
</head>
<body>

  <div id="login">
      <h1>Login</h1>

      <form method="post" action="/login">
          <input type="text" required="required" name="username" placeholder="用户名">
          <input type="password" required="required" name="password" placeholder="密码">

          <button class="but" type="submit">登录</button>


      </form>


  </div>


</body>
</html>