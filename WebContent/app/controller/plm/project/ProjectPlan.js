Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectPlan', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.project.ProjectPlan','core.form.Panel','core.button.ExportTemplate','core.grid.Panel2','core.grid.Panel5',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.ResAudit',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.TurnProjectReview'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   this.save(btn);
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   afterrender:function(btn){

	    				   if(Ext.getCmp('prjplan_ptid').getValue()!=0){
	    					   Ext.getCmp('prjplan_prjid').hide();
	    				   }else Ext.getCmp('prjplan_ptid').hide();
	    				   Ext.getCmp('prjplan_description').setHeight(350);
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   var grids = Ext.ComponentQuery.query('gridpanel');
	    					   Ext.Array.each(grids,function(grid){
	    						   grid.setReadOnly(true); 
	    					   });
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onDelete({prj_id: Number(Ext.getCmp('prjplan_id').value)});
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpSubmitButton':{
	    			   click:function(btn){
	    				   this.FormUtil.onSubmit(Ext.getCmp('prjplan_id').getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   click:function(btn){
	    				   this.FormUtil.onResSubmit(Ext.getCmp('prjplan_id').getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpAuditButton':{
	    			   click:function(btn){
	    				   this.FormUtil.onAudit(Ext.getCmp('prjplan_id').getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpResAuditButton':{
	    			   click:function(btn){
	    				   this.FormUtil.onResAudit(Ext.getCmp('prjplan_id').getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();  
	    				   }
	    			   }
	    		   },
	    		   'erpExportTemplateButton': {
	    			   click: function(btn){
	    				   if(!this.pressed){
	    					   Ext.getCmp('prjplan_ptid').show();	
	    					   Ext.getCmp('prjplan_prjid').setValue(0);
	    					   Ext.getCmp('prjplan_prjid').hide();
	    					   Ext.getCmp('prjplan_organiger').addCls('x-form-search-trigger');
	    					   this.pressed=true;
	    				   }else{
	    					   Ext.getCmp('prjplan_ptid').hide();	
	    					   Ext.getCmp('prjplan_ptid').setValue(0);
	    					   Ext.getCmp('prjplan_prjid').show();
	    					   Ext.getCmp('prjplan_organiger').removeCls('x-form-search-trigger');
	    					   this.pressed=false;
	    				   }

	    			   }
	    		   },
	    		   'erpTurnProjectReviewButton':{
	    			   afterrender:function(btn){
	    				   var statuscode=Ext.getCmp('prjplan_statuscode').getValue();
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var form=me.getForm(btn);
	    				   var id=Ext.getCmp('prjplan_id').getValue();
	    				   Ext.Ajax.request({
                               method:'POST',
                               url:basePath+form.turnReviewItemUrl,
                               params:{
                            	   id:id
                               },
                               callback : function(options,success,response){
                   	   			var rs = new Ext.decode(response.responseText);
                   	   			if(rs.exceptionInfo){
                           			showError(rs.exceptionInfo);return;
                           		}
                       			if(rs.success){
                   	   				Ext.Msg.alert('提示','转评审成功!单号为:'+rs.code,function(){
                   	   				window.location.reload();
                   	   				});
                   	   			}
                   	   		}
	    				   });
	    			   }
	    		   },
	    		   'dbfindtrigger[name=prjplan_organiger]':{
	    			   beforerender:function(dbfindtrigger){

	    			   }			
	    		   },
	    		   'hidden[name=prjplan_id]':{
	    			   afterrender:function(field){
	    				   if(field.value){
	    					   var grid=Ext.getCmp('team');
	    					   var param={
	    							   caller:grid.caller,
	    							   condition:grid.mainField+"="+field.value
	    					   };
	    					   me.GridUtil.loadNewStore(grid,param);
	    				   }
	    			   }
	    		   },
	    		   'dbfindtrigger[name=prjplan_prjid]':{
	    			   afterrender:function(dbfindtrigger){  	        
	    				   dbfindtrigger.dbBaseCondition="prj_statuscode IS 'AUDITED'";    	        
	    			   },    		
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       save: function(btn){
	    	   var me = this;
	    	   if(Ext.getCmp('prjplan_code').value == null || Ext.getCmp('prjplan_code').value == ''){
	    		   me.BaseUtil.getRandomNumber();
	    	   }
	    	   var mm = me.FormUtil;
	    	   var form = Ext.getCmp('form');
	    	   if(! mm.checkForm()){
	    		   return;
	    	   }
	    	   if(form.keyField){
	    		   if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
	    			   mm.getSeqId(form);
	    		   }
	    	   }
	    	   var grids = Ext.ComponentQuery.query('gridpanel');
	    	   var arg=new Array();
	    	   if(grids.length > 0){
	    		   for(var i=0;i<grids.length;i++){
	    			   var param = me.GridUtil.getGridStore(grids[i]);
	    			   if(grids[i].necessaryField.length > 0 && (param == null || param == '')){
	    				   arg.push([]);
	    			   } else {
	    				   arg.push(param);
	    			   }
	    		   }
	    		   me.onSave(arg[0],arg[1]);
	    	   }else {
	    		   me.onSave([]);
	    	   }
	       },
	       onSave:function(param,param1){
	    	   var me = this;
	    	   var form = Ext.getCmp('form');
	    	   param = param == null ? [] : "[" + param.toString() + "]";
	    	   param1 = param1 == null ? [] : "[" + param1.toString() + "]";
	    	   if(form.getForm().isValid()){
	    		   //form里面数据
	    		   Ext.each(form.items.items, function(item){
	    			   if(item.xtype == 'numberfield'){
	    				   //number类型赋默认值，不然sql无法执行
	    				   if(item.value == null || item.value == ''){
	    					   item.setValue(0);
	    				   }
	    			   }
	    		   });
	    		   var r = form.getValues();
	    		   //去除ignore字段
	    		   var keys = Ext.Object.getKeys(r), f;
	    		   var reg = /[!@#$%^&*()'":,\/?]/;
	    		   Ext.each(keys, function(k){
	    			   f = form.down('#' + k);
	    			   if(f && f.logic == 'ignore') {
	    				   delete r[k];
	    			   }
	    			   //codeField值强制大写,自动过滤特殊字符
	    			   if(k == form.codeField && !Ext.isEmpty(r[k])) {
	    				   r[k] = r[k].trim().toUpperCase().replace(reg, '');
	    			   }
	    		   });
	    		   if(!me.FormUtil.contains(form.saveUrl, '?caller=', true)){
	    			   form.saveUrl = form.saveUrl + "?caller=" + caller;
	    		   }
	    		   me.FormUtil.save(r,param,param1);
	    	   }else{
	    		   me.FormUtil.checkForm();
	    	   }
	       }
});