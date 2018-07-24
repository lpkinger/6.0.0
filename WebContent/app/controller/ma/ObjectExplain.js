Ext.QuickTips.init();
Ext.define('erp.controller.ma.ObjectExplain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'ma.ObjectExplain','ma.ObjectExplainFormPanel','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpSaveButton': {
        			click: function(btn){
						this.FormUtil.beforeSave(this);
        			}
        		},
        		'erpCloseButton': {
        			click: function(btn){
        				this.FormUtil.beforeClose(this);				
        			}
        		}
        	});
        },
        getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
});