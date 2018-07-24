Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.NewProductCon', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.NewProductCon','core.form.Panel','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
      var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				//申请编号
    				if(Ext.getCmp('npc_code').value == null || Ext.getCmp('npc_code').value == ''){
    					me.BaseUtil.getRandomNumber();
    				}
    				
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    		    afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('npc_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('newProductCon', '新物料受控试产申请单', 'jsps/scm/product/newProductCon.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('npc_id').value);
    			}
    		},
    		'erpResSubmitButton':{
    		afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('npc_id').value);
    			}
    		
    		},
    	   'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('npc_id').value);
    			}
    		},   
    	   'erpResAuditButton':{
    	      afterrender: function(btn){
    				var status = Ext.getCmp('npc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('npc_id').value);
    			}
    	   }	 		    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
		me.FormUtil.beforeSave(me);
	}
});