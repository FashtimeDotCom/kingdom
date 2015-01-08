<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dashboard</title>

        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/bootstrap-responsive.min.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/fullcalendar.css' />" />	
        <link rel="stylesheet" href="<c:url value='/css/unicorn.main.css' />" />
        <link rel="stylesheet" href="<c:url value='/css/unicorn.grey.css' />" class="skin-color" />

        <!-- Angular -->
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-route.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.8/angular-resource.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        
        <script src="<c:url value='/angularjs/app.js' />"></script>
        <script src="<c:url value='/angularjs/services.js' />"></script>
        <script src="<c:url value='/angularjs/controllers.js' />"></script>

        <script src="<c:url value='/js/ui-bootstrap-tpls-0.11.0.min.js' />"></script>
        

    </head>


    <body ng-app="myApp">

        <div id="header">
            <h1><a href="./dashboard.html">Credential Manager</a></h1>		
        </div>

        <div id="search">
            <input type="text" placeholder="Search here..."/><button type="submit" class="tip-right" title="Search"><i class="icon-search icon-white"></i></button>
        </div>
        <div id="user-nav" class="navbar navbar-inverse">
            <ul class="nav btn-group">
                <li class="btn btn-inverse" ><a title="" href="#"><i class="icon icon-user"></i> <span class="text">Profile</span></a></li>
                <li class="btn btn-inverse dropdown" id="menu-messages"><a href="#" data-toggle="dropdown" data-target="#menu-messages" class="dropdown-toggle"><i class="icon icon-envelope"></i> <span class="text">Messages</span> <span class="label label-important">5</span> <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a class="sAdd" title="" href="#">new message</a></li>
                        <li><a class="sInbox" title="" href="#">inbox</a></li>
                        <li><a class="sOutbox" title="" href="#">outbox</a></li>
                        <li><a class="sTrash" title="" href="#">trash</a></li>
                    </ul>
                </li>
                <li class="btn btn-inverse"><a title="" href="#"><i class="icon icon-cog"></i> <span class="text">Settings</span></a></li>
                <li class="btn btn-inverse"><a title="" href="<c:url value='/logout' />"><i class="icon icon-share-alt"></i> <span class="text">Logout</span></a></li>
            </ul>
        </div>

        <div id="sidebar" ng-controller="menuCtrl">
            <a href="#" class="visible-phone"><i class="icon icon-home"></i> Dashboard</a>
            <ul> 
                <li ng-class="getMenuClass('/main')"><a href="<c:url value='#/main' />"><i class="icon icon-home"></i> <span>Dashboard</span></a></li>
                <li ng-class="getSubMenuClass('/domain')">
                    <a href="#"><i class="icon icon-th-list"></i> <span>Domain</span></a>
                    <ul>
                        <li><a href="<c:url value='#/domain-owned' />">Owned</a></li>
                        <li><a href="<c:url value='#/domain-joined' />">Joined</a></li>
                    </ul>
                </li>
                <li ng-class="getMenuClass('/buttons')"><a href="<c:url value='#/buttons' />"><i class="icon icon-tint"></i> <span>Buttons &amp; icons</span></a></li>
                <li ng-class="getMenuClass('/interface')"><a href="interface.html"><i class="icon icon-pencil"></i> <span>Interface elements</span></a></li>
                <li ng-class="getMenuClass('/tables')"><a href="tables.html"><i class="icon icon-th"></i> <span>Tables</span></a></li>
                <li ng-class="getMenuClass('/grid')"><a href="grid.html"><i class="icon icon-th-list"></i> <span>Grid Layout</span></a></li>
                <li class="submenu">
                    <a href="#"><i class="icon icon-file"></i> <span>Sample pages</span> <span class="label">4</span></a>
                    <ul>
                        <li><a href="invoice.html">Invoice</a></li>
                        <li><a href="chat.html">Support chat</a></li>
                        <li><a href="calendar.html">Calendar</a></li>
                        <li><a href="gallery.html">Gallery</a></li>
                    </ul>
                </li>
                <li ng-class="getMenuClass('/charts')">
                    <a href="charts.html"><i class="icon icon-signal"></i> <span>Charts &amp; graphs</span></a>
                </li>
                <li ng-class="getMenuClass('/widgets')">
                    <a href="widgets.html"><i class="icon icon-inbox"></i> <span>Widgets</span></a>
                </li>
            </ul>

        </div>

        <div id="content">
            <div  ng-view></div>
        </div>


        <script src="<c:url value='/js/excanvas.min.js' />"></script>
        <script src="<c:url value='/js/jquery.min.js' />"></script>
        <script src="<c:url value='/js/jquery.ui.custom.js' />"></script>
        <script src="<c:url value='/js/bootstrap.min.js' />"></script>
        <script src="<c:url value='/js/jquery.flot.min.js' />"></script>
        <script src="<c:url value='/js/jquery.flot.resize.min.js' />"></script>
        <script src="<c:url value='/js/jquery.peity.min.js' />"></script>
        <script src="<c:url value='/js/fullcalendar.min.js' />"></script>
        <script src="<c:url value='/js/unicorn.js' />"></script>
        <script src="<c:url value='/js/unicorn.dashboard.js' />"></script>
    </body>

</html>
