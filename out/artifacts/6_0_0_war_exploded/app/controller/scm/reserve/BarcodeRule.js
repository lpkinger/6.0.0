Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarcodeRule', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.BarcodeRule','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.trigger.TextAreaTrigger',
	       'core.button.CopyByConfigs'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.FormUtil = Ext.create('erp.util.FormUtil');
	    	   me.GridUtil = Ext.create('erp.util.GridUtil');
	    	   me.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.control({
	    		   'erpGridPanel2': { 
	    			   itemclick: this.onGridItemClick
	    		   },	  
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				var me = this;
		       			var form = me.getForm(btn);
		       			if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	       					me.BaseUtil.getRandomNumber();//自动添加编号
	       				}
	    			      me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {	    			
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({id: Number(Ext.getCmp('nr_id').value)});
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
	    				   me.FormUtil.onAdd('add' + caller, '新增条码规则', "jsps/scm/reserve/barcodeRule.jsp?whoami=" + caller);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },	    		 
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
   				this.GridUtil.onGridItemClick(selModel, record);
	       }
});