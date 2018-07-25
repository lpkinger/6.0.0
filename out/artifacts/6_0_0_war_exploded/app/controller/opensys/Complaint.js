Ext.QuickTips.init();
Ext.define('erp.controller.opensys.Complaint', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:['opensys.complaint.ViewPort','core.form.Panel2','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
		   'core.trigger.AddDbfindTrigger','core.form.FileField','core.form.CheckBoxGroup','core.form.SpecialContainField',
	       'core.button.Save','core.button.Close','core.button.Add','core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit',
	       'core.button.Audit','core.button.ResAudit'],
	init:function(){
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		var me=this;
		this.control({			
			'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addComplaint', '新增订单软件需求', 'jsps/opensys/complaint.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('co_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('co_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('co_statuscode');
					if(statu && statu.value != 'AUDITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('co_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('co_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
					var statu = Ext.getCmp('co_statuscode');
					if(statu && statu.value != 'COMMITED'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('co_id').value);
    			}
    		}
		});
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});