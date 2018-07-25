Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.UpdateVendor', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.purchase.UpdateVendor','core.grid.Panel4','core.toolbar.Toolbar3',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
      			'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Close',
      			'core.button.Update','core.button.DeleteDetail','core.button.ResSubmit','core.button.Scan',
      		'core.trigger.DbfindTrigger', 'core.grid.YnColumn','core.form.MonthDateField','core.button.BankInit'
      	],
    init:function(){
    	this.control({
    		'erpGridPanel4': {
    			reconfigure: function(grid) {
    				if(grid.headerCt) {
    					var cols = grid.headerCt.getGridColumns();
    					Ext.each(cols, function(c){
    						var v = getUrlParam(c.dataIndex);
    						if (v) {
    							c.defaultValue = v;
    						}
    					});
    				}
    			},
    			itemclick: function(selModel, record) {
    				this.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var data = grid.getGridStore();
    				if(data != null) {
    					var bool = btn.fireEvent('beforesave', btn);
    					if (bool === undefined || bool === true) {
    						grid.setLoading(true);
    						console.log('URL:'+basePath + (btn.url || grid.saveUrl));
    						console.log(data.toString());
            				Ext.Ajax.request({
            		        	url : basePath + (btn.url || grid.saveUrl),
            		        	params: {
            		        		data: "[" + data.toString() + "]"
            		        	},
            		        	method : 'post',
            		        	callback : function(options,success,response){
            		        		grid.setLoading(false);
            		        		btn.fireEvent('aftersave', btn);
            		        		var res = new Ext.decode(response.responseText);
            		        		if(res.exceptionInfo){
            		        			showError(res.exceptionInfo);return;
            		        		}
            		        		if(res.success){
            		        			saveSuccess(function(){
            		        				window.location.reload();
            		        			});
            		        		};
            		        	}
            		        });
    					}
    				}
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