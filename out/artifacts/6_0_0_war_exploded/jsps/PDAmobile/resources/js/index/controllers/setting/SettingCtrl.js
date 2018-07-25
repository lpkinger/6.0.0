define([ 'app/app','service/Purc','service/SupportServices','common/services' ], function(app) {
	app.register.controller('SettingCtrl', ['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster','SupportUtil', 'Ring', 'Online','ngTableParams','SessionService','$q','Print',function($scope, $http, $rootScope, $location, 
	      $filter,$stateParams,$modal,toaster, SupportUtil, Ring, Online,ngTableParams,SessionService,$q,Print){	  
	    $scope.setDefaultWhcode = function (){//设置默认仓库，打印IP
	      $scope.item = {};	  
	  	  $scope.item.defaultWhcode = SessionService.getCookie('defaultWhcode');
		   var modalInstance = $modal.open({  
		         templateUrl: 'myModalContent.html',  
		         controller: 'ModalInstanceCtrl',  
		          resolve: { 
		          	 items: function () {  
	                         return $scope.item;  
	                   }
		              }  
	           });  
	           modalInstance.result.then(function(items) {
					  SessionService.setCookie('defaultWhcode',items.defaultWhcode);//将默认仓库存放在cookie中					 				
			});	
	  };
	  
	  $scope.setDefaultStyle =  function(){//设置默认样式
	  	$scope.item = {};	  
	  	$scope.item.defaultFontSize = SessionService.getCookie('defaultFontSize');
	  	$scope.item.defaultColor = SessionService.getCookie('defaultColor');
	  	 var modalInstance = $modal.open({  
		         templateUrl: 'myStyleContent.html',  
		         controller: 'ModalInstanceCtrl',  
		          resolve: { 
		          	 items: function () {  
	                       return $scope.item;  
	                   }
		              }  
	           });  
	           modalInstance.result.then(function(items) {
					  SessionService.setCookie('defaultFontSize',items.defaultFontSize);//将默认编辑项字体大小
					  SessionService.setCookie('defaultColor',items.defaultColor);//将默认必填项 Label颜色  勾选为必填项的字段标题显示成指定的特殊颜色
					 });	
	  };
	  var getDefaultPrint =  function(){
	  	var defer = $q.defer();
	  	Print.getDefaultPrint({},{},function(data){	 
	  	  	   defer.resolve(data.message);
	  	 },function(res){
		  	  if(res.status == 0){
		  	  		Online.setOnline(false);//修改网络状态	
					Ring.error();
				   toaster.pop('error', "网络连接不可用，请稍后再试");
		  	  } 	
	  	     defer.reject(res.data.exceptionInfo);	  	 
	  	  });
	  	   return defer.promise;
	  }
	  $scope.setDefaultPrint = function (){//设置默认仓库，打印IP,打印机分辨率
	      $scope.item = {};	  
		  getDefaultPrint().then(function(data) {  
	          $scope.item = data;
	          var modalInstance = $modal.open({  
		        templateUrl: 'myPrintContent.html',  
		        controller: 'ModalInstanceCtrl',  
		         resolve: { 
		          	items: function () {  
	                       return $scope.item;  
	                  }
		          }  
	           });  
	        modalInstance.result.then(function(items) {			
			    Print.setDefaultPrint({},items,function(data){					
					     toaster.pop('success', "设置成功!");
					  },function(res){
					     if(res.status == 0){
					     	Online.setOnline(false);//修改网络状态	
							Ring.error();
						    toaster.pop('error', "网络连接不可用，请稍后再试");
					     }else{
					  		toaster.pop('error', "设置失败",res.data.exceptionInfo);
					     }
					});					  
			 });	
		   }, function(data) {  // 处理错误 .reject  
		      toaster.pop('error', data); 
		   }); 
	  };
	}]);
});