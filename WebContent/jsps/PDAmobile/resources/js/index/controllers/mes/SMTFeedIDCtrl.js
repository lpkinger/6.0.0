define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('SMTFeedIDCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'ShopFloorManageOper','SupportUtil','Ring', 'Online','SessionService','$document','$window','ngTableParams' , function($scope, $http, $rootScope, $location, $filter,$stateParams,$modal,toaster, 
	  ShopFloorManageOper, SupportUtil,Ring, Online,SessionService,$document, $window,ngTableParams){
	  	$scope.msl = $scope.mslJC = {};
	  	$scope.makeCraft = $rootScope.makeCraft;//作业单
	  	$scope.type = $stateParams.type;
	  	$scope.tip = {};
	  	if($scope.type != 'QUERY' || $scope.type != 'FeederLocation'){
		  	ShopFloorManageOper.getCollectDetailData({},{devcode:$scope.makeCraft.mc_devcode,prodcode:$scope.makeCraft.mc_prodcode},function(data){
		  			$scope.smtLocation = data.message;
		  	},function(res){
		  			
		  	});	 
	  	}
	  	if($scope.type == 'QUERY'){//料卷查询获取数据
	  		ShopFloorManageOper.queryData({id:$scope.makeCraft.mc_id},function(data){
	  			$scope.queryData = data.message;
		  	    $scope.tableParams = new ngTableParams({
					page : 1,
					count : 10						
				}, {
					total: $scope.queryData.length,
					getData : function($defer, params) {
						 var orderedData = params.sorting() ?
		                        $filter('orderBy')($scope.queryData, params.orderBy()) :
		                        $scope.orders;
		                orderedData = params.filter() ?
		                        $filter('filter')(orderedData, params.filter()) :
		                        orderedData;
		                params.total(orderedData.length);
					   $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
					}
				});					  					  				  			
	  		},function(res){
	  		
	  		});
	  	}
	  	$scope.getSetData = function(event,data){//上料获取数据，JS缓存判断输入的编号是否正确
	  		if(event.keyCode == 13 && data){
	  			$scope.tip = {};
	  			if(!$scope.msl.msl_location){
	  			    if(SupportUtil.contains(JSON.stringify($scope.smtLocation),data,"psl_location")){
		  				$scope.msl.msl_location = data;
		  				for(var i=0;i<$scope.smtLocation.length;i++){
		  					if($scope.smtLocation[i].psl_location == data){
		  						$scope.msl.msl_prodcode = $scope.smtLocation[i].psl_prodcode;
		  						$scope.msl.msl_repcode = $scope.smtLocation[i].psl_repcode;
		  						$scope.msl.psl_id = $scope.smtLocation[i].psl_id;
		  						$scope.msl.msl_fespec = $scope.smtLocation[i].psl_feeder;
		  						break;
		  					}
		  				}
		  				$scope.mslD = '';
			  		    document.getElementById("mslD").placeholder="请录入料卷编号";
			  		    $scope.tip.message = '站位采集成功，请采集物料'+$scope.msl.msl_prodcode+'的条码';
	  			    }else{
	  			    	$scope.mslD = '';
	  			    	$scope.tip.message = "站位编号错误";
	  			    	toaster.pop("error","站位编号错误");
	  			    	return;
	  			    }
			  	}else if(!$scope.msl.msl_barcode){
			  		ShopFloorManageOper.getBarRemain({},{bar_code:data,prod_code:$scope.msl.msl_prodcode,rep_code:$scope.msl.msl_repcode},function(d){
			  			$scope.msl.msl_barcode = d.message.bar_code;
			  			$scope.msl.msl_prodcode = d.message.bar_prodcode;
			  			$scope.msl.msl_remainqty = d.message.bar_remain;
			  			$scope.mslD = '';
			  			document.getElementById("mslD").placeholder="请录入飞达编号";
			  		},function(res){
			  			$scope.mslD = '';			  			
	  				    $scope.tip.message = res.data.exceptionInfo;
			  			toaster.pop("error",res.data.exceptionInfo);
	  			    	return;
			  		});		  		 
			  	}else if(!$scope.msl.msl_fecode){			  		
		  				$scope.msl.msl_fecode = data;
		  				$scope.loading();
		  				$scope.mslD = '';
			  		    document.getElementById("mslD").placeholder="请录入站位编号";	  			   
			  	}
	  		}	  		
	  	};
	  	$scope.loading =  function(){//确认上料  		
	  		ShopFloorManageOper.loading({},{msl:$scope.msl,makeCraft:$scope.makeCraft},function(data){
	  		   $scope.tip = data.message;	  		  
	  		   $scope.tip.message ="上料成功!";
	  		   $scope.msl = {};
	  		},function(res){	  		   
	  		   $scope.tip.message = "上料失败,"+res.data.exceptionInfo;
	  		   toaster.pop("error",$scope.tip.message);
	  		   if(res.data.exceptionInfo.indexOf('飞达') > -1){
	  		   	$scope.msl.msl_fecode = '';
	  		   }else{
	  		   	 $scope.msl ={};
	  		   }	  		   
	  		});
	  	};
	  	
	  	$scope.cuttingStock = function(){//确认下料
	  		$scope.msl.mc_code = $scope.makeCraft.mc_code;
	  		$scope.msl.mc_devcode = $scope.makeCraft.mc_devcode;
	  		$scope.msl.mc_sourcecode = $scope.makeCraft.mc_sourcecode;
	  		if($scope.msl.msl_location){
	  		    $scope.msl.type="location";
	  		}else if($scope.msl.msl_barcode){
	  			$scope.msl.type="barcode";
	  		}else if($scope.msl.msl_fecode){
	  			$scope.msl.type="fecode";
	  		}
	  		ShopFloorManageOper.cuttingStock({},$scope.msl,function(data){
	  		   $scope.tip = data.message;
	  		   $scope.tip.message = "下料成功!";	
	  		   $scope.msl = {};
	  		},function(res){
	  		   toaster.pop("error",res.data.exceptionInfo);
	  		   $scope.tip.message = "下料失败,"+res.data.exceptionInfo;
	  		   $scope.msl = {};
	  		});	  		
	  	};
	  	
	  	$scope.getJCData = function($event,data){//接料换料输入
	  		if(event.keyCode == 13){
	  		    $scope.tip = {};
	  			if(!$scope.mslJC.msl_location){
	  			    if(SupportUtil.contains(JSON.stringify($scope.smtLocation),data,"psl_location")){
		  				$scope.mslJC.msl_location = data;
		  				for(var i=0;i<$scope.smtLocation.length;i++){
		  					if($scope.smtLocation[i].psl_location == data){
		  						$scope.mslJC.msl_prodcode = $scope.smtLocation[i].psl_prodcode;
		  						$scope.mslJC.msl_repcode = $scope.smtLocation[i].psl_repcode;
		  						$scope.mslJC.psl_id =  $scope.smtLocation[i].psl_id;
		  						break;
		  					}
		  				}
		  				$scope.mslD = '';
			  		    document.getElementById("mslD").placeholder="请录入料卷编号";
			  		    $scope.tip.message = '站位采集成功，请采集物料'+$scope.msl.msl_prodcode+'的条码';
	  			    }else{
	  			    	$scope.mslD = '';
	  			    	 $scope.tip.message = "站位编号错误";
	  			    	toaster.pop("error","站位编号错误");
	  			    	return;
	  			    }
			  	}else if(!$scope.mslJC.msl_barcode){
			  		ShopFloorManageOper.getBarRemain({},{bar_code:data,prod_code:$scope.mslJC.msl_prodcode,rep_code:$scope.mslJC.msl_repcode},function(d){
			  			$scope.mslJC.msl_barcode = d.message.bar_code;
			  			$scope.mslJC.msl_prodcode = d.message.bar_prodcode;
			  			$scope.mslJC.msl_remainqty = d.message.bar_remain;
			  			$scope.mslD = '';
			  			$scope.confirmJC();
			  			document.getElementById("mslD").placeholder="请录入站位编号";
			  		},function(res){
			  			$scope.mslD = '';			  		
	  				    $scope.tip.message = res.data.exceptionInfo;
			  			toaster.pop("error",res.data.exceptionInfo);
	  			    	return;
			  		});		  		 
			  	}
	  		}	  		
	  	}
	  	$scope.confirmJC =  function(){//接料,换料
	  		$scope.tip = {};
	  		$scope.mslJC.mc_code = $scope.makeCraft.mc_code;
  			$scope.mslJC.mc_id = $scope.makeCraft.mc_id;
  		    $scope.mslJC.mc_devcode = $scope.makeCraft.mc_devcode;
  		    $scope.mslJC.mc_sourcecode = $scope.makeCraft.mc_sourcecode;
	  		if($scope.type == 'JOIN'){//接料	  	
	  			ShopFloorManageOper.joinMaterial({},$scope.mslJC,function(data){
	  				$scope.tip = data.message;
		  		    $scope.tip.message = "接料成功!";	
		  		    $scope.mslJC = {};	
	  			},function(res){	  			
	  				$scope.tip.message = "接料失败，"+res.data.exceptionInfo;	
	  				$scope.mslJC.msl_barcode = '';	
	  			});
	  		}else if($scope.type == 'CHANGE'){//换料	  			
	  			ShopFloorManageOper.changeMaterial({},$scope.mslJC,function(data){
	  				$scope.tip = data.message;
		  		    $scope.tip.message = "换料成功!";
		  		    $scope.mslJC = {};	
	  			},function(res){	  			  
	  			   $scope.tip.message = "换料失败，"+res.data.exceptionInfo;
	  			   $scope.mslJC.msl_barcode = '';	
	  			});	  		
	  		}	  		  
	  	};	  		  	
	    if($scope.type =='FeederLocation'){//默认根据飞达+站位进行校验
	    	$scope.checkType = {};
	    	$scope.checkTypes = [//校验方式
			      {id:1,code: 'FeederLocation',name: '飞达+站位'},
			      {id:2,code: 'BarcodeLocation',name: '料卷+站位'},
			      {id:3,code: 'Feeder',name: '飞达按顺序'},
			      {id:4,code: 'Barcode',name: '料卷按顺序'}
			    ];
			 for(i=0;i<$scope.checkTypes.length;i++){			 	
			   	if($scope.checkTypes[i].code == $scope.type){
			   	   $scope.checkType = $scope.checkTypes[i];
			   	}
			 }	
			 ShopFloorManageOper.checkMakeSMTLocation({id:$scope.makeCraft.mc_id},function(data){
			 	$scope.checkMakeSMTLocation = data.message;
			 },function(res){
			 });
	    }
	   $scope.changeCheckType =  function(){//切换校验方式
	   	if($scope.checkType.id == 4){
	   		$scope.checkType = $scope.checkTypes[0];
	   	}else{
	   	   $scope.checkType = $scope.checkTypes[$scope.checkType.id];
	   	}
	   	if($scope.checkType.id == 3){
	   		$scope.fe_code = $scope.checkMakeSMTLocation[0].msl_fecode;
	   	}else if($scope.checkType.id == 4){
	   		$scope.bar_code = $scope.checkMakeSMTLocation[0].msl_barcode;
	   	}
	   	$scope.tip = {};
	   }
	   $scope.enterCheck = function (event,code){	   	
	   	  if(event.keyCode == 13 && code){
	   	  	$scope.tip = {};
	   	  	 if($scope.checkType.id == 1){
	   	  	 	 if(SupportUtil.contains(JSON.stringify($scope.checkMakeSMTLocation),code,"msl_fecode")){
		  				for(var i=0;i<$scope.checkMakeSMTLocation.length;i++){
		  					if($scope.checkMakeSMTLocation[i].msl_fecode == code){
		  						$scope.location = $scope.checkMakeSMTLocation[i].msl_location;
		  						break;
		  					}
		  				}	
			  		    $scope.tip.message = '飞达正确，请采集站位！';
	  			  }else{	  			  	 
	  			  	 $scope.tip.message = "飞达错误，不存在!";
	  			  	 toaster.pop("error",$scope.tip.message);
	  			  	 $scope.msl.msl_fecode = '';
	        	  	 document.getElementById("msl_fecode").focus();
	  			  }
	   	  	 }else if($scope.checkType.id == 2){
	   	  	 	if(SupportUtil.contains(JSON.stringify($scope.checkMakeSMTLocation),code,"msl_barcode")){
		  				for(var i=0;i<$scope.checkMakeSMTLocation.length;i++){
		  					if($scope.checkMakeSMTLocation[i].msl_barcode == code){
		  						$scope.location = $scope.checkMakeSMTLocation[i].msl_location;
		  						break;
		  					}
		  				}	
			  		    $scope.tip.message = '料卷号正确，请采集站位！';
	  			  }else{	  			  	 
	  			  	 $scope.tip.message = "料卷号错误，不存在!";
	  			  	 toaster.pop("error",$scope.tip.message);
	  			  	 $scope.msl.msl_barcode = '';
	  			  	 document.getElementById("msl_barcode").focus();
	  			  }
	   	  	 }
	   	  }
	   };
	   $scope.check = function(){//校验
	   	 if($scope.checkType.id ==1 ){//飞达+站位
	   	 	if($scope.msl.msl_location != $scope.location){
	   	 		$scope.tip.message = '站位:'+$scope.msl.msl_location+'与飞达不一致！';	
	   	 	}else{
	   	 		$scope.tip.message = '站位:'+$scope.msl.msl_location+'正确,请采集下一飞达编号！';
	   	 		//更新msl_ifcheck为已校验
	   	 		ShopFloorManageOper.updateChecked({},{id:$scope.makeCraft.mc_id,code:$scope.location,type:'location'},function(data){},function(res){});
	   	 		document.getElementById("msl_fecode").focus();	   	 		
	   	 	}
	   	 }else if($scope.checkType.id ==2){//料号+站位
	   	 	if($scope.msl.msl_location != $scope.location){
	   	 		$scope.tip.message = '站位:'+$scope.msl.msl_location+'与料卷号不一致！';	
	   	 	}else{
	   	 		$scope.tip.message = '站位:'+$scope.msl.msl_location+'正确,请采集下一料卷号！';
	   	 		//更新msl_ifcheck为已校验
	   	 		ShopFloorManageOper.updateChecked({},{id:$scope.makeCraft.mc_id,code:$scope.location,type:'location'},function(data){},function(res){});
	   	 		document.getElementById("msl_barcode").focus();
	   	 	}
	   	 }else if($scope.checkType.id ==3){//飞达按顺序
	   	 	if($scope.msl.msl_fecode  != $scope.fe_code){
	   	 		 $scope.tip.message = '飞达:'+$scope.msl.msl_fecode+'顺序错误！';	   	 		 
	   	 	}else{
	   	 		 $scope.tip.message = '飞达：'+$scope.msl.msl_fecode+'正确,请采集下一飞达号！';	
	   	 		 //更新msl_ifcheck为已校验
	   	 		ShopFloorManageOper.updateChecked({},{id:$scope.makeCraft.mc_id,code:$scope.fe_code,type:'fecode'},function(data){},function(res){});
	   	 		 for(var i=0;i<$scope.checkMakeSMTLocation.length;i++){
		  			if($scope.checkMakeSMTLocation[i].msl_fecode == $scope.fe_code){
		  				$scope.fe_code = $scope.checkMakeSMTLocation[i+1].msl_fecode;
		  				break;
		  			}
		  		}	
	   	 	}	   	 	
	   	 }else if($scope.checkType.id ==4){//料号按顺序
	   	 	if($scope.msl.msl_barcode  != $scope.bar_code){
	   	 		 $scope.tip.message = '料卷号:'+$scope.msl.msl_barcode+'顺序错误！';	   	 		 
	   	 	}else{
	   	 		 $scope.tip.message = '料卷号：'+$scope.msl.msl_barcode+'正确，请采集下一料号！';	
	   	 		 for(var i=0;i<$scope.checkMakeSMTLocation.length;i++){
		  			if($scope.checkMakeSMTLocation[i].msl_barcode == $scope.bar_code){
		  				$scope.bar_code = $scope.checkMakeSMTLocation[i+1].msl_barcode;
		  				break;
		  			}
		  		}	
	   	 	}
	   	 }
	   	 $scope.msl = {};
	   }
	}]);
});