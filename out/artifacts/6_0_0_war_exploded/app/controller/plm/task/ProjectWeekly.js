Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.ProjectWeekly', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','plm.task.Task','core.grid.Panel2','core.toolbar.Toolbar','plm.task.TaskForm','core.button.Upload','core.grid.YnColumn',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.ResSubmit','core.button.Save','core.button.Close','core.button.Print','core.trigger.MultiDbfindTrigger',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.form.MultiField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField','core.form.FileField'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    	   	'erpGridPanel2' : {	    	
					beforereconfigure : function(grid,store, columns, oldStore, oldColumns) {
						var code = Ext.getCmp('wr_code');
						if(code&&code.value==''&&store.data.items.length==0){
							me.getGridData(grid,store);
						}
														
						Ext.Array.each(columns,function(column){	
    	   					if(column.editor){	
    	   						if(column.dataIndex!='wrd_prjcode'){
	    	   						column.editor={
	    	   							xtype:'textarea',
	    	   							enableKeyEvents:true,
								       	grow:true,
								        growMin:26,
								        growAppend:"↵-",
								        growMax:1000,
								        floating:false,
	    	   							listeners:{	    	   								
	    	   								keyup:function(field,e,eOpts){
	    	   									var height = field.getHeight()+'px';
	    	   									var tr = document.getElementsByClassName('x-grid-row-selected')[0];
	    	   									tr.style.height = height;
	    	   								},
	    	   								keydown:function(field,e,eOpts){
	    	   									var height = field.getHeight()+'px';	    	   					
	    	   									var tr = document.getElementsByClassName('x-grid-row-selected')[0];
	    	   									tr.style.height = height;
	    	   								},
	    	   								keypress:function(field,e,eOpts){
	    	   									var height = field.getHeight()+'px';	    	   									
	    	   									var tr = document.getElementsByClassName('x-grid-row-selected')[0];
	    	   									tr.style.height = height;	    	   									
	    	   								}
	    	   							}
								 	};
								 	if(column.dataIndex=='wrd_mileplan'){	
	    	   							column.renderer = function(val, meta, record, x, y, store, view){	
		    	   							if(val){
		    	   								val = val.replace(/ 进行中\n/g,' <font color="red">进行中</font></br>')
		    	   								.replace(/ 已完成\n/g,' <font color="green">已完成</font></br>')
		    	   								.replace(/ 未启动\n/g,' <font color="gray">未启动</font></br>')
		    	   								.replace(/ \n/g,' </br>');
		   									}
		    	   							return val;
		   								}
	    	   						}else if(column.dataIndex=='wrd_finishedtask'||column.dataIndex=='wrd_summary'||
	    	   							column.dataIndex=='wrd_nextplan'||column.dataIndex=='wrd_difficulty'){	
	    	   							column.renderer = function(val, meta, record, x, y, store, view){	
		    	   							if(val){
		   										val = val.replace(/\n/g,'</br>');		
		   									}
		    	   							return val;
		   								}
	    	   						}
    	   						}else{   	   				
    	   							column.renderer = function(val, meta, record, x, y, store, view){
    	   								
    	   								if(typeof heg !='undefined'&&x<heg&&(!record.data['wrd_id']||record.data['wrd_id']==''||record.data['wrd_id']=='0')){   	   									    	   						
    	   									if(val&&val!=''){    	   					
    	   										meta.tdCls="tdcss"; 
    	   									}
    	   								}
    	   								meta.style='text-align:center!important';    	   						
    	   								return val;		    	   						
	   								}	   									   						
    	   						}
    	   					}else{
    	   						if(column.dataIndex=='wrd_mileplan'){	
    	   							column.renderer = function(val, meta, record, x, y, store, view){	
	    	   							if(val){
	    	   								val = val.replace(/ 进行中\n/g,' <font color="red">进行中</font></br>')
	    	   								.replace(/ 已完成\n/g,' <font color="green">已完成</font></br>')
	    	   								.replace(/ 未启动\n/g,' <font color="gray">未启动</font></br>')
	    	   								.replace(/ \n/g,' </br>');
	   									}
	    	   							return val;
	   								}
    	   						}else if(column.dataIndex=='wrd_finishedtask'||column.dataIndex=='wrd_summary'||
    	   							column.dataIndex=='wrd_nextplan'||column.dataIndex=='wrd_difficulty'){	
    	   							column.renderer = function(val, meta, record, x, y, store, view){	
	    	   							if(val){
	   										val = val.replace(/\n/g,'</br>');		
	   									}
	    	   							return val;
	   								}
    	   						}else{	
	    	   						column.renderer = function(val, meta, record, x, y, store, view){
	    	   							meta.style='text-align:center!important';	    	
	    	   							return val;
		   							}	    	  
	    	   					}	    	
	    	   					column.tdCls='tdcss';
    	   					}
    	   				});
    	   				   	 
    	   			},
    	   			beforeedit : function (editor, e, eOpts) {
						var el = Ext.get(editor.grid.getView().getCell(editor.row, editor.column));
						var ed = editor.column.getEditor(editor.record);						
						height = el.getHeight();	
						if(ed&&(ed.xtype=='textarea')){														
							ed.growMin=height;
						}		
						if(ed&&(ed.name=='wrd_prjcode')){
							if(typeof heg !='undefined'&&(editor.rowIdx<heg)){
								return false;
							}
						}
					},
					beforeselect: function(){
						var tds = document.getElementsByClassName('tdcss');
						Ext.Array.each(tds,function(td){
							td.style= 'background-color: #e0e0e0!important;';
						}) 	   				
					},
					edit: function(){						
						var tds = document.getElementsByClassName('tdcss');		
						Ext.Array.each(tds,function(td){
							td.style= 'background-color: #e0e0e0!important;';
						}) 	   				
					},
					itemclick:function(grid,record,item,index,e,eOpts){
						me.GridUtil.onGridItemClick(grid, record);	
						var tds = document.getElementsByClassName('tdcss');
						Ext.Array.each(tds,function(td){
							td.style= 'background-color: #e0e0e0!important;';
						})
					}
	    	   	},
    	   		'field[name=wr_week]': {
    			   afterrender : function(f) {
    				  if(f.value==''){
    					var value = '第'+Ext.Date.format(new Date(Ext.getCmp('wr_date').value),'W')+'周';
				   		f.setValue(value);
    				  }    			   		
    			   }
    		   	},
    		   	'field[name=wr_date]': {
    			   blur : function(f) {
    				  if(f.value!=''){
    					var value = '第'+Ext.Date.format(new Date(f.value),'W')+'周';
				   		Ext.getCmp('wr_week').setValue(value);
    				  }    			   		
    			   }
    		   	},
    		   	'dbfindtrigger[name=wrd_prjcode]':{   		  
    		   		focus:function(t){  
    		   			t.autoDbfind = false;
    		   			var Editor = document.getElementsByClassName('x-grid-editor');					
						var top = Editor[0].style.top
						top = parseInt(top.substring(0,top.indexOf('px')));	
						var Top = top+(height-28)/2+'px';					
						Editor[0].style.top = Top;
    		   			t.setHideTrigger(false);
  				   		t.setReadOnly(false);
    		   			var man = Ext.getCmp('wr_responsiblemancode');
    		   			if(man&&man.value!=''){
    		   				t.dbBaseCondition = "prj_assigntocode='" + man.value + "'";
    		   			}else{
    		   				showError("请先选择项目负责人!");
    		   				 t.setHideTrigger(true);
 	    					 t.setReadOnly(true);  
    		   			}
    		   		},
    		   		aftertrigger:function(t,record,dbfinds){   	   		  
    		   			var grid = t.owner;    
    		   			var store = grid.store;
    		   			if(record){
	    		   			var prjcode = record.data['prj_code'];   
	    		   			me.getGridData(grid,store,prjcode);
    		   			}
    		   		}
    		   	},   		
    		   'erpSaveButton': {
    			   	click: function(btn){
					   	if(me.checkGrid()==1){
					   		this.FormUtil.beforeSave(this); 	
					   	}else if(me.checkGrid()==-1){
					   		showError('存在相同的项目，保存失败！');
					   	}else{
					   		showError('没有项目，保存失败！');
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
				     	var status=Ext.getCmp('wr_statuscode');
    				 	if(status && status.value!='ENTERING'){
    					  	btn.hide();
    				  	}
    			   },
    			   click: function(btn){     			
    			   		if(me.checkGrid(true)!=-1){
					   		this.FormUtil.onUpdate(this);	
					   	}else{
					   		showError('存在相同的项目，更新失败！');
					   	}
    			   }
    		   },
    		   'erpDeleteButton': {
    			   click: function(btn){
    				   this.FormUtil.onDelete(Ext.getCmp('wr_id').value);
    			   }
    		   },
    		   'erpSubmitButton': {
    			   afterrender: function(btn){
    				   var status = Ext.getCmp('wr_statuscode');
    				   if(status && status.value != 'ENTERING'){
    					   btn.hide();
    				   }
    			   },
    			   click: function(btn){
/*    			   		var front = Ext.Date.format(new Date(Ext.getCmp('wr_date').value),'W');
	    			   	var now = Ext.Date.format(new Date(),'W');
	                    if (front != now) {
	                        showError('录入日期必须是本周！');
	                        return;
	                    }*/
	                    if(me.checkGrid()==1){
	                    	 me.FormUtil.onSubmit(Ext.getCmp('wr_id').value);
	                    }else if(me.checkGrid()==0){
	                    	showError('没有项目，提交失败！');
	                    }else{
					   		showError('存在相同的项目，提交失败！');
					   	}    				  
    			   }
    		   },
    		   'erpResSubmitButton':{
    			  afterrender:function (btn){
    				  var status=Ext.getCmp('wr_statuscode');
    				  if(status && status.value != 'COMMITED'){
    					  btn.hide();
    				  }
    			  },
    			  click:function(btn){
    				  me.FormUtil.onResSubmit(Ext.getCmp('wr_id').value);
    			  }
    		   },
    		   'erpAuditButton': {
    			   afterrender: function(btn){
    				   var status = Ext.getCmp('wr_statuscode');
    				   if(status && status.value != 'COMMITED'){
    					   btn.hide();
    				   }
    			   },
    			   click: function(btn){
    				   me.FormUtil.onAudit(Ext.getCmp('wr_id').value);
    			   }
    		   },
    		   'erpResAuditButton': {
	                afterrender: function(btn) {
	                    var status = Ext.getCmp('wr_statuscode');
	                    if (status && status.value != 'AUDITED') {
	                        btn.hide();
	                    }
	                },
	                click: function(btn) {
	                    me.FormUtil.onResAudit(Ext.getCmp('wr_id').value);
	                }
            	},
    		   'erpAddButton': {
    			   click: function(){
    				me.FormUtil.onAdd('addWeekly', '项目周报', 'jsps/plm/task/ProjectWeekly.jsp');
    			   }
    		   }	    		  
    	   });
	    },   
		getGridData:function(grid,store,prjcode){
			var me =this;
			var man = Ext.getCmp('wr_responsiblemancode');
			var manCode ='';
			if(man&&man.value!=''){
				manCode = man.getValue();
			}
			Ext.Ajax.request({
				url : basePath + 'plm/task/autoGetGridData.action',
				params: {man:manCode,prjcode:prjcode},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						if(res.data.length>0){
							if(prjcode&&prjcode!=''){
								var select = grid.getSelectionModel().getSelection()[0];
								select.set('wrd_mileplan',res.data[0]['wrd_mileplan']);
								select.set('wrd_finishedtask',res.data[0]['wrd_finishedtask']);	
								if(grid.editingPlugin.activeEditor != null){  
    								grid.editingPlugin.activeEditor.completeEdit();      				
								} 								
							}else{
								heg = res.data.length;
								store.loadData(res.data);							
							}
						}
					} else if(res.exceptionInfo){					
						showError(res.exceptionInfo);
					} 
				}
			});
		},
		checkGrid:function(update){
			var grid = Ext.getCmp('grid');
			var s=grid.store.data.items;
			var result = 0;
			for(var i=0;i<s.length;i++){
				if(update){
					if(s[i].dirty){
						if(!s[i].data['wrd_prjcode']||s[i].data['wrd_prjcode']==''){
	    					s[i].dirty=false;	    					
						}
					}
				}else{
					if(s[i].data['wrd_prjcode']&&s[i].data['wrd_prjcode']!=''){
	    				s[i].dirty=true;
	    				result = 1;
					}else{
						s[i].dirty=false;
					}
				}				
    		}
			for(var i=0;i<s.length-1;i++){
				if(update){
					if(!s[i].data['wrd_prjcode']||s[i].data['wrd_prjcode']=='')
						continue;
					for(var j=i+1;j<s.length;j++){
						if(!s[i].data['wrd_prjcode']||s[i].data['wrd_prjcode']=='')
							continue;		
						if(s[i].data['wrd_prjcode']==s[j].data['wrd_prjcode']){			
							result = -1;
						}
					}
				}else{
					if(!s[i].dirty)
						continue;
					for(var j=i+1;j<s.length;j++){
						if(!s[j].dirty)
							continue;		
						if(s[i].data['wrd_prjcode']==s[j].data['wrd_prjcode']){			
							result = -1;
						}
					}
				}
			}
			return result;
		}
});