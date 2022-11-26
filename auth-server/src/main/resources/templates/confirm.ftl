<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>授权页面</title>
</head>
<body>

<div id="content-container">
    <form name="f" action="/oauth/authorize" method="post">
        <div class="col-xs-4 col-md-offset-4">
            <div class="panel  ">
                <div class="panel-heading">
                    <h3 class="panel-title">用户授权</h3>
                </div>
                <div class="panel-body ">
                    <#list scopes?keys as scope>
                        <div class="input-group mar-btm " >
                            <input type="text" class="form-control" disabled value="${scope}">
                            <span class="input-group-addon">
                                <label class="form-checkbox form-icon active">
                                    <input type="checkbox" name="${scope}" >
                                </label>
                        </span>
                        </div>
                    </#list>

                    <div class="form-group ">
                        <label class="col-md-11 control-label">确认用户勾选授权:</label>
                        <div >
                            <label class="form-checkbox form-icon active">
                                <input type="checkbox" name="user_oauth_approval" checked="false">
                            </label>
                        </div>
                    </div>
                    <button  class="btn btn-primary col-xs-2 col-md-offset-5" type="submit">确认</button>
                </div>
            </div>
        </div>
    </form>
</div>

</body>
</html>