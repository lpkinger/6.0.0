Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ChartMang', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.ChartMang','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.form.FileField','core.form.MultiField','core.trigger.TextAreaTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
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
    				me.FormUtil.onAdd('ChartMang', '新增图号', 'jsps/scm/product/ChartMang.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('ct_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var statuscodeField = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(statuscodeField && statuscodeField.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('ct_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var statuscodeField = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(statuscodeField && statuscodeField.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('ct_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var statuscodeField = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(statuscodeField && statuscodeField.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('ct_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var statuscodeField = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(statuscodeField && statuscodeField.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('ct_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});