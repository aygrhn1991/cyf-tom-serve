var app = angular.module('app', ['ngRoute']);
app.config(function ($routeProvider) {
    $routeProvider
        .when('/consult', {
            templateUrl: '/admin/consult',
            controller: 'consultCtrl'
        })
        .otherwise({
            redirectTo: '/consult'
        });
});
app.run(function ($rootScope, $http, $location) {
    var systemPage = ['#/welcome', '#/password'];
    var pages = [
        {id: 1, name: '加盟咨询', group_name: '加盟咨询', path: '#/consult'},
    ];
    $rootScope.getAdmin = function () {
        $http.post('/admin/getAdmin').success(function (data) {
            $rootScope.admin = data.data;
            window.Util.setCookie('admin', JSON.stringify(data.data));
            $rootScope.menu = [];
            var set = new Set();
            pages.forEach(function (x) {
                set.add(x.group_name);
            });
            Array.from(set).forEach(function (x) {
                var menu = {name: x, select: false, pages: []};
                pages.forEach(function (y) {
                    if (y.group_name == x) {
                        menu.pages.push(y);
                    }
                })
                $rootScope.menu.push(menu);
            })
            layui.use('element', function () {
                var element = layui.element;
            });
            $rootScope.matchMenu();
            $rootScope.startListener();
        });
    };
    if (!window.Util.isNull(window.Util.getCookie('admin'))) {
        $rootScope.getAdmin();
    } else {
        window.location.href = '/admin/login';
    }
    $rootScope.matchMenu = function () {
        var hasPage = false;
        var path = '#' + $location.path();
        $rootScope.menu.forEach(function (x) {
            x.select = false;
            x.pages.forEach(function (y) {
                y.select = false;
                if (y.path == path) {
                    y.select = true;
                    x.select = true;
                    hasPage = true;
                }
            })
        })
        if (!hasPage) {
            for (var i = 0; i < systemPage.length; i++) {
                if (path == systemPage[i]) {
                    return;
                }
            }
            $location.path('/unauthorized');
        }
    };
    $rootScope.menuClick = function (e) {
        $rootScope.menu.forEach(function (x) {
            x.select = false;
        });
        e.select = true;
    };
    $rootScope.logout = function () {
        window.Util.removeCookie('admin');
        window.location.href = '/admin/login';
    };
    $rootScope.startListener = function () {
        $rootScope.$on('$routeChangeStart', function (event, next, current) {
            $rootScope.matchMenu();
        });
    };
});
app.controller('consultCtrl', function ($scope, $http) {
    $scope.get = function () {
        $scope.search.loading = layer.load();
        $http.post('/admin/getConsult', $scope.search).success(function (data) {
            layer.close($scope.search.loading);
            $scope.data = data.data;
            $scope.makePage(data);
        });
    };
    $scope.delete = function (e) {
        layer.confirm('此操作将删除该信息', null, function () {
            $http.post(`/admin/deleteConsult/${e.id}`).success(function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.get();
                }
            });
        });
    };
    $scope.setState = function (e) {
        layer.confirm('此操作将更改咨询信息状态', null, function () {
            $http.post(`/admin/setConsultState/${e.id}`).success(function (data) {
                layer.msg(data.message);
                if (data.success) {
                    $scope.get();
                }
            });
        });
    };
    $scope.makePage = function (data) {
        layui.laypage.render({
            elem: 'page',
            count: data.count,
            curr: $scope.search.page,
            limit: $scope.search.limit,
            limits: [10, 20, 30, 40, 50],
            layout: ['prev', 'page', 'next', 'count', 'limit'],
            jump: function (obj, first) {
                $scope.search.page = obj.curr;
                $scope.search.limit = obj.limit;
                if (!first) {
                    $scope.get();
                }
            }
        });
    };
    $scope.pageModel = {
        id: null,
        cat_id: null,
        title: null,
        tags: null,
    };
    $scope.reset = function () {
        $scope.search = window.Util.getSearchObject();
        $scope.get();
    };
    $scope.reset();
});