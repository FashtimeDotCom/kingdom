<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Signup</title>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />

        <link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/bootstrap-responsive.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/unicorn.signup.css' />" />

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



    <body ng-app="myApp">
        <div id="logo">
            <img src="img/logo.png" alt="" />
        </div>
        <div id="signupbox" ng-controller="signupCtrl">            
            <form id="loginform" class="form-vertical" method="post" ng-submit="signup('${sessionScope.token}')">
                <p>Enter your account details.</p>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span>
                            <!-- -->
                            <input  name="email" type="text" value="${sessionScope.email}" disabled/>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span>
                            <input ng-model="signupCredential.login" name="username" type="text" placeholder="Username"/>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span>
                            <input ng-model="signupCredential.manager.firstName" name="firstName" type="text" placeholder="First name" />
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span>
                            <input ng-model="signupCredential.manager.lastName" type="text" name="password" placeholder="Last name" />
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span><input ng-model="signupCredential.password" equals="{{passwordConfirm}}" required type="password" name="password" placeholder="Password" />
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"></span>
                            <input ng-model="passwordConfirm" type="password" name="passwordConfirm" equals="{{signupCredential.password}}" required placeholder="Repeat password" />
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <span class="pull-right">
                        <input type="submit" class="btn btn-inverse" value="Signup" /></span>
                </div>

                <div>
                    <alert ng-repeat="alert in signupAlerts" type="{{alert.type}}" close="closeAlert($index)">{{alert.msg}}</alert>
                </div>
            </form>
        </div>

        <script src="<c:url value='/js/jquery.min.js' />"></script>  
        <script src="<c:url value='/js/unicorn.login.js' />"></script> 
    </body>

</html>

