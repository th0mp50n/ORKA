angular.module('redux', ['ngRoute', 'ui.bootstrap'])
  .config(function($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'html/home.html',
        showBtn: true,
        reqLogin: false
      })
      .when('/about', {
        templateUrl: 'html/about.html',
        showBtn: true,
        reqLogin: false
      })
      .when('/annotate/edit', {
        templateUrl: 'html/annotate-edit.html',
        controller: 'AnnotateEditCtrl',
        showBtn: false,
        reqLogin: true
      })
      .when('/annotate/review', {
        templateUrl: 'html/annotate-review.html',
        controller: 'AnnotateReviewCtrl',
        showBtn: false,
        reqLogin: true
      })
      .when('/annotate/complete', {
        templateUrl: 'html/annotate-complete.html',
        controller: 'AnnotateCompleteCtrl',
        showBtn: false,
        reqLogin: true
      })
      .otherwise({
        redirectTo: '/'
      });
  })
  // https://stackoverflow.com/questions/14681654/i-need-two-instances-of-angularjs-http-service-or-what
  .config(function($httpProvider) {
    $httpProvider.interceptors.push(function($q, $injector) {
      return {
        responseError: function(rejection) {
          if (rejection.status == 403) {
            $injector.invoke(function($modal) {
              $modal.open({
                templateUrl: 'html/login-modal.html',
                controller: 'LoginModalCtrl',
                backdrop: 'static'
              });
            });
          }
          return $q.reject(rejection);
        }
      };
    });
  })
  .factory('labelStuff', function() {
    return function(label, target, $scope) {
      if (label.length > 30) {
        $scope[target] = {
          full: label,
          short: label.substr(0, 30).trim() + '...'
        };
      } else {
        $scope[target] = {
          short: label
        };
      }
    };
  })
  .run(function($http, $rootScope) {
    console.log('executing run method');
    
    $rootScope.$on('$routeChangeStart', function(event, next, current) {
      if (next.reqLogin && !$rootScope.user) {
        $http.get('session/user').success(function(data, status) {
          $rootScope.user = data;
          console.log('user retrieved');
        }); 
      }
    });
  })
  .controller('NavbarCtrl', function($scope, $rootScope, $location, $http) {
    $scope.$on('$routeChangeStart', function(event, next, current) {
      $scope.showEditReturn = $rootScope.user && $rootScope.metadata && next.showBtn;
    });
    
    $scope.logout = function() {
      // TODO create service for logout
      $http.get('logout').success(function(data, status) {
        console.log('logout status: ' + status);
        
        $rootScope.user = undefined;
        $rootScope.metadata = undefined;
        
        $location.path('/');
      });
    };
  })
  .controller('LoginModalCtrl', function($scope, $location) {
    $scope.current = encodeURIComponent($location.path());
  })
  .controller('AnnotateEditCtrl', function($scope, $rootScope, $http, $location, labelStuff) {
    $http.get('predicates').success(function(data, status) {
      $scope.predicates = data;
    }).error(function(data, status) {
      console.log(status + '|failed to retrieved predicates');
    });
    
    $http.get('session/metadata').success(function(data, status) {
      $rootScope.metadata = data;
      labelStuff(data.subject, 'subject', $scope);
      //labelStuff(data.predicate, 'predicate', $scope);
      $scope.predicate = data.predicate;
      labelStuff(data.object, 'object', $scope);
    });
    
    $scope.next = function() {
      $rootScope.predicate = $scope.predicate;
      $rootScope.narrative = $scope.narrative;
      $location.path("/annotate/review");
    };
  })
  .controller('AnnotateReviewCtrl', function($scope, $rootScope, $http, $location, labelStuff) {
    $scope.predicate = $rootScope.predicate;
    $scope.narrative = $rootScope.narrative;
    
    labelStuff($rootScope.metadata.subject, 'subject', $scope);
    labelStuff($scope.predicate, 'predicate', $scope);
    labelStuff($rootScope.metadata.object, 'object', $scope);
    
    $scope.submit = function() {
      var $promise = $http.post('annotation', {
        predicate: $scope.predicate.short,
        narrative: $scope.narrative
      }).success(function(data, status) {
        $rootScope.nanopub = data.nplocation;
        $location.path("/annotate/complete");
      }).error(function(data, status) {
        $location.search('error', 'could not submit nanopub, error ' + status);
      });
    };
  })
  .controller('AnnotateCompleteCtrl', function($scope, $rootScope, $http) {
    $scope.$on('$routeChangeStart', function(event, next, current) {
      if (current.originalPath == '/annotate/complete' && next.originalPath == '/annotate/review') {
        event.preventDefault();
        $scope.showWarning = true;
      }
    });
    
    $scope.nanopub = $rootScope.nanopub;
    console.log($scope.nanopub);
    
    $scope.loadNanopub = function() {
      $http.get($scope.nanopub).success(function(data, status) {
        $scope.nanopubRaw = data;
      }).error(function(data, status) {
        $scope.nanopubRaw = 'Could not load nanopub';
      });
    };
    
    $scope.sourceLocation = $rootScope.metadata.returnUri;
    $scope.returnLabel = $rootScope.metadata.returnLabel;
    
    $scope.returnToSource = function() {
      if ($scope.logoutOnReturn) {
        // TODO create service for logout
        $http.get('logout').success(function(data, status) {
          console.log('logout on return successful');
        });
      }
    };
  })
;//EOF