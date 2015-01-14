'use strict';

/* Controllers */

angular.module('myApp.controllers', [])
        .controller('mainCtrl', ['$scope', 'Resources', 'ModalService', 'DomainService', 'AccountService', function ($scope, Resources, ModalService, DomainService, AccountService) {
                $scope.sample = 'Josue';

                $scope.currentDomain = {};

                $scope.init = function () {
                    $scope.currentDomain = DomainService.getCurrentDomain();


                };

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
        .controller('menuBarCtrl', ['$scope', 'Resources', 'DomainService', 'AlertService', 'AccountService', function ($scope, Resources, DomainService, AlertService, AccountService) {
                //TODO last login date and last
                $scope.joinedDomains = [];
                $scope.currentDomain = null;

                $scope.currentAccount = null;

                $scope.init = function () {
                    $scope.getJoinedDomains(false); //lazy loading
                    $scope.currentDomain = DomainService.getCurrentDomain();

                    AccountService.initAccount();
                    $scope.currentAccount = Resources.account.get();
                    console.log('Current Account: ' + $scope.currentAccount);
                };

                $scope.getJoinedDomains = function (forceFetch) {
                    //Lazy loading, to avoid load every page reload (using cookies here)
                    if (forceFetch || DomainService.getCurrentDomain() == null) {
                        Resources.domain.queryJoined(function (response) {
                            $scope.joinedDomains = response.items;
                        });
                    }
                };

                $scope.changeDomain = function (joined) {
                    DomainService.changeDomain(joined);
                    $scope.currentDomain = DomainService.getCurrentDomain();
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

                        $scope.createAlert('success', 'Domain created');

                    },
                            function (response) {
                                $scope.createAlert('danger', response.data.message);
                            });
                };
                $scope.update = function () {
                    Resources.domain.update({uuid: $scope.ownedDomain.uuid}, $scope.ownedDomain, function (response) {
                        console.log(response);
                        $scope.getOwnedDomains();
                        $scope.ownedDomain = {};
                        $scope.ownedDivStep = 1;

                        $scope.createAlert('success', 'Domain updated');
                    }, function (response) {
                        $scope.createAlert('danger', response.data.message);
                    });
                };
                $scope.delete = function (uuid) {
                    Resources.domain.delete({uuid: uuid}, function (response) {
                        console.log(response);
                        $scope.getOwnedDomains();
                        $scope.ownedDomain = {};
                        $scope.ownedDivStep = 1;
                        $scope.createAlert('success', 'Domain deleted');
                    },
                            function (response) {
                                $scope.createAlert('danger', response.status + ': ' + response.message);
                            });
                };

                $scope.domainAlerts = [];

                $scope.createAlert = function (type, msg) {
                    var alert = AlertService.addAlert(type, msg);
                    $scope.domainAlerts.push(alert);
                    $timeout(function () {
                        $scope.domainAlerts.shift();
                    }, 3000);
                };
                $scope.closeAlert = function () {
                    $scope.domainAlerts.shift();
                };


                $scope.openDialog = function (obj) {

                    var modalInstance = $modal.open({
                        templateUrl: '../partials/delete-modal.html',
                        controller: function ($scope, $modalInstance, ownedDomain) {
                            $scope.ownedDomain = ownedDomain;
                            $scope.bodyMessage = 'Are you sure you wanto to delete Domain ' + $scope.ownedDomain.name + ' ?';

                            $scope.ok = function () {
                                $modalInstance.close($scope.ownedDomain);
                            };

                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            bodyMessage: function () {
                                return obj;
                            },
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
        .controller('apiKeyCtrl', ['$scope', '$location', 'Resources', '$rootScope', 'AlertService', 'DomainService', '$modal', '$timeout', function ($scope, $location, Resources, $rootScope, AlertService, DomainService, $modal, $timeout) {

                $scope.apiKeyStatuses = ['ACTIVE', 'INACTIVE'];

                $scope.domainApiKeys = [];
                $scope.selectedApiKey = {};
                $scope.createdCredential = null;

                $scope.currentDomain = null;

                $scope.init = function () {
                    $scope.currentDomain = DomainService.getCurrentDomain();
                    Resources.apiKey.query({domainUuid: $scope.currentDomain.domain.uuid}, function (response) {
                        $scope.domainApiKeys = response.items;
                    });
                };

                $scope.list = function () {
                    if ($scope.currentDomain == null) {
                        $scope.currentDomain = DomainService.getCurrentDomain();
                    }
                    Resources.apiKey.query({domainUuid: $scope.currentDomain.domain.uuid}, function (response) {
                        $scope.domainApiKeys = response.items;
                    });
                };

                $scope.create = function () {
                    if ($scope.currentDomain == null) {
                        $scope.currentDomain = DomainService.getCurrentDomain();
                    }
                    $scope.selectedApiKey = {name: $scope.selectedApiKey.name, credential: {}, role: $scope.selectedApiKey.role};

                    Resources.apiKey.create({domainUuid: $scope.currentDomain.domain.uuid}, $scope.selectedApiKey, function (response) {
                        $scope.list();
                        $scope.selectedApiKey = {};

                        $scope.createAlert('success', 'Domain created');

                        $scope.createdCredential = response;
                        $scope.apiKeyStep = 4;

                    },
                            function (response) {
                                $scope.createAlert('danger', response.data.message);
                            });
                };
                $scope.dismiss = function () {
                    $scope.createdCredential = null;
                    $scope.apiKeyStep = 1;
                };


                $scope.update = function () {
                    if ($scope.currentDomain == null) {
                        $scope.currentDomain = DomainService.getCurrentDomain();
                    }
                    $scope.selectedApiKey.domain = null;

                    var apiKeyToUpdate = {name: $scope.selectedApiKey.name, credential: {status: $scope.selectedApiKey.credential.status}, role: $scope.selectedApiKey.role};

                    Resources.apiKey.update({domainUuid: $scope.currentDomain.domain.uuid, uuid: $scope.selectedApiKey.uuid}, apiKeyToUpdate, function (response) {
                        $scope.list();
                        $scope.selectedApiKey = {};
                        $scope.apiKeyStep = 1;

                        $scope.createAlert('success', 'API Key updated');
                    }, function (response) {
                        $scope.createAlert('danger', response.data.message);
                    });
                };
                $scope.delete = function (uuid) {
                    if ($scope.currentDomain == null) {
                        $scope.currentDomain = DomainService.getCurrentDomain();
                    }
                    Resources.apiKey.delete({domainUuid: $scope.currentDomain.domain.uuid, uuid: uuid}, function (response) {
                        $scope.list();
                        $scope.selectedApiKey = {};
                        $scope.ownedDivStep = 1;
                        $scope.createAlert('success', 'Domain deleted');
                    },
                            function (response) {
                                $scope.createAlert('danger', response.status + ': ' + response.message);
                            });
                };


                $scope.apiKeyStep = 1; // default step
                $scope.showApiKeyList = function () {
                    $scope.apiKeyStep = 1;
                };
                $scope.showApiKeyCreateForm = function () {
                    $scope.selectedApiKey = {};
                    $scope.apiKeyStep = 2;
                };
                $scope.showApiKeyUpdateForm = function (apiKey) {
                    $scope.selectedApiKey = apiKey;
                    $scope.apiKeyStep = 3;
                };

                $scope.apiKeyAlerts = [];
                $scope.createAlert = function (type, msg) {
                    var alert = AlertService.addAlert(type, msg);
                    $scope.apiKeyAlerts.push(alert);
                    $timeout(function () {
                        $scope.apiKeyAlerts.shift();
                    }, 3000);
                };
                $scope.closeAlert = function () {
                    $scope.apiKeyAlerts.shift();
                };


                $scope.systemRoles = [];
                Resources.role.query({domainUuid: DomainService.getCurrentDomain().domain.uuid}, function (response) {
                    console.log(response);
                    $scope.systemRoles = response;
                }, function (response) {
                    alert(response.status + " - " + response.message);
                });

                $scope.openDialog = function (obj) {

                    var modalInstance = $modal.open({
                        templateUrl: '../partials/delete-modal.html',
                        controller: function ($scope, $modalInstance, selectedApiKey) {
                            $scope.selectedApiKey = selectedApiKey;
                            $scope.bodyMessage = 'Are you sure you wanto to delete API Key ' + $scope.selectedApiKey.name + ' ?';

                            $scope.ok = function () {
                                $modalInstance.close($scope.selectedApiKey);
                            };

                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                            };
                        },
                        resolve: {
                            bodyMessage: function () {
                                return obj;
                            },
                            selectedApiKey: function () {
                                return obj;
                            }
                        }
                    });
                    //When OK
                    modalInstance.result.then(function (selectedApiKey) {
                        console.log('User really want to delete...' + selectedApiKey.name);
                        $scope.delete(selectedApiKey.uuid);
                    }, function () { //when dismiss

                    });
                };

            }])
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
        .controller('accountCtrl', ['$scope', '$timeout', 'Resources', 'AlertService', '$modal', function ($scope, $timeout, Resources, AlertService, $modal) {

                $scope.email = null;

                $scope.recoverPassword = function () {
                    Resources.account.passwordRecovery({email: $scope.email}, function (response) {

                    });
                };

            }])
        .controller('invitationCtrl', ['$scope', '$timeout', 'Resources', 'AlertService', '$modal', 'DomainService', function ($scope, $timeout, Resources, AlertService, $modal, DomainService) {

                $scope.invitations = [];
                $scope.invitation = {};

                $scope.signupCredential = {};
                $scope.passwordConfirm = '';
                

                $scope.list = function () {
                    Resources.invitation.query(function (response) {
                        $scope.invitations = response.items;
                    });
                };

                $scope.create = function () {
                    var inv = {targetEmail: $scope.invitation.targetEmail,
                        domain: {uuid: $scope.invitation.domain.uuid},
                        role: {id: $scope.invitation.role.id}};

                    Resources.invitation.create(inv, function (response) {
                        $scope.list();
                        $scope.invitation = {};

                        $scope.createdInvitation = response;
                        $scope.invitationStep = 1;

                        $scope.createAlert('success', 'Domain created');

                    },
                            function (response) {
                                $scope.createAlert('danger', response.data.message);
                            });
                };

                $scope.systemRoles = [];
                Resources.role.queryAll(function (response) {
                    $scope.systemRoles = response;
                }, function (response) {
                    alert(response.status + " - " + response.message);
                });

                $scope.ownedDomains = [];
                Resources.domain.queryOwned(function (response) {
                    $scope.ownedDomains = response.items;
                }, function (response) {
                    alert(response.status + " - " + response.message);
                });


                $scope.invitationsAlerts = [];
                $scope.createAlert = function (type, msg) {
                    var alert = AlertService.addAlert(type, msg);
                    $scope.invitationsAlerts.push(alert);
                    $timeout(function () {
                        $scope.invitationsAlerts.shift();
                    }, 3000);
                };
                $scope.closeAlert = function () {
                    $scope.invitationsAlerts.shift();
                };

                $scope.invitationStep = 1; // default step
                $scope.showInvitationsList = function () {
                    $scope.invitationStep = 1;
                };
                $scope.showInvitationForm = function () {
                    $scope.invitation = {};
                    $scope.invitationStep = 2;
                };

            }])
        .controller('signupCtrl', ['$scope', '$timeout', 'Resources', 'AlertService', '$modal', 'DomainService', '$window', function ($scope, $timeout, Resources, AlertService, $modal, DomainService, $window) {

                $scope.signupCredential = {};
                $scope.passwordConfirm = '';
                
                $scope.signup = function (token) {
                    //TODO password validation
                    if($scope.signupCredential.password != $scope.passwordConfirm){
                        $scope.createAlert('danger', 'Passwords doesnt match');
                        return;
                    }
                    
                     Resources.account.create({token: token},$scope.signupCredential, function (response) {
                        $scope.signupCredential = {};

                      $window.location.href = 'login.jsp';

                    },
                            function (response) {
                                $scope.createAlert('danger', response.data.message);
                            });
                    
                };

                $scope.list = function () {
                    Resources.invitation.query(function (response) {
                        $scope.invitations = response.items;
                    });
                };
  
                $scope.signupAlerts = [];
                $scope.createAlert = function (type, msg) {
                    var alert = AlertService.addAlert(type, msg);
                    $scope.signupAlerts.push(alert);
                    $timeout(function () {
                        $scope.signupAlerts.shift();
                    }, 3000);
                };
                $scope.closeAlert = function () {
                    $scope.signupAlerts.shift();
                };

                $scope.invitationStep = 1; // default step
                $scope.showInvitationsList = function () {
                    $scope.invitationStep = 1;
                };
                $scope.showInvitationForm = function () {
                    $scope.invitation = {};
                    $scope.invitationStep = 2;
                };

            }]);

