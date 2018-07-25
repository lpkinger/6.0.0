Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProdResource', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'scm.product.ProdResource.Viewport','scm.product.ProdResource.ProdResourceGrid','scm.product.ProdResource.Form','core.form.MultiField',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.grid.YnColumn', 'core.form.MonthDateField'
     	],
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({ 
    		'erpQueryGridPanel':{
    			storeloaded:function (grid){
    				var count=grid.store.getCount();
    				Ext.getCmp('gridcount').setText('总条数:'+count);
    			}
    		},
    		'button[name=import]':{
    			click:function(btn){ 		
    			   me.vastDeal();  	
    			}
    		},
    		'erpImportAllButton':{
    			click:function(btn){
    				 //全部装载会选不中所有数据    			
    				var form=btn.ownerCt.ownerCt;
    				var id = getUrlParam('code');
    				var condition=form.spellCondition(urlcondition);     	
    				var url ,ds_caller,params;
    				
					url = 'scm/product/loadAllProd.action';
					ds_caller = 'ProductBatchUUId';
					params = {condition:condition,caller:caller,code:code};
					
    				if(form){   				   									
    					var win = parent.Ext.getCmp('dlwin');
				        win.setLoading(true);
    					Ext.Ajax.request({
    						method:'post',
    						url:basePath+url,
    						params:params,
    						callback : function(options,success,response){
    				   			win.setLoading(false);
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					grid.multiselected = new Array();
    				   					Ext.getCmp('queryform').onQuery();
    				   				}
    				   				showError(str);return;
    				   			}
    			    			if(localJson.success){
    			    				if(localJson.log){
    			    					showMessage("提示", localJson.log);
    			    				}
    				   				Ext.Msg.alert("提示", "处理成功!", function(){
    				   					grid.multiselected = new Array();
    				   					Ext.getCmp('queryform').onQuery();
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
    	var me = this;
    	var url = 'scm/product/loadProd.action';
    	var code = getUrlParam('code');
    	var params = new Object();
		params.caller = caller;    				
		ds_caller = 'ProductBatchUUId'; 				
		
    	var grid = Ext.getCmp('grid');       
    	var form = Ext.getCmp('queryform');
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
				params.code = code;	
				var win = parent.Ext.getCmp('dlwin');
				win.setLoading(true);
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			win.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					grid.multiselected = new Array();
			   					Ext.getCmp('queryform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "成功!", function(){
			   				   grid.multiselected = new Array();
			   				   grid.view.selModel.deselectAll(true);
    				   		   Ext.getCmp('queryform').onQuery();
			   				});
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}else {
			showError("没有需要处理的数据!");
		}
    } 
});