<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />

        <link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/bootstrap-responsive.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/unicorn.login.css' />" />
        
        <link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/bootstrap-responsive.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/fullcalendar.css' />" />	
        <link rel="stylesheet" href="<c:url value='/css/unicorn.main.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/unicorn.grey.css' />" class="skin-color" />

        <!-- Angular -->
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-route.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-resource.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-resource.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-cookies.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

        <script src="<c:url value='/angularjs/app.js' />"></script>
        <script src="<c:url value='/angularjs/services.js' />"></script>
        <script src="<c:url value='/angularjs/controllers.js' />"></script>

        <script src="<c:url value='/js/ui-bootstrap-tpls-0.11.0.min.js' />"></script>
        
    </head>



    <body>
        <div id="logo">
            <img src="img/logo.png" alt="" />
        </div>
        <div id="loginbox">            
            <form id="loginform" class="form-vertical" method="post">
                <p>Enter username and password to continue.</p>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-user"></i></span><input name="username" type="text" placeholder="Username" />
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-lock"></i></span><input type="password" name="password" placeholder="Password" />
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <span class="pull-left"><a href="#" class="flip-link" id="to-recover">Lost password?</a></span>
                    <span class="pull-right"><input type="submit" class="btn btn-inverse" value="Login" /></span>
                </div>
            </form>

            
            <form ng-app="myApp" ng-controller="accountCtrl" id="recoverform" ng-submit="recoverPassword()" class="form-vertical" >
                <p>Enter your e-mail address below and we will send you instructions how to recover a password.</p>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-envelope"></i></span><input type="text" placeholder="E-mail address" ng-model="email"/>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <span class="pull-left"><a href="#" class="flip-link" id="to-login">Back to login</a></span>
                    <span class="pull-right"><input type="submit" class="btn btn-inverse" value="Recover" /></span>
                </div>
            </form>
        </div>

        <script src="<c:url value='/js/jquery.min.js' />"></script>  
        <script src="<c:url value='/js/unicorn.login.js' />"></script> 
    </body>

</html>
