define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {		
	app.register.controller('CombineBatchCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'BatchOper','ngTableParams','toaster', 'Ring', 'SupportUtil', 'Online','$modal',function($scope, $http, $stateParams, $rootScope,$filter,$location, BatchOper,ngTableParams,toaster, Ring, SupportUtil, Online,$modal){	    
	    $scope.grid = [];
		$scope.whcode = $stateParams.whcode;	
		$scope.btnInvalid = true;	 
		if($scope.grid){
			 $scope.tableParams = new ngTableParams({//批次合并
			        page: 1,           
			        count: 10,          
			        filter: { },
			        sorting: { }
			    }, {
			        total: $scope.grid.length,
			        getData: function ($defer, params) {			          
			            params.total($scope.grid.length); // set total for recalc pagination
			            $defer.resolve($scope.grid.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			        }
			    });
		 };
	   $scope.confirm =  function(barcode,type){//根据输入的条码编号查询相关信息
	   	    if(SupportUtil.contains(JSON.stringify($scope.grid),barcode,"bar_code")){
		  		Ring.error();
		  		alert("条码重复！");
		  		return ;
		  	}
	    	BatchOper.get({code:barcode,pr_ismsd:false},function(data){
	    		$scope.barData = data.message.data;
	    		$scope.grid.push($scope.barData);
	    		$scope.tableParams.reload();
	    		$scope.bar_prodcode = $scope.barData.bar_prodcode
	    		$scope.pr_detail =  $scope.barData.pr_detail;
	    		$scope.barcode.or_remain = $scope.barData.bar_remain;
	    		Ring.success();
	    		$scope.barcode.or_barcode='';
	    		document.getElementById("or_barcode").focus();
	    	},function(response){
	    		Ring.error();
	    		toaster.pop('error',response.data.exceptionInfo);
	    		document.getElementById("or_barcode").focus();
	    		$scope.barcode.or_barcode='';
	    	});
	    };
	     $scope.GetTotalRemain = function () {
                var sum = 0;
                for (var i = 0; i < $scope.grid.length; i++) {
                    sum += parseFloat($scope.grid[i].bar_remain);
                }
                return sum;
            }

	    $scope.combine =  function(){//合并
	    	//合并之前进行判断物料号一致、仓库一致、储位一致
	    	if($scope.grid.length>1){
	    		//有两条或者以上的数据才进行合并
			  BatchOper.combineBatch({total_remain:$scope.GetTotalRemain()},angular.fromJson($scope.grid),function(data){
	    		modal(data.message);
	    		Ring.success();
	    		$scope.grid ='';//清空
	    		toaster.pop('success', '合并成功');		    		    		
	    	  },function(res){
	    	  	if(res.status == 0) {//无网络错误
					Online.setOnline(false);//修改网络状态					
				}
				 Ring.error();
	    		 toaster.pop('error',"合并失败",response.data.exceptionInfo);
	    	  });	
	      }	    	
	    };	    
	    //	    
	    var  modal= function(data){
	    	var modalInstance = $modal.open({  
	               templateUrl: 'myModalContent.html',  
	                controller: 'ModalInstanceCtrl',  
	                resolve: {  
	                    items: function () {  
	                        return data;  
	                     }  
	                }  
           		}); 
           	modalInstance.result.then(function(items) {
           		if(items != null){//继续分拆获取数据
           			 $scope.barcode.bar_remain='';
           			 document.getElementById("bar_remain").focus();
           		}
			  },function() {
				 $scope.barcode = $scope.pr_detail = $scope.bar_prodcode = '';//清空
           		 document.getElementById("or_barcode").focus();
			});	
	    };	    
	}]);
});