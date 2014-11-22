var app = angular.module('configApp', []);

app.factory("model", function ($http) {
  var getConfig = function () {
    return $http.get("/config");
  };

  var getEnv = function () {
    return $http.get("/env");
  };

  return {
    getConfig: getConfig,
    getEnv: getEnv
  };
});

app.controller('configController', function ($scope, $log, $interval, model) {
  $scope.getEnv = function () {
    model.getEnv().then(function (response) {
      $scope.env = response.data;
    }, function (error) {
      $log.debug(error);
      $scope.env = "N/A";
    });
  };

  $scope.refreshConfig = function () {
    model.getConfig().then(function (response) {
      if (response.status === 204) {
        $scope.message = "No config found";
        $scope.config = "";
      }
      else {
        $scope.config = response.data;
        $scope.message = "";
      }
    }, function (error) {
      $scope.message = "An error occurred loading config from Zookeeper";
      $log.debug(error);
      $scope.config = "";
    });
  };

  $scope.getEnv();
  $scope.refreshConfig();

  var stop = $interval(function () {
    $scope.refreshConfig();
  }, 5000);

  $scope.$on('$destroy', function () {
    $interval.cancel(stop);
  });

});

