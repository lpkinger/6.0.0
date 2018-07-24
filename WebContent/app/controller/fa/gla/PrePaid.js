Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.PrePaid', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.PrePaid','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Audit','core.button.ResAudit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.DeleteDetail','core.button.Add','core.button.Save','core.button.Post','core.button.ResPost',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
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
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('pp_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && (status.value == 'AUDITED' || status.value == 'POSTED')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
				}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
				}
    		},
    		'erpPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			buffer : 1000,
    			click: function(btn){
    				me.FormUtil.onPost(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpResPostButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'POSTED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResPost(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPrePaid', '新增摊销单据', 'jsps/fa/gla/prePaid.jsp');
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});