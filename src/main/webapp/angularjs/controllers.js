'use strict';

/* Controllers */

angular.module('myApp.controllers', [])
        .controller('mainCtrl', ['$scope', function ($scope) {
                $scope.sample = 'Josue';

            }])
        .controller('menuCtrl', ['$scope', '$location', function ($scope, $location) {
                $scope.menu = 'Josue';

                $scope.getClass = function (path) {
                    if ($location.path().substr(0, path.length) == path) {
                        return "active";
                    } else {
                        return "";
                    }
                }

            }]);

