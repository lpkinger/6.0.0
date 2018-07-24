define(['app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('CheckCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil','CheckOper', 'Online','$modal','SessionService',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil,CheckOper, Online,$modal,SessionService){
	   $scope.grid = [];
	   $scope.whcode = SessionService.getCookie('defaultWhcode');
	   $scope.tableParams = new ngTableParams({//已经采集完成的列表
		        page: 1,           
		        count: 10,          
		        filter: {  },
		        sorting: {	}
		    }, {
		        total: $scope.grid.length,
		        getData: function ($defer, params) {
		            var filteredData = params.filter() ?
		                    $filter('filter')($scope.grid, params.filter()) :
		                    data;
		            var orderedData = params.sorting() ?
		                    $filter('orderBy')(filteredData, params.orderBy()) :
		                    data;	
		            params.total(orderedData.length); // set total for recalc pagination
		            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		        }
		    });		
		$scope.search = function (event){
	      if(event.keyCode == 13 && $scope.prodcode && $scope.whcode){
	      	 $scope.checkMM($scope.prodcode);
	      }else if(event.keyCode == 13 && $scope.prodcode && !$scope.whcode){
	      	document.getElementById("whcode").focus();
	      }else if(event.keyCode == 13){
	      	document.getElementById("prodcode").focus();
	      }
	    }
	    $scope.checkMM = function(prodcode,whcode){
	    	if(prodcode){//列出该prodcode的barcode信息根据仓库、储位分组汇总结果
		    	CheckOper.checkMM({pr_code:prodcode,wh_code:whcode},{},function(data){
		    		$scope.grid = data.target; 
		    		$scope.tableParams.reload();
		    		$scope.pr_detail = $scope.grid[0]['pr_detail'];
		    		$scope.pr_spec = $scope.grid[0]['pr_spec'];
		    		//$scope.prodcode = '';
		    		//document.get = ElementById("prodcode").focus();
		    	},function(res){
		    		Ring.error();
		    	    toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    	})
	    	}
	    };		
		 $scope.showMMDetail =  function(item){//点击表格中的一行显示明细数据，供应商，批号，出厂日期之类的
		    var data = {'pr_code':item.bar_prodcode,'wh_code':item.bar_whcode,'bar_location':(item.bar_location!=null)? item.bar_location:' '};
		    CheckOper.checkMMDetail({data:JSON.stringify(data)},{},function(data){
		    	$scope.items = data.target; 
		    	$scope.items.pr_code = item.bar_prodcode;
		    	$scope.items.wh_code = item.bar_whcode;
		    	$scope.items.location = item.bar_location;
		    	$scope.tableParams.reload();
		    	var modalInstance = $modal.open({  
	               	templateUrl: 'detailModalMM.html',  
	                controller: 'ModalInstanceCtrl',  
	                resolve: {  
	                   items: function () {  
	                         return $scope.items;  
	                   }	                     			
	                }  
           		});  	
		    },function(res){
		    	    Ring.error();
		    	   toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    })
		  }	
		 //barcode条码库存
	    $scope.searchBarcode = function (event){
	       if(event.keyCode == 13 && $scope.barcode && $scope.whcode){
	      	  $scope.checkBarcode($scope.barcode);
	       }else if(event.keyCode == 13 && $scope.barcode && !$scope.whcode){
	      	  document.getElementById("whcode").focus();
	       }else if(event.keyCode == 13){
	          document.getElementById("barcode").focus();
	       }
	    };
	     $scope.checkBarcode = function(barcode,whcode){
	    	if(barcode){//列出该prodcode的barcode信息根据仓库、储位分组汇总结果
		    	CheckOper.checkBarcode({barcode:barcode,wh_code:whcode},{},function(data){
		    		$scope.barData = data.data; 
		    	},function(res){
		    		Ring.error();
		    	    toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    	});
	    	}
	    };	
	    //包装箱号信息核查
	    $scope.searchPackage  =  function (event){
	       if(event.keyCode == 13 && $scope.outboxCode ){
	      	  $scope.checkOutboxCode($scope.outboxCode);
	       }else if(event.keyCode == 13){
	          document.getElementById("outboxCode").focus();
	       }
	    };
	    
	    $scope.checkOutboxCode =  function(outboxCode){//根据boxcode关联package 表信息
	    	CheckOper.checkPackage({outboxCode:outboxCode},{},function(data){
		    		$scope.outBoxData = data.data; 
		    	},function(res){
		    		Ring.error();
		    	    toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    	});
	      };
	    //工单完工品核查
	    $scope.searchMakecode =  function (event){
	    	if(event.keyCode == 13 && $scope.makeCode ){
	      	  $scope.checkMakeCode($scope.makeCode);
	       }else if(event.keyCode == 13){
	          document.getElementById("makeCode").focus();
	       }
	    };
	    
	    $scope.checkMakeCode =  function (makeCode){//根据makecode从makeserial关联ms_code，再从barcode表关联barcode
	    	CheckOper.checkMakeFin({makeCode:makeCode},{},function(data){
		    		$scope.grid = data.target; 
		    		$scope.tableParams.reload();
		    		$scope.code = $scope.grid[0]["ma_code"];
		    		$scope.makeCode = '';
		    		document.getElementById("makeCode").focus();
		    	},function(res){
		    		document.getElementById("makeCode").select();
		    		 Ring.error();
		    	    toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    });
	    }
	    //订单完工品核查
	    $scope.searchSaleCode =  function (event){
	    	if(event.keyCode == 13 && $scope.saleCode ){
	      	  $scope.checkSaleCode($scope.saleCode);
	       }else if(event.keyCode == 13){
	          document.getElementById("saleCode").focus();
	       }
	    };
	    
	    $scope.checkSaleCode =  function (saleCode){//根据makecode从makeserial关联ms_code，再从barcode表关联barcode
	    	CheckOper.checkOrderFin({saleCode:saleCode},{},function(data){
		    		$scope.grid = data.target; 
		    		$scope.tableParams.reload();
		    		$scope.code = $scope.grid[0]["ma_salecode"];
		    		$scope.saleCode = '';
		    	},function(res){
		    		Ring.error();
		    	    toaster.pop('error', '查询失败',res.data.exceptionInfo);
		    });
	    }
   }]);
});