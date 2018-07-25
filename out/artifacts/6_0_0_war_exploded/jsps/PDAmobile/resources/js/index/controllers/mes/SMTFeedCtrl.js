define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('SMTFeedCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'ShopFloorManageOper','SupportUtil','Ring', 'Online','SessionService','$document','$window', '$timeout', function($scope, $http, $rootScope, $location, $filter,$stateParams,$modal,toaster, 
	  ShopFloorManageOper, SupportUtil,Ring, Online,SessionService,$document, $window, $timeout){
		$rootScope.title = {};
		$scope.makeCraft = $rootScope.makeCraft || {};
		if(!angular.isUndefined($scope.makeCraft.mc_code)){
			 $rootScope.title.pi_inoutno = $scope.makeCraft.mc_devcode+' '+$scope.makeCraft.mc_code;
		}
		$scope.search = function($event,code){//enter键触发事件
			if($event.keyCode == 13 && !$scope.makeCraft.mc_makecode && !$scope.makeCraft.mc_code) {//Enter事件
				ShopFloorManageOper.get({devCode:code},function(data){
					if(data.message != null){
				        $rootScope.makeCraft = $scope.makeCraft = data.message;
				        $location.path("SMTFeedContents");
					}else{
						$scope.tip = '请输入制造单号或者作业单号！';
					}
				},function(res){
					toaster.pop('error', res.data.exceptionInfo);
					$scope.makeCraft.mc_devcode =''
				});
			}
		};				
		$scope.confirm = function(){//确认采集的机型编号，制造单号，作业单号
			if(!$scope.makeCraft.mc_devcode){
				 toaster.pop('error', '设备编号不允许为空');
				 document.getElementById("mc_devcode").focus();
				 return;
			}
			/*}else if(!$scope.makeCraft.mc_code){
				toaster.pop('error', '请输入作业单号');
				document.getElementById("mc_code").focus();
				return;
			}else if($scope.makeCraft.mc_makecode){//判断制造单号是否存在
				ShopFloorManageOper.checkCode({},{devCode:$scope.makeCraft.mc_devcode,code:$scope.makeCraft.mc_makecode,type:'makecode'},function(data){
				    if(data.message.length > 1){//一个制造单分成多个作业单
				    	var modalInstance = $modal.open({  
	               			 templateUrl: 'makeCodeContent.html',  
	                		 controller: 'ModalInstanceCtrl',  
	                		 resolve: {  
	                    		items: function () {  
	                        	  return data.message[0];  
	                     		}  
	                		 }  
           				  });  
           				 modalInstance.result.then(function(selectedItem) {
						     $rootScope.makeCraft = $scope.makeCraft = selectedItem;			
						     $location.path("SMTFeedContents");
						 });					
				    }else{
				    	$rootScope.makeCraft = $scope.makeCraft = data.message[0];		
				    	$location.path("SMTFeedContents");
				    }
				   
				},function(res){
					if(res.data.exceptionInfo){
						toaster.pop('error', res.data.exceptionInfo);
						document.getElementById("mc_makecode").focus();
						$scope.makeCraft.mc_makecode='';
					}
				});				
			}else if($scope.makeCraft.mc_code){//判断作业单号是否存在
*/			   if(!$scope.makeCraft.mc_code){
	              $scope.makeCraft.mc_code='';
               }
               ShopFloorManageOper.checkCode({},{devCode:$scope.makeCraft.mc_devcode,code:$scope.makeCraft.mc_code,type:'mccode'},function(data){				    
				    $rootScope.makeCraft = $scope.makeCraft = data.message.craft;				    
				    if(data.message.prepare){//检测是否需要导入备料单号
				    	if(confirm("是否导入对应备料单:"+data.message.prepare+"',数据")){//导入
				    		ShopFloorManageOper.importMPData({},{mp_code:data.message.prepare,devCode:$scope.makeCraft.mc_devcode,mc_code:$scope.makeCraft.mc_code},function(data){
				    		    toaster.pop('success',"导入成功!");
				    		    Ring.success();
				    		    $location.path("SMTFeedContents");
				    		},function(res){
				    		    toaster.pop('error',res.data.exceptionInfo);
				    		    Ring.error();
				    		});
				    	}else{
				    		$location.path("SMTFeedContents");
				    	}
				    }else{
				    	$location.path("SMTFeedContents");
				    }				    
				},function(res){
					if(res.data.exceptionInfo){
						toaster.pop('error',res.data.exceptionInfo);
						document.getElementById("mc_code").focus();
						$scope.makeCraft.mc_code='';
					}
				});		
			}
		//}				
		$scope.chooseOperation = function(operation){//在上料目录界面中选择上料，下料，接料，换料......中的一项点击进入
			if(operation == 'SMTFeedI'){//上料获取
				$location.path("SMTFeedI/IN");			
			}else if(operation == 'SMTFeedD'){//下料
				$location.path("SMTFeedD/DOWN");	
			}else if(operation == 'SMTFeedJ'){//接料
				$location.path("SMTFeedJ/JOIN");
			}else if(operation == 'SMTFeedC'){//换料
				$location.path("SMTFeedC/CHANGE");
			}else if(operation == 'SMTFeedAD'){//全部下料				
				if(confirm("确认全部下料")){								
					//更新所有记录状态为-1.
				    ShopFloorManageOper.cuttingAllStock({mc_id:$scope.makeCraft.mc_id,mc_sourcecode:$scope.makeCraft.mc_sourcecode},{},function(data){
					      	 alert("全部下料成功！");
					},function(res){
						if(res.data.exceptionInfo){
							toaster.pop('error',res.data.exceptionInfo);
						}
					});
				}	
			}else if(operation == 'SMTFeedQ'){//料卷查询
				$location.path("SMTFeedQ/QUERY");
			}else if(operation == 'Check'){//校验			
	           	 $location.path("SMTFeedCheck/FeederLocation");				  
			}
		};
		$scope.returnSMTFeed = function (){
		   $scope.makeCraft = $rootScope.makeCraft='';
		   $location.path("/SMTFeed");
		}
	}]);
	});