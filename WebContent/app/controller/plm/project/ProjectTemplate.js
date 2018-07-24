Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectTemplate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.ProjectTemplate','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField'
    	],
    init:function(){
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpCloseButton': {    	
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
    				this.FormUtil.onDelete({pre_id: Number(Ext.getCmp('pt_id').value)});
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addProjectTeamplate', '新增项目模板', 'jsps/plm/project/projecttemplate.jsp');
    			}
    		},
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('pt_code').value == null || Ext.getCmp('pt_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	}
});