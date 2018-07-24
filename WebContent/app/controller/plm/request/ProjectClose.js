Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.ProjectClose', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.request.ProjectClose','core.form.Panel','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit',
    		'core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.grid.ItemGrid',
    		'core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField','core.form.YnField','core.form.TimeMinuteField',
    		'core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('ProjectClose', '新增项目结案申请单', 'jsps/plm/request/ProjectClose.jsp');
        			}
        		},
        		'erpSaveButton': {
        			click: function(btn){				
						this.FormUtil.beforeSave(this);				
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
        				me.FormUtil.onDelete((Ext.getCmp('pc_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
        			}
        		},
        		'erpResAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pc_statuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
        			}
        		}
        	});
        }
});