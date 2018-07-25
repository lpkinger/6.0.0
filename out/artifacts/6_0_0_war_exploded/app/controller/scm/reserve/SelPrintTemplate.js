Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SelPrintTemplate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.SelPrintTemplate','scm.reserve.SelPrintTemplate',
   		    'core.button.Confirm','core.button.Close', 		
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){        				
	    				Ext.getCmp('printLabelForm').close();	    						   
        			}
        		},
        		'button[id=barPrintPreview]': {//打印预览
        			click :function (btn){       				  									
				    	LODOP  = getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM'));    			
				    	if(caller == 'ProdIO!PurcInBarPrint' ||caller == 'BarSProfit!BarPrint'||caller == 'PdaBarcodePrint' || caller == 'VerifyAP!BarPrint'){
	    					if(me.getSelData()){
	    						me.getPrintData(function(data){
				    				var printData = data;   	  		    	
					    		    if( printData != null){
					    				 me.printViewLabel(printData);	
					    		    }					    		  
				    	  		});	
				    	  		return;
	    				    }else{
	    				    	if(caller =='PdaBarcodePrint'){
	    							window.parent.showError("没有勾选需要预览的行,请勾选!");
	    				             return;
	    						}
	    				       window.parent.showError("没有勾选需要预览的行，将预览全部");
	    				    }
	    				}
				    	var params = new Object();
				    	if(Ext.getCmp("template").value == "" || Ext.getCmp("template").value == null){
				    		window.parent.showError("请选择打印模板");
				    		return ;
				    	}
				    	var data = new Object();
				    	params.caller = window.parent.parent.caller;
				    	if(caller =='ProdIO!PurcInBarPrint' || caller == 'ProdIO!PurcInObxPrint'){//出入库单，验收单，xi
					        data["bi_piid"] = window.parent.Ext.getCmp("bi_piid").value;
					    	data["bi_pdno"] = window.parent.Ext.getCmp("bi_pdno").value;
				    	}else if(caller == 'BarSProfit!BarPrint' || caller == 'BarSProfit!ObxPrint'){//条码盘盈
				    		data["bdd_bsdid"] = window.parent.Ext.getCmp("bsd_id").value;
				    	}else if(caller == 'VerifyAP!BarPrint' || caller == 'VerifyAP!ObxPrint'){//采购收料单
				    		data["vadp_vadid"] = Ext.getCmp("vad_id").value;
    						data["vadp_vacode"] = Ext.getCmp("vad_code").value;
				    	}else if(caller == 'BarStockPrint'){
				    		data["bs_id"] = window.parent.Ext.getCmp("bs_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'ProdIO!BarPrintAll'){//出入库单主表点击打印所有条码
				    		data['pi_id'] = window.parent.Ext.getCmp("pi_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCodePrintAll'){//序列号维护打印序列号
				    		data['mc_id'] = window.parent.Ext.getCmp("mc_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCombinePrintAll'){//序列号维护打印拼板号
				    		data['mc_id'] = window.parent.Ext.getCmp("mc_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCodePrint'){//序列号维护补打单个序列号				    		
				    		me.getSinglePrintData(function(data){
				    			var printData = data;   	  		    	
					    		   if( printData != null){
					    				me.printViewLabel(printData);	
					    		  }					    		  
				    	  	});	
				    	}
				        params.printStore = unescape(escape(Ext.JSON.encode(data)));   								    				
						var r = new Object();
						r["template"] = Ext.getCmp("template").value;
						params.printForm = unescape(escape(Ext.JSON.encode(r)));
						params.lps_caller = caller;
					    params.caller = window.parent.parent.caller;
				        Ext.Ajax.request({//获取打印数据
							 url : basePath + "common/barcode/PrintAll.action",
							 params :params,
							 method : 'post',
							 callback : function(opt, s, res){
							 	var r = new Ext.decode(res.responseText);
							 	if(r && r.exceptionInfo){
							 		window.parent.showError(r.exceptionInfo);return; 
								 }else if(r.data.length>0){
					    			var  printData = r.data;
					    			if(printData != null){
					    				me.printViewLabel(printData);
					    			}   					    					
								 }
							   }
						});			    			  					    		
        			}
        		},
        		'button[id=barPrint]':{//打印
	        		click :function (btn){        				   
	    				var LODOP  = getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM'));  
	    				if(caller == 'ProdIO!PurcInBarPrint' ||caller == 'BarSProfit!BarPrint'||caller == 'PdaBarcodePrint' ||caller == 'VerifyAP!BarPrint'){
	    					if(me.getSelData()){
	    						me.getPrintData(function(data){
				    				var printData = data;   	  		    	
					    		    if( printData != null){
					    				 me.printLabel(printData);	
					    		    }					    		   
				    	  		});	
				    	  		 return ;
	    				    }else {
	    				    	if(caller =='PdaBarcodePrint'){
	    							 window.parent.showError("没有勾选需要打印的行,请勾选!");
	    				             return;
	    						}
	    				       window.parent.showError("没有勾选需要打印的行，将全部打印!");
	    				    }
	    				}
	    				var params = new Object();
	    		        if(Ext.getCmp("template").value == "" || Ext.getCmp("template").value == null){
	    					window.parent.showError("请选择打印模板");
	    					return ;
	    				}
	    				var data = new Object();
	    				params.caller = window.parent.parent.caller;
	    				if(caller =='ProdIO!PurcInBarPrint' || caller == 'ProdIO!PurcInObxPrint'){
					        data["bi_piid"] = window.parent.Ext.getCmp("bi_piid").value;
					    	data["bi_pdno"] = window.parent.Ext.getCmp("bi_pdno").value;
				    	}else if(caller == 'BarSProfit!BarPrint' || caller == 'BarSProfit!ObxPrint'){
				    		data["bdd_bsdid"] = window.parent.Ext.getCmp("bsd_id").value;
				    	}else if(caller == 'VerifyAP!BarPrint' || caller == 'VerifyAP!ObxPrint'){//采购收料单
				    		data["vadp_vadid"] = Ext.getCmp("vad_id").value;
    						data["vadp_vacode"] = Ext.getCmp("vad_code").value;
				    	}else if(caller == 'BarStockPrint'){
				    		data["bs_id"] = window.parent.Ext.getCmp("bs_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'ProdIO!BarPrintAll'){//出入库单主表点击打印所有条码
				    		data['pi_id'] = window.parent.Ext.getCmp("pi_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCodePrintAll'){//序列号维护打印序列号
				    		data['mc_id'] = window.parent.Ext.getCmp("mc_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCombinePrintAll'){//序列号维护打印拼板号
				    		data['mc_id'] = window.parent.Ext.getCmp("mc_id").value;
				    		params.caller = window.parent.caller;
				    	}else if(caller == 'MakeSerialCodePrint'){
				    		me.getSinglePrintData(function(data){
				    			var printData = data;   	  		    	
					    		   if( printData != null){
					    				me.printViewLabel(printData);	
					    		  }					    		  
				    	  	});	
				    	}
	    				params.printStore = unescape(escape(Ext.JSON.encode(data)));   				
	    				var form = Ext.getCmp("printLabelForm");
						var r = new Object();
						Ext.each(form.items.items, function(item){
						  if(item.value != null && item.value != ''){		
						      r[item.id]=item.value;
						   }
						});
						params.printForm = unescape(escape(Ext.JSON.encode(r)));
						params.lps_caller = caller;
						params.caller = window.parent.parent.caller;
	    				Ext.Ajax.request({
							url : basePath + "common/barcode/PrintAll.action",
							params :params, 
							method : 'post',
							callback : function(options, success, response) {
								var res = new Ext.decode(response.responseText);
								if (res.exceptionInfo != null) {
									window.parent.showError(res.exceptionInfo);
									return;
								}else if(res.data.length>0){									
									var printData = res.data;
	    						    me.printLabel(printData);	
	    					     }	    					   
								}
					    	});    			  	    			 
	        			}
        		  }       		
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	getSelData :function(){//判断是否勾选了需要操作的数据   	
    		if(caller =='PdaBarcodePrint'){
    			var grid =window.parent.Ext.getCmp("batchDealGridPanel");	     	
		    	if( grid.selModel.getCount() == 0 ){	    		
		    		return false;
		    	}		    	
	    	   return true;
    		}else if( caller == 'BarSProfit!BarPrint'){
    			var grid =window.parent.Ext.getCmp("barStockCodeGridPanel");	     	
		    	if( grid.selModel.getCount() == 0 ){	    		
		    		return false;
		    	}
	    	   return true;				
    		}else if(caller == 'VerifyAP!BarPrint'){
				var grid  = window.parent.Ext.getCmp('verifyApplyDetailGridPanel');
				if( grid.selModel.getCount() == 0 ){	    		
		    		return false;
		    	}
	    	   return true;	
	    	}else {
		     	var grid =window.parent.Ext.getCmp("setBarcodeGridPanel");	     	
		    	if( grid.selModel.getCount() == 0 ){	    		
		    		return false;
		    	}
	    	   return true;
	       }
	    },
	   getPrintAction :function(params,callback){
	       Ext.Ajax.request({
		 		  url : basePath + "common/barcode/Print.action",
				  params : params,
				  method : 'post',
		 		  callback : function(opt, s, res){
		 			   var r = new Ext.decode(res.responseText);
		 			   if(r && r.exceptionInfo){
		 				   window.parent.showError(r.exceptionInfo);return;
		 			   } else if(r.data){		   
		 				  callback && callback.call(null, r.data);
		 			   }else{
		 			   	  return;
		 			   }
		 		   }
		 	  });
	   },
	   getSinglePrintData:function(callback){
		   	var form = Ext.getCmp("printLabelForm");
			var r = new Object();
			Ext.each(form.items.items, function(item) {
				if (item.value != null && item.value != '') {
					r[item.id] = item.value;
				}
			});	 
			var params = new Object();
			params.caller = window.parent.caller;
			params.lps_caller = caller;
			params.printForm = unescape(escape(Ext.JSON.encode(r)));	
			params.gridStore = unescape(escape(Ext.JSON.encode({ms_id:formCondition})));	
	    	this.getPrintAction(params, callback);
	   },
	   getPrintData : function(callback) {  
			var form = Ext.getCmp("printLabelForm");
			var r = new Object();
			Ext.each(form.items.items, function(item) {
				if (item.value != null && item.value != '') {
					r[item.id] = item.value;
				}
			});	 
			var grid ;
			if(caller =='PdaBarcodePrint'){
    			 grid =window.parent.Ext.getCmp("batchDealGridPanel");
			}else if(caller == 'BarSProfit!BarPrint'){
				grid = window.parent.Ext.getCmp("barStockCodeGridPanel");
			}else if(caller == 'ProdIO!PurcInBarPrint'){
				grid = window.parent.Ext.getCmp("setBarcodeGridPanel");
			}else if(caller == 'VerifyAP!BarPrint'){
				grid = window.parent.Ext.getCmp('verifyApplyDetailGridPanel');
			}
			var items = grid.selModel.getSelection();
			Ext.each(items, function(item, index) {
				if (this.data[grid.keyField] != null&& this.data[grid.keyField] != ''
					&& this.data[grid.keyField] != '0'&& this.data[grid.keyField] != 0) {
							item.index = this.data[grid.keyField];
							grid.multiselected.push(item);
				}
			});
			var records = Ext.Array.unique(grid.multiselected);
			if (records.length > 0) {			
				var params = new Object();
				params.caller = window.parent.parent.caller;
				params.lps_caller = caller;
				params.printForm = unescape(escape(Ext.JSON.encode(r)));				
				var bool = false;
				var data = new Array();
				Ext.each(records, function(record, index) {
					if ((grid.keyField && this.data[grid.keyField] != null&& this.data[grid.keyField] != ''
							&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0)) {
						bool = true;
						var o = new Object();
						if (grid.keyField) {
							o[grid.keyField] = record.data[grid.keyField];
						}
						data.push(o);
					}
				});
				params.gridStore = unescape(escape(Ext.JSON.encode(data)));			
	        	this.getPrintAction(params, callback);
			   } 
		},
		updatePrintStatus : function(params,uri){			
			Ext.Ajax.request({//更新打印状态
		 		  url : basePath + uri,
				  params :params,
				  method : 'post',
		 		  callback : function(opt, s, res){
		 			   var r = new Ext.decode(res.responseText);
		 			   if(r && r.exceptionInfo){
		 				   showError(r.exceptionInfo);return; 
		 			   }else{
		 			   	var grid ;
		 			   	if(caller == 'ProdIO!PurcInBarPrint'){
		 			   	    grid =window.parent.Ext.getCmp("setBarcodeGridPanel");
		 			   	}else if(caller == 'VerifyAP!BarPrint'){
				            grid = window.parent.Ext.getCmp('verifyApplyDetailGridPanel');
		 			   	}
		 			   	 var gridCondition = window.parent.gridCondition;
		 			   	 gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
		 			   	 grid.GridUtil.loadNewStore(grid,{caller:window.parent.caller,condition:gridCondition});
		 			   }
		 		   }
		 	   });
		},
		
		printViewLabel:function (printData){//打印预览
			var params = new Object();
		    var ids = new Array();
			Ext.each(printData, function(record, index){
			   LODOP.NewPage();
			   Ext.each(record['store'],function(data,index){
			      LODOP.SET_PRINT_STYLE("FontSize",data['LP_SIZE']);							   	  
			      if(data['LP_VALUETYPE'] == 'barcode'){				        
				     LODOP.ADD_PRINT_BARCODE(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm",data['LP_ENCODE'],data['value']);
				     LODOP.SET_PRINT_STYLEA(0,"ShowBarText",data['LP_IFSHOWNOTE']);						    			  	
				     LODOP.SET_PRINT_STYLEA(0,"AlignJustify",data['LP_NOTEALIGNJUSTIFY']);
				 }else if(data['LP_VALUETYPE'] == 'text'){
					 LODOP.SET_PRINT_STYLE("FontName",data['LP_FONT']);
					 LODOP.ADD_PRINT_TEXT(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm",data['value']);						    			  	
				  }
			 });
			  var o = new Object();
			 if(caller == 'ProdIO!PurcInBarPrint'){				
				 o['bi_id'] = record['bi_id'];
			     ids.push(o);
			 }else if (caller == 'VerifyAP!BarPrint'){
				 o['vadp_id'] = record['vadp_id'];
				 ids.push(o);
			 }
		  });
		 var pagesize = printData[0]['store'][0]['LA_PAGESIZE'];//获取标签的大小
		 var strs= new Array(); //定义一数组 
		 strs = pagesize.split("*"); //字符分割 								
		 LODOP.SET_PREVIEW_WINDOW(0,0,0,0,0,"");
		 LODOP.SET_SHOW_MODE("HIDE_SBUTTIN_PREVIEW",1);
		 LODOP.SET_PRINT_PAGESIZE(1, strs[0]*10, strs[1]*10, "");		
		 var times = LODOP.PREVIEW();//打印预览 
		 if(caller == 'ProdIO!PurcInBarPrint' || caller == 'VerifyAP!BarPrint'){
			 if(times > 0){//点击了打印预览内的打印按钮
				params.ids = unescape(escape(Ext.JSON.encode(ids)));
			    params.caller = window.parent.caller;			    
			    var uri ;
				if (caller == 'VerifyAP!BarPrint'){
					uri = "common/purchase/updatePurPrintStatus.action";
				}else if (caller == 'ProdIO!PurcInBarPrint'){
					uri = "common/barcode/updatePrintStatus.action";
				}
			    this.updatePrintStatus(params,uri);
			}
		 }
	   },
		printLabel:function(printStore){	//打印
		  var LODOP  = getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM'));  
		  var selectOrNot = LODOP.SELECT_PRINTER();//选择打印机，未-1就是取消，如果是其他数字就是设置打印机序号
		  if (selectOrNot != -1) {
			LODOP.PRINT_INIT("标签打印");//设定纸张大小
			var pagesize = printStore[0]['store'][0]['LA_PAGESIZE'];//获取标签的大小
			var strs= new Array(); //定义一数组 
			strs=pagesize.split("*"); //字符分割 	
			LODOP.SET_PRINT_PAGESIZE(1, strs[0]*10, strs[1]*10, "");
			var params = new Object();
			var ids = new Array();	
			Ext.each(printStore, function(record, index) {
				//LODOP.NewPage();
			    Ext.each(record['store'], function(data, index) {
			    	    LODOP.SET_PRINT_STYLE("FontSize", data['LP_SIZE']);
			    	    LODOP.SET_PRINT_STYLE("FontName",data['LP_FONT']);
					if (data['LP_VALUETYPE'] == 'barcode') {				  
						LODOP.ADD_PRINT_BARCODE(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm", data['LP_ENCODE'], data['value']);
						LODOP.SET_PRINT_STYLEA(0,"ShowBarText", data['LP_IFSHOWNOTE']);
						LODOP.SET_PRINT_STYLEA(0,"AlignJustify", data['LP_NOTEALIGNJUSTIFY']);
						//“ShowBarText” ：(一维)条码的码值是否显示  0/1
						//“AlignJustify”：设置“text文本”是否两端对齐或“barcode条码文字”靠齐方式
						//设置“barcode条码文字”时，0-两端对齐(默认)  1-左靠齐  2-居中  3-右靠齐
					} else if (data['LP_VALUETYPE'] == 'text') {
						LODOP.ADD_PRINT_TEXT(data['LP_TOPRATE']+"mm",data['LP_LEFTRATE']+"mm",data['LP_WIDTH']+"mm",data['LP_HEIGHT']+"mm", data['value']);
					}
				});
				var o = new Object();
				if(caller =='ProdIO!PurcInBarPrint'){
					o['bi_id'] = record['bi_id'];
					ids.push(o);
				}else if (caller == 'VerifyAP!BarPrint'){
					 o['vadp_id'] = record['vadp_id'];
					 ids.push(o);
			    }				
				LODOP.SET_PRINT_STYLEA(0,"NotOnlyHighPrecision",true);//设置打印的精度
				LODOP.PRINT();								
			});
			if(caller == 'ProdIO!PurcInBarPrint' || caller == 'VerifyAP!BarPrint'){
				params.ids = unescape(escape(Ext.JSON.encode(ids)));
			    params.caller = window.parent.caller;			    
			    var uri ;
				if (caller == 'VerifyAP!BarPrint'){
					uri = "common/purchase/updatePurPrintStatus.action";
				}else if (caller == 'ProdIO!PurcInBarPrint'){
					uri = "common/barcode/updatePrintStatus.action";
				}
			    this.updatePrintStatus(params,uri);		
		    }
		    if(caller == 'ProdIO!BarPrintAll'){
		    	params.ids = unescape(escape(Ext.JSON.encode(window.parent.Ext.getCmp("pi_id").value)));
			    params.caller = 'ProdIO!BarPrintAll';			   
				uri = "common/barcode/updatePrintStatus.action";
			    Ext.Ajax.request({//更新打印状态
		 		  url : basePath + uri,
				  params :params,
				  method : 'post',
		 		  callback : function(opt, s, res){
		 			   var r = new Ext.decode(res.responseText);
		 			   if(r && r.exceptionInfo){
		 				   showError(r.exceptionInfo);return; 
		 			   }
		 		   }
		 	   });	
		    }
		  }									
		}
    });