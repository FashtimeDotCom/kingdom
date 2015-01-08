'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
    'ui.bootstrap',
    'ngRoute',
//  'myApp.filters',
//  'myApp.directives',
    'myApp.services',
    'myApp.controllers',
]).
        config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/main', {templateUrl: '../partials/main.html', controller: 'mainCtrl'})
                        .when('/buttons', {templateUrl: '../partials/buttons.html'})
                
                        .when('/domain-joined', {templateUrl: '../partials/domain/domain-joined.html', controller: 'domainCtrl'})
                        .when('/domain-owned', {templateUrl: '../partials/domain/domain-owned.html', controller: 'domainCtrl'})
                
                        .otherwise({redirectTo: '/main'});
            }]);