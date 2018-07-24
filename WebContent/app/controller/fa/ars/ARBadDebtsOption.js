Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ARBadDebtsOption', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.ars.ARBadDebtsOption','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.Scan',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.form.MonthDateField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp('bdo_rate').value > 100){
    					showError('计提比例填写不能大于100！');
    					return
    				}
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('bdo_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp("bdo_statuscode");
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bdo_rate').value > 100){
    					showError('计提比例填写不能大于100！');
    					return
    				}
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addARBadDebtsOption', '新增坏账计提方案', 'jsps/fa/ars/arBadDebtsOption.jsp');
    			}
    		},
    		'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bdo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bdo_rate').value > 100){
    					showError('计提比例填写不能大于100！');
    					return
    				}
    				me.FormUtil.onSubmit(Ext.getCmp('bdo_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bdo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('bdo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bdo_auditstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				if(Ext.getCmp('bdo_rate').value > 100){
    					showError('计提比例填写不能大于100！');
    					return
    				}
    				me.FormUtil.onAudit(Ext.getCmp('bdo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('bdo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('bdo_id').value);
    			}
    		},

    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});