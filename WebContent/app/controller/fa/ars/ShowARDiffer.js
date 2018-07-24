Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ShowARDiffer', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'fa.ars.ShowARDiffer'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#ardiffergrid':{
    			afterrender: function(grid){
    				me.getArAccountDetail(grid);
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
    				var grid = Ext.getCmp('ardiffergrid');
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'-'+custname+'-'+currency+'_','  客户名称:'+custname+'    币别:'+currency+'    期间:'+yearmonth);
    			}
    		}
    	});
    },
    getArAccountDetail: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getARDifferByCust.action',
    		params: {
    			yearmonth: yearmonth,
    			custcode: custcode,
    			currency: currency,
    			catecode: catecode,
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