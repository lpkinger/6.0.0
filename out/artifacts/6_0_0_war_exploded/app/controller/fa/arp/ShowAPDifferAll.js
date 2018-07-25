Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.ShowAPDifferAll', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'fa.arp.ShowAPDifferAll'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#apdifferallgrid':{
    			afterrender: function(grid){
    				me.getApDifferAll(grid);
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('apdifferallgrid');
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'_','    期间:'+yearmonth);
    			}
    		}
    	});
    },
    getApDifferAll: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/arp/getAPDifferAll.action',
    		params: {
    			yearmonth: yearmonth,
    			chkun: chkun
    		},
    		callback: function(opt, s, r) {
    			me.FormUtil.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(res.success){
    				if(grid && res.data) {
    					grid.store.loadData(res.data);
    				}
    			}else if(res.exceptionInfo){
    				showError(res.exceptionInfo);
    			}
    			
    		}
    	});
    }
});