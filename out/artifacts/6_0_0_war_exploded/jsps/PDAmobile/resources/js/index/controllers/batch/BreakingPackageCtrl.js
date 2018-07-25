define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
    app.register.controller('BreakingPackageCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'BatchOper','ngTableParams','toaster', 'Ring', 'SupportUtil', 'Online','$modal',function($scope, $http, $stateParams, $rootScope,$filter,$location, BatchOper,ngTableParams,toaster, Ring, SupportUtil, Online,$modal){	    
	    $scope.grid = [];
	    $scope.barcode = {};
	    $scope.bar = {};
	    $scope.bar.new_qty = 0;
	    $scope.bar.new_packageqty = 0;
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
		  
		  $scope.search =  function(){//根据外箱号，内箱号，或者序列号获取相关数据
		  	if(!$scope.barcode.inner){
		  		 Ring.error();
		  		alert("请选择拆分内箱号或者序列号！");
		  		return ;
		  	}
		  	if($scope.barcode.inner == 'inner' && !$scope.barcode.innerBox){
		  	    Ring.error();
		  		alert("请输入拆分内箱号！");
		  		return ;
		  	}else if($scope.barcode.inner == 'serial' && !$scope.barcode.innerSerial){
		  		 Ring.error();
		  		alert("请输入拆分序列号！");
		  		return ;
		  	}
		  	if($scope.grid.length > 0){
		  		if($scope.barcode.or_outbox != $scope.grid[0].pa_outboxcode){
		  			 Ring.error();
		  			alert("只能对同一外箱号进行拆分操作！");
		  			return ;
		  		}
		  		if($scope.barcode.inner == 'serial' && $scope.barcode.innerSerial){
		  			if(SupportUtil.contains(JSON.stringify($scope.grid),$scope.barcode.innerSerial,"pd_barcode")){
		  				Ring.error();
		  				alert("拆分序列号重复！");
		  				return ;
		  			}
		  		}else if($scope.barcode.inner == 'inner' && $scope.barcode.innerBox){
		  			if(SupportUtil.contains(JSON.stringify($scope.grid),$scope.barcode.innerBox,"pd_outboxcode")){
		  				Ring.error();
		  				alert("拆分箱号重复！");
		  				return ;
		  			}
		  		}  			
		  	  }
		  	BatchOper.searchPackageData({data:$scope.barcode},{},function(data){		  	    	 
		  	   $scope.grid.push(data.data); 
		  	   $scope.bar.new_qty ++;				  	   //件数
		  	   $scope.bar.newOr_qty = data.data.pa_totalqty-$scope.bar.new_qty;//件数
		  	   $scope.packageqty  = data.data.pd_innerqty;//箱内数量
		  	   $scope.bar.new_packageqty += $scope.packageqty;	//新箱内总数	
		  	   $scope.bar.newOr_packageqty = data.data.pa_packageqty-$scope.bar.new_packageqty;//原箱总数
		  	   $scope.tableParams.reload();
		  	},function(response){
		  		if(response.status == 0) {//无网络错误
					Online.setOnline(false);//修改网络状态，离线
		  		} else{
			  		Ring.error();
			  		toaster.pop('error',response.data.exceptionInfo);
		  		}
		  	});
		  };
		  $scope.breakingoutBox = function(){//确认拆分
		  	BatchOper.breakingPackage({param:$scope.bar},angular.fromJson($scope.grid),function(data){
		  	 $scope.bar.new_qty =  $scope.bar.new_packageqty = 0;
		  	 modal(data.message);
		  	  Ring.success();
		  	 toaster.pop('success',"拆分成功");
		  	},function(response){
		  		if(response.status == 0) {//无网络错误
					Online.setOnline(false);//修改网络状态，离线
		  		}
		  	});
		  }
	   
		  $scope.getoutbox1 = function (){//获取原始拆分 新箱号
		  	BatchOper.getOutboxCode({pr_code:$scope.grid[0].pa_prodcode},{},function(data){
		  	  $scope.bar.newOr_outbox = data.data;
		  	},function(response){});
		  };
		  $scope.getoutbox2 = function (){//获取拆分箱号
		  	BatchOper.getOutboxCode({pr_code:$scope.grid[0].pa_prodcode},{},function(data){
		  	  $scope.bar.new_outbox = data.data;		  	  
		  	},function(response){});
		  }
	     $scope.deleteB = function(item){//删除明细
	     	angular.forEach($scope.grid, function(value,key){
	     	   if(item.pd_innerboxcode == value.pd_innerboxcode){
	     	   	 $scope.grid.splice(key,1);
	     	   }
	     	});
	     };
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
	    };
	}])
});