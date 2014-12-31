'use strict';

/* Controllers */

angular.module('myApp.controllers', [])
        .controller('mainCtrl', ['$scope', function ($scope) {
                $scope.sample = 'Josue';

            }])
        .controller('menuCtrl', ['$scope', function ($scope) {
                $scope.menu = 'Josue';

            }]);

