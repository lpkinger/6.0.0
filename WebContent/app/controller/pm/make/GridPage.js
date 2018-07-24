Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.GridPage', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'common.GridPage','core.grid.Panel4','core.toolbar.Toolbar3',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
      			'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Close',
      			'core.button.Update','core.button.DeleteDetail','core.button.ResSubmit','core.button.Scan',
      		'core.trigger.DbfindTrigger', 'core.grid.YnColumn','core.form.MonthDateField','core.button.BankInit'
      	],
    init:function(){
    	this.control({
    		'erpGridPanel4': {
    			reconfigure: function(g) {
    				var grid = Ext.getCmp('grid');
    				if(grid.store.data.items[0].data['mf_code']==''){
    					return;
    				}
    				var codes = Ext.Array.concate(grid.store.data.items, ',', 'mf_code');
    				Ext.Ajax.request({
			        	url : basePath + "pm/make/checkmfcode.action",
			        	params: {
			        		mf_code: codes
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        		grid.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(!res.success){
			        			var data = res.data;
			        			grid.store.each(function(item){
			        				Ext.each(data, function(d){
			        					if(d == item.get('mf_code')) {
			        						item.readonly = true;
			        					}
			        				});
			        			});
			        		};
			        	}
    				});
    				g.plugins[0].on('beforeedit', function(args){
						return !args.record.readonly;
    				});
    			},
    			itemclick: function(selModel, record) {
    				this.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var num=0;
    				Ext.each(items, function(item){
    					if(item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ''){
    						item.set('mf_maid', parent.Ext.getCmp('ma_id').value);
    						num =num+item.data['mf_qty'];
    					}
					});
    				if(num>parent.Ext.getCmp('ma_qty').value){
    					showError("数量总数超过制造单总数！");
    					return;
    				}
    				var data = grid.getGridStore();
    				grid.setLoading(true);
    				Ext.Ajax.request({
    		        	url : basePath + (btn.url || grid.saveUrl),
    		        	params: {
    		        		gridStore: "[" + data.toString() + "]"
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.success){
    		        			saveSuccess(function(){
    		        				window.location.href = window.location.href;
    		        			});
    		        		};
    		        	}
    		        });
    			}
    		},
			'erpDeleteButton': {
				click: function(btn){
    				this.GridUtil.deleteDetailForEditGrid(btn);
    			}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpBankInitButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var data = grid.getGridStore();
    				grid.setLoading(true);
    				Ext.Ajax.request({
    		        	url : basePath + btn.url,
    		        	params: {
    		        		gridStore: "[" + data.toString() + "]"
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		if(res.success){
    		        			saveSuccess(function(){
    		        				window.location.href = window.location.href;
    		        			});
    		        		};
    		        	}
    		        });
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getGrid: function(btn){
		return btn.ownerCt.ownerCt;
	}
});