define([ 'angularAMD','toaster', 'ngTable', 'common/services', 'service/Purc','service/SupportServices', 'ui.router', 'ui.bootstrap','directive/SmartDirectives','ngTouch'], function(angularAMD) {
	'use strict';
	var app = angular.module('myApp', ['ngAnimate', 'toaster','ngTable', 'ui.router', 'common.services', 'PurcServices', 'SupportServices','ui.bootstrap','SmartDirectives','ngTouch']);
	app.init = function() {
		angularAMD.bootstrap(app);
	};
	app.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise('/index');
		$stateProvider.state('index', {
			url : "/index",
			templateUrl : "resources/tpl/index/index.html"
		}).state('inContents',angularAMD.route({
		    url:'/inContents',
		    templateUrl:'resources/tpl/input/inContents.html',
		    controllerUrl : 'app/controllers/input/InputCtrl'
		})).state('inMakeMaterial', angularAMD.route({
			url : "/inMakeMaterial",
			templateUrl : "resources/tpl/input/inMakeMaterial.html",
			controllerUrl : 'app/controllers/input/InputCtrl'
		})).state('inMMOperation', angularAMD.route({
			url: '/inMMOperation/:code',
			templateUrl: 'resources/tpl/input/inMMOperation.html',
			controllerUrl : 'app/controllers/input/InMMOperationCtrl'
		})).state('inMMWaitSubmit',angularAMD.route({
		   url:'/inMMWaitSubmit/:ioNocode',
		   templateUrl:'resources/tpl/input/inMMWaitSubmit.html',
		   controllerUrl : 'app/controllers/input/InMMwaitSubmitCtrl'
		})).state('inFinish',angularAMD.route({
    		url:'/inFinish',
	        templateUrl : 'resources/tpl/input/inFinish.html',
			controllerUrl : 'app/controllers/input/InFinishCtrl'	       	   	  
    	})).state('inFinishOperation',angularAMD.route({
		   url:'/inFinishOperation/:code',
		   templateUrl:'resources/tpl/input/inFinishOperation.html',
		   controllerUrl : 'app/controllers/input/InFinishOprCtrl'
		})).state('inFinishWaitSubmit',angularAMD.route({
		   url:'/inFinishWaitSubmit/:ioNocode',
		   templateUrl:'resources/tpl/input/inFinishWaitSubmit.html',
		   controllerUrl : 'app/controllers/input/InfinishWaitSubmitCtrl'	  
		})).state('inMakeMaterialCheck',angularAMD.route({
    		url:'/inMakeMaterialCheck/:type',
	        templateUrl : 'resources/tpl/input/inMakeMaterialCheck.html',
			controllerUrl : 'app/controllers/input/InMMCheckCtrl'	       	   	  
    	})).state('inMMCheckOp',angularAMD.route({
    		url:'/inMMCheckOp/:code',
	        templateUrl : 'resources/tpl/input/inMMCheckOp.html',
			controllerUrl : 'app/controllers/input/InMMCheckOpCtrl'	       		  
    	})).state('inFinishCheckOp',angularAMD.route({
    		url:'/inFinishCheckOp/:code',
	        templateUrl : 'resources/tpl/input/inFinishCheckOp.html',
			controllerUrl : 'app/controllers/input/InFinCheckOpCtrl'	       		  
    	})).state('outContents',angularAMD.route({
    		url:'/outContents',
	        templateUrl : 'resources/tpl/output/outContents.html',  
			controllerUrl : 'app/controllers/output/OutputCtrl' 
    	})).state('outMakeMaterial',angularAMD.route({
    		url:'/outMakeMaterial',
	        templateUrl : 'resources/tpl/output/outMakeMaterial.html',  
			controllerUrl : 'app/controllers/output/OutputCtrl' 
    	})).state('outMMOperation',angularAMD.route({
    		url:'/outMMOperation/:code',
	        templateUrl : 'resources/tpl/output/outMMOperation.html',  
			controllerUrl : 'app/controllers/output/OutMMOperationCtrl' 
    	})).state('outMMWaitSubmit',angularAMD.route({
		   url:'/outMMWaitSubmit/:ioNocode',
		   templateUrl:'resources/tpl/output/outMMWaitSubmit.html',
		   controllerUrl : 'app/controllers/output/OutMMwaitSubmitCtrl' 
		})).state('outFinish',angularAMD.route({
		   url:'/outFinish',
		   templateUrl:'resources/tpl/output/outFinish.html',
		   controllerUrl : 'app/controllers/output/OutFinishCtrl' 
		})).state('outFinishOprCtrl',angularAMD.route({
		   url:'/outFinishOperation/:code',
		   templateUrl:'resources/tpl/output/outFinishOperation.html',
		   controllerUrl : 'app/controllers/output/OutFinishOprCtrl' 
		})).state('outFinWaitSubmit',angularAMD.route({
		   url:'/outFinWaitSubmit/:ioNocode',
		   templateUrl:'resources/tpl/output/outFinWaitSubmit.html',
		   controllerUrl : 'app/controllers/output/OutFinWaitSubmitCtrl' 
		})).state('countingContents',{
		   url:'/countingContents',
		   templateUrl:'resources/tpl/counting/countingContents.html'
		}).state('countingMM',{
		   url:'/countingMM',
		   templateUrl:'resources/tpl/counting/countingMM.html'
		}).state('countingFinish',{
		   url:'/countingFinish',
		   templateUrl:'resources/tpl/counting/countingFinish.html'
		}).state('countingMMWaitSub',{
		   url:'/countingMMWaitSub',
		   templateUrl:'resources/tpl/counting/countingMMWaitSub.html'
		}).state('countingFinWaitSub',{
		   url:'/countingFinWaitSub',
		   templateUrl:'resources/tpl/counting/countingFinWaitSub.html'
		}).state('checkContents',angularAMD.route({
		   url:'/checkContents',
		   templateUrl:'resources/tpl/check/checkContents.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkMM',angularAMD.route({
		   url:'/checkMM',
		   templateUrl:'resources/tpl/check/checkMM.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkBarcode',angularAMD.route({
		   url:'/checkBarcode',
		   templateUrl:'resources/tpl/check/checkBarcode.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkPackage',angularAMD.route({
		   url:'/checkPackage',
		   templateUrl:'resources/tpl/check/checkPackage.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkMakeFin',angularAMD.route({
		   url:'/checkMakeFin',
		   templateUrl:'resources/tpl/check/checkMakeFin.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkOrderFin',angularAMD.route({
		   url:'/checkOrderFin',
		   templateUrl:'resources/tpl/check/checkOrderFin.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('checkPO',angularAMD.route({
		   url:'/checkPO',
		   templateUrl:'resources/tpl/check/checkPO.html',
		   controllerUrl : 'app/controllers/check/CheckCtrl'
		})).state('locationTransfer',angularAMD.route({
		   url:'/locationTransfer',
		   templateUrl:'resources/tpl/locationTransfer/locationTransfer.html',
		   controllerUrl : 'app/controllers/locationTransfer/LocationTransferCtrl'
		})).state('batchContents',angularAMD.route({
		   url:'/batchContents',
		   templateUrl:'resources/tpl/batch/batchContents.html',
		   controllerUrl : 'app/controllers/batch/BreakingBatchCtrl'
		})).state('breakingBatch',angularAMD.route({
		   url:'/breakingBatch/:type',
		   templateUrl:'resources/tpl/batch/breakingBatch.html',
		   controllerUrl : 'app/controllers/batch/BreakingBatchCtrl'
		})).state('breakingPackage',angularAMD.route({
		   url:'/breakingPackage',
		   templateUrl:'resources/tpl/batch/breakingPackage.html',
		   controllerUrl : 'app/controllers/batch/BreakingPackageCtrl'
		})).state('combineBatch',angularAMD.route({
		   url:'/combineBatch',
		   templateUrl:'resources/tpl/batch/combineBatch.html',
		   controllerUrl : 'app/controllers/batch/CombineBatchCtrl'
		})).state('settingContents',angularAMD.route({
		   url:'/settingContents',
		   templateUrl:'resources/tpl/setting/settingContents.html',
		   controllerUrl : 'app/controllers/setting/SettingCtrl'
		})).state('haveSubmitList',angularAMD.route({
		   url:'/haveSubmitList/:pi_id/:inout/:pi_inoutno',
		   templateUrl:'resources/tpl/input/haveSubmitList.html',
		   controllerUrl : 'app/controllers/input/HaveSubmitListCtrl'
		})).state('manageContents',angularAMD.route({
		   url:'/manageContents',
		   templateUrl:'resources/tpl/mes/manageContents.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedCtrl'
		})).state('SMTFeed',angularAMD.route({
		   url:'/SMTFeed',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeed.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedCtrl'
		})).state('SMTFeedContents',angularAMD.route({
		   url:'/SMTFeedContents',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedContents.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedCtrl'
		})).state('SMTFeedI',angularAMD.route({
		   url:'/SMTFeedI/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedI.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('SMTFeedD',angularAMD.route({
		   url:'/SMTFeedD/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedD.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('SMTFeedJ',angularAMD.route({
		   url:'/SMTFeedJ/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedJC.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('SMTFeedC',angularAMD.route({
		   url:'/SMTFeedC/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedJC.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('SMTFeedQ',angularAMD.route({
		   url:'/SMTFeedQ/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedQuery.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('SMTFeedCheck',angularAMD.route({
		   url:'/SMTFeedCheck/:type',
		   templateUrl:'resources/tpl/mes/SMTFeed/SMTFeedCheck.html',
		   controllerUrl : 'app/controllers/mes/SMTFeedIDCtrl'
		})).state('FeederUse',angularAMD.route({
		   url:'/FeederUse',
		   templateUrl:'resources/tpl/mes/FeederUse.html',
		   controllerUrl : 'app/controllers/mes/FeederUseCtrl'
		})).state('FeederOperate',angularAMD.route({
		   url:'/FeederOperate/:type',
		   templateUrl:'resources/tpl/mes/FeederOperate.html',
		   controllerUrl : 'app/controllers/mes/FeederUseCtrl'
		})).state('makePrepare',angularAMD.route({
		   url:'/makePrepare/:type',
		   templateUrl:'resources/tpl/mes/makePrepare/makePrepare.html',
		   controllerUrl : 'app/controllers/mes/MakePrepareCtrl'
		})).state('makePrepareFeeder',angularAMD.route({
		   url:'/makePrepareFeeder/:type',
		   templateUrl:'resources/tpl/mes/makePrepare/makePrepareList.html',
		   controllerUrl : 'app/controllers/mes/MPrepareFeederCtrl'
		})).state('feederGet',angularAMD.route({
		   url:'/feederGet/:type',
		   templateUrl:'resources/tpl/mes/makePrepare/FeederGet.html',
		   controllerUrl : 'app/controllers/mes/MPrepareFeederCtrl'
		})).state('msdContents',angularAMD.route({
		   url:'/msdContents',
		   templateUrl:'resources/tpl/msd/msdContents.html',
		   controllerUrl : 'app/controllers/msd/MsdCtrl'
		})).state('msdinOven',angularAMD.route({
		   url:'/inOven',
		   templateUrl:'resources/tpl/msd/inOven.html',
		   controllerUrl : 'app/controllers/msd/MsdCtrl'
		})).state('msdoutOven',angularAMD.route({
		   url:'/outOven',
		   templateUrl:'resources/tpl/msd/outOven.html',
		   controllerUrl : 'app/controllers/msd/MsdCtrl'
		})).state('tailingBack',angularAMD.route({
		   url:'/tailingBack',
		   templateUrl:'resources/tpl/mes/tailingBack.html',
		   controllerUrl : 'app/controllers/mes/TailingBackCtrl'
		}));
	}]);
	app.factory('Ring', function(){//响铃，直接调用
		return {
			success: function(){
				document.getElementById('successRing').play();
			}, error: function(){
				document.getElementById('errorRing').play();
			}
		}
	}).factory('Online',['$rootScope', function($rootScope){//在线状态，全局获取、设置
		var status = {online: true};
		return {
			setOnline: function(value){
				status.online = value;
				$rootScope.$broadcast('online', value);
			}, getOnline: function(){
				return status.online;
			}
		}
	}]);
	app.factory('StatusCode', function(){
		var statusConfig = {
				'101' : '已采集',
				'102' : '未采集',
				'103' : '采集中'
		};
		return {
			get: function(code) {
				return statusConfig[code];
			}
		};
	}).filter('status', ['StatusCode', function(StatusCode){
		return function(data) {
			return StatusCode.get(data);
		}
	}]);
	app.controller('AuthCtrl',['$scope', '$rootScope','$window', 'AuthenticationService','Online','BaseService','SessionService','$modal','$timeout','PurcOrder','$state', function($scope, $rootScope,$window, AuthenticationService,Online,BaseService,SessionService,$modal,$timeout,PurcOrder,$state) {
		//控制手机浏览器全屏 找到支持的方法, 使用需要全屏的 element 调用
		$rootScope.title = {};	  		
		$scope.status = {online: true};		
		$scope.$watch('status.online', function(value){//检测用户手动切换在线状态
			Online.setOnline(value);
		});
		$scope.$on('online', function(data){//监听Service中的状态变化
			$scope.status.online = Online.getOnline();			
		});
 		Online.setOnline(AuthenticationService.isAuthed());
		$rootScope.userInfo = {};
		AuthenticationService.getAuthentication().success(function(data) {
			$rootScope.userInfo = data;
			if (data == null || !data.em_code){
			  Online.setOnline(false);
			}
		});
		$scope.logout = function() {//注销
			AuthenticationService.logout().success(function() {
				var  rootPath= BaseService.getRootPath();
				$window.location.href = rootPath+'/jsps/PDAmobile/signin.html';
			});
		};
		$rootScope.$on('$stateChangeSuccess',function(event, toState, toParams, fromState, fromParams){
			// 如果用户不存在
			if(!$rootScope.userInfo){
				event.preventDefault();// 取消默认跳转行为
				var  rootPath= BaseService.getRootPath();
				window.location.replace('signin.html');
				$state.go("login",{from:fromState.name,w:'notLogin'});//跳转到登录界面
			}
		 });
		
		/*$scope.reLogin = function(){//用户点击在线时
			//读取cookie中的信息，提示登录
		    var modalInstance = $modal.open({  
	              templateUrl: 'againLogin.html',  
	              controller: 'LoginCtrl',  
	              resolve: {  
	                   items: function () {  
	                      return $rootScope.userInfo.currentMaster;  
	                    }  
	                }  
           	});  
           	modalInstance.result.then(function(result) {
				  Online.setOnline(result);
			    }, function() {
				  $log.info('Modal dismissed at: ' + new Date());
			});
		};
		
		var checkOk =  function(){
			console.log('执行$timeout回调');//发送心跳包
		   $timeout(function(){
              checkOk();
            },1000);
		};
		 $timeout(function(){
              checkOk();
          },8000);*/
		   PurcOrder.getDescription({		 
			    tablename:'configs', 
				field:'data', 
				condition:"code='UseLocationOrNot' and caller='BarCodeSetting'"
		   },{},function(data){        
			  if(data.description == 'N' || data.description == 0){//不启用仓位
					$rootScope.UseLocation = false;
			  }else{
					$rootScope.UseLocation = true;
				}
		   },function(res){
		       	
		  });    		
	}]);	
	
	app.controller('LoginCtrl', ['$rootScope','$scope', '$modalInstance', 'toaster', 'AuthenticationService','BaseService','SessionService','items',function($rootScope,$scope, $modalInstance, toaster, AuthenticationService,BaseService,SessionService,items) {
		$scope.loading = false;
		$scope.user = {
			j_username : SessionService.getCookie('PDA_USERNAME'),
			j_password :'',
			remember_me : true,
			master:	items.ma_name
		};
			
		$scope.login = function(user, _url) {			
			if($scope.user.j_username == '' ||$scope.user.j_password == ''){
				alert("还有必填项没有填写！");
				return ;
			}else{
				$scope.loading = true;
				AuthenticationService.login(user).success(function(responseText, status) {								
					if(responseText) {
					  $scope.loading = false;
					  toaster.pop('error', '登录失败', responseText);
					  $modalInstance.close(false);
					}else if(status == 200){
						if(user.remember_me)
							SessionService.setCookie('PDA_USERNAME', user.j_username);
						else
							SessionService.removeCookie('PDA_USERNAME');
							//登录成功
							$modalInstance.close(true);
							AuthenticationService.getAuthentication().success(function(data) {
							$rootScope.userInfo = data;
							if (data == null || !data.em_code){
							  Online.setOnline(false);
							}
						});
					}
				}).error(function(responseText,status) {
					$scope.loading = false;
					if(status == 0){
						Online.setOnline(false);//修改网络状态	
						Ring.error();
					    toaster.pop('error', '登录失败',"网络连接不可用，请稍后再试");
					}else{
					   toaster.pop('error', '登录失败', responseText || '用户名或密码错误');
					}
				});
			}
		};
	}]);
	
	app.controller('IndexCtrl',['$scope','$rootScope','$state', function($scope, $rootScope,$state){		
	}]);			
	app.controller('ModalInstanceCtrl',['$scope', '$modalInstance', 'items','SessionService','Print','toaster','MakePrepareOper','Ring',function($scope,$modalInstance,items,SessionService,Print,toaster,MakePrepareOper,Ring) {
	  $scope.items = items;	
	  $scope.colors = [//可以选择的颜色
	      {
	        id: 'red',
	        name: '红色'	        
	      }
	     ];
	  $scope.choose = [{id:1,name:'是'},{id:0,name:'否'}];//是否停用
	  $scope.items.isuse = 0;//绑定，默认是否停用中的0,不停用
	  $scope.ok = function(selectValue) {	  	
	    $modalInstance.close(selectValue.PD_WHCODE);
	  };
	  $scope.selectBill = function(item){
	  	$modalInstance.close(item);
	  };
	  $scope.cancel = function() {
	    $modalInstance.dismiss('cancel');	   
	  };	  
	  $scope.confirmDefault = function(items){//设置默认仓库,Ip
	   	$modalInstance.close(items);
	   };	
	  $scope.confirmBack = function(){
	  	if($scope.items.isuse == 1){
		   	if($scope.items.reason == ''){
		   		alert("请填写停用原因!");	   		
		   	}
		}
	  }
	  $scope.search =  function ($event,items){
	   	if($event == 13){
	   		$modalInstance.close(items);
	   	}	   	
	   };
	   $scope.setStyle =  function(){//设置样式
	   	 $modalInstance.close($scope.items);
	   };
	   $scope.confirmPrint =  function(items){//设置默认打印机
	   	$modalInstance.close(items);
	   };
	   $scope.returnBatching =  function(item){//返回继续拆分
	   		$modalInstance.close(items);
	   }
	   $scope.print = function (){//打印
	   	  if(confirm('确认打印')){
		   	 Print.get({data:angular.fromJson($scope.items)},function(data){
		   	     if(data.message){
		   	      	toaster.pop('error', data.message);		   	      	
		   	      }else {
		   	      	 toaster.pop('success', "打印成功!");
		   	      }
		   	   },function(res){
		   	   	 if(res.status == 0){ //无网络错误
					Online.setOnline(false);//修改网络状态	
					Ring.error();
					toaster.pop('error', "网络连接不可用，请稍后再试");
				 }else{
				 	 toaster.pop('error', res.data.exceptionInfo);
				 }
		   	   });
	   		}
	    };
	    $scope.enterCode = function(event,code,type){
	    	if(event.keyCode == '13'){
	    		if(type == 'bar'){
	    		  $scope.confirmBarcodeBack(code);
	    		}else{
	    		  $scope.confirmFBarcode(code);
	    		}
	    	}
	    };
	    $scope.confirmFBarcode = function(code){//备料单飞达取消上料
	    	MakePrepareOper.feederBack({bar_code:code,mp_id:$scope.items.mp_id},{},function(data){
	    	  toaster.pop('success', "料卷号："+code+"取消飞达上料成功!");
	    	  $modalInstance.close("料卷号："+code+"取消飞达上料成功!");
	    	},function(res){
	    		$scope.barcode='';
	    		toaster.pop('error', res.data.exceptionInfo);
	    	});
	    };
	    $scope.confirmBarcodeBack = function(code){//备料单取消barcode 备料
	    	MakePrepareOper.barBack({barcode:code,mpid:$scope.items.mp_id},{},function(data){
				   toaster.pop('success', '料卷号：'+code+'退回成功!');	
				   $modalInstance.close(data.message);			 
			 },function(res){
			 	    $scope.barcode = '';
					Ring.error();
					toaster.pop('error', '错误',res.data.exceptionInfo);
			}); 	
	    }
     }]);
	
	app.controller('ProdModalInstanceCtrl',['$scope', '$modalInstance', 'items',function($scope, $modalInstance, items) {
	  $scope.items = items;	
	  $scope.wh = {};
	  $scope.selectPrCode = function(selectValue) {	
	    $modalInstance.close(selectValue);
	  };
	  $scope.cancel = function() {
	    $modalInstance.dismiss('cancel');
	  };
     }]);
				
	app.controller('CountingCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil','CountingOper', 'Online','SessionService',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil,CountingOper, Online,SessionService){
		$scope.stocking = $rootScope.stocking||'';
		$scope.st_whcode = SessionService.getCookie('defaultWhcode');
		$scope.find = function(event,st_code){
			if(event.keyCode === 13){
			    $scope.search(st_code);
			}
		}
		$scope.search =  function(st_code){
		 if(st_code){
			CountingOper.get({st_code:st_code},function(data){
				if(data.target && data.totalCount > 0){
						var or = new Object();
						or  = data.target;
						$scope.stocking = data.target[0];
						$rootScope.stocking = $scope.stocking;
						$scope.st_whcode = $scope.stocking.ST_WHCODE;
		           }
				},function(response){
					 toaster.pop('error', '查询失败',response.data.exceptionInfo);
		             Ring.error();
				});	
			}else{
				alert("请输入盘点底稿编号！");
				document.getElementById("st_code").focus()
			}
		};		
		$scope.confirm  =  function(){
			if($scope.Counting == 'makeMaterial'){
				$location.path("countingMM");
			}else if($scope.Counting == 'Finish'){
				$location.path("countingFinish");
			}else{
				alert("请选择判断类型！");
			}
		}								
	}]);

	app.controller('CountingMMCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil','CountingOper', 'Online',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil,CountingOper, Online){
	   $scope.stocking = $rootScope.stocking||'';	
	   $scope.grid = $rootScope.cmGrid||[];
	   var check =  function(){//条码号不为空；2、数量大于0；3、条码不存在于已采集列表
	   	  if(!$scope.barcodes.stb_barcode){
	   	  	document.getElementById("bi_barcode").focus();
	   	    Ring.error();
	   	  	 alert("条码号不为空");
	   	  	 return false;
	   	  }else if(SupportUtil.contains(JSON.stringify($scope.grid),$scope.barcodes.stb_barcode,"stb_barcode")){
	   	  	alert("条码编号重复！");
	   	  	Ring.error();
	   	  	document.getElementById("bi_barcode").focus();
	   	  	document.getElementById("bi_barcode").select();
	   	  	return false;
	   	  }else if($scope.barcodes.stb_qty <=0){
	   	  	alert ("数量必须大于0");
	   	  	Ring.error();
	   	  	return false;
	   	  }else {
	   	  	return true;
	   	  }
	   }
	   $scope.barcodes = {};
	   $scope.search =  function($event,bi_barcode){
	   	if($event.keyCode == 13) {//Enter事件
	   		if(check()){
				CountingOper.getBarData({bar_code:bi_barcode,bar_whcode:$scope.stocking.ST_WHCODE,st_code:$scope.stocking.ST_CODE},{},function(data){
					$scope.barcodes.stb_barcode = data.data.BAR_CODE;
					$scope.barcodes.stb_vendcode = data.data.BAR_VENDCODE;
					$scope.barcodes.stb_prodcode = data.data.BAR_PRODCODE;
					$scope.barcodes.stb_barid = data.data.BAR_ID;
					$scope.barcodes.stb_qty = data.data.BAR_REMAIN;
					$scope.barcodes.stb_stid =  $scope.stocking.ST_ID;
					$scope.barcodes.stb_stcode = $scope.stocking.ST_CODE;
					$scope.barcodes.pr_detail = data.data.PR_DETAIL;
				 },function(response){
				    toaster.pop('error', '条码号错误',response.data.exceptionInfo);
			         Ring.error();
				 })
	   		  }
			}
	   }
	   $scope.confirm = function(){
	   	if(check()){
	   	  $scope.show = true;
	   	  $scope.grid.push($scope.barcodes);
	   	  $scope.shows = $scope.barcodes;
	   	  $scope.barcodes ='';
	   	  document.getElementById("bi_barcode").focus();
	   	}
	   	$rootScope.cmGrid = $scope.grid ;
	   };	   
	    $scope.returnCC = function (){
	         if($scope.grid.length>0){
	         	if(confirm("返回，将删除已采集未提交的数据?")){
	         		 $scope.grid = $rootScope.cfGrid = '';
	         		 $location.path("countingContents");
	         	}
	         }else{
	         	 $location.path("countingContents");
	         }
	     };
	   $scope.submitGet = function(){//提交盘点数据
	   	if($scope.grid.length>0){
		   	 CountingOper.saveBarcode({},angular.fromJson($scope.grid),function(data){
		   	 	  $scope.grid = $rootScope.cmGrid = '';
		   	 	  toaster.pop('success', '提交成功');
				  Ring.success();
		   	 },function(response){
		   	 	  toaster.pop('error', '提交失败',response.data.exceptionInfo);
				  Ring.error();
		   	 });
		   }else{
		   	  Ring.error();
		   	  alert("没有需要提交的数据！");
		   }
	   };
	   if($scope.grid){
			 $scope.tableParams = new ngTableParams({//盘点料号名称规格及剩余数量表格【待提交的】
			        page: 1,           
			        count: 10,          
			        filter: { },
			        sorting: { }
			    }, {
			        total: $scope.grid.length,
			        getData: function ($defer, params) {			          
			            params.total($scope.grid.length); // set total for recalc pagination
			            $defer.resolve($scope.grid.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			        }
			    });
		 };		 
		 $scope.deleteAll = function(){//全部删除
		 	if(confirm("确定删除全部？")){
		 		$scope.grid = $rootScope.cmGrid = '';
		 		Ring.success();
		 		toaster.pop('success', '删除成功');
		 		$scope.tableParams.reload();
		 	}
		 };		 
		 $scope.deleteWaitSubmitItem = function(s){//单行删除
		 	for (var n in $scope.grid){  
               if($scope.grid[n].stb_barcode == s.stb_barcode){
                      $scope.grid.splice(n,1);
                 }
              } 
              $rootScope.cmGrid = $scope.grid;
			  $scope.tableParams.reload();
		   };
	}]);
	
	app.controller('CountingFinishCtrl',['$scope', '$http', '$stateParams', '$rootScope','$filter','$location', 'ngTableParams','toaster', 'Ring', 'SupportUtil','CountingOper', 'Online',function($scope, $http, $stateParams, $rootScope,$filter,$location, ngTableParams,toaster, Ring, SupportUtil,CountingOper, Online){
	     $scope.stocking = $rootScope.stocking||'';	
	     $scope.grid = $rootScope.cfGrid||[];
	     
	     var checkSerialSearch = function(serial_code){	 
	     	if(SupportUtil.contains(JSON.stringify($scope.grid),serial_code,"stb_barcode")){
	     		Ring.error();
	     		alert("序列号重复！");
	     	    document.getElementById("serial_code").focus().select();
	     	    return ;
	     	 }else{  	  	 
	     	  	 CountingOper.serialSearch({code:serial_code,whcode:$scope.stocking.ST_WHCODE,st_code:$scope.stocking.ST_CODE},{},function(data){
	     		   	$scope.serialMessage = data.message;
	     		   	$scope.makeCode = $scope.serialMessage.MA_CODE;
	     		   	$scope.prodcode = $scope.serialMessage.stb_prodcode;
	     		  },function(response){
	     		     Ring.error();
	     			 toaster.pop('error', '序列号不存在',response.data.exceptionInfo);
	     		 });
	     	   }	     	
	     }
	     $scope.serialSearch = function ($event,serial_code){//序列号enter事件	     		     	
	     	if($event.keyCode == 13 && serial_code){
	     	   checkSerialSearch(serial_code);
	     	}
	     };
	     
	     var checkOutboxSearch = function (boxcode){
	     		if(SupportUtil.contains(JSON.stringify($scope.grid),serial_code,"stb_barcode")){
	     			alert("包装箱号重复");
	     			Ring.error();
	     			return ;
	     		}else {
	     			CountingOper.outboxSearch({code:boxcode,whcode:$scope.stocking.ST_WHCODE,st_code:$scope.stocking.ST_CODE},{},function(data){
		     			$scope.outboxMessage = data.message;
		     		   	$scope.totalqty = $scope.outboxMessage.stb_qty;
		     		   	$scope.prodcode = $scope.outboxMessage.stb_prodcode;
	     		  },function(response){
		     		  	Ring.error();
		     		 	toaster.pop('error', '包装箱号不存在',response.data.exceptionInfo);
	     		  });
	     		}	     		
	     };	
	     $scope.outboxSearch = function($event,boxcode){//包装箱号enter事件
	     	if($event.keyCode == 13 && boxcode){
	     		checkOutboxSearch(boxcode);
             }
	     };
	     
	     $scope.confirm = function(){
	     	if(!$scope.serial){
	     		alert("请选择包装箱号或者序列号");
	     		return ;
	     	}else if($scope.serial == 'outbox' && !$scope.boxcode){
	     		alert("请输入包装箱号");
	     		return;
	     	}else if($scope.serial == 'serial' && !$scope.serial_code){
	     		alert("请输入序列号");
	     		return ;
	     	}else if($scope.serial == 'serial' && $scope.serial_code ){//选择序列号执行确认
	     		checkSerialSearch($scope.serial_code);
	     		$scope.serialMessage.stb_stcode = $scope.stocking.ST_CODE;
	     		$scope.serialMessage.stb_stid = $scope.stocking.ST_ID;
	     		$scope.grid.push($scope.serialMessage);
	     		$scope.serial_code = $scope.makeCode ='';
	     		document.getElementById("serial_code").focus();
	     		Ring.success();
	     	}else if($scope.serial == 'outbox' && $scope.boxcode ){
	     		checkOutboxSearch($scope.boxcode);
	     		$scope.outboxMessage.stb_stcode = $scope.stocking.ST_CODE;
	     		$scope.outboxMessage.stb_stid = $scope.stocking.ST_ID;
	     		$scope.grid.push($scope.outboxMessage);
	     		$scope.boxcode = $scope.totalqty ='';
	     		document.getElementById("boxcode").focus();
	     		Ring.success();
	     	}
	     	$rootScope.cfGrid = $scope.grid;
	     	$scope.shows= $scope.grid[$scope.grid.length-1];
	     };
	     
	     $scope.returnCC = function (){
	         if($scope.grid.length>0){
	         	if(confirm("返回，将删除已采集未提交的数据?")){
	         		 $scope.grid = $rootScope.cfGrid = '';
	         		 $location.path("countingContents");
	         	}
	         }else{
	         	$location.path("countingContents");
	         }
	     }
	     
	     $scope.submitGet = function(){
	      if($scope.grid.length>0){
		   	 CountingOper.saveBarcode({},angular.fromJson($scope.grid),function(data){
		   	 	  $scope.grid = $rootScope.cfGrid = '';
		   	 	  toaster.pop('success', '提交成功');
				  Ring.success();
		   	 },function(response){
		   	 	  toaster.pop('error', '提交失败',response.data.exceptionInfo);
				  Ring.error();
		   	 });
		   }else{
		      Ring.error();
		   	  alert("没有需要提交的数据！");
		   }
	     };	   
	     
	      if($scope.grid){
			 $scope.tableParams = new ngTableParams({//盘点料号名称规格及剩余数量表格【待提交的】
			        page: 1,           
			        count: 10,          
			        filter: { },
			        sorting: { }
			    }, {
			        total: $scope.grid.length,
			        getData: function ($defer, params) {			          
			            params.total($scope.grid.length); // set total for recalc pagination
			            $defer.resolve($scope.grid.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			        }
			    });
		 };
		 
		 $scope.deleteWaitSubmitItem = function( s){
		 	for (var n in $scope.grid){  
               if($scope.grid[n].stb_barcode == s.stb_barcode){
                      $scope.grid.splice(n,1);
                 }
              } 
              $rootScope.cfGrid = $scope.grid;
			  $scope.tableParams.reload();
		 };
		 $scope.deleteAll = function(){
		 	if(confirm("确定删除全部？")){
		 		$scope.grid = $rootScope.cfGrid = '';	
		 		Ring.success();
		 		toaster.pop('success', '删除成功');
		 		$scope.tableParams.reload();
		 	}
		 }
	     
	}]);
     /**
	  * 湿敏元件拆分
	  */
	app.controller('SplitModalCtrl',['$scope', '$modalInstance', 'items','MsdOper',function($scope, $modalInstance, items,MsdOper) {
		  $scope.items = {};
		  $scope.items.bar_code = items.bar_code;	
		  var loadMSDLog = function(){
		  	MsdOper.loadMSDLog({code:$scope.items.bar_code}, function(data){
		        $scope.items = data.message;
			}, function(response){
				toaster.pop('error', '获取数据失败', response.data.exceptionInfo);
			});
		  };
		  loadMSDLog();
		  $scope.msdConfirm = function(){
		  	if($scope.items.status == "已拆封" && $scope.items.ms_resttime <= 0){//已拆封，并且无剩余寿命。不允许确认
		  		toaster.pop('error', '剩余寿命不足，请烘烤再使用！');
		  		$modalInstance.close();
		  	}else if($scope.items.status == "已拆封"){
		  		MsdOper.loadMSDLog({code: $scope.items.bar_code}, function(data){
		           if(data.message.ms_resttime <= 0 ){
		            	toaster.pop('error', '剩余寿命不足，请烘烤再使用！');
		  			    $modalInstance.close();
		           }else{
		           	    $modalInstance.close('s');
		           }
				}, function(response){
					toaster.pop('error', '失败', response.data.exceptionInfo);
				});
		  	}else if($scope.items.status =='在烤箱'){//在烘烤提示是否出烤箱
		  		if(confirm("是否出烤箱？")){
		  			MsdOper.confirmOutOven({},{bar_code:$scope.items.bar_code},function(data){
		       	 	   $scope.btnInvalid = true;
		   			   toaster.pop("success","已出烘烤!");
		   			   $modalInstance.close('s');
		       	 	},function(res){
		       	 	   toaster.pop('error', '出烘烤失败',response.data.exceptionInfo);  
		       	 	});
		  		}else{
		  			$modalInstance.close();
		  		}
		  	}else {
		  		$modalInstance.close('s');
		  	}
		  }
		  $scope.cancel = function(){
		  	$modalInstance.close();
		  }
     }]);   
     
	return app;
});