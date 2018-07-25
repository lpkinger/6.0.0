Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.SaleResource', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.query.Viewport', 'pm.make.SaleResourceGrid', 'common.query.Form', 'core.form.MultiField',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.trigger.AddDbfindTrigger',
     		'core.trigger.TextAreaTrigger','core.grid.YnColumn', 'core.form.MonthDateField','core.button.Import','core.button.ImportAll'
     	],
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({ 
    		'button[id=query]':{
    		  beforerender:function(btn){
    			btn.handler=function(){
    				var form=btn.ownerCt.ownerCt;
    				if(form){
    					var condition=form.spellCondition(urlcondition);
    					if(!Ext.isEmpty(condition)) {
    						var grid=Ext.getCmp('querygrid'); 
    						Ext.create('erp.util.GridUtil').loadNewStore(grid,{caller:caller,condition:condition});
    					}
    				}
    			};
    		 }
    		},
    		'erpQueryGridPanel':{
    			storeloaded:function (grid){
    				var count=grid.store.prefetchData.items.length;
    				Ext.getCmp('gridcount').setText('总条数:'+count);
    			},
    			afterrender:function (grid){
    				var count=grid.store.prefetchData.items.length;
    				Ext.getCmp('gridcount').setText('总条数:'+count);
    			}
    		},
    		'button[id=close]':{
    	    		afterrender:function(btn){
    	    			btn.ownerCt.insert(2,{
  	      				  xtype:'erpImportButton'
  	      			  });
    	    			btn.ownerCt.insert(2,{
    	      				  xtype:'erpImportAllButton'
    	      			  });
    	       		 }
    				
    		},
    		'erpImportButton':{
    			click:function(btn){ 		
    			   me.vastDeal();  						 
    			}
    		},
    		'erpImportAllButton':{
    			click:function(btn){
    				 //全部装载会选不中所有数据    			
    				var form=btn.ownerCt.ownerCt;
    				var panelId=getUrlParam('panelId');
    				var id = getUrlParam('keyValue');
    				var condition=form.spellCondition(urlcondition);     	
    				var url ,ds_caller,params,ds_condition;
    				if(caller == 'ATPSaleSource'){
    					url = 'pm/atp/loadAllSale.action';
    					ds_caller = 'ATPMain';
    					params = {am_id:id,condition:condition,caller:caller};
    					ds_condition = 'ad_amid='+id;
    				}else if(caller == 'WCPlanSaleSource'){
    					url = 'pm/make/loadAllSaleByCondition.action';
    					ds_caller = 'WCPlan';
    					params = {wc_id:id,condition:condition,caller:ds_caller};
    					ds_condition = 'wd_wcid='+id;
    				}else if(caller == 'WCPlanSaleForecastSource'){
    					url = 'pm/make/loadAllSaleForecastByCondition.action';
    					ds_caller = 'WCPlan';
    					params = {wc_id:id,condition:condition,caller:ds_caller};
    					ds_condition = 'wd_wcid='+id;
    				}   			
    				if(form){							
    					var main = parent.Ext.getCmp("content-panel");
    					main.getActiveTab().setLoading(true);
    					Ext.Ajax.request({
    						method:'post',
    						url:basePath+url,
    						params:params,
    						callback : function(options,success,response){
    				   			main.getActiveTab().setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					grid.multiselected = new Array();
    				   					Ext.getCmp('dealform').onQuery();
    				   				}
    				   				showError(str);return;
    				   			}
    			    			if(localJson.success){
    			    				if(localJson.log){
    			    					showMessage("提示", localJson.log);
    			    				}
    				   				Ext.Msg.alert("提示", "处理成功!", function(){
    				   					var panel=parent.Ext.getCmp(panelId);
    				   					main.getActiveTab().close();
    				   					var loadgrid=panel.currentGrid; 
    				   					loadgrid.GridUtil.loadNewStore(loadgrid,{caller:ds_caller,condition:ds_condition});
    				   					main.setActiveTab(panel);
    				   				});
    				   			}
    				   		}    					      						
    					});
    				}
    			}
    		} 
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
			var height = window.innerHeight;
			var cw = 0;
			Ext.each(form.items.items, function(){
				if(!this.hidden && this.xtype != 'hidden') {
					cw += this.columnWidth;
				}
			});
			cw = Math.ceil(cw);
			if(cw == 0){
				cw = 5;
			} else if(cw > 2 && cw <= 5){
				cw -= 1;
			} else if(cw > 5 && cw < 8){
				cw = 4;
			}
			cw = Math.min(cw, 5);
			form.setHeight(height*cw/10 + 10);
			grid.setHeight(height*(10 - cw)/10 - 10);
			this.resized = true;
		}
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('querygrid');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    vastDeal: function(type){
    	var url ;
    	var id = getUrlParam('keyValue');
    	var params = new Object();
		params.caller = caller;
    	if(caller == 'ATPSaleSource'){
    		url = 'pm/atp/loadSale.action';
    		params.am_id = id;
    		ds_caller = 'ATPMain'; 				
    		ds_condition = 'ad_amid='+id;
    	}else if(caller == 'WCPlanSaleSource'){
    		url = 'pm/make/loadSale.action';
    		params.wc_id = id;
    		ds_caller = 'WCPlan';
    		ds_condition = 'wd_wcid='+id;   		
    	}else if(caller == 'WCPlanSaleForecastSource'){
    		url = 'pm/make/loadSaleForecast.action';
    		params.wc_id = id;
    		ds_caller = 'WCPlan';
    		ds_condition = 'wd_wcid='+id;   		
    	}
    	var grid = Ext.getCmp('querygrid');       
    	var form = Ext.getCmp('queryform');
    	var panelId=getUrlParam('panelId');
		var records =null;
		if(type && type=='all'){
			records=Ext.Array.unique(grid.getStore().data.items);
		}else {
			var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		item.index = this.data[grid.keyField];
	        		grid.multiselected.push(item);
	        	}
	        });
			records = Ext.Array.unique(grid.multiselected);
		}
		if(records.length > 0){			
			var data = new Array();
			var bool = false;
			Ext.each(records, function(record, index){
				var f = form.fo_detailMainKeyField;
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        		||(f && this.data[f] != null && this.data[f] != ''
		        		&& this.data[f] != '0' && this.data[f] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
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
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));				
				params._noc=1;
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					var panel=parent.Ext.getCmp(panelId); 
			   					var loadgrid=panel.currentGrid;  			   					  				
								loadgrid.GridUtil.loadNewStore(loadgrid,{caller:ds_caller,condition:ds_condition});											   					
			   					main.getActiveTab().close();
			   					main.setActiveTab(panel); 
			   				});
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}
    } 
});