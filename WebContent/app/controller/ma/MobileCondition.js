Ext.QuickTips.init();
Ext.define('erp.controller.ma.MobileCondition', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['ma.MobileCondition', 'core.grid.HeaderFilter'],
    init:function(){
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	var me = this;
    	this.control({ 
    		'#refresh': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
		    		grid.store.load();
    			}
    		},
    		'#close': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		}
    	});
    }
});