Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.ShowDiffer', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'co.cost.ShowDiffer'],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'grid': {
    			afterrender: function(grid){
    				me.getDetailDate(grid);
    			},
    			itemclick:this.onGridItemClick
    		},
    		'button[id=close]': {
    			click: function() {
    				parent.Ext.getCmp('win').close();
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('grid');
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'-'+name+'_','  科目:'+name+'    期间:'+yearmonth);
    			}
    		}
    	});
    },
    
    getDetailDate: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'co/cost/getDifferByCode.action',
    		params: {
    			yearmonth: yearmonth,
    			type: type,
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