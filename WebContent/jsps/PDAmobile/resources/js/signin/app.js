define([ 'toaster', 'services' ], function() {
	'use strict';
	var app = angular.module('myApp', [ 'toaster', 'common.services' ]);
	app.init = function() {
		angular.bootstrap(document, [ 'myApp' ]);
	};
	app.controller('AuthCtrl', ['$scope', '$window', '$location', 'toaster', 'AuthenticationService','BaseService','SessionService',function($scope, $window, $location, toaster, AuthenticationService,BaseService,SessionService) {
		$scope.loading = true;
		$scope.user = {
			j_username : SessionService.getCookie('PDA_USERNAME'),
			j_password : "",
			remember_me : true,
			master:""
		};
		  AuthenticationService.getMasters().success(function(responseText,status){//获取帐套
		  	$scope.loading = false;
		  	if(responseText.masters){
			  	$scope.masters =  responseText.masters;
			  	$scope.user.master = $scope.masters[0].ma_name;
		  	}
		  });				
		$scope.login = function(user, _url) {	//登录		
			if($scope.user.j_username == '' ||$scope.user.j_password == ''){
				alert("还有必填项没有填写！");
				return ;
			}else{
				$scope.loading = true;
				AuthenticationService.login(user).success(function(responseText, status) {		
					if(!responseText.success) {
					  $scope.loading = false;
					  toaster.pop('error', '登录失败', responseText);
					}else if(responseText.success){
						if(user.remember_me)
							SessionService.setCookie('PDA_USERNAME', user.j_username);
						else
							SessionService.removeCookie('PDA_USERNAME');
						var  rootPath= BaseService.getRootPath();	
						$window.location.href = rootPath+'/jsps/PDAmobile/index.html';
					}
				}).error(function(responseText) {
					$scope.loading = false;
					toaster.pop('error', '登录失败', responseText.data.exceptionInfo || '用户名或密码错误');
				});
			}
		};
		
		$scope.enter =  function(event){
			if(event.keyCode == 13)
			   document.getElementById("j_password").focus();
		}
		var decodeUrl = function(url) {
			url = unescape(url.replace(/\$2F/g, '%2F').replace(/@/g, '#'));
			if(url.indexOf('http:') == -1 && url.indexOf('https:') == -1) {
				url = AuthenticationService.root() + '/' + url;
			}
			return url;
		};
		var loginAndRedirect = function() {
			console.log('fadsfds'+$location.path());
			var path = $location.path();
			if(path) {
				var params = path.split('/'), _params = [];
				angular.forEach(params, function(param){
					param && _params.push(param);
				});
				if(_params.length == 4 && _params[0] == 'redirect') {
					$scope.login({
						j_username: _params[1],
						j_password: _params[2]
					}, decodeUrl(_params[3]));
				}
			}
		};
		loginAndRedirect();
	}]);
	return app;
});