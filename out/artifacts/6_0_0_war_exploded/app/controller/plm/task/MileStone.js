Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.MileStone', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','plm.task.MileStone','core.toolbar.Toolbar','core.grid.Panel2','core.form.MultiField','core.form.FileField',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.DeleteDetail',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField','core.grid.YnColumn'
	       ],
	       init:function(){
	    	   var me=this;
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
	    		   'datefield[name=enddate]':{
	    			   change: function(field){
	    				   Ext.getCmp('baselineenddate').setValue(field.value);
	    				   var grid=Ext.getCmp('grid');
	    				   Ext.Array.each(grid.store.data.items, function(item){
	    					   item.set('ra_enddate',field.value);
	    				   });
	    			   }
	    		   },
	    		   'combo[name=rank]':{
	    			   change:function(combo){
	    				   var parentid=Ext.getCmp('parentid');
	    				   var relateid=Ext.getCmp('relateid');
	    				   var enddate=Ext.getCmp('enddate');
	    				   if(combo.value==1){
	    					   //任务级  里程碑的交付时间不恩能够修改 即为该任务的结束时间
	    					   enddate.setReadOnly(true);
	    					   relateid.reset();
	    					   parentid.reset(); 
	    					   parentid.show();
	    					   relateid.show();  
	    				   }else {
	    					   enddate.setReadOnly(false);
	    					   parentid.setValue(-1);
	    					   relateid.setValue(-1);
	    					   parentid.hide();
	    					   relateid.hide();    		  
	    				   }
	    			   }    		
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				   var percentdone = Ext.getCmp('percentdone');
	    				   if(percentdone && percentdone.value == 100){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){		
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onDelete({id: Number(Ext.getCmp('id').value)});
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var statu = Ext.getCmp('ms_statuscode');
	    				   if(statu && statu.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('ms_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var statu = Ext.getCmp('ms_statuscode');
	    				   if(statu && statu.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('ms_id').value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var statu = Ext.getCmp('ms_statuscode');
	    				   if(statu && statu.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('ms_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var statu = Ext.getCmp('ms_statuscode');
	    				   if(statu && statu.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('ms_id').value);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addMileStone', '创建里程碑', 'jsps/plm/task/milestone.jsp');
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       }
});