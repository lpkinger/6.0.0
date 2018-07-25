Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.BarAcceptNotify.SetBarcode', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'vendbarcode.barAcceptNotify.setBarcode.Viewport','vendbarcode.barAcceptNotify.setBarcode.GridPanel','core.trigger.AddDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.button.Close',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.button.GenerateBarcode','core.button.PrintAll','core.button.DeleteAllDetails',
     	    'core.button.PrintAllPackage','core.button.GeneratePaCode','core.button.VendPrintMore','core.button.VendPrintMoreBox'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	var LODOP = null;
    	this.control({
    		'erpGridPanel2': { 
    			reconfigure:function(grid){
    			},
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=refresh]':{//刷新
    			click: function(btn){
    			  window.location.href = window.location.href;
    			}
    		},
    		'button[id=batchGenBarcode]':{//生成条码
    			afterrender: function(btn){
    				if(status != 'ENTERING' ){
    					btn.hide();
    				}
    			},
    			click: function (btn){
    				var id =key;
    				var no=inoutno;
    				var pi_class=(formCondition.split('IS')[formCondition.split('IS').length-1]).replace("'","").replace("'","");
                    var formCondition1 = "and_anidIS" + id +" and an_codeIS'"+no;
                    var gridCondition1 = "and_anidIS" + id+" and and_inqty-nvl(and_barqty,0)>0 order by and_detno asc";
                    var linkCaller = '';
                                var win = new Ext.window.Window({
                                    id: 'win',
                                    height: '80%',
                                    width: '90%',
                                    maximizable: true,
                                    title:'<span><font color=blue>条形码维护[送货通知单:'+inoutno+']</font></span>',
                                    buttonAlign: 'center',
                                    layout: 'anchor',
                                    closeAction:'hide',
                                    items: [{
                                        tag: 'iframe',
                                        frame: true,
                                        anchor: '100% 100%',
                                        layout: 'fit',
                                        html: '<iframe id="iframe_' + linkCaller + '" src="' + basePath + 'jsps/vendbarcode/saveBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutno+ '&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1 + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                    }],
                                    listeners:{
                                    	hide:function(){
                                    		window.location.reload();
                                    	}
                                    }
                                });
                                win.show();
                   
                }
    		},    		
    		'button[id=confirmZplPrint]':{
    			click:function(btn){
    				if(printType == 'Barcode'){
    					 var me = this;
    	    		    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
    	    		    	var condition='';
    	    		    	var type ='';
    	    		    	var items1 = grid.selModel.getSelection();
    	    		    	if(items1.length == 0){
    	    		    		type ='ALL';
    	    		    	}
    	    		    	if(caller =='Vendor!Baracceptnotify'){
    	    					caller ='VendBarcodeInPrint';
    	    				}
    	    		    	if(type =='ALL'){//全部打印
    	    		 	    	condition = '( ban_anid='+key+")";
    	    		 	    	var result = 0;
    	    		            Ext.Ajax.request({
    	    		                url : basePath + 'common/getFieldData.action',
    	    		                async: false,
    	    		                params: {
    	    		                    caller: 'baracceptnotify',
    	    		                    field: 'count(ban_id)',
    	    		                    condition: condition
    	    		                },
    	    		                method : 'post',
    	    		                callback : function(opt, s, res){
    	    		                    var r = new Ext.decode(res.responseText);
    	    		                    if(r.exceptionInfo){
    	    		                        showError(r.exceptionInfo);return;
    	    		                    } else if(r.success){
    	    		                        result = r.data;
    	    		                        if(result > 50){
    	    		                        	var num = Math.ceil(result/50);
    	    		        	            	for(var i = 1;i<= num;i++){ //循环i次发送请求
    	    		        	            		   var params = new Object();
    	    		 	        	        	    	params['id'] = key;
    	    		 	        	        	    	params['condition']= condition;
    	    		 	        	            		params['page'] = i;
    	    		 	        	            		params['pageSize'] = 50;
    	    		 	        	            		me.sendSelfData(caller,dpi,params);
    	    		        	            	}
    	    		        	            }else if(result >0){   
    	    		        	            	var params = new Object();
    	    		        	        	    params['id'] = key;
    	    		        	        	    params['condition']= condition;
    	    		        	        	    me.sendSelfData(caller,dpi,params);
    	    		        	            }
    	    		                    }
    	    		                }
    	    		            });
    	    		    	}else{	//勾选打印    		
    	    		 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
    	    			    	var o;
    	    			    	var printcondition;
    	    			    	var data = new Array();
    	    			    	var count =0;
    	    		 	        Ext.each(items, function(item, index){
    	    		 	        	if(this.data['ban_id'] != null && this.data['ban_id'] != ''
    	    		 	        		&& this.data['ban_id'] != '0' && this.data['ban_id'] != 0){
    	    		 	        	     o = this.data['ban_id'];   		
    	    		 	        	}
    	    		 	        	data.push(o);
    	    		 	        	count++;
    	    		 	        	if(count>=50){
    	    		 	        		if(count%50 == 0){
    	    		 	        			condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
    	    		 	        			var params = new Object();
    	    		 	        			params['id'] = key;
    	    		 	        			params['condition']= condition;
    	    		 	        			me.sendSelfData(caller,dpi,params);
    	    		 	        			data = new Array();
    	    		 	        			count = 0;
    	    		 	        		}
    	    		 	        	}
    	    		 	        });
    	    		 	        if((items.length<50 && items.length>0) || count > 0){
    	    		 	        	condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
    	    		 	        	var params = new Object();
    	    		        	    params['id'] = key;
    	    		        	    params['condition']= condition;
    	    		 	        	me.sendSelfData(caller,dpi,params);
    	    		 	        }
    	    		    	}	
    				}else if(printType == 'Box'){
    					var me = this;
    	    	    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
    	    	    	var condition='';
    	    	    	var params = new Object();
    	    	    	params['id'] = key;
    	    	    	var type = '';
    	    	    	var items1 = grid.selModel.getSelection();
    			    	if(items1.length == 0){
    			    		type ='ALL';
    			    	}
    			    	if(caller =='Vendor!Baracceptnotify' || caller == 'VendBarcodeInPrint'){
    						caller ='VendBarcodeInPrintBox';
    					}
    	    	    	if(type =='ALL'){//全部打印
    	    	 	    	condition = '(ban_anid='+key+")";
    	    	    	}else{	//勾选打印    		
    	    	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
    	    		    	var o;
    	    		    	var printcondition;
    	    		    	var data = new Array();
    	    	 	        Ext.each(items, function(item, index){
    	    	 	        	if(this.data['ban_outboxcode'] != null && this.data['ban_outboxcode'] != ''
    	    	 	        		&& this.data['ban_outboxcode'] != '0' && this.data['ban_outboxcode'] != 0){
    	    	 	        	     o = this.data['ban_outboxcode']; 
    	    	 	        	}
    	    	 	        	data.push(o);
    	    	 	        });
    	    	 	        data = Ext.Array.unique(data);
    	    	 	        condition=' (ban_anid='+ key +' and ban_outboxcode in ('+data+'))';
    	    	    	}	
    	    	    	params['condition']= condition;
    	    	    	me.sendSelfData(caller,dpi,params);
    				}
    	       	   
    			 }
    		},
    		'button[id=confirmPicturePrint]':{
    			click:function(btn){
    				if(printType == 'Barcode'){
    					 var me = this;
    	    		    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
    	    		    	var condition='';
    	    		    	var type ='';
    	    		    	var items1 = grid.selModel.getSelection();
    	    		    	if(items1.length == 0){
    	    		    		type ='ALL';
    	    		    	}
    	    		    	if(caller =='Vendor!Baracceptnotify'){
    	    					caller ='VendBarcodeInPrint';
    	    				}
    	    		    	if(type =='ALL'){//全部打印
    	    		 	    	condition = '( ban_anid='+key+")";
    	    		 	    	var result = 0;
    	    		            Ext.Ajax.request({
    	    		                url : basePath + 'common/getFieldData.action',
    	    		                async: false,
    	    		                params: {
    	    		                    caller: 'baracceptnotify',
    	    		                    field: 'count(ban_id)',
    	    		                    condition: condition
    	    		                },
    	    		                method : 'post',
    	    		                callback : function(opt, s, res){
    	    		                    var r = new Ext.decode(res.responseText);
    	    		                    if(r.exceptionInfo){
    	    		                        showError(r.exceptionInfo);return;
    	    		                    } else if(r.success){
    	    		                        result = r.data;
    	    		                        if(result > 50){
    	    		                        	var num = Math.ceil(result/50);
    	    		        	            	for(var i = 1;i<= num;i++){ //循环i次发送请求
    	    		        	            		   var params = new Object();
    	    		 	        	        	    	params['id'] = key;
    	    		 	        	        	    	params['condition']= condition;
    	    		 	        	            		params['page'] = i;
    	    		 	        	            		params['pageSize'] = 50;
    	    		 	        	            		me.sendSelfPicData(caller,dpi,params);
    	    		        	            	}
    	    		        	            }else if(result >0){   
    	    		        	            	var params = new Object();
    	    		        	        	    params['id'] = key;
    	    		        	        	    params['condition']= condition;
    	    		        	        	    me.sendSelfPicData(caller,dpi,params);
    	    		        	            }
    	    		                    }
    	    		                }
    	    		            });
    	    		    	}else{	//勾选打印    		
    	    		 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
    	    			    	var o;
    	    			    	var printcondition;
    	    			    	var data = new Array();
    	    			    	var count =0;
    	    		 	        Ext.each(items, function(item, index){
    	    		 	        	if(this.data['ban_id'] != null && this.data['ban_id'] != ''
    	    		 	        		&& this.data['ban_id'] != '0' && this.data['ban_id'] != 0){
    	    		 	        	     o = this.data['ban_id'];   		
    	    		 	        	}
    	    		 	        	data.push(o);
    	    		 	        	count++;
    	    		 	        	if(count>=50){
    	    		 	        		if(count%50 == 0){
    	    		 	        			condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
    	    		 	        			var params = new Object();
    	    		 	        			params['id'] = key;
    	    		 	        			params['condition']= condition;
    	    		 	        			me.sendSelfPicData(caller,dpi,params);
    	    		 	        			data = new Array();
    	    		 	        			count = 0;
    	    		 	        		}
    	    		 	        	}
    	    		 	        });
    	    		 	        if((items.length<50 && items.length>0) || count > 0){
    	    		 	        	condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
    	    		 	        	var params = new Object();
    	    		        	    params['id'] = key;
    	    		        	    params['condition']= condition;
    	    		 	        	me.sendSelfPicData(caller,dpi,params);
    	    		 	        }
    	    		    	}	
    				}else if(printType == 'Box'){
    					var me = this;
    	    	    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
    	    	    	var condition='';
    	    	    	var params = new Object();
    	    	    	params['id'] = key;
    	    	    	var type = '';
    	    	    	var items1 = grid.selModel.getSelection();
    			    	if(items1.length == 0){
    			    		type ='ALL';
    			    	}
    			    	if(caller =='Vendor!Baracceptnotify' || caller == 'VendBarcodeInPrint'){
    						caller ='VendBarcodeInPrintBox';
    					}
    	    	    	if(type =='ALL'){//全部打印
    	    	 	    	condition = '(ban_anid='+key+")";
    	    	    	}else{	//勾选打印    		
    	    	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
    	    		    	var o;
    	    		    	var printcondition;
    	    		    	var data = new Array();
    	    	 	        Ext.each(items, function(item, index){
    	    	 	        	if(this.data['ban_outboxcode'] != null && this.data['ban_outboxcode'] != ''
    	    	 	        		&& this.data['ban_outboxcode'] != '0' && this.data['ban_outboxcode'] != 0){
    	    	 	        	     o = this.data['ban_outboxcode']; 
    	    	 	        	}
    	    	 	        	data.push(o);
    	    	 	        });
    	    	 	        data = Ext.Array.unique(data);
    	    	 	        condition=' (ban_anid='+ key +' and ban_outboxcode in ('+data+'))';
    	    	    	}	
    	    	    	params['condition']= condition;
    	    	    	me.sendSelfPicData(caller,dpi,params);
    				}
    	       	   
    			 }
    		},
    		//删除全部明细
    		'erpDeleteAllDetailsButton':{
    			afterrender: function(btn){
    				if(status != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click:function (btn){
	    			var grid =Ext.getCmp("vendSetBarcodeGridPanel");
	    			var items = grid.store.data.items;
	    			var bool = false;
	    			var array = new Array();
	    			Ext.each(items, function(item, index){
        	         if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		        && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){    
        		        	bool = true;
        		        } 
	    			})
	    			if(!bool){
	    				showError("没有需要处理的数据!");
	    				 return;
	    			  }else{
	    				  var records=grid.getSelectionModel().getSelection();
                          if(records.length<=0){
     	    			  	 warnMsg("确定清空全部条码", function(btn){
     					         if(btn == 'yes'){
     					        	me.FormUtil.setLoading(true);
     			    			  	Ext.Ajax.request({
     								url : basePath + "vendbarcode/acceptNotify/deleteAllBarDetails.action",			
     								params: {     
     									      caller: caller,
     					 			          an_id:key
     					 			        },			
     								method : 'post',
     								callback : function(options,success,response){
     									me.FormUtil.setLoading(false);
     									var res = new Ext.decode(response.responseText);
     									if(res.exceptionInfo != null){
     										var str = res.exceptionInfo;
     										if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
     											str = str.replace('AFTERSUCCESS', '');
     											window.location.href = window.location.href;
     										}
     										showError(str);return;
     									}else {
     										window.location.href = window.location.href;
     									}
     								 }
     							    }) 
     					       }else{
     					       	  return ;
     					       }
     			         })
                          }else{
                        	  Ext.each(records, function (item) {
                        		  array.push(item.data.ban_id);
                              })
                              var biids=array;
                        	  me.FormUtil.setLoading(true);
                          Ext.Ajax.request({
								url : basePath + "vendbarcode/acceptNotify/deleteAllBarDetails.action",			
								params: {     
									      caller: caller,
					 			          an_id:key,
					 			          biids:biids
					 			        },			
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										var str = res.exceptionInfo;
										if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
											str = str.replace('AFTERSUCCESS', '');
											window.location.href = basePath +'jsps/vendbarcode/setBarcode.jsp?_noc=1&whoami='+caller+'&key='+key+'&status='+getUrlParam('status')+'&inoutno='+inoutno+'&formCondition='+formCondition+'&gridCondition='+gridCondition;
										}
										showError(str);return;
									}else {
										window.location.href = basePath +'jsps/vendbarcode/setBarcode.jsp?_noc=1&whoami='+caller+'&key='+key+'&status='+getUrlParam('status')+'&inoutno='+inoutno+'&formCondition='+formCondition+'&gridCondition='+gridCondition;
									}
								 }
							    }) 
                           
                          }
	    			}
	    		}
    		}
      });
     },
     onGridItemClick: function(selModel, record){// grid行选择
     	this.GridUtil.onGridItemClick(selModel, record);
     },
     sendSelfData:function(caller,dpi,params){
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/zplPrint.action',
				params: {
					caller:caller,
					dpi:dpi,
					data: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						/*Ext.Msg.alert("提示","打印成功！");*/
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
 	    });
		},
		sendSelfPicData:function(caller,dpi,params){
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/vendorZplPrint.action',
				params: {
					caller:caller,
					dpi:dpi,
					data: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						/*Ext.Msg.alert("提示","打印成功！");*/
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
 	    });
		} 
});
    
