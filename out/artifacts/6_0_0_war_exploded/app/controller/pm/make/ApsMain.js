Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.ApsMain', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','pm.make.ApsMain','core.grid.Panel2','core.toolbar.Toolbar','core.button.DeleteAllDetails','core.button.LoadingSource','core.button.GoMpsDesk',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.Update','core.button.Delete',
	       'core.button.Upload','core.button.ResAudit','core.button.DeleteDetail','core.button.ResSubmit',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.Flow','core.button.Refresh'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    		   'erpGridPanel2': { 
	    			   itemclick: function(selModel, record){
	    				   if(!Ext.getCmp('grid').readOnly){
	    					   this.onGridItemClick(selModel, record);
	    				   }
	    			   }
	    		   },
	    		   'textfield[name=mm_kind]':{
	    			   afterrender:function(field){
	    				   if(field.value==''){
	    					   field.setValue(me.BaseUtil.getUrlParam('kind'));
	    				   }
	    			   }
	    		   },
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   this.save(this);
	    			   }
	    		   },
	    		   'erpGoMpsDeskButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var form=Ext.getCmp('form');  
	    				   var MainCode=Ext.getCmp(form.codeField).value; 
	    				   me.FormUtil.onAdd('MpsDesk','工作台','/jsps/pm/make/ApsDesk.jsp?code='+MainCode);
	    			   }
	    		   },
	    		   'erpLoadingSourceButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var form=Ext.getCmp('form');
	    				   var keyField=form.keyField;
	    				   var keyValue=Ext.getCmp(keyField).value;
	   	        		  var win = new Ext.window.Window({
	   				    	id : 'win',
	   	   				    height: "100%",
	   	   				    width: "95%",
	   	   				    maximizable : true,
	   	   				    title:'销售排程装载',
	   	   					buttonAlign : 'center',
	   	   					layout : 'anchor',
	   	   					draggable:false, 
	   	   				    items: [{
	   	   				    	  tag : 'iframe',
	   	   				    	  frame : true,
	   	   				    	  anchor : '100% 100%',
	   	   				    	  layout : 'fit',
	   	   				    	 html : '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/pm/mps/loadSaleDetailDet.jsp?type=APS&keyValue='+keyValue 
	   	   				    	  	+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	   	   				    }],
	   	   				    buttons : [{
	   	   				    	text : $I18N.common.button.erpCloseButton,
	   	   				    	iconCls: 'x-button-icon-close',
	   	   				    	cls: 'x-btn-gray',
	   	   				    	handler : function(){
	   	   				    		Ext.getCmp('win').close();
	   	   				    	}
	   	   				    }]
	   	   				});
	   	   				win.show(); 
	    				   
	    			   }
	    		   },
	    		   'dbfindtrigger': {
	    			   change: function(trigger){
	    				   if(trigger.name == 'team_prjid'){
	    					   this.changeGrid(trigger);
	    				   }
	    			   }
	    		   },
	    		   'button[id=deleteallbutton]':{
	    			   click:function(btn){
	    				   var form=me.getForm(btn);
	    				   var id=Ext.getCmp('am_id').getValue();
	    				   if(!id){
	    					   showError('单据不存在任何明细!');
	    					   return
	    				   }
	    				   Ext.Ajax.request({
	    					   method:'post',
	    					   url:basePath+form.deleteAllDetailsUrl,
	    					   params:{
	    						   id:Ext.getCmp('am_id').getValue()
	    					   },
	    					   callback : function(options,success,response){
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.success){
	    							   Ext.Msg.alert('提示','清除成功!',function(btn){
	    								   //update成功后刷新页面进入可编辑的页面 
	    								   window.location.reload();
	    							   });
	    						   } else if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);return;
	    						   } 
	    					   }
	    				   });
	    			   },
	    		     afterrender:function(btn){
	    		    	 var statuscode=Ext.getCmp('am_statuscode').getValue();
	    		    	 if(statuscode&&statuscode!='ENTERING'){
	    		    		 btn.hide();
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
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onDelete(Ext.getCmp('am_id').value);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp("am_id").value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp("am_id").value);
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp("am_id").value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp("am_id").value);
	    			   }
	    		   },
	    		   'erpRefreshButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp("am_statuscode");
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var grid=Ext.getCmp('grid');
	    				   var value=Ext.getCmp('am_id').value;
	    				   var gridCondition=grid.mainField+'='+value;
	    				   gridParam = {caller: 'ApsMain', condition: gridCondition};
	    				   me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
	    			   }   		    		
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){

	    			   }
	    		   },

	    	   });
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   this.GridUtil.onGridItemClick(selModel, record);
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){
	    	   var me = this;
	    	   if(Ext.getCmp('am_code').value == null || Ext.getCmp('am_code').value == ''){
	    		   me.BaseUtil.getRandomNumber();
	    	   }
	    	   me.FormUtil.beforeSave(me);
	       }

});