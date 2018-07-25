define([ 'app/app','common/services','service/Purc','service/SupportServices'], function(app) {
    app.register.controller('InMMOperationCtrl',['$scope','$http','$modal', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'PurcOrderItem','SupportUtil','PurcOrder', 'Online',
	  function($scope, $http,$modal, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, PurcOrderItem,SupportUtil,PurcOrder, Online){
		$scope.grid = $rootScope.grid||[];    
	    $scope.getOnline = Online.getOnline;
	    $scope.UseLocation = $rootScope.UseLocation;	    	
	  /*   setTimeout( function(){
		  try{
		     document.getElementById("bi_barcode").focus();
		     var labels = document.getElementsByName("required-label");
		     for(var n in labels){
		     	labels[n].style.color = 'red';
		     }
		     var inputs = document.getElementsByTagName("input");
		     for(var n in inputs){
		     	if(typeof inputs[n].getAttribute("required") === "string"){
		     		inputs[n].labels[0].style.color = 'red';
		     	}
		     }
		  } catch(e){}
		}, 100);	*/	   
		var getOrder = function(code) {//根据路径中的id号获取对应的单据
			var result = null;
			angular.forEach($rootScope.orders, function(value, key){
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
			$scope.bi_inoutno = $scope.order.PI_INOUTNO;
			$scope.bi_inqty = item.PR_ZXBZS || $scope.bi_inqty;
			$scope.pr_fbzs = item.PD_INQTY;
			$scope.detno = item.PD_PDNO;
			$scope.pd_id = item.PD_ID;
			$scope.bi_prodname = item.PR_DETAIL;
			if($scope.UseLocation){
			   $scope.bi_location = item.PR_LOCATION || $scope.bi_location;
			}
			$scope.pr_detail = item.PR_DETAIL+item.PR_SPEC;
			$scope.ifbarcodecheck = item.PR_IFBARCODECHECK;
			$scope.pr_id =  item.PR_ID;
         }
		$scope.order = getOrder($stateParams.code);
		$rootScope.title.pi_inoutno =$scope.order.PI_INOUTNO +'   '+$scope.order.PI_WHCODE;
		if($scope.order){
		 var keepGoing = true; 
		 angular.forEach($scope.order.product, function(value, key){	
			if( keepGoing ){
				if(value.PD_INQTY == 0 && $scope.order.product[key+1]){
					if($scope.order.product[key+1].PD_INQTY != 0){
						$scope.changeSelection($scope.order.product[key+1]);
							keepGoing = false ;
					}
				 }else if(value.PD_INQTY != 0)	{
				 	$scope.changeSelection($scope.order.product[key]);
				 	     keepGoing = false;
				 }
			 }
			});				
		  $scope.tableParams = new ngTableParams({//未完成料号名称规格及剩余数量表格
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
		}			
         //进入界面，表单值默认为grid中的第一行              
		var check =  function (productCode){
			  //校验：1、物料编号存在, 2、数量不超过剩余数量，3、条码号唯一性		
			  //条码储位不允许为空
			  var reg = /^[0-9]*[1-9][0-9]*$/;
			   if(!$scope.bi_barcode){
					alert("请输入条码!");
					document.getElementById("bi_barcode").focus();
					return ;
				}else if($scope.UseLocation &&!$scope.bi_location){					
					alert("请输入储位!");
					document.getElementById("bi_location").focus();
					return ;
				}else if(!reg.test($scope.bi_inqty)){
					Ring.error();
					alert("数量格式不正确！");
					document.getElementById("bi_inqty").focus();
					return ;
				}
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
								alert ("条码校验错误，该条码与物料不匹配");
								$scope.bi_barcode ='';
								return false;
							}
					}						
					if($scope.bi_inqty !=0 && $scope.bi_inqty >0 && ($scope.bi_inqty < $scope.pr_fbzs || $scope.bi_inqty == $scope.pr_fbzs) && $scope.pr_fbzs > -1 ){
		                  if($rootScope.grid){
							    if(!(SupportUtil.contains(JSON.stringify($rootScope.grid) ,$scope.bi_barcode,"bi_barcode"))){
							 	   		return true;				 	
							    }else{
							       Ring.error();
							 	   toaster.pop('error', '条码号重复');
							 	   $scope.bi_barcode ='';
							 	   document.getElementById("bi_barcode").focus();
							 	   return false;
							    }
						     }else{
						     	return true;
						     }
				     }else {
				     	 Ring.error();
				     	 $scope.bi_inqty = '';
				     	 document.getElementById("bi_inqty").focus();
				   	     alert("数量不能大于剩余数量或者为空");
				   	     return false;
				     }
				}else {
					alert("物料不存在"); 					
					Ring.error();
					return false;
				}				
		}	  
		$scope.message = {};
		$scope.scan =  function(){//确认按钮事件
			 if(check($scope.bi_prodcode)){		
			 	    $scope.barcodes = {};
			 	    $scope.barcodes.bi_barcode = $scope.bi_barcode;
			 	    $scope.barcodes.bi_prodcode = $scope.bi_prodcode ;
			        $scope.barcodes.bi_prodid = $scope.bi_prodid ;	
					$scope.barcodes.bi_inoutno = $scope.bi_inoutno;
					$scope.barcodes.bi_inqty = $scope.bi_inqty;
					if($scope.UseLocation){
					   $scope.barcodes.bi_location = $scope.bi_location;
					}
			 	    $scope.barcodes.bi_whcode = $scope.order.PI_WHCODE;
			 	    $scope.barcodes.bi_prodname = $scope.bi_prodname;
			 	    $scope.barcodes.bi_pdno = $scope.detno;
			 	    $scope.barcodes.bi_pdid = $scope.pd_id;
			 	    $scope.barcodes.bi_piid = $scope.order.PI_ID;
					//插入数据到待提交缓存列表中grid(barcode,prodcode,inqty,location)	
					$scope.grid.push($scope.barcodes);
					$rootScope.grid = $scope.grid;	
					$scope.pr_fbzs -= $scope.barcodes.bi_inqty;	
					$scope.bi_barcode ='';
					document.getElementById("bi_barcode").focus();
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
							$location.path('inMMWaitSubmit/' + $scope.order.PI_INOUTNO);
						}						
					}					 
			 }
		};			
		$scope.submitGet = function(){//提交采集操作，与后台交互	
			if($scope.grid.length != 0){
				PurcOrderItem.saveBarcode({}, angular.fromJson($scope.grid),function(data) {//获取成功		
					    $rootScope.grid = $scope.grid ='';
					    Ring.success();
					    toaster.pop('success', '提交成功');
					}, function(response){//获取失败处理
						if(response.status == 0){ //无网络错误
						   Online.setOnline(false);//修改网络状态	
						   Ring.error();
						   toaster.pop('error', '提交失败',"网络连接不可用，请稍后再试");
						}
						else {
							Ring.error();
						  toaster.pop('error', '提交失败',response.data.exceptionInfo);
						}
					});
			}else {
				 Ring.error();
				 toaster.pop('error', '无数据');						 
			}
		};
				
		$scope.getList = function(argOrder){//点击已采集列表
			if(argOrder){				
				$location.path('inMMWaitSubmit/' + argOrder);
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
					$rootScope.grid = $scope.grid ='';
					$location.path('inMakeMaterial');
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
				$location.path('inMakeMaterial');
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
			}, function() {
				 /*$log.info('Modal dismissed at: ' + new Date());*/
			});
		}
	}]);
});