define(['app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('MPrepareFeederCtrl',['$scope','$rootScope', '$location','$stateParams','toaster','SupportUtil','Ring', 'Online','SessionService','ngTableParams','MakePrepareOper','$filter','$modal',function($scope, $rootScope, $location,$stateParams,toaster, 
	   SupportUtil,Ring, Online,SessionService,ngTableParams,MakePrepareOper,$filter,$modal){
	   	$scope.makePrepare = $rootScope.makePrepareFeeder||{};	
	   	$scope.MDetail = {};
	   	$scope.searchMp = function(mp_code){//根据扫描的备料单号获取相关的备料信息
	   		MakePrepareOper.get({mp_code:mp_code,type:'feeder'},function(data){
		    	$rootScope.makePrepareFeeder = $scope.makePrepare = data.message;
		    	$location.path("feederGet/get");
		    },function(res){
		    	Ring.error();
		    	toaster.pop('error', '备料单号错误',res.data.exceptionInfo);
		    });
	   	};	  
	   	if($stateParams.type == 'list'){//备料单列表
	   		 $scope.mpcodeList = [];
	   		$scope.tableParamsMP = new ngTableParams({//备料清单
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
	   		getMpcodeList();	   		
	   	}else{//需要上飞达的明细
	   		needPreparedList();
	   	}
	   	$scope.checkCode =  function (code){
	   		if($scope.MDetail.md_location){//采集的是飞达编号
	   			//飞达编号采集后判断规格是否等于备料单明细行的飞达规格，飞达是否重复
	   			MakePrepareOper.feederGet({},{fe_code:code,mp_id:$scope.makePrepare.mp_id,md_fespec:$scope.MDetail.md_fespec,md_location:$scope.MDetail.md_location},function(data){	   				
	   				if(data.message != null){
	   					$scope.message = "飞达："+code+"上料成功,备料单完成飞达上料！";
	   				}else{
	   					$scope.message = "飞达："+code+"上料成功！";
	   				}
	   			   $scope.MDetail = {};
	   			   document.getElementById("code").placeholder="请输入料卷编号";
	   			},function(res){
	   				Ring.error();
	   				$scope.message = '错误,'+res.data.exceptionInfo;
				    toaster.pop('error', '错误',res.data.exceptionInfo);
				   if(res.data.exceptionInfo.indexOf('站位') > -1){
				   	 $scope.MDetail = {};
				   	 document.getElementById("code").placeholder="请输入料卷编号";
				   }
	   			});
	   		}else{//采集的是料号
	   			//料卷采集后校验是否属于本备料单的料卷，
	   			//如果是，则显示该明细对应的站位和飞达规格到上方，采集栏背景显示“请录入飞达编号”。	   			
	   			if(SupportUtil.contains(JSON.stringify($scope.preparedFeederList),code,"md_barcode")){
		  				$scope.MDetail.barcode = code;
		  				for(var i=0;i<$scope.preparedFeederList.length;i++){
		  					if($scope.preparedFeederList[i].md_barcode == code){
		  						$scope.MDetail = $scope.preparedFeederList[i];		  						
		  						break;
		  					}
		  				}		  				
		  				document.getElementById("code").placeholder="请输入飞达编号";
		  		}else{
		  			$scope.message="料卷号："+code+"错误，不属于该备料单!";
		  		}		  		
	   		}
	   		$scope.code = '';
	   	},
	   	$scope.feederBack =  function(){//取消上料
	   		$scope.items = {};
	   		$scope.items.mp_id = $scope.makePrepare.mp_id;
	   		var modalInstance = $modal.open({  
	               templateUrl: 'prepareFeederBack.html',  
	               controller: 'ModalInstanceCtrl',  
	               resolve: {  
	                    items: function () {  
	                        return $scope.items;  
	                     }                      
	                }  
           	});  
           	modalInstance.result.then(function(data) {
	           	if(data != null){//
	           			 $scope.message = data;	           			  
	           	}
			},function() {					
			});	
	   	}
		function needPreparedList(){//获取需要飞达上料的明细行
		      MakePrepareOper.preparedFeederList({mp_id:$scope.makePrepare.mp_id},{},function(data){		    	
	               $scope.preparedFeederList = data.message ||[];
			  },function(res){
				   Ring.error();
				   toaster.pop('error', '错误',res.data.exceptionInfo);
			  });		
		}
		function getMpcodeList (){//获取已审核并且是当前账户的备料单列表
		   	 MakePrepareOper.getMpcodeList({type:'feeder'},{},function(data){		    	
	               $scope.mpcodeList = data.message ||[];
	               $scope.tableParamsMP.reload();	               
			},function(res){
				   Ring.error();
				   toaster.pop('error', '错误',res.data.exceptionInfo);
		     });	
		  }
		  $scope.clickReturn = function(){//返回
		   	 $scope.makePrepare = $rootScope.makePrepareFeeder = '';
		   	 $location.path("/makePrepareFeeder/list");
		  };
	   	}]);
	});