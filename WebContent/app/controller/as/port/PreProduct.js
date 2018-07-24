Ext.define('erp.controller.as.port.PreProduct', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'as.port.PreProduct','core.button.VastDeal','core.button.Delete','core.button.Close'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'#grid1': {
    			beforeheaderfiltersapply: function(grid, filters) {
    				if(!grid.filterRendered) {
    					grid.filterRendered = true;
    					return false;
    				}
    				var validFilters = {}, keys = Ext.Object.getKeys(filters);
    				Ext.Array.each(keys, function(key){
    					if(!Ext.isEmpty(filters[key]))
    						validFilters[key] = filters[key];
    				});
    				grid.getStore().load({
    					params: {
    						filters: Ext.encode(validFilters)
    					}
    				});
    				return false;
    			}
    		},
    		'erpVastDealButton': {
    			click: function(btn){
    				me.vastDeal('as/port/applyToProdIO.action');
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.vastDeal('as/port/applyDelete.action');
    			}
			}
    	});
    },
    vastDeal: function(url){
    	var me = this, grid = Ext.getCmp('grid1');
    	var records = grid.selModel.getSelection();
    	var data = new Array();
    	if(records.length > 0){
    		Ext.each(records, function(record, index){
    			var v = record.data['APPLY_NO'];
    			data.push(v);
    		});
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: {
		   			data: data.join(',')
		   		},
		   		method : 'post',
		   		timeout: 6000000,
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			me.dealing = false;
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				showError(str);return;
		   			}
	    			if(localJson.success){
	    				if(localJson.log){
	    					showMessage("提示", localJson.log);
	    				}
	    				grid.getStore().load();
		   			}
		   		}
			});
    	} else {
			showError("请勾选需要的明细!");
		}
    }
});