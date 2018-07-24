Ext.QuickTips.init();
Ext.define('erp.controller.plm.change.ProjectChange', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.change.ProjectChange','core.form.Panel','core.button.Upload','core.button.DownLoad','core.form.MultiField',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
	       'core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.button.Delete','core.trigger.DbfindTrigger'
	       ],
	       init:function(){
	    	   var me = this;
	    	   me.attachcount = 0;
	    	   this.control({
	    	   	   'erpFormPanel':{
	    	   	   		afterload:function(){
	    	   	   			var prjcode = Ext.getCmp('pc_oldprjcode');
	    	   	   			var prjstatus = Ext.getCmp('pc_prjstatus');
	    	   	   			if(prjcode.value&&prjstatus.value){
	    	   	   				me.setCombo(prjstatus.value);
	    	   	   			}
	    	   	   			var main = getUrlParam('main');
							formCondition = getUrlParam('formCondition');
							if(main&&!formCondition){
								me.FormUtil.autoDbfind(caller, 'pc_oldprjcode', main);
							}
	    	   	   		}
	    	   	   },
	    	       'dbfindtrigger[name=pc_oldprjcode]':{
	    	       		aftertrigger:function(trigger, record, dbfinds){    	
	    	       			var status = Ext.getCmp('pc_statuscode');
							if(status && status.value == 'ENTERING'){
								var projectStatus = record.data['prj_status']; //项目的状态
								me.setCombo(projectStatus);
		    	       		}
		    	 			var form = Ext.getCmp('form');
		    	 			
		    	 			//变更前的值赋给变更后
		    	 			var oldValue = new Array();
		    	 			var newValue = new Array();
		    	 			Ext.Array.each(form.items.items,function(item,index){
		    	 				if(item.groupName=='变更前'){
		    	 					oldValue.push(item);
		    	 				}
		    	 				if(item.groupName=='变更后'){
		    	 					newValue.push(item);
		    	 				}
		    	 			});
		    	 			for(var i=0;i<oldValue.length;i++){
		    	 				newValue[i].setValue(oldValue[i].value);
		    	 			}
		    	 			
		    	 			//项目变更操作默认为不变
		    	 			Ext.getCmp('pc_changetype').setValue('不变');
						}

	    	       },
	    	       'field[name=pc_changetype]':{
	    	       		afterrender:function(combo){
	    	       			combo.getStore().removeAll();  //清除下拉框的值
	    	       		}
	    	       },
	    		   'erpSaveButton': {
	    			   click: function(btn){
		    			   	var bool = me.checkTime();
							if(bool==='over'){
								showError("新结束日期不能小于新开始日期");
							}else{
								this.save(btn);
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
	    				   var status = Ext.getCmp('prj_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    			   		var bool = me.checkTime();
							if(bool==='over'){
								showError("新结束日期不能小于新开始日期");
							}else{
								this.FormUtil.onUpdate(this);
							}
	    			   }
	    		   },
	    		   'dbfindtrigger[name=pc_proposer]': {
	    			   afterrender:function(trigger){
	    				   trigger.dbKey='pc_oldprjid';
	    				   trigger.mappingKey='tm_prjid';
	    				   trigger.dbMessage='请选择需变更项目计划';
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pc_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onDelete(Ext.getCmp('pc_id').value);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addProjectChange', '新增项目计划变更', 'jsps/plm/change/ProjectPlanChange.jsp');
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pc_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pc_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value); 
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pc_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('pc_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				  // me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);  //注：项目变更单不允许反审核!!!
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
	    		   me.BaseUtil.getRandomNumber();
	    	   }
	    	   me.FormUtil.beforeSave(me);
	       },
	       checkTime: function(){
	    		var date = new Date();
	        	var start=Ext.getCmp('pc_newstartdate').value;        	
	        	var end=Ext.getCmp('pc_newenddate').value;	
				if(start>end){
					return 'over';
				}
				return true;
        	},
        	setCombo:function(prjstatus){
   				var combo = Ext.getCmp('pc_changetype');
   				var comboStore = combo.getStore();
				comboStore.removeAll();  //清空store,防止在form里设置了下拉框选项
				var projectStatusAll = ['已启动','未启动','暂停中'];
				var newStatus = ['暂停,不变','暂停,不变','重启,不变'];
				//var projectStatus = prjstatus.value; //项目的状态
				var index = Ext.Array.indexOf(projectStatusAll,prjstatus);
				if(index>-1){
					var arr = new Array();
					var type = newStatus[index].split(",");
					for(var j=0;j<type.length;j++){
						var obj = new Object();											
						obj.value = type[j];
						obj.display = type[j];
						arr.push(obj);
					}
					comboStore.loadData(arr);
				}        		
        	}
});