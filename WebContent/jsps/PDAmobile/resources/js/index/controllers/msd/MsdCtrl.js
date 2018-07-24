define(['app/app','common/services','service/SupportServices','service/Purc' ], function(app) {
	app.register.controller('MsdCtrl',['$scope', '$http', '$rootScope','toaster', 'Ring', 'Online','$modal','SessionService','MsdOper',function($scope, $http,$rootScope,toaster, Ring, Online,$modal,SessionService,MsdOper){
	   $scope.grid = [];
	   $scope.btnInvalid = true;	
	   $scope.ml = {};
	   /*$scope.search = function(event,barcode){//输入条码enter,查询属性，湿敏等级等属性等信息
	   	   if(event.keyCode == 13 && barcode){
	   	   	 MsdOper.get({code:barcode},function(data){
	   	   	 	
	   	   	 },function(res){
	   	   	 	
	   	   	 });
	   	   }
	   }*/
       /**
        * 确认烘烤
        */
       $scope.confirmInOven = function(){
       	 $scope.btnInvalid = false;
       	 MsdOper.confirmInOven({},$scope.ml,function(data){      	 	 
       	 	 $scope.btnInvalid = true;
             toaster.pop("success","已进入烘烤!");
             $scope.ml = {};
       	 },function(res){
       	 	 $scope.btnInvalid = true;
       	 	 toaster.pop("error",res.data.exceptionInfo);
       	 	 $scope.ml.bar_code ='';
       	 });
       }
       	 
       	 /**
       	  * 出烘烤前获取烘烤时间
       	  */
       	 $scope.getOvenTime = function(event){
       	 	if($scope.ml.bar_code && event.Keycode == 13){
       	 		MsdOper.getOvenTime({code:$scope.ml.bar_code},function(data){
       	 			$scope.ml = data.message;
       	 		},function(res){
       	 			toaster.pop("error",res.data.exceptionInfo);
       	 	        $scope.ml.bar_code ='';
       	 		});
       	 	}
       	 },
       	 /**
       	  * 出烘烤
       	  */
       	 $scope.confirmOutOven = function(){    
       	 	$scope.btnInvalid = false;
       	 	MsdOper.confirmOutOven({code:$scope.ml.bar_code},{},function(data){
       	 	   $scope.btnInvalid = true;
   			   toaster.pop("success","已出烘烤!");
   			   $scope.message = data.message;
       	 	},function(res){
	       	 	toaster.pop("error",res.data.exceptionInfo);
	       	 	$scope.ml.bar_code ='';   	 	  
       	 	});
       	  }       	 
   }]);
});