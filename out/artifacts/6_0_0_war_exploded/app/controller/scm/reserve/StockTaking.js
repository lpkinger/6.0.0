Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.StockTaking', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.StockTaking','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.TurnNotify','core.button.FeatureDefinition',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.button.Print'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('st_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('st_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('st_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('st_id').value);
				}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('st_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('st_id').value);
				}
    		},
    		//新增打印按钮
			'erpPrintButton': {
                click: function(btn) {
                	 var id = Ext.getCmp('st_id').value;
                     me.FormUtil.onwindowsPrint2(id, "", "");
                }
            }
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});