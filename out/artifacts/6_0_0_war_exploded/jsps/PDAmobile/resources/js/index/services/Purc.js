define([ 'ngResource','common/services' ], function() {
	angular.module('PurcServices', [ 'ngResource' ,'common.services'
	]).factory('PurcOrder',['$resource','BaseService', function($resource,BaseService) {
		return $resource(BaseService.getRootPath()+'/pda/getProdInData.action', {},{
		   getWhcode : {
		   	 	url: BaseService.getRootPath()+'/pda/getWhcode.action',
				method: 'POST',			
				params: {					
				}
		   },
		   clearGet :{
		   	   url: BaseService.getRootPath()+'/pda/clearGet.action',
				method: 'POST',
				params: {					
				}
		   },
		   getHaveSubmitList:{
		   	    url: BaseService.getRootPath()+'/pda/getHaveSubmitList.action',
				method: 'GET',
				params: {					
				}
		   },
		   deleteDes :{
		   	    url: BaseService.getRootPath()+'/pda/delHaveSubDetail.action',
				method: 'POST',
				params: {					
				}		   	
		   },
		   getBarIoCheck :{
		   	    url: BaseService.getRootPath()+'/pda/getBarIoCheck.action',
				method: 'POST',
				params: {					
				}	
		   },
		   updateBarIoQty:{
		   	    url: BaseService.getRootPath()+'/pda/updateBarIoQty.action',
				method: 'POST',
				params: {					
				}	
		   },
		   getBarIoBoxCheck:{
		   	    url: BaseService.getRootPath()+'/pda/getBarIoBoxCheck.action',
				method: 'POST',
				params: {					
				}	
		   },
		   getDescription:{//获取是否显示储位
		        url: BaseService.getRootPath()+'/pm/bom/getDescription.action',
				method: 'POST',
				params: {					
				}	
		   }
		});
	}]).factory('PurcOrderItem',['$resource','BaseService', function($resource,BaseService) {
		return $resource('pda/orders/items', {}, {
		   saveBarcode: {
				url: BaseService.getRootPath()+'/pda/saveBarcode.action',
				method: 'POST',
				params: {					
				}
			}, checkMakeSerial: {
				url: BaseService.getRootPath()+'/pda/checkMakeSerial.action',
				method: 'POST',
				params: {					
				}
			},getPackageCode: {
				url: BaseService.getRootPath()+'/pda/getPackageCode.action',
				method: 'POST',				
				params: {					
				}
			}
		});
	}]).factory('OutOper',['$resource','BaseService',function($resource,BaseService){
		return $resource(BaseService.getRootPath()+'/pda/out/getProdOutData.action', {}, {
		   checkSerialqty: {
				url: BaseService.getRootPath()+'/pda/out/checkSerialqty.action',
				method: 'POST',
				params: {	
				}
			},saveOutBarcode: {
				url: BaseService.getRootPath()+'/pda/out/saveOutBarcode.action',
				method: 'POST',
				params: {				
				}
			}, checkOutbox: {
				url: BaseService.getRootPath()+'/pda/out/checkOutbox.action',
				method: 'POST',
				params: {					
				}
			},checkOutqty:{
				url: BaseService.getRootPath()+'/pda/out/checkOutqty.action',
				method: 'POST',
				params: {					
				}
			},
			loadMSDLog:{
				url: BaseService.getRootPath()+'/pda/out/loadMSDLog.action',
				method: 'GET',
				params: {					
				}
			},
			ifNeedBatch:{
				url: BaseService.getRootPath()+'/pda/out/ifNeedBatch.action',
				method: 'POST',
				params: {					
				}
			}
	  });
   }]).factory('CountingOper',['$resource','BaseService',function($resource,BaseService){
   		return $resource(BaseService.getRootPath()+'/pda/counting/getCountingData.action', {}, {
   			getBarData:{
   			  url: BaseService.getRootPath()+'/pda/counting/getBarData.action',
				method: 'GET',
				params: {					
				}
   			},
   			saveBarcode:{
   				url: BaseService.getRootPath()+'/pda/counting/saveBarcode.action',
				method: 'POST',
				params: {					
				}   				
   			} ,
   			serialSearch:{
   				url: BaseService.getRootPath()+'/pda/counting/serialSearch.action',
				method: 'GET',
				params: {					
				}   
   			},
   			outboxSearch:{
   				url: BaseService.getRootPath()+'/pda/counting/outboxSearch.action',
				method: 'GET',
				params: {					
				}  
   			}
   		});
   }]).factory('CheckOper',['$resource','BaseService',function($resource,BaseService){
   		return $resource(BaseService.getRootPath()+'/pda/check/makeMaterialCheck.action',{},{
   		     checkMM:{
   		     	url: BaseService.getRootPath()+'/pda/check/makeMaterialCheck.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkMMDetail:{
   		     	url: BaseService.getRootPath()+'/pda/check/makeMaterialDetail.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkBarcode:{
   		     	url: BaseService.getRootPath()+'/pda/check/barcodeCheck.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkPackage:{
   		     	url: BaseService.getRootPath()+'/pda/check/packageCheck.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkMakeFin:{
   		     	url: BaseService.getRootPath()+'/pda/check/makeFinishCheck.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkOrderFin:{
   		     	url: BaseService.getRootPath()+'/pda/check/orderFinishCheck.action',
				method: 'GET',
				params: {					
				} 
   		     },
   		     checkPO:{
   		     	url: BaseService.getRootPath()+'/pda/check/checkPO.action',
				method: 'GET',
				params: {					
				} 
   		     }
   		});
   }]).factory('LocaTransOper',['$resource','BaseService',function($resource,BaseService){
   		return $resource(BaseService.getRootPath()+'/pda/transfer/getCodeData.action', {}, {
   			locaTransfer:{
   				url: BaseService.getRootPath()+'/pda/transfer/locaTransfer.action',
				method: 'POST',
				params: {					
				}   				
   			}  	
   		});   	   	
   }]).factory('BatchOper',['$resource','BaseService',function($resource,BaseService){
   		return $resource(BaseService.getRootPath()+'/pda/batch/getBarcodeData.action', {}, {
   			breakingBatch:{
   				url: BaseService.getRootPath()+'/pda/batch/breakingBatch.action',
				method: 'POST',
				params: {					
				}   				
   			},
   			combineBatch:{
   				url: BaseService.getRootPath()+'/pda/batch/combineBatch.action',
				method: 'POST',
				params: {					
				}  
   			},
   			searchPackageData:{
   				url: BaseService.getRootPath()+'/pda/batch/searchPackageData.action',
				method: 'GET',
				params: {					
				}  
   			},
   			breakingPackage:{
   				url: BaseService.getRootPath()+'/pda/batch/breakingPackage.action',
				method: 'POST',
				params: {					
				}    				
   			},
   			getOutboxCode:{
   				url: BaseService.getRootPath()+'/pda/batch/getOutboxCode.action',
				method: 'GET',
				params: {					
				}   				
   			}  			
   		});   	   	
   }]).factory('Print',['$resource','BaseService',function($resource,BaseService){
         return $resource(BaseService.getRootPath()+'/pda/print/labelPrint.action', {}, {
              setDefaultPrint:{
              	url: BaseService.getRootPath()+'/pda/print/setDefaultPrint.action',
				method: 'POST',
				params: {					
				}   
              },
              getDefaultPrint:{
              	url: BaseService.getRootPath()+'/pda/print/getDefaultPrint.action',
				method: 'POST',
				params: {					
				}   
              }
         })
   }]).factory('ShopFloorManageOper',['$resource','BaseService',function($resource,BaseService){
       return $resource(BaseService.getRootPath()+'/pda/shopFloorManage/getMakeData.action', {}, {
             checkCode:{
             	url: BaseService.getRootPath()+'/pda/shopFloorManage/checkCode.action',
				method: 'POST',
				headers:{'Content-Type': 'application/x-www-form-urlencoded'},
				transformRequest:[function(obj) {
			       var str = [];
			      for(var p in obj){
			        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
			      }
			      return str.join("&");
			    }],
				params: {					
				}   
             },
       	     getCollectDetailData:{
              	url: BaseService.getRootPath()+'/pda/shopFloorManage/getCollectDetailData.action',
				method: 'POST',
				params: {					
				}   
              } ,
            getBarRemain:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/getBarRemain.action',
				method: 'POST',
				params: {					
				}   
            },
            loading:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/loading.action',
				method: 'POST',
				headers:{'Content-Type': 'application/x-www-form-urlencoded'},
				transformRequest:[function(obj) {
			       var str = [];
			      for(var p in obj){
			        str.push(encodeURIComponent(p) + "=" +angular.toJson(obj[p]));
			      }
			      return str.join("&");
			    }],
				params: {					
				}  
            },
            cuttingStock:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/cuttingStock.action',
            	method: 'POST',				
				method: 'POST',
				params: {					
			    } 
            },
            cuttingAllStock:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/cuttingAllStock.action',
            	headers:{'Content-Type': 'application/x-www-form-urlencoded'},
				transformRequest:[function(obj) {
			       var str = [];
			      for(var p in obj){
			        str.push(encodeURIComponent(p) + "=" +angular.toJson(obj[p]));
			      }
			      return str.join("&");
			    }],
				method: 'POST',
				params: {					
			    } 
            },
            joinMaterial:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/joinMaterial.action',
				method: 'POST',
				params: {					
			    } 
            },
            changeMaterial:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/changeMaterial.action',
				method: 'POST',
				params: {					
			    } 
            },
            queryData :{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/queryData.action',
				method: 'GET',
				params: {					
			    } 
            },
            checkMakeSMTLocation:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/checkMakeSMTLocation.action',
				method: 'GET',
				params: {					
			    } 
            },
            updateChecked:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/updateChecked.action',
				method: 'POST',
				params: {					
			    } 
            },
            importMPData:{
            	url: BaseService.getRootPath()+'/pda/shopFloorManage/importMPData.action',
				method: 'POST',
				params: {					
			    } 
            }
         })
   }]).factory('FeederOper',['$resource','BaseService',function($resource,BaseService){
       return $resource(BaseService.getRootPath()+'/pda/shopFloorManage/searchMa.action', {}, {
          feederMakeQuery:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/feederMakeQuery.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          feederGet :{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/feederGet.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          feederBack :{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/feederBack.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          feederUsedlist:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/feederUsedlist.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          feederBackAll:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/feederBackAll.action',
			  method: 'POST',
			  params: {					
			  } 
          }
       })
   }]).factory('MakePrepareOper',['$resource','BaseService',function($resource,BaseService){
       return $resource(BaseService.getRootPath()+'/pda/shopFloorManage/searchMp.action', {}, {
          barGet :{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/barGet.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          barBack :{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/barBack.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          barcodeList:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/barcodeList.action',
			  method: 'POST',
			  params: {					
			  } 
          },
          needPreparedList:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/needPreparedList.action',
			  method: 'POST',
			  params: {					
			  } 
          },       
          getMpcodeList:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/getMpcodeList.action',
			  method: 'GET',
			  params: {					
			  }
          },
          preparedFeederList:{
          	url: BaseService.getRootPath()+'/pda/shopFloorManage/preparedFeederList.action',
			  method: 'POST',
			  params: {					
			  }
          },
          feederGet:{
          	url: BaseService.getRootPath()+'/pda/shopFloorManage/makePrepareFeederGet.action',
			  method: 'POST',
			  params: {					
			  }
          },
          feederBack:{
          	  url: BaseService.getRootPath()+'/pda/shopFloorManage/makePrepareFeederBack.action',
			  method: 'POST',
			  params: {					
			  }
          }
       })
   }]).factory('MsdOper',['$resource','BaseService',function($resource,BaseService){
         return $resource(BaseService.getRootPath()+'/pda/msd/getLog.action', {}, {
              confirmInOven:{
              	url: BaseService.getRootPath()+'/pda/msd/confirmInOven.action',
				method: 'POST',
				params: {					
				}   
              },
              getOvenTime:{
              	url: BaseService.getRootPath()+'/pda/msd/getOvenTime.action',
				method: 'GET',
				params: {					
				}  
              },
              confirmOutOven:{
              	url: BaseService.getRootPath()+'/pda/msd/confirmOutOven.action',
				method: 'POST',
				params: {					
				}  
              }
         })
   }]).factory('TailingBackOper',['$resource','BaseService',function($resource,BaseService){
       return $resource(BaseService.getRootPath()+'/pda/mes/getForcastRemain.action', {}, {             
          tailingBack:{
            url: BaseService.getRootPath()+'/pda/mes/tailingBack.action',
			method: 'POST',
			params: {					
			}  
          }
      })
   }]);
})