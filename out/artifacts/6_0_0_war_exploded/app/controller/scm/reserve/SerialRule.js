Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SerialRule', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.SerialRule','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
	       'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	       'core.button.Banned','core.button.ResBanned'
	       ],
	       init:function(){
	    	   var me = this;
	    	   var grid = Ext.getCmp('grid');
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({
	    		   'erpGridPanel2': { 
	    			   afterrender: function(grid){},
	    			   itemclick: function(selModel, record){
					    	if(!selModel.ownerCt.readOnly){
					    		this.GridUtil.onGridItemClick(selModel, record);
					    	}
	    			   }
	    		   },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    			      me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {	    			
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('bs_id').value)});
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    			   	var status = Ext.getCmp(me.getForm(btn).statuscodeField);
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
	    				   me.FormUtil.onAdd('add' + caller, '新增序列产生规则', "jsps/scm/reserve/serialRule.jsp?whoami=" + caller);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },	    		  
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    			      me.FormUtil.onAudit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if((status && status.value != 'AUDITED')){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }	    				  
	    			   },
	    			   click: function(btn){
	    			   	me.FormUtil.onSubmit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }	    				  
	    			   },
	    			   click: function(btn){
	    			   	 me.FormUtil.onResSubmit(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpBannedButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value == 'DISABLE'){
								btn.hide();
							}    				  
	    			   },
	    			   click: function(btn){
	    			   	this.FormUtil.onBanned(Ext.getCmp('bs_id').value);
	    			   }
	    		   },
	    		   'erpResBannedButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				  if(status && status.value != 'DISABLE'){
								btn.hide();
							}	    				  
	    			   },
	    			   click: function(btn){
	    			   	 this.FormUtil.onResBanned(Ext.getCmp('bs_id').value);
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){}	  	       
});