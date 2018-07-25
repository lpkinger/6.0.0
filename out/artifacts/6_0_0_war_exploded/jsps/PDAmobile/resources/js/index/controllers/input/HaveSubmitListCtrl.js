define(['app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('HaveSubmitListCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil', 'Online','PurcOrder',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil, Online,PurcOrder){	    
		$scope.grid = [];
		$scope.inout = $stateParams.inout;	
		$scope.pi_inoutno = $stateParams.pi_inoutno;
		PurcOrder.getHaveSubmitList({bi_piid:$stateParams.pi_id},function(data){//获取已提交的采集数据
				$scope.grid = data.message;
				$scope.tableParams = new ngTableParams({//已经采集完成的列表
			        page: 1,           
			        count: 10,          
			        filter: {  },
			        sorting: {	}
				  }, {
			        total: $scope.grid.length,
			        getData: function ($defer, params) {
			            var filteredData = params.filter() ?
			                    $filter('filter')($scope.grid, params.filter()) :$scope.grid;
			            var orderedData = params.sorting() ?
			                    $filter('orderBy')(filteredData, params.orderBy()): filteredData;	
			            params.total(orderedData.length); // set total for recalc pagination
			            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			        }
			      });		
				 Ring.success();
			},function(res){
				Ring.error();
				if(res.status == 0){//无网络
				   Online.setOnline(false);//修改网络状态
				}else{
					toaster.pop('error', res.data.exceptionInfo);
				}
			});
		   
		    $scope.deleteDes = function(s){//删除已提交的明细行，重新获取数据
		    	if(confirm("确认删除?")){
		    		s.type = $scope.inout;
			    	PurcOrder.deleteDes({},s,function(data){			   
			    	     toaster.pop('success', '删除成功');
		                 Ring.success();
		                 for (var n in $scope.grid){  
			               if($scope.grid[n].bi_barcode == s.bi_barcode){
			                      $scope.grid.splice(n,1);//JS 明细行中移除
			                 }
			              }
			              //重新获取$rootScope
			              if(data.target && data.totalCount > 0){
			              	var or = new Object();
							or = data.target;
			              	if($scope.inout == 'inMMWaitSubmit'){
			              		 angular.forEach($rootScope.orders, function(value, key) {//
									if (value.PI_INOUTNO == s.bi_inoutno) {
										$rootScope.orders.splice(key,1,or[0]);
									}
							    });		 	
			              	}else if($scope.inout == 'inFinishWaitSubmit'){
			              		angular.forEach($rootScope.fisOrders, function(value, key) {//
									if (value.PI_INOUTNO == s.bi_inoutno) {
										$rootScope.fisOrders.splice(key,1,or[0]);
									}
							    });		
			                 }else if($scope.inout == 'outMMWaitSubmit'){	
			              		angular.forEach($rootScope.outMMOrders, function(value, key) {
									if (value.PI_INOUTNO == s.bi_inoutno) {								
										$rootScope.outMMOrders.splice(key,1,or[0]);							     
									}
							    });
			               }else if($scope.inout == 'outFinWaitSubmit'){
			              		angular.forEach($rootScope.outFinOrders, function(value, key) {//
									if (value.PI_INOUTNO == s.bi_inoutno) {
										$rootScope.outFinOrders.splice(key,1,or[0]);	
									}
							    });		
			                }			             
			    	    }
			    	      $scope.tableParams.reload();	
			    	},function(res){
			    		Ring.error();
						if(res.status == 0){//无网络
						   Online.setOnline(false);//修改网络状态
						}else{
							 toaster.pop('error', res.data.exceptionInfo);
						}
			    	});
		    	}		    	
		    }		
   }]);
});