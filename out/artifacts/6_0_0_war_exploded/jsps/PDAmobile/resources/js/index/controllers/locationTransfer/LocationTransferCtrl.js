define(['app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('LocationTransferCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil','LocaTransOper', 'Online','$modal','SessionService',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil,LocaTransOper, Online,$modal,SessionService){
	   $scope.grid = [];
	   $scope.whcode = SessionService.getCookie('defaultWhcode');
	   $scope.tableParams = new ngTableParams({//待转移储位批次列表
		        page: 1,           
		        count: 10,          
		        filter: {  },
		        sorting: {	}
		    }, {
		        total: $scope.grid.length,
		        getData: function ($defer, params) {
		            var filteredData = params.filter() ?
		                    $filter('filter')($scope.grid, params.filter()) :data;
		            var orderedData = params.sorting() ?
		                    $filter('orderBy')(filteredData, params.orderBy()) :data;	
		            params.total(orderedData.length); // set total for recalc pagination
		            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		        }
		});	
		
		$scope.confirm =  function (){				
			if($scope.grid.length>0){
				if($scope.grid[0].bar_whcode != $scope.whcode){
					alert("不允许操作与上次不同的仓库！");
					return ;
				}
			}
			if(!$scope.barcode && !$scope.outboxcode){
				document.getElementById("barcode").focus();
				alert("请输入条码号或者外箱号！");
                return ;				
			}else if($scope.barcode){
				if(SupportUtil.contains(JSON.stringify($scope.grid),$scope.barcode ,"bar_code")){
					alert("条码号重复!");
					return ;
				}
				LocaTransOper.get({whcode:$scope.whcode,bar_code:$scope.barcode},function(data){
					$scope.grid.push(data.data);
					$scope.barcode='';
					document.getElementById("barcode").focus();
					$scope.tableParams.reload();
				},function(res){
					toaster.pop('error',res.data.exceptionInfo);
				});
			}else if($scope.outboxcode){
				if(SupportUtil.contains($scope.grid,$scope.outboxcode ,"pa_outboxcode")){
					alert("外箱号重复!");
					return ;
				}
				LocaTransOper.get({whcode:$scope.whcode,outboxCode:$scope.outboxcode},function(data){
					$scope.grid.push(data.data);
					$scope.outboxcode='';
					document.getElementById("outboxcode").focus();
					$scope.tableParams.reload();
				},function(res){
					 Ring.error();
					toaster.pop('error',res.data.exceptionInfo);
				});
			}			
		};
		$scope.transfer = function(){
			LocaTransOper.locaTransfer({location:$scope.new_location},$scope.grid,function(data){
				Ring.success();
				toaster.pop('success',"转移成功!");
			},function(res){});
		}		    		    
   }]);
});