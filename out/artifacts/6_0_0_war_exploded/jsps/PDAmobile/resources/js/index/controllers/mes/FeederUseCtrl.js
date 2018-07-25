define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('FeederUseCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','toaster','SupportUtil','Ring', 'Online','SessionService','FeederOper','ngTableParams','$modal', function($scope, $http, $rootScope, $location, $filter,$stateParams,toaster, 
	   SupportUtil,Ring, Online,SessionService,FeederOper,ngTableParams,$modal){
	   	$scope.FeederUse = $rootScope.FeederUse||{};
	   	
	   	$scope.generateTable = function(){
			 return new ngTableParams({//未完成料号名称规格及剩余数量表格
					page: 1,           
					count: 10,          
					filter: {  },
					sorting: {'fe_code' :'asc' }
				  }, {
					total: $scope.feederUsedlist.length,
					getData: function ($defer, params) {		          
						 params.total($scope.feederUsedlist.length); // set total for recalc pagination
						 $defer.resolve($scope.feederUsedlist.slice((params.page() - 1) * params.count(), params.page() * params.count()));
					}
				});
			 },
	   	$scope.searchMa = function(event,fu_makecode){//根据扫描的制造单号获取产品编号等信息
	   		if(event.keyCode == 13){//触发enter 事件
	   			FeederOper.get({fu_makecode:fu_makecode},function(data){
		    	    $rootScope.FeederUse = $scope.FeederUse = data.data;
		    	},function(res){
		    		Ring.error();
		    	    toaster.pop('error', '制造单号错误',res.data.exceptionInfo);
		    	});
	   		}
	   	};
	   	$scope.feederMakeQuery =  function(){//领取飞达的制造单号和线别
	   		FeederOper.feederMakeQuery({fu_makecode:$scope.FeederUse.fu_makecode,fu_linecode:$scope.FeederUse.fu_linecode},{},function(data){
		    	  $rootScope.PMFeederUse = data.data||[];   	  
		    	  $location.path('FeederOperate/list');
		    },function(res){
		    	Ring.error();
		    	toaster.pop('error', '错误',res.data.exceptionInfo);
		    });
	   	};
	   	$scope.clickReturn = function(type){
	   		if(type == 'list'){
	   			$location.path("FeederOperate/list");
	   		}else if(type == 'use'){
	   			$rootScope.FeederUse = '';
	   			$location.path("FeederUse");
	   		}else if(type == 'getE'){
	   			$location.path("FeederOperate/get");
	   		}
	   		
	   	}
		   if($stateParams.type ){
		   	    $scope.type = $stateParams.type;
		   		if($stateParams.type == 'feederUsedlist'){	  //不带条件的飞达列表				   	
		   		   FeederOper.feederUsedlist({},{},function(data){		    	
	                   $scope.feederUsedlist = data.data ||[];
	                   $scope.tableParams = (!angular.isUndefined($scope.tableParams)) ? $scope.tableParams.reload():$scope.generateTable() ;
				    },function(res){
				    	Ring.error();
				    	toaster.pop('error', '错误',res.data.exceptionInfo);
				    });
			     }else if( $stateParams.type == 'get'){		//飞达领用			 
				   	$scope.FeederGet = {};				   				  
				   	$scope.tableParamsU = new ngTableParams({//未完成料号名称规格及剩余数量表格
				        page: 1,           
				        count: 10,          
				        filter: {   },
				        sorting: {  }
				    }, {
				        total: $rootScope.PMFeederUse.length,
				        getData: function ($defer, params) {		          
				            params.total($rootScope.PMFeederUse.length); // set total for recalc pagination
				            $defer.resolve($rootScope.PMFeederUse.slice((params.page() - 1) * params.count(), params.page() * params.count()));
				        }
				    });
				  }else if($stateParams.type == 'back')	{//飞达退回
				  	  	$scope.choose = [{id:1,name:'是'},{id:0,name:'否'}];//是否停用
				   	    $scope.FeederBack = {};
				   	    $scope.FeederBack.isuse = 0;//绑定，默认是否停用中的0,不停用
				  }else if($stateParams.type == 'listC'){		//根据制造单号和线别查询的已领用飞达列表	
		   		       FeederOper.feederUsedlist({fu_makecode:$scope.FeederUse.fu_makecode,fu_linecode:$scope.FeederUse.fu_linecode},{},function(data){		    	
		                   $scope.feederUsedlist = data.data ||[];
		                   $scope.tableParams = (!angular.isUndefined($scope.tableParams)) ? $scope.tableParams.reload():$scope.generateTable();
				    },function(res){
				    	Ring.error();
				    	toaster.pop('error', '错误',res.data.exceptionInfo);
				    });
			     
				  }
		   	}
		   $scope.feederGet =  function(){//飞达领取
		   		FeederOper.feederGet({},{fu_makecode:$scope.FeederUse.fu_makecode,fu_linecode:$scope.FeederUse.fu_linecode,fe_code:$scope.FeederGet.fe_code},function(data){		    	
                   toaster.pop('success', '领取成功!');
                   $rootScope.PMFeederUse = data.data||[]; 
                   $scope.message = '飞达编号:'+$scope.FeederGet.fe_code+",领用成功!";
                   $scope.tableParamsU.reload();
			    },function(res){
			    	Ring.error();
			    	toaster.pop('error', '错误',res.data.exceptionInfo);
			    });
		   	};
		   	$scope.feederBack =  function(){//飞达退回
		   		if($scope.FeederBack.isuse == 1){
		   			if($scope.FeederBack.reason == ''){
		   				toaster.pop('error', '请填写停用原因!');
		   			}
		   		}
		   		FeederOper.feederBack({},$scope.FeederBack,function(data){
		    	    toaster.pop('success', '退回成功!');	
		    	    $scope.message = '飞达编号:'+$scope.FeederGet.fe_code+",退回成功!";
		    	    $scope.FeederBack = '';
			    },function(res){
			    	Ring.error();
			    	toaster.pop('error', '错误',res.data.exceptionInfo);
			    });
		   	};
		   	$scope.feederBackAll =  function(){//飞达退回
		   		FeederOper.feederBackAll({},$scope.FeederGet,function(data){
		    	   toaster.pop('success', '退回成功!');	
			    },function(res){
			    	Ring.error();
			    	toaster.pop('error', '错误',res.data.exceptionInfo);
			    });
		   	};
		   	$scope.feederBackFromList = function(item){//在列表中选择退回，弹出填写退回原因，是否停用等
		   		 var modalInstance = $modal.open({  
		             templateUrl: 'back.html',  
		             controller: 'ModalInstanceCtrl',  
		             resolve: {  
		                  items: function () {  
		                      return item;  
		                  }  
		             }  
	           	 });  
	           	modalInstance.result.then(function(item) {
					FeederOper.feederBack({},item,function(data){
			    	    toaster.pop('success', '退回成功!');	
			    	    if($scope.type == 'feederUsedlist'){//无条件的列表
				    	    FeederOper.feederUsedlist({},{},function(data){		    	
		                       $scope.feederUsedlist = data.data;
		                       $scope.tableParams.reload();
					    	},function(res){
						    	Ring.error();
						    	toaster.pop('error', '错误',res.data.exceptionInfo);
						    });
			    	    }else if($scope.type == 'listC'){//带条件的列表
			    	    	FeederOper.feederUsedlist({fu_makecode:$scope.FeederUse.fu_makecode,fu_linecode:$scope.FeederUse.fu_linecode},{},function(data){		    	
		                       $scope.feederUsedlist = data.data;
		                       $scope.tableParams.reload();
					    	},function(res){
						    	Ring.error();
						    	toaster.pop('error', '错误',res.data.exceptionInfo);
						    });
			    	    }
				    },function(res){
				    	Ring.error();
				    	toaster.pop('error', '错误',res.data.exceptionInfo);
				    });
				}, function() {
					 /*$log.info('Modal dismissed at: ' + new Date());*/
				});
			 }			 
	   	}]);
	});