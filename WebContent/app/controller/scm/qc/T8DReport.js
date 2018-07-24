Ext.QuickTips.init();
Ext.define('erp.controller.scm.qc.T8DReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.qc.T8DReport','core.toolbar.Toolbar','core.form.MultiField',
      			'core.form.FileField','core.form.CheckBoxGroup',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      			'core.button.ResSubmit','core.button.Flow','core.button.Check','core.button.ResCheck',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
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
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addT8DReport', '新增8D报告', 'jsps/scm/qc/t8dreport.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_statuscode'), checkstatus = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_statuscode'), checkstatus = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'UNAPPROVED'){
    					btn.hide();
    				}
    				var auditstatus = Ext.getCmp('re_statuscode');
    				if(auditstatus && auditstatus.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_statuscode'), checkstatus = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value == 'APPROVE'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_statuscode'), checkstatus = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(checkstatus && checkstatus.value != 'UNAPPROVED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onCheck(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpResCheckButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('re_checkstatuscode');
    				if(status && status.value != 'APPROVE' ){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResCheck(Ext.getCmp('re_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{T8DReport.re_id}=' + Ext.getCmp('re_id').value + '';
    				var id = Ext.getCmp('re_id').value;
    				reportName="T8DReport";
    				me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});