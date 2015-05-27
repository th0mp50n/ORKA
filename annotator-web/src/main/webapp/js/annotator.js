/*
 * Copyright (C) 2015 DTL (${email})
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
angular.module('annotator', ['ngRoute'])
  .config(function($routeProvider) {
    $routeProvider
      .when('/annotate', {
        templateUrl: 'html/annotation-edit.html',
        controller: 'AnnotationEditCtrl'
      })
      .when('/submit', {
        templateUrl: 'html/annotation-submit.html',
        controller: 'AnnotationSubmitCtrl'
      })
      .when('/complete', {
        templateUrl: 'html/annotation-complete.html',
        controller: 'AnnotationCompleteCtrl'
      })
      .otherwise({
        redirectTo: '/annotate'
      });
  })
  
  .controller('AnnotationEditCtrl', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http) {
    $scope.annotation = window.ANNOTATION;
    
    $http.get('predicate').success(function(data, status) {
      if (data != '200') {
        console.log('failed to retrieve predicates')
      }
      $scope.predicates = data;
    });
    $scope.selectedPredicate = $scope.annotation.predicate;
    
    $scope.saveState = function() {
      $rootScope.newPredicate = $scope.selectedPredicate;
      $rootScope.comment = $scope.comment;
    };
  }])
  .controller('AnnotationSubmitCtrl', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http) {
    $scope.annotation = window.ANNOTATION;
    
    $scope.comment = $rootScope.comment;
    $scope.predicate = $rootScope.newPredicate;
    
    $scope.submit = function() {
      $http.post('annotation').success(function(data, status) {
        $rootScope.nanopubUri = data;
      })
    };
  }])
  .controller('AnnotationCompleteCtrl', ['$scope', '$rootScope', function($scope, $rootScope) {
    $scope.annotation = window.ANNOTATION;
    $scope.nanopubUri = $rootScope.nanopubUri;
  }])
// EOF
;