Ext.QuickTips.init();
Ext.define('erp.controller.pm.WorkCenter', {
	extend : 'Ext.app.Controller',
    init:function(){
		var me = this;
		this.control({
    		'#workcenter': {
    			afterrender:function(grid){
    		        grid.setLoading(true);//loading...
    				Ext.Ajax.request({//拿到grid的columns
    		        	url : basePath + "pm/mes/getMakeCraft.action",
    		        	params:{
    		        	  caller:'WorkSchedul'
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		grid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);return;
    		        		}
    		        		var data = res.data;
    		        		 grid.store.loadData(data); 
    		        		//自定义event
    		        		grid.addEvents({
    		        		    storeloaded: true
    		        		});
    		        		grid.fireEvent('storeloaded', grid, data);
    		        	}
    		        });
    			},
    			itemclick : function(view,record){
    				var dealgrid = parent.Ext.getCmp('batchDealGridPanel');
    				var wc_code = record.data.WC_CODE;
    				dealgrid.setLoading(true);//loading...
    				Ext.Ajax.request({
    		        	url : basePath + "pm/mes/getWorkCenter.action",
    		        	params:{
    		        		wc_code:wc_code
    		        	},
    		        	method : 'post',
    		        	callback : function(options,success,response){
    		        		dealgrid.setLoading(false);
    		        		var res = new Ext.decode(response.responseText);
    		        		if(res.exceptionInfo){
    		        			showError(res.exceptionInfo);
    		        			return;
    		        		}
    		        		var data = res.data;
    		        		dealgrid.store.loadData(data);
    		        		//自定义event
    		        		dealgrid.addEvents({
    		        		    storeloaded: true
    		        		});
    		        		dealgrid.fireEvent('storeloaded', dealgrid, data);
    		        	}
    		        });
    			}
    		}
		});
		me.callParent(arguments);
    }
});