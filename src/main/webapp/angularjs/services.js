'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('myApp.services', ['ngResource'])
        .factory('Resources', ['$resource', function ($resource) {

                var Resources = function () {
                    var _private = this;
                    var app = '/credential-manager/api';

                    _private.version = $resource(app + '/version', {}, {
                        get: {
                            method: 'GET',
                            isArray: false
                        }
                    });

                    _private.account = $resource(app + '/account', {}, {
                        get: {
                            method: 'GET',
                            isArray: false
                        }
                    });

                    _private.domain = $resource(app + '/domains', {}, {
                        queryJoined: {
                            url: app + '/domains/joined',
                            method: 'GET',
                            isArray: false
                        },
                        getJoinedByUuid: {
                            url: app + '/domains/joined/:uuid',
                            method: 'GET',
                            isArray: false
                        },
                        queryOwned: {
                            url: app + '/domains/owned',
                            method: 'GET',
                            isArray: false
                        },
                        update: {
                            method: 'PUT',
                            url: app + '/domains/:uuid'
                        },
                        create: {method: 'POST'},
                        delete: {
                            method: 'DELETE',
                            url: app + '/domains/:uuid'
                        }
                    });

                    _private.apiKey = $resource(app + '/domains/:domainUuid/credentials/apikeys/:uuid', {}, {
                        query: {
                            method: 'GET',
                            isArray: false
                        },
                        get: {
                            method: 'GET',
                            isArray: false
                        },
                        update: {method: 'PUT'},
                        create: {method: 'POST'},
                        delete: {method: 'DELETE'}
                    });

                    _private.role = $resource(app + '/domains/:domainUuid/roles', {}, {
                        query: {
                            method: 'GET',
                            isArray: true
                        }
                        //TODO how to create new Roles for system ?
                    });

                };

                return new Resources();
            }])
        .service('ModalService', ['$modal', function ($modal) {

                var modalDefaults = {
                    backdrop: true,
                    keyboard: true,
                    modalFade: true,
                    templateUrl: '../partials/modal.html'
                };

                var modalOptions = {
                    closeButtonText: 'Close',
                    actionButtonText: 'OK',
                    headerText: 'Proceed?',
                    bodyText: 'Perform this action?'
                };

                this.showModal = function (customModalDefaults, customModalOptions) {
                    if (!customModalDefaults)
                        customModalDefaults = {};
                    customModalDefaults.backdrop = 'static';
                    return this.show(customModalDefaults, customModalOptions);
                };

                this.show = function (customModalDefaults, customModalOptions) {
                    //Create temp objects to work with since we're in a singleton service
                    var tempModalDefaults = {};
                    var tempModalOptions = {};

                    //Map angular-ui modal custom defaults to modal defaults defined in service
                    angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

                    //Map modal.html $scope custom properties to defaults defined in service
                    angular.extend(tempModalOptions, modalOptions, customModalOptions);

                    if (!tempModalDefaults.controller) {
                        tempModalDefaults.controller = function ($scope, $modalInstance) {
                            $scope.modalOptions = tempModalOptions;
                            $scope.modalOptions.ok = function (result) {
                                $modalInstance.close(result);
                            };
                            $scope.modalOptions.close = function (result) {
                                $modalInstance.dismiss('cancel');
                            };
                        };
                    }

                    return $modal.open(tempModalDefaults).result;
                };

            }])
        .service('AlertService', ['$timeout', function ($timeout) {

//                var alerts = [
//                    {type: 'danger', msg: 'Oh snap! Change a few things up and try submitting again.'},
//                    {type: 'success', msg: 'Well done! You successfully read this important alert message.'}
//                ];

                this.addAlert = function (type, msg) {
                    msg = msg == null ? 'Success' : msg;
                    return {type: type, msg: msg};
                };

            }])
        .service('DomainService', ['Resources', '$cookieStore', function (Resources, $cookieStore) {

                var cookieKey = 'currentDomain';

                this.getCurrentDomain = function () {
                    var currentDomain = $cookieStore.get(cookieKey);
                    if (currentDomain == null) {
                        Resources.domain.queryJoined(function (response) {
                            $cookieStore.put(cookieKey, response.items[0]);
                            currentDomain = $cookieStore.get(cookieKey);
                        });
                    }
                    return currentDomain;
                };

                this.changeDomain = function (domain) {
                    $cookieStore.put(cookieKey, domain);
                };
            }])
        //Not used... because async, check how to
        .service('AccountService', ['Resources', '$cookieStore', function (Resources, $cookieStore) {

                var cookieKey = 'currentAccount';

                this.getCurrentAccount = function () {
                    var currentAccount = $cookieStore.get(cookieKey);
                    if (currentAccount == null) {
                        Resources.account.get(function (response) {
                            $cookieStore.put(cookieKey, response);
                            currentAccount = $cookieStore.get(cookieKey);
                            return currentAccount;
                        });
                    }
                };
                
                this.initAccount = function () {
                    $cookieStore.remove(cookieKey);
                };
            }]);

