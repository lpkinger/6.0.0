Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.ProjectGanttTask', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	stores: ['plm.Assignment','plm.Dependency','plm.Resource','plm.Task'],
	models: ['plm.Dependency','plm.Resource','plm.Task'],
	views:[
	       'plm.task.ProjectGanttTask','core.form.Panel','core.grid.Panel2','core.grid.YnColumn','core.button.ImportExcel','core.button.Load','core.button.ImportMpp',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit','core.form.HrefField',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.button.CreateCheckList','core.grid.detailAttach',
	       'core.button.Confirm','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.button.TurnTask','core.button.End','core.button.ResEnd',
	       'plm.task.GanttPanel','plm.task.ProjectImportPanel','plm.task.ProdTaskForm'],
	       init:function(){
	    	   var me=this,statuscode=null;
	    	   this.control({ 
	    		   'erpGridPanel2': {		  
	    			   itemclick: this.onGridItemClick
	    		   },
	    		   'erpSaveButton': {
	    			   click:function(btn){
	    				   var form = me.getForm(btn);
	    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					   me.BaseUtil.getRandomNumber();
	    				   }
	    				   this.FormUtil.beforeSave(this);
	    			   },
	    			   afterender:function(btn){	
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   }, 
	    		   'erpUpdateButton':{
	    			   click:function(){
	    				   var g=Ext.getCmp('gantt_panel');
	    				   g.sync(null,null,true);
	    				   this.onUpdate(this);	   
	    			   },
	    			   beforerender:function(btn){
	    				   btn.formBind=true;  
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();	    					 
	    				   }
	    			   }
	    		   },
	    		   'erpImportMppButton':{
	    			   click:function(){
	    				   var window =  Ext.create('erp.view.plm.task.ProjectImportPanel');
							window.show();
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();	
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpDeleteButton':{
	    			   click:function(){
	    				   this.FormUtil.onDelete(Ext.getCmp("pt_id").getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();	
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpLoadButton':{
	    			   afterrender:function(btn){
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(button){
						   var items = Ext.getCmp('gantt_panel').store.treeStore.nodeStore.data.items;
						   var flag = false;
						   if(items){
							   Ext.Array.each(items,function(item){
							       if(item.data.Name){
							       	   flag = true;
							       }
							   });						   	
						   }

						   if(flag){
		    				   warnMsg("请确认是否载入任务书模板，如载入明细数据将会覆盖!", function(btn){
		    					   if(btn == 'yes'){
		    					 	   me.showWin();	    						   
		    					   }
		    				   });						   	
						   }else{
						   	    me.showWin();
						   }

	    			   }  
	    		   },
	    		   'erpExportExcelButton':{
	    			   afterrender:function(btn){
	    				   btn.exportCaller='ImportProjectTask';
	    			   }   
	    		   },
	    		   'erpSubmitButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue(),g=Ext.getCmp('gantt_panel'),params={};
	    				   if(g.getSyncParams(g,params)){
	    					   Ext.MessageBox.show({
	    							title:'保存修改?',
	    							msg: '任务明细已被修改，提交前要先保存吗？',
	    							buttons: Ext.Msg.YESNO,
	    							icon: Ext.Msg.WARNING,
	    							fn: function(btn){
	    								if(btn == 'yes'){
	    									g.sync(params,function(){
	    										window.location.reload();
	    			  							alert('保存成功!');			
	    									},true);	
	    								}else if(btn == 'no'){
	                                    	me.FormUtil.onSubmit(id);	
	    								} else {
	    									return;
	    								}
	    							}
	    						});
	    				   }else me.FormUtil.onSubmit(id);
	    			   },
	    			   afterrender:function(btn){	    				
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResSubmit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpAuditButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onAudit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResAuditButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResAudit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpEndButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   warnMsg('确认结案研发任务书吗?', function(btn){
	    					   if(btn == 'yes'){
	    						   me.FormUtil.onEnd(id);
	    					   } else {
	    						   return;
	    					   }
	    				   });

	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='AUDITED'&&statuscode!='DOING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResEndButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResEnd(id);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='FINISHED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpTurnTaskButton':{
	    			   click:function(btn){
	    				   me.TurnTask(btn);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   var g=Ext.getCmp('gantt_panel');g.setReadOnly(true);
	    				   }
	    			   }
	    		   },	
	    		   'htmleditor[name=pt_prjcode]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openFormUrl(' + editor.value + ',\'prj_code\',\'jsps/plm/project/project.jsp\',\'立项申请\''+ ');">' + editor.value + '</a>');

	    			   }
	    		   },
	    		   'htmleditor[name=pt_prcode]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openFormUrl(\'' + editor.value + '\',\'pr_code\',\'jsps/plm/project/projectReview.jsp\',\'项目评审\''+ ');">' + editor.value + '</a>');

	    			   }
	    		   },
	    		   'multidbfindtrigger[name=resourcecode]':{
	    			   afterrender:function(trigger){
	    				   trigger.gridKey='pt_prjid';
	    				   trigger.mappinggirdKey='tm_prjid';
	    				   trigger.gridErrorMessage='请选择该任务的项目ID';
	    			   }	
	    		   },
	    		   'datefield':{
	    		   		beforerender:function(field){
							var dataIndex = field.dataIndex;
							if(dataIndex=='pt_recorddate'||dataIndex=='prj_start'||dataIndex=='prj_end'){
								me.setDateFiledValue(field);
							}
	    		   		}
	    		   }
	    	   });
	       },
	       setDateFiledValue:function(field){
			   if(field.value && field.value!=null) {
				   field.rawValue=field.value.substring(0,10);
			   }	       		
	       },
	       onUpdate:function(){
	    	   var form = Ext.getCmp('form'),me=this;
	    	   if(form && form.getForm().isValid()){
	    		var r=form.getValues();
	   			var keys = Ext.Object.getKeys(r), f;
	   			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
	   			Ext.each(Ext.Object.getKeys(r), function(k){
	   				if(k && k.indexOf('ext-')>-1) delete r[k];
	   				f = form.down('#' + k);
	   				if(f && f.logic == 'ignore') {
	   					delete r[k];
	   				}
	   			});
	   			me.FormUtil.setLoading(true);
	   			Ext.Ajax.request({
	   				url : basePath + form.updateUrl,
	   				params: {
	   					formStore:Ext.JSON.encode(r)
	   				},
	   				method : 'post',
	   				callback : function(options,success,response){
	   					me.FormUtil.setLoading(false);
	   					var localJson = new Ext.decode(response.responseText);
	   					if(localJson.success){
	   						showMessage('提示', '保存成功!', 1000);
	   						//update成功后刷新页面进入可编辑的页面
	   						var u = String(window.location.href);
	   						if (u.indexOf('formCondition') == -1) {
	   							var value = r[form.keyField];
	   							var formCondition = form.keyField + "IS" + value ;
	   							var gridCondition = '';
	   							var grid = Ext.getCmp('grid');
	   							if(grid && grid.mainField){
	   								gridCondition = grid.mainField + "IS" + value;
	   							}
	   							if(me.contains(window.location.href, '?', true)){
	   								window.location.href = window.location.href + '&formCondition=' + 
	   								formCondition + '&gridCondition=' + gridCondition;
	   							} else {
	   								window.location.href = window.location.href + '?formCondition=' + 
	   								formCondition + '&gridCondition=' + gridCondition;
	   							}
	   						} else {
	   							window.location.reload();
	   						}
	   					} else if(localJson.exceptionInfo){
	   						var str = localJson.exceptionInfo;
	   						if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   							str = str.replace('AFTERSUCCESS', '');
	   							//update成功后刷新页面进入可编辑的页面 
	   							var u = String(window.location.href);
	   							if (u.indexOf('formCondition') == -1) {
	   								var value = r[form.keyField];
	   								var formCondition = form.keyField + "IS" + value ;
	   								var gridCondition = '';
	   								var grid = Ext.getCmp('grid');
	   								if(grid && grid.mainField){
	   									gridCondition = grid.mainField + "IS" + value;
	   								}
	   								if(me.contains(window.location.href, '?', true)){
	   									window.location.href = window.location.href + '&formCondition=' + 
	   									formCondition + '&gridCondition=' + gridCondition;
	   								} else {
	   									window.location.href = window.location.href + '?formCondition=' + 
	   									formCondition + '&gridCondition=' + gridCondition;
	   								}
	   							} else {
	   								window.location.reload();
	   							}
	   						}
	   						showError(str);return;
	   					} else {
	   						updateFailure();
	   					}
	   				}
	   			});
	         }
	       },
	       TurnTask:function(btn){
	    	   var form=btn.ownerCt.ownerCt;
	    	   var id=Ext.getCmp('pt_id').getValue();
	    	   Ext.Ajax.request({
	    		   url : basePath + 'plm/gantt/TurnTask.action',
	    		   params: {
	    			   id: id
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','任务节点激活成功!',function(){window.location.reload();});
	    			   } else {
	    				   if(localJson.exceptionInfo){
	    					   var str = localJson.exceptionInfo;
	    					   if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	    						   str = str.replace('AFTERSUCCESS', '');
	    						   submitSuccess(function(){
	    							   window.location.reload();
	    						   });
	    					   }
	    					   showMessage("提示", str);return;
	    				   }
	    			   }
	    		   }
	    	   });
	       },
	       showWin:function(){
	       		var me = this;
	        	var startTimeWin = Ext.getCmp('startTimeWin');
	        	if(startTimeWin){
	        		startTimeWin.show();
	        	}else{
	        		var win = Ext.create('Ext.window.Window',{
	         			title : '确认',
						height : 155,
						width : 363,
						border:false,
						id : 'startTimeWin',
						bodyStyle : 'background:#F2F2F2;',
						layout:'fit',
						items:[{
							xtype : 'form',
							layout: {
						        type: 'hbox',
						        pack: 'end',
						        align: 'middle'
					    	},
							bodyStyle : 'background:#F2F2F2;',
							border:false,
							defaults : {
								flex:1,
								labelWidth:120,
								margin:'10 30 10 35'
							},
							items : [{
									xtype : 'datefield',
									name : "starttime",
									id:'starttime',
									fieldLabel : '请确认项目启动日期',
									border : false,
									listeners:{
										change:function(self,newValue,oldValue,eOpts ){
											Ext.getCmp('confirmbutton').setDisabled(false);
										}
									}
								}
							],
							buttons:[
								{
									xtype : 'button',
									width : 70,
									height:24,
									disabled:true,
									id:'confirmbutton',
									text: $I18N.common.button.erpConfirmButton,
									iconCls: 'x-button-icon-save', 
							    	cls: 'x-btn-gray',
							    	style: {
							    		marginLeft: '10px'
							        },
									handler : function(btn) {
										var time = Ext.getCmp('starttime').value;
										var startdate = Ext.Date.format(time,'Y-m-d');
										if(time){
											me.LoadTaskNode(startdate);
										}
										Ext.getCmp('startTimeWin').close();
									}
								},{
									xtype : 'button',
									id : 'winCloseButton',
									width : 70,
									height:24,
									text : $I18N.common.button.erpCloseButton,
									iconCls : 'x-button-icon-close',
									cls : 'x-btn-gray',
									style : {
										marginLeft : '10px'
									},
									handler:function(btn){
										Ext.getCmp('startTimeWin').close();
									}
								}
							],
							buttonAlign:'center'
						}]
	        		});
	        		win.show();
	        	}
	       },
	       LoadTaskNode:function(startdate){
	       		var mainid = Ext.getCmp('pt_id');
	       		if(mainid){
	       			Ext.Ajax.request({
		       			url:basePath + 'plm/gantt/LoadTaskNode.action',
		       			method:'post',
		       			params:{
		       				id:mainid.value,
		       				startdate:startdate
		       			},
		       			callback:function(options,success,response){
		       				var res = Ext.decode(response.responseText);
		       				if(res.success){
		       					showMessage('提示','载入成功!');
		       					window.location.href = window.location.href;
		       				}else if(res.exceptionInfo){
		       					showError(res.exceptionInfo);
		       				}else{
		       					showError('未知错误');
		       				}
		       			}
	       			});
	       		}
 
	       }
});