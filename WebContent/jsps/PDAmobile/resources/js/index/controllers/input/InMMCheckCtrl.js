define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
  app.register.controller('InMMCheckCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'PurcOrder', 'Ring','SupportUtil', 'Online','ngTableParams','SessionService','$document', 'smartySuggestor', '$window', '$timeout', function($scope, $http, $rootScope, $location, $filter,$stateParams,$modal,toaster, PurcOrder,
          Ring,SupportUtil, Online,ngTableParams,SessionService,$document, smartySuggestor, $window, $timeout){         
		if($stateParams.type == 'MM'){
			$scope.orders = $rootScope.ordersMMCheck || [];	
		}else if($stateParams.type == 'Fin'){
			$scope.orders = $rootScope.ordersFinCheck || [];	
		}		
		$rootScope.title = {};
		$scope.whcode = SessionService.getCookie('defaultWhcode');
		var again = 1;//解决请求在未返回之前重复
		$scope.tableParams = new ngTableParams({
			page : 1,
			count : 10,
			sorting : {
				enDate: 'ASC'
			},
			filter : {}
		}, {
			total: $scope.orders.length,
			getData : function($defer, params) {
				 var orderedData = params.sorting() ?
                        $filter('orderBy')($scope.orders, params.orderBy()) :
                        $scope.orders;
                orderedData = params.filter() ?
                        $filter('filter')(orderedData, params.filter()) :
                        orderedData;
                params.total(orderedData.length);
			   $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			}
		});		
	    var  getWhcode = function(code,whcode,pi_id){
	    	again = 0;
	       PurcOrder.getWhcode({inoutNo:code,whcode:whcode,condition:"pd_inqty",pi_id:pi_id},{},function(data){	
					if(data.data.length > 1 && data.data[0].PI_INOUTNO){	//在出入库单号重复时，弹出选择框，选择出入库单号，类型
							$scope.items = data.data; 
							var modalInstance = $modal.open({  
	               				 templateUrl: 'billModalContent.html',  
	                			 controller: 'ModalInstanceCtrl',  
	                			 resolve: {  
	                    			items: function () {  
	                        		 return $scope.items;  
	                     			}  
	                		 }  
           				  });  
           				  modalInstance.result.then(function(selectedItem) {
						      $scope.selected = selectedItem;
						      getWhcode($scope.selected.PI_INOUTNO,whcode,$scope.selected.PI_ID);
						    });
						}else if(data.data.length > 1 && data.data[0].PD_WHCODE){
						//出入库单中明细行仓库不一样,弹出选择框，选择仓库,选择一条明细行	
							$scope.items = data.data; 
							var modalInstance = $modal.open({  
	               				 templateUrl: 'myModalContent.html',  
	                			 controller: 'ModalInstanceCtrl',  
	                			 resolve: {  
	                    			items: function () {  
	                        		 return $scope.items;  
	                     			}  
	                		 }  
           				  });  
           				  modalInstance.result.then(function(selectedItem) {
						      $scope.selected = selectedItem;
						     // getProdcode($scope.batchCode,$scope.selected,data.data[0].PI_ID);
						      var or =  new Object();
						      or['pi_inoutno'] = $scope.batchCode;
						      or['bi_whcode'] =  $scope.selected;
						      or['pi_id'] = data.data[0].PI_ID;
						      or['gridData'] = new Array();
						      $scope.orders.push(or) ;
						      if($stateParams.type == 'MM'){
									$rootScope.ordersMMCheck = $scope.orders;
							  }else if($stateParams.type == 'Fin'){
									$rootScope.ordersFinCheck = $scope.orders;	
							  }							         
					          $scope.tableParams.reload();
					          again = 1;
					          $scope.batchCode = '';
						    }, function() {
						      //$log.info('Modal dismissed at: ' + new Date());
						    });					
						}else{		
						    var or =  new Object();
						    or['pi_inoutno'] = $scope.batchCode;
						    or['bi_whcode'] =  data.data[0].PD_WHCODE;
						    or['pi_id'] = data.data[0].PI_ID;						   
						    or['gridData'] = new Array();
						    $scope.orders.push(or) ;
						    if($stateParams.type == 'MM'){
								$rootScope.ordersMMCheck = $scope.orders;
							}else if($stateParams.type == 'Fin'){
								$rootScope.ordersFinCheck = $scope.orders;	
							}	
						    $scope.tableParams.reload();
						    again = 1;
						    $scope.batchCode = '';
						  //  getProdcode(code,data.data[0].PD_WHCODE,data.data[0].PI_ID);	//单号，仓库都是唯一时，获取需要采集的数据				
						}					
				},function (response){
					 again = 1;
					 Ring.error();
					 toaster.pop('error', '查询失败',response.data.exceptionInfo);
				});	    	
	    };
		$scope.getOrder = function(code,whcode) {//根据输入的单据id号获取下载单据信息	
			if(again == 1){
				if(SupportUtil.contains(JSON.stringify($scope.orders),code,"pi_inoutno") || SupportUtil.contains(JSON.stringify($scope.orders),code,"pi_id")){//请求的单据id或者单号已经被下载在本地了
					 Ring.error();
					 toaster.pop('error', '单据重复',"请勿重复采集");
				} else {//请求的单据id或者单号未被下载在本地了，向服务器发送请求获取数据		
						if(!whcode){
							whcode = '';
						}
						if(!code){
							alert("请输入入库单号！");
							return ;
						}	
						getWhcode(code,whcode);	//获取pi_id 或者仓库			
				}
			}
		};
		$scope.operate = function(order) {//跳转至对应的单据操作页面			
			  $scope.selectedId = order.pi_inoutno;
			  if($stateParams.type == 'MM'){
					$location.path('inMMCheckOp/' + order.pi_inoutno);	
			  }else if($stateParams.type == 'Fin'){
					$location.path('inFinishCheckOp/' + order.pi_inoutno);		
			  }	
			 	
		};		
		
		$scope.deleteDe = function (order){
			if(confirm("确认删除?")){	
			   angular.forEach($scope.orders, function(value, key){				
					if(value.pi_id == order.pi_id) {//修改状态
					  $scope.orders.splice(key,1);
					}				 
			    });
			     if($stateParams.type == 'MM'){
					$rootScope.ordersMMCheck = $scope.orders;
				}else if($stateParams.type == 'Fin'){
					$rootScope.ordersFinCheck = $scope.orders;	
				}	
			    $scope.tableParams.reload();
			    Ring.success();
			    toaster.pop('success', '删除成功');
			}
		};		
		//模糊查询
		$scope.suggestions = function(val){
			 if (val == "" || angular.isUndefined(val) || val.length < 3) {
                    return null ;
              }else{             	          	          	   
               return  smartySuggestor.getSmartySuggestions(val,'pd_inqty').then(function(res) {    
				     if(res.data != null && res.data.length > 0){
					     return $filter("limitTo")(res.data, 5).map(function(item) {
					             return item.PI_INOUTNO;
					          });					    
				       }
                  });  
             }
		};
      }
   ])
});
 