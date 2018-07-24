Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.ShowDifferAll', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'fa.gs.ShowDifferAll'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'grid':{
    			afterrender: function(grid){
    				me.getDifferAll(grid);
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('grid');
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'_','    期间:'+yearmonth);
    			}
    		}
    	});
    },
    getDifferAll: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/getDifferAll.action',
    		params: {
    			yearmonth: yearmonth,
    			chkun: chkun
    		},
    		callback: function(opt, s, r) {
    			me.FormUtil.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				grid.store.loadData(res.data);
    			}
    		}
    	});
    }
});