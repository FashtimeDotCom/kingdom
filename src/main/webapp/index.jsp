<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>

        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-route.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-resource.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

        <script src="angularjs/app.js"></script>
        <script src="angularjs/controllers.js"></script>
        <script src="angularjs/services.js"></script>
        
         <script src="<c:url value='/js/ui-bootstrap-tpls-0.11.0.min.js' />"></script>

    </head>
    <body ng-app="myApp" ng-controller="mainCtrl">
        <h3>Angular: {{sample}}</h3>
        <a href="<c:url value="secured/dashboard.jsp"/>">Dashboard</a>
        <a href="<c:url value="login.jsp"/>">Login</a>
    </body>
</html>
