'use strict';

/* Controllers */

angular.module('myApp.controllers', [])
        .controller('mainCtrl', ['$scope', 'Resources', function ($scope, Resources) {
                $scope.sample = 'Josue';

                angular.element(document).ready(function () {
//                    $scope.version();
                });

                $scope.version = function () {
                    Resources.version.get(function (response) {
                        console.log(response.version);
                        console.log(response.version);
                    }, function (errorResp) {
                        alert('Error: ' + errorResp.status);
                    });
                };

            }])
        .controller('menuCtrl', ['$scope', '$location', function ($scope, $location, Resources) {
                $scope.menu = 'Josue';

                $scope.getMenuClass = function (path) {
                    if ($location.path().substr(0, path.length) == path) {
                        return "active";
                    } else {
                        return "";
                    }
                };
                $scope.getSubMenuClass = function (path) {
                    if ($location.path().substr(0, path.length).indexOf(path) > -1) {
                        return "submenu active";
                    } else {
                        return "submenu";
                    }
                };

            }])
        .controller('domainCtrl', ['$scope', '$location', function ($scope, $location, Resources) {


            }]);

