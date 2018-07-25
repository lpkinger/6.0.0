Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarStockLoss', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views:[
	       'core.form.Panel','scm.reserve.BarStockLoss','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
	       'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit', 'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	       'core.button.Banned','core.button.ResBanned','core.button.Post','core.button.ResPost','core.button.Query','core.trigger.DbfindTrigger'
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
	    				   this.GridUtil.onGridItemClick(selModel, record);
	    			   }
	    		   },
    		      'dbfindtrigger[name=bsd_batchcode]':{
		    			focus: function(t){}	    			
    		        },
    		       'dbfindtrigger[name=bsd_barcode]':{
			    		focus: function(t){		    				
		    				t.autoDbfind = false;
		    				t.setHideTrigger(false);
		    				t.setReadOnly(false); 	    					    
		    				var record = Ext.getCmp('grid').selModel.getLastSelected();
		    			    var batchcode = record.data['bsd_batchcode'];
		    			    if(batchcode !='' && batchcode != null){
		    			    	t.dbBaseCondition ="bar_batchcode='"+batchcode+"'";
		    			    }	    			 			    				
			    		}	    			
    		        },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				  var form = me.getForm(btn);
	    				  if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					  me.BaseUtil.getRandomNumber();//自动添加编号
	    				  }
	    				   me.FormUtil.beforeSave(me);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				   if(status && status.value == 'DELETED'){
	    					   btn.hide();
	    				   }	    				   
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onDelete({pu_id: Number(Ext.getCmp('bs_id').value)});
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
	    				   me.FormUtil.onUpdate(me);  			
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('add' + caller, '新增库存条码盘亏维护', "jsps/scm/reserve/barStockLoss.jsp?whoami=" + caller);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
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
	    				   var grid = Ext.getCmp('grid');
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
	    				   if((status && status.value != 'AUDITED') ){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('bs_id').value);
	    			   }
	    		   }    					    		   
	     });
	    },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){}
	   
});