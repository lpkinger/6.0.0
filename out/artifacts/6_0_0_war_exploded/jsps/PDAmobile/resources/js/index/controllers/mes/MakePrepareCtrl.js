define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('MakePrepareCtrl',['$scope','$rootScope', '$location','$stateParams','toaster','SupportUtil','Ring', 'Online','SessionService','ngTableParams','MakePrepareOper','$filter','$modal',function($scope, $rootScope, $location,$stateParams,toaster, 
	   SupportUtil,Ring, Online,SessionService,ngTableParams,MakePrepareOper,$filter,$modal){
	   	$scope.makePrepare = $rootScope.makePrepare||{};	    
		if($stateParams.type){
		   	 $scope.type = $stateParams.type;
		   	 if($stateParams.type == 'list'){	  //获取已备料的料卷编号	
		   	 	    barcodeList();			   	 	   
			     }else if( $stateParams.type == 'getBack'){//备料采集,备料退回				 
				   	$scope.needPreparedList = [];
				   	setTimeout("document.getElementById('bar_code').focus()",200);
				   	$scope.tableParamsU = new ngTableParams({//备料清单
					        page: 1,           
					        count: 10,          
					        filter: {   },
					        sorting: {'md_location':'asc' }
				     }, {
					        total: $scope.needPreparedList.length,
					        getData: function ($defer, params) {		          
					            params.total($scope.needPreparedList.length); // set total for recalc pagination
					            $defer.resolve($scope.needPreparedList.slice((params.page() - 1) * params.count(), params.page() * params.count()));
					        }
				     });
				   	needPreparedList();
				  }else if($stateParams.type == 'first'){//录入备料单界面
				  	getMpcodeList();
				  }
		   	};
		   	$scope.searchMp = function(mp_code){//根据扫描的备料单号获取相关的备料信息
		   		MakePrepareOper.get({mp_code:mp_code,type:'barcode'},function(data){
			    	$rootScope.makePrepare = $scope.makePrepare = data.message;
			    	$location.path("makePrepare/getBack");
			    },function(res){
			    	Ring.error();
			    	toaster.pop('error', '备料单号错误',res.data.exceptionInfo);
			    });
		   	};	   	 
		    $scope.barGet =  function(){//备料采集		  
			    MakePrepareOper.barGet({},{barcode:$scope.bar_code,whcode:$scope.makePrepare.mp_whcode,maid:$scope.makePrepare.mp_maid,
				   mpid:$scope.makePrepare.mp_id},function(data){		    		 
		           $scope.result = data.message;		               
		           needPreparedList();	
		           $scope.bar_code = '';
		           toaster.pop('success', '成功');
		           Ring.success();
			    },function(res){
				   Ring.error();	
				   $scope.result = {};
				   $scope.result.error = res.data.exceptionInfo;
				   toaster.pop('error',res.data.exceptionInfo);
				   $scope.bar_code = '';
			   });			 		   
		    };
		   	$scope.barBack =  function(){//备料退回	
		   		$scope.items = {};
		   		$scope.items.mp_id = $scope.makePrepare.mp_id;
		   		var modalInstance = $modal.open({  
		               templateUrl: 'prepareBarBack.html',  
		               controller: 'ModalInstanceCtrl',  
		               resolve: {  
		                    items: function () {  
		                        return $scope.items;  
		                     }                      
		                }  
	           	});  
	           	modalInstance.result.then(function(data) {
	           		if(data != null){//
	           			  $scope.result = data;
	           			  needPreparedList();
	           		}
				  },function() {					
				});					 		   		
		   	};				  
		   	$scope.clickReturn = function(){//返回
		   		$scope.makePrepare = $rootScope.makePrepare = '';
		   		$location.path("/makePrepare/first");
		   	};
		   function needPreparedList(){//根据制造单号获取需要上料
		      	MakePrepareOper.needPreparedList({mp_id:$scope.makePrepare.mp_id},{},function(data){		
	                   $scope.needPreparedList = data.message ||[];
	                   $scope.tableParamsU.reload();			       
				    },function(res){
				    	Ring.error();
				    	toaster.pop('error', '错误',res.data.exceptionInfo);
				    });		
		   };		 
		   function  getMpcodeList(){//获取在录入并且是当前账户的备料单列表
		   	  MakePrepareOper.getMpcodeList({type:'barcode'},{},function(data){		    	
	                   $scope.mpcodeList = data.message ||[];
	                   $scope.tableParamsMP = (!angular.isUndefined($scope.tableParamsMP)) ? $scope.tableParamsMP.reload():new ngTableParams({//备料清单
					        page: 1,           
					        count: 10,          
					        filter: {   },
					        sorting: {'mp_code':'desc' }
				      }, {
					        total: $scope.mpcodeList.length,
					        getData: function ($defer, params) {		          
					            params.total($scope.mpcodeList.length); // set total for recalc pagination
					            $defer.resolve($scope.mpcodeList.slice((params.page() - 1) * params.count(), params.page() * params.count()));
					        }
				       });
				    },function(res){
				    	Ring.error();
				    	toaster.pop('error', '错误',res.data.exceptionInfo);
				    });	
		    }
	   	}]);
	});