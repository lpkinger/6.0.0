Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.productStandard', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','scm.product.productStandard','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.CopyAll'
    	],
    init:function(){
    	var me=this;
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
    				this.FormUtil.onDelete(Ext.getCmp('psr_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addProductStandard', '物料标准库存周转率更新', 'jsps/scm/product/productStandard.jsp');
    			}
    		},
    		'erpSubmitButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp('psr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click : function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('psr_id').value);
    			}
    		},
    		'erpResSubmitButton' : {
    			afterrender: function(btn){
    				var status = Ext.getCmp('psr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('psr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('psr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('psr_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('psr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('psr_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});