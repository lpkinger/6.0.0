define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('BreakingBatchCtrl',['$scope', '$http', '$stateParams', '$modal', 'BatchOper','toaster', 'Ring', 'Online','MsdOper',function($scope, $http, $stateParams, $modal, BatchOper,toaster, Ring, Online,MsdOper){	    
	    $scope.ispr_msd = false;
	    $scope.barcode = {};
		if($stateParams.type){
			if($stateParams.type =='msd'){
				$scope.ispr_msd = true; 
			}
		}		
	   $scope.btnInvalid = true;	 
	   $scope.search = function($event,barcode,type){//根据输入的条码编号查询相关信息
	    	if($event.keyCode == 13 && barcode){
	    		BatchOper.get({code:barcode,pr_ismsd:$scope.ispr_msd},function(data){
	    			$scope.barData = data.message.data;
	    			$scope.bar_prodcode = $scope.barData.bar_prodcode
	    			$scope.pr_detail =  $scope.barData.pr_detail;
	    			$scope.barcode.or_remain = $scope.barData.bar_remain;
	    			if($scope.ispr_msd){//湿敏元件分拆
	    				$scope.msdLog = data.message.msdLog;
	    			}
	    			Ring.success();
	    		},function(response){
	    		  Ring.error();
	    		  toaster.pop('error',response.data.exceptionInfo);
	    		  document.getElementById("or_barcode").select();
	    		});
	    	}
	    };
	    $scope.breaking = function(){//分拆
	    	if($scope.btnInvalid){
	    		$scope.btnInvalid = false;	
		    	//分拆之前判断定，拆分数量必须小于批数量大于0
		    	if($scope.barcode.bar_remain <0 || $scope.barcode.bar_remain > $scope.barcode.or_remain || $scope.barcode.bar_remain  == 0 ||$scope.barcode.bar_remain == '0'){
		    		 Ring.error();
		    		 toaster.pop('error', '拆分数量必须在大于0，小于批数量,');
		    		 document.getElementById("bar_remain").select();
		    		 $scope.btnInvalid = true;	
		    		 return ;
		    	}	 
		    	if($scope.ispr_msd){//湿敏元件
	    			if($scope.msdLog.status =='入烤箱'){//
	    				if(confirm('确认出烤箱，再拆分')){
	    					MsdOper.confirmOutOven({},{bar_code:$scope.barcode.or_barcode},function(data){
				   			   toaster.pop("success","已出烘烤!");
				   			   breakingBatch();
				       	 	},function(res){
				       	 	   toaster.pop('error', '出烘烤失败',response.data.exceptionInfo);  
				       	 	   $scope.btnInvalid = true;	
				       	 	   return ;
				       	 	});
	    				}
	    			}
	    		}else{
	    			 breakingBatch();
	    		}
	    	}
	    }  ;
	    var breakingBatch = function(){
	    	BatchOper.breakingBatch({},angular.fromJson($scope.barcode),function(data){
				$scope.btnInvalid = true;	
				Ring.success();
				toaster.pop('success', '分拆成功');
				modal(data.message);	    			    	 
			},function(response){
				$scope.btnInvalid = true;	
				Ring.error();
				toaster.pop('error',"拆分失败",response.data.exceptionInfo);
			});  
	    }
	     $scope.confirm =  function(barcode,type){
	    	var event = new Object();
	    	event.keyCode = 13;
	    	$scope.search(event,barcode,type);
	    }
	    var  modal= function(data){
	    	var modalInstance = $modal.open({  
	               templateUrl: 'myModalContent.html',  
	                controller: 'ModalInstanceCtrl',  
	                resolve: {  
	                    items: function () {  
	                        return data;  
	                     }  
	                }  
           		}); 
           	modalInstance.result.then(function(items) {
           		if(items != null){//继续分拆获取数据
           			 $scope.confirm($scope.barcode.or_barcode,'breaking');
           			 $scope.barcode.bar_remain='';
           			 document.getElementById("bar_remain").focus();
           		}
			  },function() {
				 $scope.barcode = $scope.pr_detail = $scope.bar_prodcode = '';//清空
           		 document.getElementById("or_barcode").focus();
			});	
	    };	    
	}]);
});