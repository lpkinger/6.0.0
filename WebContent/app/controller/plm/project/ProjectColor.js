Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectColor', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.ProjectPlan','core.form.Panel','core.button.ExportTemplate','core.form.ColorField','core.form.ColorTypeField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.WordSizeField'
    	],
    init:function(){
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    			var aa=Ext.getCmp('form');
    			console.log(Ext.getCmp('form').getForm());
    				this.save(btn);
    			}
    		},
    		'erpCloseButton': {
    		afterrender:function(btn){
    		
    		 
    		},
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete({pre_id: Number(Ext.getCmp('prj_id').value)});
    			}
    		},

    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('pc_code').value == null || Ext.getCmp('pc_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	}
});