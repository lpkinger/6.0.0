Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.gantt', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	stores: ['plm.Assignment','plm.Dependency','plm.Resource','plm.Task'],
	models: ['plm.Dependency','plm.Resource','plm.Task'],
	views : [ 'core.form.Panel', 'plm.task.gantt','plm.task.GanttPanel','plm.task.GanttToolBar','plm.task.ProjectImportPanel','core.trigger.DbfindTrigger'],
	init : function() {
		var me = this;		
		this.control({
			'button[id=save]':{
				click:function(){
					var g=Ext.getCmp('gantt_panel');
  				        g.sync(null,function(){
  				        	if(!hideToolBar){
				        		Ext.Msg.alert('保存成功!')
								window.location.reload();
							}	
  				        });
				},
				afterrender:function(btn){
					var gantt=Ext.getCmp("gantt_panel");
					var statuscode=gantt.prjData.prj_statuscode;
					if(statuscode=='FINISHED'){
						btn.disable();
					}
				}
			},
			'gantt_toolbar':{
				afterrender:function(){
					if(hideToolBar){
						var a = Ext.getCmp('gantt_toolbar');
						a.hide();
					}
				}
			}
		});
	},
	sync:function(){
		var g=this.getGantt();
		this.syncTasks(g.taskStore,g);
		this.syncDependencys(g.dependencyStore);
		this.syncAssigns(g.assignmentStore);
	},
	syncTasks:function(tasks,g){
		var params={},task_add  = tasks.getNewRecords(),task_update= tasks.getUpdatedRecords(),task_remove = tasks.getRemovedRecords(),jsonData;
		if(task_add.length>0){
			jsonData=new Array();
			for(var i=0;i<task_add.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_add[i].data)));
			}
			params.create=unescape(jsonData.toString());
		}
		if(task_update.length>0){
			jsonData=new Array();
			for(var i=0;i<task_update.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_update[i].data)));
			}
			params.update=unescape(jsonData.toString());
		}
		if(task_remove.length>0){
			jsonData=new Array();
			for(var i=0;i<task_remove.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_remove[i].data)));
			}
			params.remove=unescape(jsonData.toString());
		}
		/*if(!this.isChanged(params)){
			alert('未做任何修改!');
			return false;
		}*/
		params.prjId=prjplanid;
		Ext.Ajax.request({
			url : basePath + "plm/gantt/syncTask.action",
			params:params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//window.location.reload();
					//alert('保存成功!');					
				}
			}
		});
	},
	syncDependencys:function(dependency){
		var params={},dependency_add=dependency.getNewRecords(),dependency_remove=dependency.getRemovedRecords(),dependency_update=dependency.getUpdatedRecords(),me=this;
		if(dependency_add.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_add,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.create=unescape(jsonData.toString());
		}
		if(dependency_remove.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_remove,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.remove=unescape(jsonData.toString());
		}
		if(dependency_update.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_update,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.update=unescape(jsonData.toString());
		}
		params.prjId=prjplanid;
		Ext.Ajax.request({
			url : basePath + "plm/gantt/syncDependency.action",
			params:params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//window.location.reload();
					//alert('保存成功!');					
				}
			}
		});
	},
	syncAssigns:function(assigns){
		var params={},assign_add=assigns.getNewRecords(),
		assign_update=assigns.getUpdatedRecords(),
		assign_remove=assigns.getRemovedRecords(),nodeName,t,me=this;
		console.log(assign_add);
		if(assign_add.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_add,function(record){
				t=record.get('TaskId');
				if(!record.get('TaskId') || (!Ext.isNumber(t) && t.indexOf('ext-')>-1)){
					nodeName=record.store.taskStore.getById(record.get('TaskId')).getName();
					alert('任务节点:'+nodeName+'，请先保存再分配资源!');
					return false;
				}
				jsonData.push(Ext.JSON.encode(me.format_assign(record)));
			});
			params.create=unescape(jsonData.toString());
		}
		if(assign_remove.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_remove,function(record){
				jsonData.push(Ext.JSON.encode(record.data));
			});
			params.remove=unescape(jsonData.toString());
		}
		if(assign_update.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_update,function(record){
				jsonData.push(Ext.JSON.encode(me.format_assign(record)));
			});
			params.update=unescape(jsonData.toString());
		}
		params.prjId=prjplanid;
		Ext.Ajax.request({
			url : basePath + "plm/gantt/syncAssigns.action",
			params:params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					//window.location.reload();
					//alert('保存成功!');					
				}
			}
		});
	},
	getGantt:function(){
		return Ext.getCmp('gantt_panel');	
	},
	format_task:function(data){
		delete data['index'];
		data.StartDate=Ext.Date.format(data.StartDate, 'Y-m-d H:i:s');
		data.EndDate=Ext.Date.format(data.EndDate, 'Y-m-d H:i:s');
		data.BaselineStartDate=Ext.Date.format(data.BaselineStartDate, 'Y-m-d H:i:s');
		data.BaselineEndDate =Ext.Date.format(data.BaselineEndDate, 'Y-m-d H:i:s');
		data.parentId=data.parentId==null?0:data.parentId;
		data.prjplanid=prjplanid;
		delete data['id'];
		delete data['checked'];
		delete data['expandable'];
		delete data['expanded'];
		delete data['children'];
		delete data['PhantomId'];
		delete data['PhantomParentId'];
		delete data['hrefTarget'];
		delete data['qtip'];
		delete data['qtitle'];
		delete data['allowDrop'];
		delete data['isFirst'];
		delete data['loaded'];
		delete data['depth'];
		delete data['allowDrag'];
		delete data['loading'];
		delete data['cls'];
		delete data['iconCls'];
		delete data['icon'];
		delete data['root'];
		delete data['type'];
		delete data['isLast'];
		delete data['qshowDelay'];
		delete data['TaskType'];
		delete data['fakeId'];
		delete data['Draggable'];
		delete data['Resizable'];
		delete data['leaf'];
		data['Description']=data['Note'];
		delete data['Note'];
		return data;
	},
	format_dependency:function(record){
		var o={},data=record.data;
		Ext.Array.each(record.fields.items,function(item){
			if(item.mapping) o[item.mapping]=data[item.name];
		});
		o.DE_PRJID=prjplanid;
		return o;
	},
	format_assign:function(record){
		var rStore=record.store.resourceStore,r=rStore.findRecord("Id",record.data['ResourceId']);
		var o=record.data;
		o.EmName=r.data['Name'];
		o.EmCode=r.data['EmCode'];
		return o;
		
	},
	checkTask:function(){
		
		
	},
	isChanged:function(param){
		var bool=false;
		for(var i in param){
			bool=true;
			break;
		}
		return bool;
	}
});