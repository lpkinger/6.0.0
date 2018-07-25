define([ 'app/app','service/Purc','service/SupportServices' ], function(app) {
	app.register.controller('OutFinishOprCtrl',['$scope', '$http', '$rootScope', '$location', '$filter','$stateParams','$modal','toaster', 'SupportUtil','OutOper','Ring', 'Online','ngTableParams','$q','PurcOrderItem', function($scope, $http, $rootScope,$location,$filter,$stateParams,$modal,toaster ,SupportUtil,OutOper, Ring,Online,ngTableParams,$q,PurcOrderItem){
	    $scope.grid = $rootScope.outFingrid||[];
	    $scope.getOnline = Online.getOnline;	
		var getOrder = function(code) {//根据路径中的id号获取对应的单据
			var result = null;
			angular.forEach($rootScope.outFinOrders, function(value, key){
				if(value.PI_INOUTNO == code) {
					result = value;
					return result;
				}
			});
			return result;
		};	
		$scope.changeSelection = function(item) {//table with row selection,表格选中列操作
			$scope.ordercode = item.PD_ORDERCODE;
			$scope.bi_prodcode = item.PD_PRODCODE;
			$scope.bi_prodid = item.PR_ID;	
			//$scope.bi_outqty = item.PR_ZXBZS;
			$scope.pr_fbzs = item.PD_OUTQTY;
			$scope.detno = item.PD_PDNO;
			$scope.bi_prodname = item.PR_DETAIL;
			$scope.pr_detail = item.PR_DETAIL+item.PR_SPEC;
			$scope.pd_id = item.PD_ID;
			$scope.pr_id = item.PR_ID;
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
		        filter: {  
		        },
		        sorting: {
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
		
		var checkMakeSerial = function(defer){					
		  //如果有设定的归属工单，必须序列号归属于选定的工单,或者订单（关联MakeSerial表判断)
		  PurcOrderItem.checkMakeSerial({ms_sncode:$scope.bi_barcode,makeCode:$scope.makeCode,saleCode:$scope.saleCode,whcode:$scope.order.PI_WHCODE}, {},function(data){			
		  	   document.getElementById("makeCode").focus();	
		  	   defer.resolve(data.message);  
		     },function(response){
			    defer.reject(response.data.exceptionInfo);	
			 });						
		};
		var checkOutbox = function(defer){
		//包装箱号  内所有序列号的bar_remain值为1，代表没有出库  。箱内总数不需要输入
			OutOper.checkOutbox({barcode:$scope.bi_outboxcode,prodcode:$scope.bi_prodcode,whcode:$scope.order.PI_WHCODE}, {},function(data){	
	            $scope.bi_outqty = data.message;
				document.getElementById("bi_outboxcode").focus();	
	            defer.resolve(data.message);             
			  },function(response){
			  	defer.reject(response.data.exceptionInfo);
			 });									
		};
		var checkSerialqty = function(defer){
			//输入的是序列号的话，bar_remain 为1，剩余数减一
			OutOper.checkSerialqty({pr_fbzs:$scope.pr_fbzs,barcode:$scope.bi_barcode, whcode:$scope.order.PI_WHCODE,prodcode:$scope.bi_prodcode},{},function(data){                             
                $scope.bi_outqty = 1;
                defer.resolve(data.message);
			},function(response){
				defer.reject(response.data.exceptionInfo);
			});							
		}
         //进入界面，表单值默认为grid中的第一行              
		var check =  function (productCode){
			  //校验：1、物料编号存在, 2、数量不超过剩余数量，3、条码号唯一性		
			  //条码储位不允许为空
			  var defer = $q.defer();
			   var reg = /^[0-9]*[1-9][0-9]*$/;
			   if(!$scope.bi_barcode && !$scope.bi_outboxcode){
					defer.reject("请输入序列号或者包装箱号！");
					document.getElementById("bi_barcode").focus();
				}else {					 	
					var item = SupportUtil.contains(JSON.stringify($scope.order.product),productCode,"PD_PRODCODE");
					if(item){
						/*//是否条码校验
					   if($scope.ifbarcodecheck == '1'){
							var pr_idlen =$scope.order.barcodeset.BS_LENPRID;
							//比较物料ID的前几位是否等于条码号的前几位
							var pr_id = new String($scope.pr_id);
							var bar_code =  new String($scope.bi_barcode);						
							if (SupportUtil.lap(pr_id,pr_idlen) != SupportUtil.lap(bar_code,pr_idlen)){
								Ring.error();								
								$scope.bi_barcode ='';
								defer.reject("条码校验错误，该条码与物料不匹配")
							}
					   }*/
					    if($rootScope.outFingrid  && SupportUtil.contains(JSON.stringify($rootScope.outFingrid) ,$scope.bi_barcode,"bi_barcode")){			
								 document.getElementById("bi_barcode").focus();
								 defer.reject("序列号重复");				 									   
					    }else if($scope.bi_barcode && ($scope.makeCode || $scope.saleCode)){
							     checkMakeSerial(defer);
							   	 checkSerialqty(defer);							  
			 	        }else if($scope.bi_barcode){
			 	                 checkSerialqty(defer);	
			 	        }else{
			 	                 checkOutbox(defer);
			 	        }					  
					}else {
						defer.reject("物料不存在"); 					
						Ring.error();						
					}
				}
		    return defer.promise;
		}	  
		$scope.scan =  function(){//确认按钮事件			
			var q = check($scope.bi_prodcode);
			q.then(function(message){				     			
			 	    $scope.barcodes = {};
			 	    $scope.barcodes.bi_barcode = $scope.bi_barcode;
			 	    $scope.barcodes.bi_prodcode = $scope.bi_prodcode ;
			        $scope.barcodes.bi_prodid = $scope.bi_prodid ;	
					$scope.barcodes.bi_inoutno = $scope.order.PI_INOUTNO;
					$scope.barcodes.bi_outqty = $scope.bi_outqty;
			 	    $scope.barcodes.bi_whcode = $scope.order.PI_WHCODE;
			 	    $scope.barcodes.bi_prodname = $scope.bi_prodname;
			 	    $scope.barcodes.bi_pdno = $scope.detno;
			 	    $scope.barcodes.bi_pdid = $scope.pd_id;
			 	    $scope.barcodes.bi_piid = $scope.order.PI_ID;
			 	    $scope.barcodes.bi_outboxcode = $scope.bi_outboxcode;
					//插入数据到待提交缓存列表中grid(barcode,prodcode,inqty,location)	
					$scope.grid.push($scope.barcodes);
					$rootScope.outFingrid = $scope.grid;	
					$scope.pr_fbzs -= $scope.barcodes.bi_outqty;	
					$scope.bi_barcode ='';					
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
						if(confirm("已完成，请提交采集")){								
							$location.path('outFinWaitSubmit/' + $scope.order.PI_INOUTNO);
						}						
					}												 
			     }, function(error){
			 	   toaster.pop('error', '错误',error);
		 	});								
		};					
	/*	$scope.search = function($event, productCode){
			if($event.keyCode == 13) {//Enter事件 、回车事件
				//完成“确认”前判断，判断通过则自动执行“确认”	
				   console.log('scan');
		              $scope.scan();
		  }
		};*/
		
		$scope.submitGet = function(){//提交采集操作，与后台交互
			if($scope.grid.length == 0){
				toaster.pop('error', '没有需要提交的数据');
				return;
			}
			OutOper.saveOutBarcode({}, JSON.stringify($scope.grid),function(data) {//获取成功		
				    toaster.pop('success', '提交成功');
				    $rootScope.outFingrid = $scope.grid ='';
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
				$location.path('outFinWaitSubmit/' + argOrder);
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
					$location.path('outFinish');
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
			  $location.path('outFinish');		
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
	}])
});