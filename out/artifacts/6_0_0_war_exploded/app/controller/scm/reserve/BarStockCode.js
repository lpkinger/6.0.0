Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarStockCode', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.reserve.barStockCode.Form','scm.reserve.barStockCode.GridPanel','core.toolbar.Toolbar','core.button.PrintAllPackage',
      		'core.button.PrintBar','core.button.Close','core.button.SaveBarcode','core.trigger.DbfindTrigger','core.button.BatchGenBO',
      		'core.trigger.TextAreaTrigger','core.button.GenerateBarcode','core.button.BatchGenBarcode','core.button.PrintAll','core.button.DeleteAllDetails'
  	],
	init:function(){
		var me = this;
		var LODOP = null;
		var statusCode = window.parent.Ext.getCmp("bs_statuscode").value;
		this.control({ 			
			'erpBarStockCodeGridPanel':{
			   itemclick : function(selModel, record){
			      if(caller == 'BarStockCode' && statusCode != 'AUDITED' && statusCode!='已审核'){
    					this.GridUtil.onGridItemClick(selModel, record);
    			  } 		     
			   },
			   beforeedit: function(){//如果是单件管控则明细行中的入库数量不允许修改
			   	 var grid =Ext.getCmp("barStockCodeGridPanel");
			   	 var pr_tracekind  = Ext.getCmp("pr_tracekind").value;
    			 if(pr_tracekind == 1 || pr_tracekind == "单件管控"){   	
			   	 Ext.each(grid.columns, function(c){
			   	 	 if(c.dataIndex == 'bdd_inqty'){
			   	 	 	c.editor = null;
			   	 	 }
			   	 }); 
			   	}
			   }
			},
			'erpBatchGenBOButton':{//批量生成条码和箱号
    			afterrender: function(btn){
    				if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},
    			enable: function (btn){   				
	    			if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},   	
				click:function(btn){					    				
    				var pr_zxbzs =  Ext.getCmp("pr_zxbzs").value;
    				if((pr_zxbzs == 0) || pr_zxbzs < 0 ||pr_zxbzs ==null){
    					showError("最小包装数必须大于0！");
    					return ;    					
    				}
    				var pr_tracekind  = Ext.getCmp("pr_tracekind").value;
    				if(pr_tracekind == 1 || pr_tracekind == "单件管控"){
    					if(pr_zxbzs!=1){
    						showError("单件管控类型的物料最小包装数只能为1 ！");
    						return ;
    					}
    				}
    				var bdd_qty = Ext.getCmp("bdd_qty");//bdd_qty 批总量，bsd_inqty：来料总量
    				if(bdd_qty && (Number(bdd_qty.value)== '0' ||Number(bdd_qty.value) == 0 )){
    					showError('批总量不允许为0！');
    				}
    				if(bdd_qty && (Number(bdd_qty.value) > Number(Ext.getCmp("bsd_inqty").value))){  				
    					showError("批总量不允许大于来料总量");return;
    				} 
    				if(Ext.getCmp("bdd_pkqty").value == ''|| Ext.getCmp("bdd_pkqty").value == 0 
          		      ||Ext.getCmp("bdd_pkqty").value == '0' ||Ext.getCmp("bdd_pkqty").value == null){
          			     showError("请输入箱内总数");return ;          			
          	    	}else {
	          			var pk_qty = Ext.getCmp("bdd_pkqty").value;
	          			var pr_zxbzs = Ext.getCmp("pr_zxbzs").value;
	          			if(pr_zxbzs == ''||pr_zxbzs == 0 || pr_zxbzs == '0'||pr_zxbzs ==null){
	          				showError("请输入最小包装数");return ;  
	          			}else {
	          				if(pk_qty%pr_zxbzs !=0 ){
	          					showError("箱内总数必须是最小包装数的整数倍！");return ;
	          				}
	          			}
          	    	}
          	    	var r = me.getData(); 
    				var param = unescape(escape(Ext.JSON.encode(me.getData(r)))); 
    				me.FormUtil.setLoading(true);
			    	Ext.Ajax.request({
						url : basePath + "scm/reserve/barStock/BatchGenBO.action",			
						params: {     
							      caller: caller,
			 			          formStore:param
			 			        },			
						method : 'post',
						timeout: 180000,
						callback : function(options,success,response){
							me.FormUtil.setLoading(false);
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo != null){
								showError(res.exceptionInfo);return;
							}else {												
								window.location.href = basePath +'jsps/scm/reserve/barStockCode.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+condition;
							}
						}
					})    		   
    			
				}
			},
			
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.onClose();
				}
			},		
    		'erpSaveBarcodeButton': {
    			afterrender: function (btn){
	    			if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},   
    			click: function (btn){
    				var grid =Ext.getCmp("barStockCodeGridPanel");
    				if( grid.selModel.getCount() == 0 ){
    					showError("没有需要处理的数据!");
    					 return;
    				}   			
    				var items = grid.selModel.getSelection();
                    Ext.each(items, function(item, index){
        	        if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		        && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        	            item.index = this.data[grid.keyField];
	        		    grid.multiselected.push(item);
        	        }        	        
        	        var records = Ext.Array.unique(grid.multiselected);
				     if(records.length > 0){
							var params = new Object();
							params.caller = 'BarStockCode';
							var data = new Array();
							var bool = false;
							Ext.each(records, function(record, index){
								if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
					        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0)){
									bool = true;
									var o = new Object();
									if(grid.keyField){
										o[grid.keyField] = record.data[grid.keyField];
									} 
									if(grid.toField){
										Ext.each(grid.toField, function(f, index){
											var v = Ext.getCmp(f).value;
											if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
												o[f] = v;
											}
										});
									}
									if(grid.necessaryFields){
										Ext.each(grid.necessaryFields, function(f, index){
											var v = record.data[f];
											if(Ext.isDate(v)){
												v = Ext.Date.toString(v);
											}
											o[f] = v;
										});
									}
									data.push(o);
								}
							});
							if(bool){
								params.gridStore = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
								me.FormUtil.setLoading(true);
							   Ext.Ajax.request({
									url : basePath + "scm/reserve/barStock/saveBarcodeDetail.action",			
									params: params,			
									method : 'post',
									callback : function(options,success,response){
										me.FormUtil.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.exceptionInfo != null){
											showError(res.exceptionInfo);return;
										}else {					
										   window.location.href = basePath +'jsps/scm/reserve/barStockCode.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+condition;
											}
										}
								})
							} else {
								showError("没有需要处理的数据!");
							}
						}      	    
                    
                    })
    			}    
    		},
    		'erpBatchGenBarcodeButton':{
    			afterrender: function(btn){
    				if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},
    			enable: function (btn){   				
	    			if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},   	
    			click: function (btn){    				
    				//产生新的条码并插到grid行中
    				var pr_zxbzs =  Ext.getCmp("pr_zxbzs").value;
    				if((pr_zxbzs == 0)|| pr_zxbzs <0){
    					showError("最小包装数必须大于等于0");
    					return ;    					
    				}
    				var pr_tracekind  = Ext.getCmp("pr_tracekind").value;
    				if(pr_tracekind == 1 || pr_tracekind == "单件管控"){
    					if(pr_zxbzs!=1){
    						showError("单件管控类型的物料最小包装数只能为1 ！");
    						return ;
    					}
    				}
    				var bdd_qty = Ext.getCmp("bdd_qty");//bdd_qty 批总量，bsd_inqty：来料总量
    				if(bdd_qty && (Number(bdd_qty.value)== '0' ||Number(bdd_qty.value) == 0 )){
    					showError('批总量不允许为0！');return ;
    				}
    				if(bdd_qty && (Number(bdd_qty.value) > Number(Ext.getCmp("bsd_inqty").value))){  				
    					showError("批总量不允许大于来料总量");return;
    				} 
    				var r = me.getData();
    				var param = unescape(escape(Ext.JSON.encode(r)));
    				me.FormUtil.setLoading(true);
			    	Ext.Ajax.request({
						url : basePath + "scm/reserve/barStock/batchGenBarcode.action",			
						params: {     
							      caller: 'BarStockCode',
			 			          formStore:param
			 			        },			
						method : 'post',
						callback : function(options,success,response){
							me.FormUtil.setLoading(false);
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo != null){
								showError(res.exceptionInfo);return;
							}else {		                      
								window.location.href = basePath +'jsps/scm/reserve/barStockCode.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+condition;
							}
						}
					})    		   
    			}
    		},
    		//删除全部明细
    		'erpDeleteAllDetailsButton':{
    			afterrender: function (btn){
	    			if(statusCode == 'AUDITED' || statusCode=='已审核'){
	    				btn.disable(true);
	    			}
    			},   
    			click:function (btn){
	    			var grid =Ext.getCmp("barStockCodeGridPanel");
	    			var items = grid.store.data.items;
	    			var bool = false;
                    Ext.each(items, function(item, index){
        	         if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		        && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){    
        		        	bool = true;
        		        }
        	        });        
	    			if(!bool){
	    				showError("没有需要处理的数据!");
	    				 return;
	    			  }else{
		    			 warnMsg("确定清空所有明细", function(btn){
			            	if(btn == 'yes'){
			            		me.FormUtil.setLoading(true);
			    			  	Ext.Ajax.request({
								url : basePath + "scm/reserve/barStock/deleteAllBarDetails.action",			
								params: {     
									      caller: 'BarStockCode',
					 			          id:Ext.getCmp("bsd_id").value,
					 			          detno:Ext.getCmp("bsd_detno").value
					 			        },			
								method : 'post',
								callback : function(options,success,response){
									me.FormUtil.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										showError(res.exceptionInfo);return;
									}else {							
										window.location.href = basePath +'jsps/scm/reserve/barStockCode.jsp?_noc=1&whoami='+caller+'&formCondition='+formCondition+'&gridCondition='+condition;
									}
								}
							    })
			            	 }else{ 
			            	 	return;
			            	 }
		    			    })
		    			 }
	    			 }
    		 },
    		//全部打印
    	  'erpPrintAllButton':{
    			click:function (btn){
    				var win = new Ext.window.Window({
				    	id : 'win',			  
						maximizable : true,
					    buttonAlign : 'center',
					    layout : 'anchor',
					    title: '打印模板选择',
					    modal : true,
	   				    items: [{
	   				          tag : 'iframe',
				    	      frame : true,
				    	      anchor : '100% 100%',
				    	      layout : 'fit',
	   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lps_barcaller +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	   				    }]
	   				         
	    	     });
	    	      win.show();	    
    			}	   			
    		},
             //打印全部箱号
            'erpPrintAllPackageButton':{
             	click: function (btn){            		
	          		var win = new Ext.window.Window({
				    	id : 'win',			  
						maximizable : true,
					    buttonAlign : 'center',
					    layout : 'anchor',
					    title: '打印模板选择',
					    modal : true,
	   				    items: [{
	   				          tag : 'iframe',
				    	      frame : true,
				    	      anchor : '100% 100%',
				    	      layout : 'fit',
	   				    	  html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/scm/reserve/selPrintTemplate.jsp?whoami='+lps_obcaller +'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	   				    }]
	   				         
	    	     });
	    	     win.show();		             
             	}
             }
       });
       
	},
	  getData : function (){             	
             	var form = Ext.getCmp('barStockCodeForm');
		        if(form.getForm().isValid()){
			      // form里面数据
			       Ext.each(form.items.items, function(item){
				      if(item.xtype == 'numberfield'){
					   // number类型赋默认值，不然sql无法执行
					     if(item.value == null || item.value == ''){
						    item.setValue(0);
					  }
				  }
			   });
              var r = form.getValues();
			  Ext.each(form.items.items, function(item){
				 if(item.xtype == 'itemgrid'){
					// number类型赋默认值，不然sql无法执行
					 if(item.value != null && item.value != ''){
						r[item.name]=item.value;
					}
				}
			});	
             	// 去除ignore字段
			 var keys = Ext.Object.getKeys(r), f;
			 var reg = /[!@#$%^&*()'":,\/?]/;
			 Ext.each(keys, function(k){
				 f = form.down('#' + k);
				 if(f && f.logic == 'ignore') {
					 delete r[k];
				 }
				 if(f) {
				 	if(f.logic != 'ignore' && f.logic){
				 		r[f.logic] = r[k];
				 		delete r[k];
				 	}
				}
				// codeField值强制大写,自动过滤特殊字符
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().toUpperCase().replace(reg, '');
				}
			});
		     Ext.each(Ext.Object.getKeys(r), function(k){// 去掉页面非表单定义字段
			  if(contains(k, 'ext-', true)){
			     delete r[k];
			   }
		      });
           }
           return r;
    	}
});