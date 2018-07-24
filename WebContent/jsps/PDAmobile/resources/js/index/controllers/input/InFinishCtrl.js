define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('InFinishCtrl', ['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'PurcOrder','SupportUtil', 'Ring', 'Online','ngTableParams','SessionService','$document', 'smartySuggestor', '$window',function($scope, $http, $rootScope, $location, 
	      $filter,$stateParams,$modal,toaster, PurcOrder,SupportUtil, Ring, Online,ngTableParams,SessionService,$document, smartySuggestor, $window){
		$scope.orders = $rootScope.fisOrders || [];
		$rootScope.title = '';
		$scope.whcode = SessionService.getCookie('defaultWhcode');
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
		  var getProdcode = function(code,whcode,pi_id){	    
			PurcOrder.get({inoutNo: code,whcode:whcode,pi_id:pi_id}, function(data) {//获取成功
				if(data.target && data.totalCount > 0){
					var or = new Object();
					or  = data.target;					
					$scope.whcode = or[0].PI_WHCODE;	
					$scope.orders.push(or[0]) ;//= data.target;
					$rootScope.fisOrders = $scope.orders;
					$scope.tableParams.reload();
					Ring.success();//提示成功响铃
					$scope.batchCode = '';//重置输入框						
					}
				}, function(response){//获取失败处理
		            toaster.pop('error', '查询失败',response.data.exceptionInfo);
		            Ring.error();
				});				  					  
	    };
	      var  getWhcode = function(code,whcode,pi_id){
	        PurcOrder.getWhcode({inoutNo:code,whcode:whcode,pi_id:pi_id,condition:"pd_inqty"},{},function(data){		 
					if(data.data.length > 1 && data.data[0].PI_INOUTNO){	//弹出选择框，选择出入库单号，类型	
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
						//弹出选择框，选择仓库	
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
						      getProdcode($scope.batchCode,$scope.selected,data.data[0].PI_ID);
						    });						
						}else{
							 getProdcode(code,data.data[0].PD_WHCODE,data.data[0].PI_ID);							
						}
				},function (response){
					    Ring.error();
					 toaster.pop('error', '查询失败',response.data.exceptionInfo);
				});	    	
	    };
	    
		$scope.getOrder = function(code,whcode) {//根据输入的单据id号获取下载单据信息	
			if(SupportUtil.contains(JSON.stringify($scope.orders),code,"PI_INOUTNO") || SupportUtil.contains(JSON.stringify($scope.orders),code,"PI_ID")){//请求的单据id或者单号已经被下载在本地了
				 Ring.error();
				 toaster.pop('error', '单据重复',"请勿重复采集");
				 return ;
			} else {//请求的单据id或者单号未被下载在本地了，向服务器发送请求获取数据
					if(!whcode){
						whcode = '';
					}
					if(!code){
						Ring.error();
						alert("请输入出库单号！");
						return ;
					}				
					getWhcode(code,whcode);	
			}
		};
		
		$scope.search = function($event, batchCode,whcode){//enter键触发事件
			if(event.keyCode == 13) {//Enter事件
				$scope.getOrder(batchCode,whcode);
			}
		};
		
		$scope.operate = function(order) {//跳转至对应的单据操作页面			
			if(! $scope.again){
				$scope.selectedId = order.PI_INOUTNO;
				$location.path('inFinishOperation/'+order.PI_INOUTNO);
			}			
			$scope.again = false;
		};
		var getClearAgain = function(order){
			 var grid = $rootScope.fisgrid;
			 angular.forEach(grid, function(value, key){
				 if(value.bi_inoutno == order.PI_INOUTNO) {//出入库单号相等删除
						$rootScope.fisgrid.splice(key,1);
				 }
			});
			 angular.forEach($scope.orders, function(value, key){				
				if(value.PI_ID == order.PI_ID) {//修改状态
					$scope.orders.splice(key,1);
				}				 
			});
			$rootScope.fisOrders = $scope.orders ;
			getWhcode(order.PI_INOUTNO,order.PI_WHCODE,order.PI_ID);
		};
		$scope.getAgain =  function (order){//重新采集
			if(confirm("确认重新采集，将会清空所有已采集数据包含已提交的数据")){	
				PurcOrder.clearGet({no:order.PI_ID,whcode:order.PI_WHCODE,type:'in'},{},function(data){
				   getClearAgain(order);
				   Ring.success();
				   toaster.pop('success', '已清空采集数据');	        
				},function(response){
					if(response.status == 0){
					   Online.setOnline(false);//修改网络状态	
					   Ring.error();
					   toaster.pop('error', '失败',"网络连接不可用，请稍后再试");
					}else if(response.data.exceptionInfo){
						var str = response.data.exceptionInfo;
						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
							str = str.replace('AFTERSUCCESS', '');	
							getClearAgain(order);
							toaster.pop('success',str);
					}else{
						Ring.error();
						toaster.pop('error', '清空失败');
					}
				  }					
				});	
			}
			$scope.again = true;
		};		
		$scope.deleteDe = function (order){
			if(confirm("确认删除?")){	
			   angular.forEach($scope.orders, function(value, key){				
					if(value.PI_ID == order.PI_ID) {//修改状态
					  $scope.orders.splice(key,1);
					}				 
			    });
			    $rootScope.fisOrders = $scope.orders ;
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
	}]);
});