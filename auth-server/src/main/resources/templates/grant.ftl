<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>应用授权</title>
</head>
<style>

    html {
        padding: 0px;
        margin: 0px;
    }

    .title {
        background-color: #007BFF;
        height: 50px;
        padding-left: 20%;
        padding-right: 20%;
        color: white;
        line-height: 50px;
        font-size: 18px;
    }

    .title-left {
        float: right;
    }

    .title-right {
        float: left;
    }

    .title-left a {
        color: white;
    }

    .container {
        clear: both;
        text-align: center;
    }

    .btn {
        width: 350px;
        height: 35px;
        line-height: 35px;
        cursor: pointer;
        margin-top: 20px;
        border-radius: 3px;
        background-color: #007BFF;
        color: white;
        border: none;
        font-size: 15px;
    }
</style>
<body style="margin: 0px">
<div class="title">
    <div class="title-right">OAUTH 授权</div>
    <div class="title-left">
        <a href="#help">帮助</a>
    </div>
</div>
<div class="container">
        <h3 > ${clientId} 请求授权，该应用将获取你的以下信息</h3>

        <#list scopes as ele>
            <block >
                <span >${ele}</span>&nbsp;
            </block>
        </#list>


        授权后表明你已同意 <a href="#boot" style="color: #007BFF">OAUTH 服务协议</a>
        <form method="post" action="/oauth/authorize">
            <input type="hidden" name="user_oauth_approval" value="true">

            <#list scopes as ele>
                <div >
                    <input type="radio" name="'scope.'+${ele}" value="true" hidden="hidden" checked="checked"/>
                </div>
            </#list>

            <button class="btn" type="submit"> 同意/授权</button>
        </form>
</div>
</body>
</html>