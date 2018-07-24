Ext.QuickTips.init();
Ext.define('erp.controller.oa.task.TaskTemplate', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'oa.task.TaskTemplate','core.form.Panel','core.button.Scan',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.Banned','core.button.ResBanned',
	       'core.form.YnField','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
	       ],
	       init:function(){
	    	   var me = this;
	    	   this.control({ 
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   var form = me.getForm(btn);
	    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					   me.BaseUtil.getRandomNumber(caller);
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
	    				   me.FormUtil.onAdd('TaskTemplate', '新增任务模板', 'jsps/oa/task/TaskTemplate.jsp');
	    			   }
	    		   },
	    		   'htmleditor':{
	    			   beforerender: function(field){
	    				   if(!field.autoHeight) field.height=window.innerHeight*0.88;
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete((Ext.getCmp('tt_id').value));
	    			   }
	    		   },
	    			'erpBannedButton': {
	    				afterrender: function(btn){
	    					var status = Ext.getCmp('TT_STATUSCODE');
	    					if(status && (status.value == 'DISABLE')){
	    						btn.hide();
	    					}
	    				},
	        			click: function(btn){
	        				this.FormUtil.onBanned(Ext.getCmp('tt_id').value);
	        			}
	        		},
	        		'erpResBannedButton': {
	        			afterrender: function(btn){
	    					var status = Ext.getCmp('TT_STATUSCODE');	    					
	    					if(status && status.value != 'DISABLE'){
	    						btn.hide();
	    					}
	    				},
	        			click: function(btn){
	        				this.FormUtil.onResBanned(Ext.getCmp('tt_id').value);
	        			}
	        		},
	    		   'multidbfindtrigger[name=TT_RELATIVENODE]':{
	    			   afterrender:function(trigger){
	    				   trigger.dbKey='TT_CALLER';
	    				   trigger.mappingKey='jd_caller';
	    				   trigger.gridErrorMessage='请先选择任务关联界面的CALLER';
	    			   }	
	    		   },
	    		   'combo[name=TT_TRIGGERTYPE]':{
	    			   change:function(combo,newvalue){
	    				   var triggerSql=Ext.getCmp('TT_TRIGGERSQL');
	    				   var relativeNode=Ext.getCmp('TT_RELATIVENODE');
	    				   if(newvalue=='AUTO'){ 
	    					   triggerSql.setFieldStyle('background:#E0E0FF;color:#515151;');
	    				   }else {
	    					   triggerSql.setFieldStyle('background:#FFFAFA;color:#515151;');
	    				   }
	    				   if(newvalue=='COMMIT'){
	    					   relativeNode.setFieldStyle('background:#E0E0FF;color:#515151;');
	    				   }else relativeNode.setFieldStyle('background:#FFFAFA;color:#515151;');
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       }
});