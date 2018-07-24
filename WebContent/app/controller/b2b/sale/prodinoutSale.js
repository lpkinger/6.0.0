Ext.QuickTips.init();
Ext.define('erp.controller.b2b.sale.prodinoutSale', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','b2b.sale.prodinoutSale','core.grid.Panel2','core.toolbar.Toolbar',
  			'core.button.Close','core.form.MultiField','core.trigger.TextAreaTrigger',
  			'core.trigger.DbfindTrigger', 'core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
        me.GridUtil = Ext.create('erp.util.GridUtil');
        me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});