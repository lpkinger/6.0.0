Ext.define('erp.view.plm.task.GanttPanel',{ 
	extend:'Gnt.panel.Gantt',
	alias: 'widget.ganttpanel',
	id:'gantt_panel',
	region:'center',
	created:null,
	showTodayLine: true,
    enableBaseline : true,
    baselineVisible : false,
    enableDependencyDragDrop : false,
	ganttConf:true,
	columnLines: true,
	requires : ["erp.view.plm.task.plugin.TaskContextMenu","erp.view.plm.task.plugin.DocFileField","erp.view.plm.task.plugin.PhaseField"],
	selModel:new Ext.selection.TreeModel({ 
		ignoreRightMouseSelection : false,
		mode : 'MULTI'
	}),
	showScheduler:true,
	loadMask: true,
	viewPreset: 'weekAndDayLetter',
	lockedViewConfig: {
		plugins: {
			ptype: 'treeviewdragdrop'
		}
	},
	plugins : [Ext.create('Sch.plugin.TreeCellEditing', {
		clicksToEdit: 2,
		onBeforeCellEdit: function(c, a) {
			if(a.record.get('PercentDone')==100){
				return false;
			}
			var b = a.column;
			var d = b.field;
			if(b && b.dataIndex=='prjdocname' && (a.record.id && a.record.id.indexOf('ext-')>-1)){
				return false;
			}
			if(b && b.xtype=='resourceassignmentcolumn' && !a.record.data.leaf){
				return false;
			}
			if (d) {
				if (d.setTask) {
					d.setTask(a.record);
					a.value = a.originalValue = d.getValue();
				} else {
					if (!b.dataIndex && a.value === undefined) {
						a.value = d.getDisplayValue(a.record);
					}
				}
			}
		}
	}),Ext.create('erp.view.plm.task.plugin.TaskContextMenu'),
	{ ptype: 'bufferedrenderer' },
	this.taskEditor = Ext.create('Gnt.plugin.TaskEditor'),new Gnt.plugin.Printable({
        printRenderer : function(task, tplData) {
            if (task.isMilestone()) {
                return;
            } else if (task.isLeaf()) {
                var availableWidth = tplData.width - 4,
                    progressWidth = Math.floor(availableWidth*task.get('PercentDone')/100);
            
                return {
                    // Style borders to act as background/progressbar
                    progressBarStyle : Ext.String.format('width:{2}px;border-left:{0}px solid #7971E2;border-right:{1}px solid #E5ECF5;', progressWidth, availableWidth - progressWidth, availableWidth)
                };
            } else {
                var availableWidth = tplData.width - 2,
                    progressWidth = Math.floor(availableWidth*task.get('PercentDone')/100);
            
                return {
                    // Style borders to act as background/progressbar
                    progressBarStyle : Ext.String.format('width:{2}px;border-left:{0}px solid #FFF3A5;border-right:{1}px solid #FFBC00;', progressWidth, availableWidth - progressWidth, availableWidth)
                };
            }
        },

        beforePrint : function(sched) {
            var v = sched.getSchedulingView();
            this.oldRenderer = v.eventRenderer;
            this.oldMilestoneTemplate = v.milestoneTemplate;
           // v.milestoneTemplate = printableMilestoneTpl;
            v.eventRenderer = this.printRenderer;
        },

        afterPrint : function(sched) {
            var v = sched.getSchedulingView();
            v.eventRenderer = this.oldRenderer;
            v.milestoneTemplate = this.oldMilestoneTemplate;
        }
    })],
	initComponent : function(){
		var me = this;
		//Live参数 控制是否为实时甘特图
		if (typeof(Live) != 'undefined' && Live == 'true') {
			me.baselineVisible = true;
		} 
		var data=this.getProject();
		if(data==null){
			 data = new Object();
			 data.prj_start = Ext.Date.format(new Date(),'Y-m-d H:i:s');
		     data.prj_end = Ext.Date.format(Ext.Date.add(new Date(),Ext.Date.DAY,30),'Y-m-d H:i:s');
		     data.latestend = data.prj_end;
		}else if(data.prj_start==null){
			 data.prj_start = Ext.Date.format(new Date(),'Y-m-d H:i:s'); data.latestend = data.prj_end;
		}else if(data.prj_end==null){
			data.prj_end = Ext.Date.format(Ext.Date.add(new Date(),Ext.Date.DAY,30),'Y-m-d H:i:s');
		}else if(data.latestend==null){
			data.latestend = data.prj_end;
		}
		var start=Ext.Date.add(Ext.Date.parse(data.prj_start,'Y-m-d H:i:s'),Ext.Date.DAY,-5),end = Ext.Date.parse(data.prj_end,'Y-m-d H:i:s'),latestend=data.latestend;
		this.prjData = data;
		var taskStore=Ext.data.StoreManager.lookup('plm.Task');	
		taskStore.on('load',function(){
			Ext.defer(function(s){				
				var v=me.getSchedulingView();
				if(!v.readOnly && taskStore.nodeStore.data.length==0){
					taskStore.loadData([{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true},{leaf:true}]);
				}
			},200,me,[taskStore]);			
		},taskStore);
		if(latestend!=null){
			end=this.setLatestEnd(end, latestend);			
		}	
		Ext.apply(this,{
			lockedGridConfig : {
				title : '任务表',
				collapsible : this.showScheduler,
				autoScroll:true,
				ignore:true,
				dbfinds:[{
					field:'preconditionname',
					dbGridField:"tt_name",
					trigger:null,
				},{
					field:'preconditioncode',
					dbGridField:"tt_code",
					trigger:null,
				}],
				viewConfig: {
					markDirty: true
				}
			},
			schedulerConfig : {
				collapsible : true,
				collapsed:!this.showScheduler,
				ignore:true,
				title : '甘特图'
			},
			leftLabelField  : 'Name',			
			resourceStore :   Ext.data.StoreManager.lookup('plm.Resource'),
			assignmentStore : Ext.create('erp.store.plm.Assignment',{
				resourceStore : Ext.data.StoreManager.lookup('plm.Resource'),
				autoLoad    : true
			}),
			taskStore :  taskStore,
			dependencyStore  : 'plm.Dependency',
			startDate       : start,
			endDate         : end,
			title:this.ganttConf?data.prj_name||'':'',
			readOnly:getUrlParam('readOnly')==1||this.prjData.prj_statuscode=='FINISHED',
			columns : [{
				header:'序号',
				dataIndex:'fakeId',
				width:50
			},{
				header:'状态',
				width:60,
				dataIndex:'handstatus',
				renderer:function(val,meta,r){
					if(!r.data.leaf && r.get('PercentDone')==100)  return "已完成";
					else  return val;					
				}
			},{ 
				xtype:'wbscolumn',
				header:'编号',
				width:50
			},{
				xtype : 'treecolumn',
				header: '任务',
				sortable: true,
				dataIndex: 'Name',
				width: 200,
				editor:{
					xtype:'textfield',
					maxLength:1,
					maxLengthText:'error'
				},
				field: {
					allowBlank: false
				},
				renderer : function(v, meta, r) {
					if (!r.data.leaf) meta.tdCls = 'sch-gantt-parent-cell';
					if(r.get('phasename')){
						return '<span style="color:red">' + v + '</span>';
					}
					return v;
				}
			},{
				header:'前置任务',
				width:70,
				xtype:'gridcolumn',
				renderer:function(v,meta,r,rowIndex,columIndex,store){
					var stores=r.getIncomingDependencies();
					var arr=new Array(),show=new Array();
					if(stores.length>0){
						Ext.each(stores,function(s,index){
							var from=s.data.From;
							arr.push(from);
						});
						Ext.each(arr,function(id){
							var r = store.getById(parseInt(id));
							if(r){
							var detno=r.get('fakeId');
							show.push(detno);}
						});
					}
					if(show.length>0)
						return show.join();
					else
						return '';
				},
			},{
				header : '资源分配', 
				width:120, 
				xtype : 'resourceassignmentcolumn'
			},{
				header:'工期',
				xtype:'durationcolumn'
			},new Gnt.column.StartDate({
				header: '开始时间'
			}),
			new Gnt.column.EndDate({
				header: '结束时间'
			}),new Gnt.column.BaselineStartDate({
				header: '基线开始时间',
				hidden:true
			}),
			new Gnt.column.BaselineEndDate({
				header: '基线结束时间',
				hidden:true
			}),{
				xtype:'datecolumn',
				dataIndex:'realstartdate',
				header:'实际开始时间',
				width:120
			},{
				xtype:'datecolumn',
				dataIndex:'realenddate',
				header:'实际结束时间',
				width:120
			},{
				header:'后置任务',
				xtype:'successorcolumn',
				hidden:true
			},{
				header:'完成率',
				xtype : 'percentdonecolumn',
				width : 50
			},{
				text : '文件信息',
				width:200,
				dataIndex : 'prjdocname',
				renderer:function(val,meta,record){
					if(val &&  record.get('prjdocstatus')){
						var d=record.get('prjdocstatus'),statusarr=d.split(','),namearr=val.split(','),c="";
						if(namearr.length==statusarr.length){
						  for(var i=0;i<namearr.length;i++){
							  c+=statusarr[i]!=0?'<span style="color:red">'+namearr[i]+'</span>':namearr[i];
							  if(i<namearr.length-1) c+=',';
						  }
						}
						return c;
					}else return val;
				},
				editor:{
					xtype:'prjdocfield',
					editable:false,
					title:'文件列表'
				}
			},{
				header: '优先级',
				width: 50,
				hidden:true,
				dataIndex: 'Priority'
			},{
				text : '项目阶段',
				width:150,
				dataIndex : 'phasename',
				editor:{
					xtype:'prjphasefield',
					editable:false,
					title:'项目阶段列表'
				}
			},{
				xtype: 'booleancolumn',
				width: 70,
				header: '计划模式',
				dataIndex: 'ManuallyScheduled',
				field: {
					xtype: 'combo',
					displayField:'display',
					valueField:'value',
					store: Ext.create('Ext.data.Store', {
						fields: ['display', 'value'],
						data:[{
							display:'手动',
							value:'true'
						},{
							display:'自动',
							value:'false'
						}]
					}) 
				},
				renderer:function(val,meta,record){
					if(val) return '手动';
					else return '自动';
				}
			},
			{
				header:'任务类型',
				width:70,
				dataIndex:'tasktype',
				renderer:function(val,meta,record){
					if(val=='normal'){
						return '一般任务';
					}else if(val=='test'){
						return '测试任务';
					}
					return val;
				},
				editor:{
					xtype:"combo",
					store: {
					    fields: ['display','value'],
					    data : [					    	
					        {display:'一般任务', value:'normal'},
					        {display:'测试任务', value:'test'}
						]
					},
				    queryMode: 'local', 
				    displayField: 'display',
				    valueField: 'value'
				}
			},{
				header:'检测条件',
				dataIndex:'preconditionname',
				width:120,
				editor:{
					xtype:'dbfindtrigger',
					name:"preconditionname",
					editable:false,
					dbBaseCondition:"tt_checked=1",
					dbfind:'ProjectTask|tt_name',
				}
			}]     
		});
		this.callParent(arguments); 
	},
	sync:function(params,fn,auto){
		var g=this.getGantt(),params=params||{};
		if(!this.getSyncParams(g,params) && !auto){
			if(hideToolBar){
				return false;
			}
			Ext.Msg.alert('提示','未作任何修改，无需保存！');
			return false;
		}	
		var detnos=this.getDetnos(g, detnos);
		params.detnos=detnos.toString();
		params.prjId=prjplanid;
		Ext.Ajax.request({
			url : basePath + "plm/gantt/sync.action",
			params:params,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					if(fn) fn.call();			
				}else if(localJson.exceptionInfo){
					Ext.Msg.alert('提示',localJson.exceptionInfo);
				}
			}
		});
	},
	getSyncParams:function(g,params){
		this.syncTasks(g.taskStore,g,params);
		this.syncDependencys(g.dependencyStore,params);
		this.syncAssigns(g.assignmentStore,params);
		if(!this.isChanged(params)){
			return false;
		}else return true;
	},
	syncTasks:function(tasks,g,params){
		var task_add  = tasks.getNewRecords(),task_update= tasks.getUpdatedRecords(),task_remove = tasks.getRemovedRecords(),jsonData;
		if(task_add.length>0){
			jsonData=new Array();
			for(var i=0;i<task_add.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_add[i].data)));
			}
			params.Taskcreate=unescape(jsonData.toString());
		}
		if(task_update.length>0){
			jsonData=new Array();
			for(var i=0;i<task_update.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_update[i].data)));
			}
			params.Taskupdate=unescape(jsonData.toString());
		}
		if(task_remove.length>0){
			jsonData=new Array();
			for(var i=0;i<task_remove.length;i++){
				jsonData.push(Ext.JSON.encode(this.format_task(task_remove[i].data)));
			}
			params.Taskremove=unescape(jsonData.toString());
		}

	},
	syncDependencys:function(dependency,params){
		var dependency_add=dependency.getNewRecords(),dependency_remove=dependency.getRemovedRecords(),dependency_update=dependency.getUpdatedRecords(),me=this;
		if(dependency_add.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_add,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.Dependencycreate=unescape(jsonData.toString());
		}
		if(dependency_remove.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_remove,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.Dependencyremove=unescape(jsonData.toString());
		}
		if(dependency_update.length>0){
			jsonData=new Array();
			Ext.Array.each(dependency_update,function(record){
				jsonData.push(Ext.JSON.encode(me.format_dependency(record)));
			});
			params.Dependencyupdate=unescape(jsonData.toString());
		}

	},
	syncAssigns:function(assigns,params){
		var assign_add=assigns.getNewRecords(),
		assign_update=assigns.getUpdatedRecords(),
		assign_remove=assigns.getRemovedRecords(),nodeName,t,me=this;
		if(assign_add.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_add,function(record){
				/*t=record.get('TaskId');
				if(!record.get('TaskId') || (!Ext.isNumber(t) && t.indexOf('ext-')>-1)){
					nodeName=record.store.taskStore.getById(record.get('TaskId')).getName();
					alert('任务节点:'+nodeName+'，请先保存再分配资源!');
					return false;
				}*/
				jsonData.push(Ext.JSON.encode(me.format_assign(record)));
			});
			params.Assigncreate=unescape(jsonData.toString());
		}
		if(assign_remove.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_remove,function(record){
				jsonData.push(Ext.JSON.encode(record.data));
			});
			params.Assignremove=unescape(jsonData.toString());
		}
		if(assign_update.length>0){
			jsonData=new Array();
			Ext.Array.each(assign_update,function(record){
				jsonData.push(Ext.JSON.encode(me.format_assign(record)));
			});
			params.Assignupdate=unescape(jsonData.toString());
		}
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
		delete data['Draggable'];
		delete data['Resizable'];
		delete data['leaf'];
		delete data['recorder'];
		delete data['recorderid'];
		delete data['prjdocname'];
		delete data['prjdocid'];
		delete data['prjdocstatus'];
		data['Description']=data['Note'];
		data['detno']=data['fakeId'];
		delete data['Note'];
		delete data['fakeId'];
		delete data['realstartdate'];
		delete data['realenddate'];
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
		var rStore=record.store.resourceStore,r=rStore.findRecord("Id",record.data['ResourceId']),e=record.getTask(),o=record.data;
		o.EmName=r.get('Name');
		o.EmCode=r.get('EmCode');
		if(e&&!e.getId()) {
			o.TaskDetno=e.data.detno;
			delete o['TaskId'];
		}
		delete o['ResourceRole'];
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
	},
	getProject:function(){
		var d;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {	
				caller: 'Project,(select max(EndDate) latestend from ProjectTask where prjplanid='+(prjplanid==null?"":prjplanid)+')',
				fields: 'prj_start,prj_end,prj_name,latestend,prj_code,prj_statuscode,prj_auditstatuscode,prj_preauditcode',//guq 添加prj_statuscode  2017-08-03  反馈编号:2017070558
				condition:'prj_id='+(prjplanid==null?"":prjplanid)
			},
			method : 'post',
			callback : function(opt, s, res){
				var r=new Ext.decode(res.responseText);
				d=r.data;
			}
		});
		return d;
	},
	getDetnos:function(g){
		var taskStore=g.taskStore,taskId,index=-1,arr=new Array();
		function setDetno(rec) {
			taskId=rec.get('Id');
			index++;
			if(rec.get('Id') && rec.get('Id')!='root') arr.push(Ext.JSON.encode({id:taskId ,detno:index}));
			Ext.each(rec.childNodes, setDetno);
		}
		setDetno(taskStore.getRootNode());
		return arr;
	},
	setLatestEnd:function(end,taskend){
	  if(taskend!=null){
		  taskend=Ext.Date.parse(taskend,'Y-m-d H:i:s');
		  if(taskend-end>172800000){	//任务最大结束日期减去项目计划结束日期大于两天(172800000ms)		  
			 end=taskend;
			 Ext.defer(function(){Ext.MessageBox.alert('提示','任务节点的最迟完成日期大于项目计划完成日期！')},300);
		  } 
	  }
	  return Ext.Date.add(end,Ext.Date.DAY,5);
	}
});