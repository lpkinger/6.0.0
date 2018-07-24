Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.ShowDiffer', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'fa.gs.ShowDiffer'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'grid':{
    			afterrender: function(grid){
    				me.getAccountDetail(grid);
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
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'-'+name+'-'+currency+'_','  名称:'+name+'    币别:'+currency+'    期间:'+yearmonth);
    			}
    		}
    	});
    },
    getAccountDetail: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/getDifferByCode.action',
    		params: {
    			yearmonth: yearmonth,
    			code: code,
    			currency: currency,
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