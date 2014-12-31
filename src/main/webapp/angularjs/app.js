'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
    'ngRoute',
//  'myApp.filters',
//  'myApp.directives',
    'myApp.services',
    'myApp.controllers'
]).
        config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/main', {templateUrl: '../partials/main.html', controller: 'mainCtrl'})
                        .when('/buttons', {templateUrl: '../partials/buttons.html'})
                        .otherwise({redirectTo: '/main'});
            }]);