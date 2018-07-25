Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectTeamRole', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.ProjectTeamRole','core.form.Panel','core.button.ExportTemplate','core.form.ColorField','core.form.ColorTypeField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.WordSizeField'
    	],
    init:function(){
    	var me = this;
    	this.control({
            'erpSaveButton': {
            	click:function(btn){
            		this.save(btn);
            		//this.FormUtil.beforeSave(this);
            	}
            },
            'erpDeleteButton': {
                click: function(btn) {
                	me.FormUtil.onDelete(Ext.getCmp('tr_id').value);
                }
            },
            'erpUpdateButton': {
                click: function(btn) {
                	me.FormUtil.onUpdate(me);
                }
            },
           'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addProject', '新增项目团队角色', 'jsps/plm/project/ProjectTeamRole.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
    	});
    },
	save: function(btn){
		var me = this;
		if(Ext.getCmp('tr_code').value == null || Ext.getCmp('tr_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	}
});