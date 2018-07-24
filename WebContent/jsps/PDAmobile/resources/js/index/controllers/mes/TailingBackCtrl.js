define(['app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('TailingBackCtrl',['$scope', '$location','toaster','SupportUtil','Ring', 'Online','SessionService','TailingBackOper',function($scope, $location,toaster, 
	   SupportUtil,Ring, Online,SessionService,TailingBackOper){
	   	 $scope.barcode = {};
	   	 $scope.enter = function(event,code){//按enter 自动获取条码剩余数量，尾料还仓，bar_place=-1线下，有剩余数
	   	 	if(event.keyCode == '13' && code){
	   	 		TailingBackOper.get({code:code},function(data){
	   	 			$scope.barcode.bar_fremain = data.message.bar_forcastremain;
	   	 			$scope.barcode.bar_location = data.message.bar_location;
	   	 			$scope.barcode.bar_prodcode = data.message.bar_prodcode;
	   	 			document.getElementById("bar_location").focus();
	   	 			Ring.success();
	   	 		},function(res){
	   	 			toaster.pop('error',res.data.exceptionInfo);
	   	 			$scope.barcode.bar_code = '';
	   	 			Ring.error();
	   	 		});
	   	 	}
	   	 };
	   	 $scope.confirmTailingBack = function(){//确认还仓
	   	 	TailingBackOper.tailingBack({},$scope.barcode,function(data){
	   	 		toaster.pop('success','还仓成功!');
	   	 	    $scope.barcode = '';
	   	 	    document.getElementById("bar_code").focus();
	   	 	    Ring.success();
	   	 	},function(res){
	   	 	     toaster.pop('error',res.data.exceptionInfo);
	   	 		 $scope.barcode.bar_location = '';
	   	 		 Ring.error();
	   	 	});
	   	 }
	   	
	   	}]);
	});