Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.OATaskChange1', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.myProcess.OATaskChange1','core.form.Panel','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'dbfindtrigger[name=ptc_oldtaskname]':{
    			aftertrigger:function(){
    				Ext.getCmp('ptc_name').setValue(Ext.getCmp('ptc_oldtaskname').value);
    				Ext.getCmp('ptc_startdate').setValue(Ext.getCmp('ptc_oldstartdate').value);
    				Ext.getCmp('ptc_enddate').setValue(Ext.getCmp('ptc_oldenddate').value);
    				Ext.getCmp('ptc_taskman').setValue(Ext.getCmp('ptc_oldtaskman').value);
    				Ext.getCmp('ptc_tasklevel').setValue(Ext.getCmp('ptc_oldtasklevel').value);
    				Ext.getCmp('ptc_standtime').setValue(Ext.getCmp('ptc_oldstandtime').value);
    				Ext.getCmp('ptc_description').setValue(Ext.getCmp('ptc_olddescription').value);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ptc_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addOATaskChange', '新增任务变更', 'jsps/oa/myProcess/oataskchange.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ptc_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ptc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ptc_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ptc_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ptc_id').value);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});