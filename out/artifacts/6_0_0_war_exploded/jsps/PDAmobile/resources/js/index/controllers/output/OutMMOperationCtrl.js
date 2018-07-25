define([ 'app/app','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('OutMMOperationCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'SupportUtil','OutOper','Ring', 'Online','ngTableParams', '$q','BatchOper', function($scope, $http, $rootScope,$location,$filter,$stateParams,$modal,toaster,SupportUtil,OutOper, Ring,Online,ngTableParams, $q,BatchOper){
	    $scope.grid = $rootScope.outMMgrid||[];
	    $scope.getOnline = Online.getOnline;
		var getOrder = function(code) {//根据路径中的id号获取对应的单据
			var result = null;
			angular.forEach($rootScope.outMMOrders, function(value, key){
				if(value.PI_INOUTNO == code) {
					result = value;
					console.log(result);
					return result;
				}
			});
			return result;
		};	
		$scope.changeSelection = function(item) {//table with row selection,表格选中列操作
			$scope.ordercode = item.PD_ORDERCODE;
			$scope.bi_prodcode = item.PD_PRODCODE;
			$scope.bi_prodid = item.PR_ID;	
			$scope.pr_fbzs = item.PD_OUTQTY;
			$scope.detno = item.PD_PDNO;
			$scope.bi_prodname = item.PR_DETAIL;
			$scope.pr_detail = item.PR_DETAIL+item.PR_SPEC;	
			$scope.pd_id = item.PD_ID;
			$scope.pr_id =  item.PR_ID;
         }
		$scope.order = getOrder($stateParams.code);
		$rootScope.title = {};
		$rootScope.title.pi_inoutno =$scope.order.PI_INOUTNO +'   '+$scope.order.PI_WHCODE;
		if($scope.order){
		 var keepGoing = true; 
		 angular.forEach($scope.order.product, function(value, key){	
			if( keepGoing ){
				if(value.PD_OUTQTY == 0 && $scope.order.product[key+1].PD_OUTQTY != 0){
						$scope.changeSelection($scope.order.product[key+1]);
							keepGoing = false ;
				 }else 	{
				 	$scope.changeSelection($scope.order.product[key]);
				 	     keepGoing = false;
				 }
			}
			});		
		  $scope.tableParams = new ngTableParams({//未完成料号名称规格及剩余数量表格
		        page: 1,           
		        count: 10,          
		        filter: { },
		        sorting: {
		        	'PD_PDNO': 'asc'
		        }
		    }, {
		        total: $scope.order.product.length,
		        getData: function ($defer, params) {
		            var filteredData = params.filter() ?
		                    $filter('filter')($scope.order.product, params.filter()) :
		                    data;
		            var orderedData = params.sorting() ?
		                    $filter('orderBy')(filteredData, params.orderBy()) :
		                    data;	
		            params.total(orderedData.length); // set total for recalc pagination
		            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		        }
		    });
		}		
		var checkBar = function(defer) {
			OutOper.checkOutqty({},{pi_id:$scope.order.PI_ID,pd_id:$scope.pd_id,pr_fbzs:$scope.pr_fbzs, barcode:$scope.bi_barcode, whcode:$scope.order.PI_WHCODE,prodcode:$scope.bi_prodcode}, function(data){
				 if(data.exceptionInfo){
					 defer.reject(data.exceptionInfo);
				  }else
				      defer.resolve(data.message);
			}, function(response){
				if(response.status == 0){ //无网络错误
					 Online.setOnline(false);//修改网络状态	
					 toaster.pop('error', '失败',"网络连接不可用，请稍后再试");
				}				
				defer.reject(response.data.exceptionInfo);
			});
		};
		
		var checkOutBox = function(defer) {
			OutOper.checkOutBoxqty({},{pi_id:$scope.order.PI_ID,pd_id:$scope.pd_id,pr_fbzs:$scope.pr_fbzs, outboxcode:$scope.bi_outboxcode, whcode:$scope.order.PI_WHCODE,prodcode:$scope.bi_prodcode}, function(data){
				 if(data.exceptionInfo){
					 defer.reject(data.exceptionInfo);
				  }else
				      defer.resolve(data.message);
			}, function(response){
				if(response.status == 0){ //无网络错误
					 Online.setOnline(false);//修改网络状态	
					 toaster.pop('error', '失败',"网络连接不可用，请稍后再试");
				}
				defer.reject(response.data.exceptionInfo);
			});
		};
         //进入界面，表单值默认为grid中的第一行              
		var check = function (productCode){
			  //校验：1、物料编号存在, 2、数量不超过剩余数量，3、条码号唯一性		
			  //条码储位不允许为空
		    var defer = $q.defer();
			  var reg = /^[0-9]*[1-9][0-9]*$/;
			   if(!$scope.bi_barcode  && !$scope.bi_outboxcode){
			     	document.getElementById("bi_barcode").focus();
					defer.reject("请输入条码或者箱号!");					
				}else{
					var item = SupportUtil.contains(JSON.stringify($scope.order.product),productCode,"PD_PRODCODE");
					if(item){
						//是否条码校验
					    if($scope.ifbarcodecheck == '1'){
							var pr_idlen =$scope.order.barcodeset.BS_LENPRID;
							//比较物料ID的前几位是否等于条码号的前几位
							var pr_id = new String($scope.pr_id);
							var bar_code =  new String($scope.bi_barcode);	
							if (SupportUtil.lapAft(pr_id,pr_idlen) != SupportUtil.lapFor(bar_code,pr_idlen)){
								Ring.error();								
								$scope.bi_barcode ='';
								defer.reject("条码校验错误，该条码与物料不匹配")
							}
					     } 
					     if($rootScope.outMMgrid){
							if($scope.bi_barcode && (SupportUtil.contains(JSON.stringify($rootScope.outMMgrid) ,$scope.bi_barcode,"bi_barcode"))){
								 document.getElementById("bi_barcode").focus();
								 document.getElementById("bi_barcode").select();
								 defer.reject("条码号重复");
							} else if($scope.bi_outboxcode && (SupportUtil.contains(JSON.stringify($rootScope.outMMgrid) ,$scope.bi_outboxcode,"bi_outboxcode"))){
								 document.getElementById("bi_outboxcode").focus();
								 document.getElementById("bi_outboxcode").select();
								 defer.reject("箱号重复");
							}
						 } 
						 if($scope.bi_barcode){
						 	checkBar(defer);	
						 }else if($scope.bi_outboxcode){
						 	checkOutBox(defer);
						 }						 					 
					} else {
						Ring.error();
						defer.reject("物料不存在");						
					}
				}	
				return defer.promise;
		}	  
		$scope.scan =  function(){//确认按钮事件		
			var q = check($scope.bi_prodcode);
			q.then(function(message){	
				if(message.data.bar_code){//对箱号中所有的条码号进行判断是否存在重复在JS[已采集列表]的列表中
					var codes = message.bar_code.split(",");
					for(var i=0;i<codes.length;i++){
						 if(SupportUtil.contains(JSON.stringify($rootScope.outMMgrid) ,codes[i],"bi_barcode")){
						 	toaster.pop('error',"箱号包含已采集列表中的条码!");
						 	return ;
						 }
					}					
				}else if(message.data.pa_outboxcode){
					if(SupportUtil.contains(JSON.stringify($rootScope.outMMgrid) ,message.pa_outboxcode,"bi_outboxcode")){
						toaster.pop('error',"条码所在的箱号在已采集列表中存在!");
						return ;
					}
				}else if(message.isMsd){//湿敏元件弹出显示湿敏元件的相关记录 MSDlog
					//拆分湿敏元件提示剩余寿命等信息
					var modalInstance = $modal.open({
					    templateUrl: 'resources/tpl/output/msdConfirm.html',
					    controller: 'SplitModalCtrl',
						resolve: {
							items: function(){return {'bar_code':$scope.bi_barcode};}
						}
					});
					modalInstance.result.then(function(s){
						if(s) {//判断数量是否需要拆分
							OutOper.ifNeedBatch({},{bar_code:$scope.bi_barcode,pr_fbzs:$scope.pr_fbzs, whcode:$scope.order.PI_WHCODE},function(data){
							   
							},function(res){
								 if(res.data.exceptionInfo.indexOf('拆批') >=0){
									if(confirm("确认拆批")){
									   batchSplit();
									}
								 }else{
								 	toaster.pop('error',res.data.exceptionInfo);
								 	return;
								 }
							});
						}else{
							$scope.bi_barcode = '';
						}
					});	
					return;
				}
			 	$scope.barcodes = {};
			 	$scope.barcodes.bi_barcode = $scope.bi_barcode ;
			 	$scope.barcodes.bi_outboxcode = $scope.bi_outboxcode ;
			 	$scope.barcodes.bi_prodcode = $scope.bi_prodcode ;
			    $scope.barcodes.bi_prodid = $scope.bi_prodid ;	
			    $scope.barcodes.bi_inoutno = $scope.order.PI_INOUTNO;
				$scope.barcodes.bi_outqty = message.remain;
			 	$scope.barcodes.bi_whcode = $scope.order.PI_WHCODE;
			 	$scope.barcodes.bi_prodname = $scope.bi_prodname;
			 	$scope.barcodes.bi_pdno = $scope.detno;
			 	$scope.barcodes.bi_pdid = $scope.pd_id;
			 	$scope.barcodes.bi_piid = $scope.order.PI_ID;
				//插入数据到待提交缓存列表中grid(barcode,prodcode,inqty,location)	
			    $scope.grid.push($scope.barcodes);
				$rootScope.outMMgrid = $scope.grid;	
				$scope.pr_fbzs -= message.remain;	
				$scope.bi_barcode ='';	
				$scope.bi_outboxcode ='';
				angular.forEach($scope.order.product, function(value, key){	
					if(value.PD_ID == $scope.pd_id) {		
						value.PD_OUTQTY = $scope.pr_fbzs ;
						$scope.tableParams.reload();
					if(value.PD_OUTQTY == 0 && $scope.order.product[key+1] ){
						$scope.changeSelection($scope.order.product[key+1]);
					   	}								
					 }					   
				});						
				var keepGoing = true; 
				angular.forEach($scope.order.product, function(value, key){	
					if(keepGoing){
						if(value.PD_OUTQTY != 0 ){									
							keepGoing = false ;
						}
					}
				});	
				if(keepGoing){	
					setTimeout( function(){
					  try{
					    if(confirm("已完成，请提交采集")){								
						   $location.path('outMMWaitSubmit/' + $scope.order.PI_INOUTNO);
					     }	
					  } catch(e){}
					}, 200);
										
				}													 			   
			}, function(error){
				toaster.pop('error', '错误',error);
				setTimeout( function(){
					try{
					    if(error.indexOf('拆批') >=0){
							if(confirm("确认拆批")){
							   batchSplit();
							}
						}else{
							$scope.barcode = '';
						}/*else if(error.indexOf('拆批') >=0 && error.indexOf('湿敏') >=0){
							//拆分湿敏元件提示剩余寿命等信息
							var modalInstance = $modal.open({
								templateUrl: 'resources/tpl/output/split.html',
								controller: 'SplitModalCtrl',
								resolve: {
									items: function(){return {'bar_code':$scope.bi_barcode};}
								}
							});
							modalInstance.result.then(function(s){
								if(s) {
									batchSplit();
								}
							});		
						}*/
					  } catch(e){}
				}, 200);
			});
		};
		 var batchSplit = function(){//拆分
		 	 BatchOper.breakingBatch({},{or_barcode:$scope.bi_barcode,bar_remain:$scope.pr_fbzs},function(data){								
				var datas  = new Object();
				datas  = data.message;
				datas[0]['or_barcode'] = $scope.bi_barcode;
				datas[0]['or_qty'] = eval(data.message[0].bar_remain+data.message[1].bar_remain);
				modal(datas);
				Ring.success();			    	 						    	 
			},function(response){
				if(response.status == 0){ //无网络错误
					Online.setOnline(false);//修改网络状态	
					toaster.pop('error', '失败',"网络连接不可用，请稍后再试");
			   }else if(response.data.exceptionInfo){
					toaster.pop('error', '拆分失败',response.data.exceptionInfo);
					Ring.error();		
				}
			});
		 }
		 var  modal= function(data){//显示拆分后的条码。提供打印
	    	var modalInstance = $modal.open({  
	               templateUrl: 'myModalContent.html',  
	                controller: 'ModalInstanceCtrl',  
	                resolve: {  
	                    items: function () {  
	                        return data;  
	                     }  
	                }  
           		}); 
           	modalInstance.result.then(function(data) {
           		angular.forEach(data, function(value, key){
				    if(value.bar_remain == $scope.pr_fbzs) {//将拆分的条码中的数量等于剩余数的条码号返回至条码号中，并且自动执行确认按钮
						$scope.bi_barcode = value.bar_code;
				   }
			   });
				$scope.scan();
			}, function() {
						
			});
	    };
		$scope.submitGet = function(){//提交采集操作，与后台交互	
			if($scope.grid.length == 0){
				toaster.pop('error', '没有需要提交的数据');
				return;
			}
			OutOper.saveOutBarcode({}, JSON.stringify($scope.grid),function(data) {//获取成功		
				    toaster.pop('success', '提交成功');
				    $rootScope.outMMgrid = $scope.grid ='';
				}, function(response){//获取失败处理
					if(response.status == 0){ //无网络错误
					   Online.setOnline(false);//修改网络状态	
					   toaster.pop('error', '提交失败',"网络连接不可用，请稍后再试");
					}
					else {
					   toaster.pop('error', '提交失败',response.data.exceptionInfo);
					}
				});
		};
				
		$scope.getList = function(argOrder){//点击
			if(argOrder){
				$location.path('outMMWaitSubmit/' + argOrder);
			}
		};		
		$scope.returnInput = function(){//当前单据有未提交的，提示先提交。
			if($scope.grid.length != 0){
				if(confirm("返回将清空未提交数据，确认返回？")){
					angular.forEach($scope.order.product, function(value, key){	
			 			for (var gs in $scope.grid){  
                            if (value.PD_ID == $scope.grid[gs].bi_pdid) {
							   value.PD_OUTQTY = eval($scope.grid[gs].bi_outqty+"+"+value.PD_OUTQTY);
						  }
                       } 						   
					});		
					$rootScope.outMMgrid = $scope.grid ='';
					 $location.path('outMakeMaterial');
				}
			}else{
				var status = '101';
				angular.forEach($scope.order.product, function(value, key){	
			 		if(value.PD_OUTQTY !=0)	{
			 				status = '102'
			 		}
				});	
				if(status == '101'){//修改采集状态
					$scope.order.ENAUDITSTATUS='101';
				}
			  $location.path('outMakeMaterial');
			}
		};
		$scope.findProdcode =  function (){
		   var modalInstance = $modal.open({  
	           templateUrl: 'resources/tpl/input/prodModalContent.html',  
	           controller: 'ProdModalInstanceCtrl',  
	           resolve: {  
	              items: function () {  
	                  return $scope.order.product;  
	              }  
	           }  
           });  
           	modalInstance.result.then(function(selectedItem) {
			    $scope.changeSelection(selectedItem);
			}, function() {
				/*$log.info('Modal dismissed at: ' + new Date());*/
			});
	   }					
	}])
});