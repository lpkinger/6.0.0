define([ 'app/app','common/services','service/Purc','service/SupportServices' ], function(app) {
  	app.register.controller('InFinishOprCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location','$modal', 'ngTableParams','toaster', 'Ring', 'PurcOrderItem','SupportUtil', 'Online','$q','PurcOrder', function($scope, $http, $stateParams, $rootScope,$filter,$location,$modal, ngTableParams,toaster, Ring, PurcOrderItem,SupportUtil, Online,$q,PurcOrder){
	    $scope.grid = $rootScope.fisgrid||[];
	    $scope.getOnline = Online.getOnline;
	    $scope.UseLocation = $rootScope.UseLocation;	     
		var getOrder = function(code) {//根据路径中的id号获取对应的单据
			var result = null;
			angular.forEach($rootScope.fisOrders, function(value, key){
				if(value.PI_INOUTNO == code) {
					result = value;
					return result;
				}
			});
			return result;
		};		
		$scope.changeSelection = function(item) {//table with row selection,表格选中列操作
			document.getElementById("bi_barcode").focus();
			$scope.ordercode = item.PD_ORDERCODE;
			$scope.bi_prodcode = item.PD_PRODCODE;
			$scope.bi_prodid = item.PR_ID;	
			$scope.bi_prodspec = item.PD_PRODCODE+item.PR_DETAIL;
			$scope.bi_inoutno = $scope.order.PI_INOUTNO;			
			$scope.bi_inqty = item.PR_ZXBZS|| $scope.bi_inqty;
			$scope.pr_fbzs = item.PD_INQTY;
			if($scope.UseLocation)
			   $scope.bi_location =item.PR_LOCATION;
			$scope.bi_prodname = item.PR_DETAIL;
			$scope.detno = item.PD_PDNO;
			$scope.pd_id = item.PD_ID;
			$scope.pr_id = item.PR_ID;
         }
		$scope.order = getOrder($stateParams.code);
		$rootScope.title = {};
		$rootScope.title.pi_inoutno = $scope.order.PI_INOUTNO +'   '+$scope.order.PI_WHCODE;
		if($scope.order){
		  $scope.tableParams = new ngTableParams({//已经采集完成的列表
		        page: 1,           
		        count: 10,          
		        filter: {  
		        },
		        sorting: {
		        	'PD_PDNO':'asc'
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
		    var keepGoing = true; 
		    angular.forEach($scope.order.product, function(value, key){	
			if( keepGoing ){
				if(value.PD_INQTY == 0 && $scope.order.product[key+1].PD_INQTY != 0){
					$scope.changeSelection($scope.order.product[key+1]);
							keepGoing = false ;
				 }else 	{
				 	$scope.changeSelection($scope.order.product[key]);
				 	     keepGoing = false;
				 }
			}
			});		
		};
		
	    var checkOutbox = function(defer){
			//包装箱号，则根据包装表packageDetail获取所有序列号和总数，
			//如果外包装不止一层可能存在递归，如果总数不超过剩余数则执行插入到grid。
			PurcOrderItem.getPackageCode({pr_fqty:$scope.pr_fbzs,pa_outboxcode:$scope.bi_outboxcode}, {},function(data){	            
                 defer.resolve(data.message);
			  },function(response){  
                document.getElementById("bi_outboxcode").focus();
                Ring.error();
			  	defer.reject(response.data.exceptionInfo);
			});
	    };
	    var checkMakeSerial = function (defer){	    			
			//如果有设定的归属工单，必须序列号归属于选定的工单（关联MakeSerial表判断），
			//5、如果该序列号已经存在库存(barcode表remain>0 )，不允许重复入库。
	    	if($scope.makeSerial){	    	
			    PurcOrderItem.checkMakeSerial({ms_sncode:$scope.bi_barcode,makeCode:$scope.makeSerial,whcode:$scope.order.PI_WHCODE}, {},function(data){
					defer.resolve(data.message);
				},function(response){ 
					document.getElementById("makeSerial").focus();
					Ring.error();
					defer.reject(response.data.exceptionInfo);		
				});	
			}else{
				defer.resolve('');
			}
	    } 		
		var check =  function (){
			  //校验：1、物料编号存在, 2、数量不超过剩余数量，3、条码号唯一性		
			  //条码储位不允许为空
			 var defer = $q.defer();
			  var reg = /^[0-9]*[1-9][0-9]*$/;
			   if(!$scope.bi_barcode && !$scope.bi_outboxcode){	
		           document.getElementById("bi_barcode").focus();
		           Ring.error();
				   defer.reject("请输入序列号或者包装箱号!");
				}else if($scope.UseLocation && !$scope.bi_location){
					document.getElementById("bi_location").focus();
					Ring.error();
					defer.reject("请输入储位!");
				}else if(!reg.test($scope.bi_inqty)){
					document.getElementById("bi_inqty").focus();
					Ring.error();
					defer.reject("数量格式不正确!");
				}else{					
					var item = SupportUtil.contains(JSON.stringify($scope.order.product),$scope.bi_prodcode,"PD_PRODCODE");
				    if(item){
				    	/*//是否条码校验
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
					   }	*/
					  if($scope.bi_inqty !=0 && $scope.bi_inqty >0 && ($scope.bi_inqty < $scope.pr_fbzs || $scope.bi_inqty == $scope.pr_fbzs) && $scope.pr_fbzs > -1 ){
		                  if($scope.bi_barcode){
		                  	 if($rootScope.fisgrid && SupportUtil.contains(JSON.stringify($rootScope.fisgrid) ,$scope.bi_barcode,"bi_barcode")){
		                  		document.getElementById("bi_barcode").focus();
		                  		Ring.error();
							    defer.reject("序列号重复");		
		                  	 }else 
		                  	    checkMakeSerial(defer);
		                  }else{
		                  	 if($rootScope.fisgrid && $scope.bi_outboxcode && SupportUtil.contains(JSON.stringify($rootScope.fisgrid) ,$scope.bi_outboxcode,"bi_outboxcode")){
		                  	 	 Ring.error();
		                  	 	 defer.reject("包装箱号重复");
		                  	 }else{
		                  	 	 checkOutbox(defer);
		                  	 }
		                  }		       
				       }else {
				       	 Ring.error();
				   	     defer.reject("数量不能大于剩余数量或者为空");
				      }
				   }else {
					  defer.reject("物料不存在");						
					  Ring.error();
				    }				 
			 }				
			  return defer.promise;						
		}; 
		$scope.scan =  function(){//确认按钮事件			
			var q = check();
			q.then(function(message){
				    $scope.barcodes = {};
			 	    $scope.barcodes.bi_barcode = $scope.bi_barcode;			 	    
			 	    $scope.barcodes.bi_prodcode = $scope.bi_prodcode ;
			        $scope.barcodes.bi_prodid = $scope.bi_prodid ;	
					$scope.barcodes.bi_inoutno = $scope.bi_inoutno;
					$scope.barcodes.bi_inqty = $scope.bi_inqty;
					if($scope.UseLocation)
					   $scope.barcodes.bi_location = $scope.bi_location;
			 	    $scope.barcodes.bi_whcode = $scope.order.PI_WHCODE;
			 	    $scope.barcodes.bi_outboxcode = $scope.bi_outboxcode ;
			 	    $scope.barcodes.bi_prodname =  $scope.bi_prodname;
			 	    $scope.barcodes.bi_pdno = $scope.detno;
			 	    $scope.barcodes.bi_pdid = $scope.pd_id;
			 	    $scope.barcodes.bi_piid = $scope.order.PI_ID;
					//插入数据到待提交缓存列表中grid(barcode,prodcode,inqty,location)	
					$scope.grid.push($scope.barcodes);
					$rootScope.fisgrid = $scope.grid;	
					$scope.pr_fbzs -= $scope.barcodes.bi_inqty;	
					$scope.bi_barcode ='';
					Ring.success();
					angular.forEach($scope.order.product, function(value, key){	
					  if(value.PD_ID == $scope.pd_id) {		
							value.PD_INQTY = $scope.pr_fbzs ;
							$scope.tableParams.reload();
						if(value.PD_INQTY == 0 && $scope.order.product[key+1] ){
							$scope.changeSelection($scope.order.product[key+1]);
					   	 }								
					  }					   
					});						
					var keepGoing = true; 
					angular.forEach($scope.order.product, function(value, key){	
						if(keepGoing){
							if(value.PD_INQTY != 0 ){									
								keepGoing = false ;
							}
						}
					});	
					if(keepGoing){						
						if(confirm("已完成，请提交采集")){								
							$location.path('inFinishWaitSubmit/' + $scope.order.PI_INOUTNO);
						}						
					}							
			 }, function(error){
			 	Ring.error();
				toaster.pop('error', '错误',error);
			});
		};					
		$scope.submitGet = function(){//提交采集操作，与后台交互	
		if($scope.grid.length >0){
			PurcOrderItem.saveBarcode({}, angular.fromJson($scope.grid),function(data) {//获取成功	
				   if(data.message){
				    Ring.error();
				   	 toaster.pop('error', '提交失败',data.message);
				   }else{
				    Ring.success();
				   	 toaster.pop('success', '提交成功');
				   }
				    $rootScope.fisgrid = $scope.grid ='';				   
				}, function(response){//获取失败处理
					if(response.status == 0){ //无网络错误
					   Online.setOnline(false);//修改网络状态	
					   Ring.error();
					   toaster.pop('error', '提交失败',"网络连接不可用，请稍后再试");
					}else {
						Ring.error();
					  toaster.pop('error', '提交失败',response.data.exceptionInfo);
					}
				});
		   }else{
		      Ring.error();
		   	  toaster.pop('error', '没有需要提交的数据');
		   }
		};
				
		$scope.getList = function(argOrder){//点击
			if(argOrder){
				$location.path('inFinishWaitSubmit/' + argOrder);
			}
		};
		
		$scope.returnInput = function(){//当前单据有未提交的，提示先提交。
			if($scope.grid.length != 0){
				if(confirm("返回将清空未提交数据，确认返回？")){										
			 		angular.forEach($scope.order.product, function(value, key){	
			 			for (var gs in $scope.grid){  
                            if (value.PD_ID == $scope.grid[gs].bi_pdid) {
							   value.PD_INQTY = eval($scope.grid[gs].bi_inqty+"+"+value.PD_INQTY);
						  }
                       } 						   
					});					 			 					    
					$rootScope.fisgrid = $scope.grid ='';
					$location.path('inFinish');
				}				
			}else{
			  var status = '101';
				angular.forEach($scope.order.product, function(value, key){	
			 		if(value.PD_INQTY !=0)	{
			 				status = '102'
			 		}
				});	
				if(status == '101'){//修改采集状态
					$scope.order.ENAUDITSTATUS='101';
				}
				$location.path('inFinish');
			}
			
		}	
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
						});
		     }					
	}]);
});