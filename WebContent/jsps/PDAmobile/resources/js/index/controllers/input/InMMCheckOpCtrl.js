define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
  app.register.controller('InMMCheckOpCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'PurcOrder', 'Ring','SupportUtil', 'Online','ngTableParams','SessionService', '$timeout', function($scope, $http, $rootScope, $location, $filter,$stateParams,$modal,toaster, PurcOrder,
          Ring,SupportUtil, Online,ngTableParams,SessionService,$timeout){  
       var getGrid = function(code) {//根据路径中的id号获取对应的单据
			var result = null;
			angular.forEach($rootScope.ordersMMCheck, function(value, key){
				if(value.pi_inoutno == code) {
					result = value.gridData;
					return result;
				}
			});
			return result;
		};	
        $scope.grid = getGrid($stateParams.code)||[];        
		$scope.tableParams = new ngTableParams({
			page : 1,
			count : 10,
			sorting : {
				'bi_pdno': 'ASC'
			},
			filter : {}
		}, {
			total: $scope.grid.length,
			getData : function($defer, params) {
				 var orderedData = params.sorting() ?
                        $filter('orderBy')($scope.grid, params.orderBy()) :
                        $scope.grid;
                orderedData = params.filter() ?
                        $filter('filter')(orderedData, params.filter()) :
                        orderedData;
                params.total(orderedData.length);
			   $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			}
		});		
		var getOrder = function(code) {//根据路径中的id号获取对应的单据
			var result = new Object();
			angular.forEach($rootScope.ordersMMCheck, function(value, key){
				if(value.pi_inoutno == code) {
					result['pi_inoutno'] = value.pi_inoutno;
					result['bi_whcode'] = value.bi_whcode;
					result['pi_id'] = value.pi_id;
					return result;
				}
			});
			return result;
		};			
		$scope.order = getOrder($stateParams.code);
		$rootScope.title = {};
		$rootScope.title.pi_inoutno =$scope.order.pi_inoutno +'   '+$scope.order.bi_whcode;
        $scope.confirmCheck =  function(){//根据输入的条码号，以及之前的单号ID,仓库，判断条码是否存在，存在其获取信息
		   PurcOrder.getBarIoCheck({},$scope.order,function(data){
			   $scope.message = data.message;
			   $scope.grid.push($scope.message);
			   $scope.tableParams.reload();
			   $scope.order.bi_barcode='';
			   document.getElementById("bi_barcode").focus();
		   },function(res){
			   	if(res.status == 0){
			   		Online.setOnline(false);//修改网络状态	
					Ring.error();
				    toaster.pop('error',"网络连接不可用，请稍后再试");
			   	}else{
			   	    toaster.pop('error', res.data.exceptionInfo);
			   	}
		   });			
		};
		$scope.returnCheck = function(){//返回
			$location.path('inMakeMaterialCheck/MM');
		};
		$scope.getSourceQty = function(){//获取原来的数据
			  editedTodo = angular.copy($scope.message.bi_inqty); 
			  angular.element(bi_inqty).removeAttr("readOnly");			  
			  angular.element(bi_inqty).removeClass("inactive");
			  angular.element(bi_inqty).addClass("active");
			  document.getElementById("bi_inqty").focus();
		};
		$scope.changeQty =  function(){
		   if(editedTodo != $scope.message.bi_inqty){
		    	PurcOrder.updateBarIoQty({id:$scope.order.pi_id},$scope.message,function(data){
		    		toaster.pop('success',"修改成功");
		    	},function(res){		    		
		    		$scope.message.bi_inqty = editedTodo ; //返回原来修改的数据
		    		if(res.status == 0){
				   		Online.setOnline(false);//修改网络状态	
						Ring.error();
					    toaster.pop('error',"网络连接不可用，请稍后再试");
				   	}else{				   						   		
				   	    toaster.pop('error', res.data.exceptionInfo);
				   	}
		    	});			    	
		    }
		    angular.element(bi_inqty).removeClass("active");
		    angular.element(bi_inqty).attr("readOnly",true);
		    angular.element(bi_inqty).addClass("inactive");
		}
      }
  ])
});

	
		
		
		
		
		
		