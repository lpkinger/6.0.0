Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.ShowAPDiffer', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.grid.LinkColumn', 'fa.arp.ShowAPDiffer'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'#apdiffergrid':{
    			afterrender: function(grid){
    				me.getApAccountDetail(grid);
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
    				var grid = Ext.getCmp('apdiffergrid');
    				me.BaseUtil.exportGrid(grid, '差异明细'+'-'+yearmonth+'-'+vendname+'-'+currency+'_','  供应商名称:'+vendname+'    币别:'+currency+'    期间:'+yearmonth);
    			}
    		}
    	});
    },
    getApAccountDetail: function(grid) {
    	var me = this;
    	me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/arp/getAPDifferByVend.action',
    		params: {
    			yearmonth: yearmonth,
    			vendcode: vendcode,
    			currency: currency,
    			catecode: catecode,
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