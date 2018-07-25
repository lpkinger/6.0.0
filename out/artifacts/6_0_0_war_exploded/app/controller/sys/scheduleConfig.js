Ext.QuickTips.init();
Ext.define('erp.controller.sys.scheduleConfig', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ForBidden','core.button.ResForBidden','core.form.FileField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','sys.scheduleConfig.scheduleConfig'
	       ],    
    init: function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			 click: function(btn) {
                 	var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
 					if(codeField.value == null || codeField.value == ''){
 						me.BaseUtil.getRandomNumber(caller);//自动添加编号
 					}
                     //保存
 					this.FormUtil.beforeSave(this);
                 }
    		},
    		'erpDeleteButton': {
                click: function(btn) {
                    me.FormUtil.onDelete(Ext.getCmp('id_').value);
                }
            },
            'erpUpdateButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('statuscode_');
                    if (status && status.value != 'ENTERING') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                	this.FormUtil.onUpdate(this);
                }
            },
            'erpAddButton': {
                click: function() {
                    me.FormUtil.onAdd('addScheduleConfig', '定时任务配置', 'jsps/sys/scheduleConfig.jsp');
                }
            },
            'erpCloseButton': {
                click: function(btn) {
                    me.FormUtil.beforeClose(me);
                }
            },
            
    	});
	},
	getForm: function(btn) {
        return btn.ownerCt.ownerCt;
    },

});