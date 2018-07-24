Ext.define("erp.view.plm.task.plugin.TaskContextMenu", {
	extend : "Ext.menu.Menu",
	mixins : ["Ext.AbstractPlugin"],
	lockableScope : "top",
	requires : ["Gnt.model.Dependency"],
	plain : true,
	triggerEvent : "taskcontextmenu",
	texts : {
		newTaskText : "新建任务",
		newMilestoneText : "新建里程碑",
		deleteTask : "删除任务",
		scanDetails:'任务详情',
		editLeftLabel : "编辑左标注",
		editRightLabel : "编辑右标注",
		add : "添加",
		deleteDependency : "删除依赖关系",
		addTaskAbove : "向上添加任务",
		addTaskBelow : "向下添加任务",
		addMilestone : "添加里程碑",
		addSubtask : "添加子任务",
		addSuccessor : "添加继承者",
		addPredecessor : "添加前任者",
		activeTask:"激活任务",
		viewTask:"查看任务进展",
		endTask:"结束任务",
	},
	grid : null,
	rec : null,
	lastHighlightedItem : null,
	createMenuItems : function() {
		var a = this.texts;
		return [{
			iconCls:'x-menu-scan',
			handler:this.scanDetails,
			requiresTask : true,
			scope : this,
			text : a.scanDetails,
		},
		{
			iconCls:'x-menu-edit',
			handler : this.activeTask,
			requiresTask : true,
			scope : this,
			text : a.activeTask
		},
		 {
			iconCls:'x-menu-edit',
			handler : this.editLeftLabel,
			requiresTask : true,
			scope : this,
			text : a.editLeftLabel
		},/* {
			iconCls:'x-menu-edit',
			handler : this.editRightLabel,
			requiresTask : true,
			scope : this,
			text : a.editRightLabel
		}, */{
			iconCls:'x-menu-add',
			text : a.add,
			requiresTask : true,
			menu : {
				plain : true,
				items : [{
					iconCls:'x-menu-add-up',
					handler : this.addTaskAboveAction,
					requiresTask : true,
					scope : this,
					text : a.addTaskAbove
				}, {
					iconCls:'x-menu-add-down',
					handler : this.addTaskBelowAction,
					scope : this,
					text : a.addTaskBelow
				}, {
					iconCls:'x-menu-menu-add',
					handler : this.addMilestone,
					requiresTask : true,
					scope : this,
					text : a.addMilestone
				}, {
					iconCls:'x-menu-menu-add',
					handler : this.addSubtask,
					requiresTask : true,
					scope : this,
					text : a.addSubtask
				}, {
					iconCls:'x-menu-menu-add',
					handler : this.addSuccessor,
					requiresTask : true,
					scope : this,
					text : a.addSuccessor
				}, {
					iconCls:'x-menu-menu-add',
					handler : this.addPredecessor,
					requiresTask : true,
					scope : this,
					text : a.addPredecessor
				}]
			}
		}
		,{//guq 结束任务 2017-09-12  反馈编号:2017070558
			iconCls:'x-menu-delete-dependency',
			handler : this.endTask,
			requiresTask : true,
			scope : this,
			disabled :true,
			text : a.endTask
		}, {
			iconCls:'x-menu-delete',
			handler : this.deleteTask,
			requiresTask : true,
			scope : this,
			disabled :true,
			text : a.deleteTask
		}, {
			iconCls:'x-menu-delete-dependency',
			text : a.deleteDependency,
			requiresTask : true,
			menu : {
				plain : true,
				listeners : {
					beforeshow : this.populateDependencyMenu,
					mouseover : this.onDependencyMouseOver,
					mouseleave : this.onDependencyMouseOut,
					scope : this
				}
			}
		},{
			iconCls:'x-menu-view-task',
			handler: this.viewTask,
			requiresTask: true,
			scope : this,
			disabled :true,
			text: a.viewTask
		}];
	},
	buildMenuItems: function() {
		this.items = this.createMenuItems()
	},
	initComponent: function() {
		this.defaults = this.defaults || {};
		this.defaults.scope = this;
		this.buildMenuItems();
		this.callParent(arguments)
	},
	init : function(b) {
		b.on("destroy", this.cleanUp, this);
		var a = b.getSchedulingView(), c = b.lockedGrid.getView();
		if (this.triggerEvent === "itemcontextmenu") {
			c.on("itemcontextmenu", this.onItemContextMenu, this);
			a.on("itemcontextmenu", this.onItemContextMenu, this)
		}
		a.on("taskcontextmenu", this.onTaskContextMenu, this);
		a.on("containercontextmenu", this.onContainerContextMenu, this);
		c.on("itemcontextmenu", this.onTaskContextMenu, this);
		c.on("containercontextmenu", this.onContainerContextMenu, this);
		this.grid = b
	},
	populateDependencyMenu: function(f) {
		var d = this.grid,
		b = d.getTaskStore(),
		e = this.rec.getAllDependencies(),
		a = d.dependencyStore;
		f.removeAll();
		if (e.length === 0) {
			return false
		}
		var c = this.rec.getId() || this.rec.internalId;
		Ext.each(e,
				function(i) {
			var h = i.getSourceId(),
			g = b.getById(h == c ? i.getTargetId() : h);
			if (g) {
				f.add({
					depId: i.internalId,
					text: Ext.util.Format.ellipsis(Ext.String.htmlEncode(g.getName()), 30),
					scope: this,
					handler: function(k) {
						var j;
						a.each(function(l) {
							if (l.internalId == k.depId) {
								j = l;
								return false
							}
						});
						a.remove(j)
					}
				})
			}
		},
		this)
	},
	onDependencyMouseOver: function(d, a, b) {
		if (a) {
			var c = this.grid.getSchedulingView();
			if (this.lastHighlightedItem) {
				c.unhighlightDependency(this.lastHighlightedItem.depId)
			}
			this.lastHighlightedItem = a;
			c.highlightDependency(a.depId)
		}
	},
	onDependencyMouseOut: function(b, a) {
		if (this.lastHighlightedItem) {
			this.grid.getSchedulingView().unhighlightDependency(this.lastHighlightedItem.depId)
		}
	},
	cleanUp: function() {
		this.destroy()
	},
	onTaskContextMenu : function(b, a, c,i,e) {
		this.activateMenu(a, e||c)
	},
	onItemContextMenu: function(b, a, d, c, f) {
		this.activateMenu(a, f)
	},
	onContainerContextMenu: function(a, b) {
		this.activateMenu(null, b)
	},
	activateMenu: function(b, a) {
		if (this.grid.isReadOnly()) {
			return
		}
		a.stopEvent();
		this.rec = b;
		this.configureMenuItems();
		this.showAt(a.getXY());
		
		//判断如果是当前record没有资源或未激活，则disable
	},
	configureMenuItems: function() {
		var b = this.query("[requiresTask]");
		var c = this.rec;
		Ext.each(b,function(e,index) {
			var grid = this.ownerCt.grid;
			//guq 当任务完结时 删除依赖关系和添加按钮为灰色不可点击  2017-08-03  反馈编号:2017070558
			var prjEnd = false;
			if(grid){
				if(grid.prjData){
					if(grid.prjData.prj_statuscode=='FINISHED'){
						prjEnd = true;
					}
				}
			}
			var taskid = c.data.Id;
			//zhouy 未激活 不能删除
			switch (e.text) {
			case '任务详情':
				e.setDisabled(!c.getId());
				break;
			case '激活任务':
				e.setDisabled(prjEnd||c.get('handstatus')!='未激活');
				break;
			case '结束任务':
				e.setDisabled(!c.get('leaf')||prjEnd||c.get('handstatus')!='已启动');
				break;
			case '删除任务':
				//luhg 当任务是某个任务的前置任务时不可删除  2017-06-26  反馈编号:2017060451
				var dependTask = false;
				Ext.Array.each(grid.store.dependencyStore.data.items,function(item){
					if(taskid==item.data.From){
						dependTask = true;
						return false;
					}	
				});
				e.setDisabled(prjEnd||dependTask||(c.get('handstatus')!='未激活' && c.get('handstatus')!="") || !c.data.leaf);
				break;	
			case '添加子任务':
				e.setDisabled(!c.getId() || c.get('handstatus')!='未激活');
				break;
			case '添加继承者':
				e.setDisabled(!c.getId() || c.get('handstatus')=='已完成');
				break;	
			case '添加前任者':
				e.setDisabled(!c.getId() ||  c.get('handstatus')!='未激活');
				break;
			case '查看任务进展':
				var resourceExist = false;
				Ext.Array.each(grid.store.assignmentStore.data.items,function(item,index){
					if(taskid==item.data.TaskId){
						resourceExist = true;
					}
				});
				if(resourceExist&&(c.get('handstatus')=='已启动'||c.get('handstatus')=='已完成')){   
					e.setDisabled(false);
				}else{
					e.setDisabled(true);
				}
				break;
			case'删除依赖关系':
				e.setDisabled(prjEnd);
				break;
			case'添加':
				e.setDisabled(prjEnd);
				break;
			default:
				break;
			}
		});
		var a = this.query("[isDependenciesMenu]")[0];
		if (c && a) {
			a.setDisabled(!c.getAllDependencies().length)
		}
		var d = this.down("#toggleMilestone");
		if (c && d) {
			d.setText(c.isMilestone() ? this.L("convertToRegular") : this.L("convertToMilestone"))
		}
	},
	copyTask: function(c) {
		var b = this.grid.getTaskStore().model;
		var a = new b({
			leaf: true
		});
		a.setPercentDone(0);
		a.setName('新任务');
		a.set(a.startDateField, (c && c.getStartDate()) || null);
		a.set(a.endDateField, (c && c.getEndDate()) || null);
		a.set(a.durationField, (c && c.getDuration()) || null);
		a.set(a.durationUnitField, (c && c.getDurationUnit()) || "d");
		return a;
	},
	addTaskAbove: function(a) {
		var b = this.rec;
		if (b) {
			b.addTaskAbove(a);		
		} else {
			this.grid.taskStore.getRootNode().appendChild(a)
		}
	},
	addTaskBelow: function(a) {
		var b = this.rec;
		if (b) {
			b.addTaskBelow(a);
		} else {
			this.grid.taskStore.getRootNode().appendChild(a)
		}
	},
	scanDetails:function(a,b){
		this.grid.fireEvent('taskdblclick',this.grid,this.grid.getSelectionModel().selected.items[0]);
	},
	activeTask:function(){
		var a = this.grid.getSelectionModel().selected.items[0];
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'plm/gantt/activeTask.action',
			params: {
				data:Ext.JSON.encode(a.data),
				prjId:prjplanid
			},
			method : 'post',
			callback : function(options, success, response){
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.success){
					a.set('handstatus','已启动');
					a.commit();
				}else{
					alert(res.exceptionInfo);
				}
			}		
	    });
	},
	endTask:function(){
		var grid=this.grid;
		var rec=grid.getSelectionModel().selected.items[0];
		var taskid=rec.data.Id;
		Ext.Ajax.request({
			method:'post',
			url:basePath+'plm/gantt/endTask.action',
			params:{
				id:taskid,
				prjId:prjplanid
			},
			callback:function(opts,suc,res){	
				var res=Ext.decode(res.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return; }
				if(res.success){
					alert('任务:'+rec.raw.Name+'已强制结束!');
					window.location.reload();
					}
				}
		});
	},
	deleteTask: function() {
		var a = this.grid.getSelectionModel().selected;
		this.grid.taskStore.remove(a.getRange());
		this.grid.assignmentStore.remove(this.grid.assignmentStore.getByInternalId(a.internalId));
	},
	editLeftLabel: function() {
		this.grid.getSchedulingView().editLeftLabel(this.rec)
	},
	editRightLabel: function() {
		this.grid.getSchedulingView().editRightLabel(this.rec)
	},
	addTaskAboveAction: function() {
		this.addTaskAbove(this.copyTask(this.rec))
	},
	addTaskBelowAction: function() {
		this.addTaskBelow(this.copyTask(this.rec))
	},
	addSubtask: function() {
		var a = this.rec;
		a.addSubtask(this.copyTask(a));
	},
	addSuccessor: function() {
		var a = this.rec;
		a.addSuccessor(this.copyTask(a))
	},
	addPredecessor: function() {
		var a = this.rec;
		a.addPredecessor(this.copyTask(a))
	},
	addMilestone: function() {
		var b = this.rec,
		a = this.copyTask(b);
		b.addTaskBelow(a);
		a.setStartEndDate(b.getEndDate(), b.getEndDate())
	},
	toggleMilestone: function() {
		if (this.rec.isMilestone()) {
			this.rec.convertToRegular()
		} else {
			this.rec.convertToMilestone()
		}
	},
	//zhouy 2016-11-24
	setDirty:function(){
		var record=this.grid.getTaskStore().currentCreated;
		record.setDirty();
		record.phantom=true;
		var row=this.grid.lockedGrid.getView().getNode(record, true);
		Ext.Array.each(row.childNodes,function(node){
			if(node.getAttribute('class').indexOf('x-grid-cell-treecolumn')>0 || node.getAttribute('class').indexOf('x-grid-cell-durationcolumn')>0 || node.getAttribute('class').indexOf('x-grid-cell-startdatecolumn')>0 || node.getAttribute('x-grid-cell-enddatecolumn')>0){
				Ext.fly(node).addCls(Ext.baseCSSPrefix + 'grid-dirty-cell');
			}
		});
	},
	//zhouy 2016-11-24
	insertBefore:function(){
		//插入子任务之前先执行
		
	},
	viewTask:function(){
		var grid = this.grid;
		var record = grid.getSelectionModel().selected.items[0];
		var taskid = record.data.Id;
		var resource = new Array();
		var taskResourceTab = new Array();
		Ext.Array.each(grid.store.assignmentStore.data.items,function(item,index){
			if(taskid==item.data.TaskId){
				resource.push(item);				
			}
		});
		Ext.Array.each(resource,function(item,index){
			var resourceId = item.data.ResourceId;
			var resc = grid.store.resourceStore.getByInternalId(item.data.ResourceId);
			taskResourceTab.push({
				title:resc.data.Name,
				resourceEmpId:resourceId,
				taskId:taskid,
				items:[{
					xtype:'grid',
					width:'100%',
					height:'100%',
					layout:'fit',
					border:false,
					store:Ext.create('Ext.data.Store',{
						fields:['WR_REDCORD','WR_PERCENTDONE','WR_RECORDDATE'],
						autoLoad:false
					}),
					columns:{
						items:[{
							header:'任务完成描述',
							align:'left',
							dataIndex:'WR_REDCORD',
							flex:2
						},{
							header:'提交完成比率(%)',
							dataIndex:'WR_PERCENTDONE'
						},{
							header:'操作时间',
							dataIndex:'WR_RECORDDATE'
						}],
						defaults:{
							style:'text-align:center;',
							align:'center',
							flex:1
						}
					},
					dockedItems : [{
						xtype : 'toolbar',
						dock : 'top',
						layout : {
							pack : 'left'
						},
						items : [{
							xtype : 'tbtext',
							text : '任务完成情况'
						}]
					}]
				}],
				listeners:{
					activate:function(tab){
						Ext.Ajax.request({
							url:basePath + 'plm/gantt/getTaskCompletion.action',
							method:'post',
							params:{
								taskId:tab.taskId,
								resourceEmpId:tab.resourceEmpId
							},
							callback:function(options,success,response){
								var res = Ext.decode(response.responseText);
								if(res.success){
									var bol = false;
									//判断res.taskMsg是否为空
									for(var x in res.taskMsg){
										bol = true;
									}				
									if(bol){
										tab.down('tbtext').setText('任务完成情况:分配比率<span style="font-weight:bold">' + res.taskMsg.assignPercent + 
											'%</span>,提交完成总比率:<span style="font-weight:bold">' + res.taskMsg.percentDone + '%</span>,完成总任务占比:<span style="font-weight:bold">' 
												+ res.taskMsg.taskPercent + '%</span>');
										if(res.workRecord.length>0){
											tab.down('grid').store.loadData(res.workRecord);										
										}										
									}									
								}else if(res.exceptionInfo){
									alert(res.exceptionInfo);
								}
							}
						});
					}
				}
			});
		});		
		var win = Ext.create('Ext.window.Window',{
			title : '任务进展',
			height : 450,
			width : 700,
			border:false,
			maximizable:true,
			bodyStyle : 'background:#F2F2F2;',
			layout:'fit',
			items:[{
				xtype:'tabpanel',
				layout:'fit',
				id:'tab',
				items:taskResourceTab
			}]		
		});
		win.show();
	}
});