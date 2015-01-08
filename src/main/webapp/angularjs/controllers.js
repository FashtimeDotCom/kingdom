'use strict';

/* Controllers */

angular.module('myApp.controllers', [])
        .controller('mainCtrl', ['$scope', 'Resources', 'ModalService', function ($scope, Resources, ModalService) {
                $scope.sample = 'Josue';

                angular.element(document).ready(function () {

                });

                $scope.version = function () {
                    Resources.version.get(function (response) {
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

        .controller('domainCtrl', ['$scope', '$timeout', 'Resources', 'AlertService', '$modal', function ($scope, $timeout, Resources, AlertService, $modal) {

                $scope.domainStatuses = ['ACTIVE', 'INACTIVE'];

                //Initialized within HTML
                $scope.joinedDomains = {};
                $scope.joinedDomain = {};

                $scope.ownedDomains = {};
                $scope.ownedDomain = {};
                $scope.getJoinedDomains = function () {
                    Resources.domain.queryJoined(function (response) {
                        $scope.joinedDomains = response.items;
                        console.log($scope.joinedDomains);
                        //TODO handle errors
                    });
                };

                $scope.getJoinedDomainByUuid = function (uuid) {
                    Resources.domain.getJoinedByUuid({uuid: uuid}, function (response) {
                        $scope.joinedDomain = response;
                        console.log($scope.joinedDomain);
                    });
                };

                $scope.getOwnedDomains = function () {
                    Resources.domain.queryOwned(function (response) {
                        $scope.ownedDomains = response.items;
                        console.log($scope.ownedDomains);
                    });
                };



                $scope.joinedDivStep = 1; // default step
                $scope.showJoinedDomainDetails = function (uuid) {
                    $scope.getJoinedDomainByUuid(uuid);
                    $scope.joinedDivStep = 2;
                };
                $scope.showJoinedDomainList = function () {
                    $scope.joinedDivStep = 1;
                };

                $scope.ownedDivStep = 1; // default step
                $scope.showOwnedDomainsList = function () {
                    $scope.ownedDivStep = 1;
//                    $scope.getOwnedDomains();
                };
                $scope.showOwnedDomainCreateForm = function () {
                    $scope.ownedDomain = {};
                    $scope.ownedDivStep = 2;
                };
                $scope.showOwnedDomainUpdateForm = function (selectedDomain) {
                    $scope.ownedDomain = selectedDomain;
                    $scope.ownedDivStep = 3;
                };

                $scope.create = function () {
                    Resources.domain.create($scope.ownedDomain, function (response) {
                        console.log(response);
                        $scope.getOwnedDomains();
                        $scope.ownedDomain = {};
                        $scope.ownedDivStep = 1;

                        $scope.createAlert('success','Domain created');

                    },
                    function(response){
                        $scope.createAlert('danger', response.data.message);
                    });
                };
                $scope.update = function () {
                    Resources.domain.update({uuid: $scope.ownedDomain.uuid}, $scope.ownedDomain, function (response) {
                        console.log(response);
                        $scope.getOwnedDomains();
                        $scope.ownedDomain = {};
                        $scope.ownedDivStep = 1;
                        
                        $scope.createAlert('success','Domain updated');
                    }, function(response){
                        $scope.createAlert('danger', response.data.message);
                    });
                };
                $scope.delete = function (uuid) {
                    Resources.domain.delete({uuid: uuid}, function (response) {
                        console.log(response);
                        $scope.getOwnedDomains();
                        $scope.ownedDomain = {};
                        $scope.ownedDivStep = 1;
                        $scope.createAlert('success','Domain deleted');
                    },
                    function(response){
                        $scope.createAlert('danger', response.status + ': ' + response.message);
                    });
                };
 
                $scope.domainAlerts = [];

                $scope.createAlert = function (type, msg) {
                    var alert = AlertService.addAlert(type, msg);
                    $scope.domainAlerts.push(alert);
                    $timeout(function (){
                        $scope.domainAlerts.shift();
                    }, 3000);
                };
                $scope.closeAlert = function () {
                   $scope.domainAlerts.shift();
                };
 

                $scope.open = function (obj) {

                    var modalInstance = $modal.open({
                        templateUrl: '../partials/delete-modal.html',
                        controller: function ($scope, $modalInstance, ownedDomain) {
                            $scope.ownedDomain = ownedDomain;

                            $scope.ok = function () {
                                $modalInstance.close($scope.ownedDomain);
                            };

                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            ownedDomain: function () {
                                return obj;
                            }
                        }
                    });
                    //When OK
                    modalInstance.result.then(function (domainFromModalCtrl) {
                        console.log('User really want to delete...' + domainFromModalCtrl.name);
                        $scope.delete(domainFromModalCtrl.uuid);
                    }, function () { //when dismiss

                    });
                };


            }])
        //Not used
        .controller('modalController', ['$scope', '$modal', 'Resources', function ($scope, $modal) {

                $scope.open = function (item) {

                    var modalInstance = $modal.open({
                        templateUrl: '../partials/modal.html',
                        controller: function ($scope, $modalInstance, items) {
                            $scope.items = items;
                            $scope.selected = {
                                item: $scope.items[0]
                            };

                            $scope.ok = function () {
                                $modalInstance.close($scope.selected.item);
                            };

                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            items: function () {
                                return item;
                            }
                        }
                    });
                    //When OK
                    modalInstance.result.then(function (selectedItem) {
                        $scope.selected = selectedItem;
                        console.log('SELECTED ITEM : ' + selectedItem);
                    }, function () { //when dismiss

                    });
                };
            }])
        .controller('ModalInstanceCtrl', ['$scope', '$modalInstance', 'Resources', function ($scope, $modalInstance, items) {
                $scope.items = items;
                $scope.selected = {
                    item: $scope.items[0]
                };

                $scope.ok = function () {
                    $modalInstance.close($scope.selected.item);
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };

            }])
        .controller('AlertDemoCtrl', ['$scope', '$timeout', function ($scope, $timeout) {


            }]);

