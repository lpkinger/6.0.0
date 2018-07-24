Ext.require(['Gnt.plugin.TaskContextMenu', 'Sch.plugin.TreeCellEditing',
		'Sch.plugin.Pan', 'Gnt.panel.Gantt', 'Gnt.column.PercentDone',
		'Gnt.column.StartDate', 'Gnt.column.EndDate', 'Gnt.plugin.Printable',
		'Gnt.widget.AssignmentCellEditor', 'Gnt.column.ResourceAssignment',
		'Gnt.model.Assignment', 'erp.util.BaseUtil', 'Gnt.widget.Calendar']);

Ext.onReady(function() {
			Gantt.init();
			Ext.QuickTips.init();
		});
Gantt = {
//tx2x9saq
	init : function() {
		Ext.define('Gnt.column.ResourceName', {
					extend : 'Gnt.column.ResourceName',
					text : "执行人"
				});
		Ext.define('Gnt.column.AssignmentUnits', {
					extend : 'Gnt.column.AssignmentUnits',
					text : "分配比例"
				});
		Ext.define('Gnt.widget.AssignmentField', {
					extend : 'Gnt.widget.AssignmentField',
					cancelText : "取消",
					closeText : "保存并关闭"
				});
        Ext.define('TaskResource', {
					extend : 'Sch.model.Resource',
					idProperty : 'EmCode',
					nameField : 'Name',
					fields : ['EmCode', 'Name', 'Id', 'EmId']
				});
		var taskResourceStore = Ext.create("Sch.data.ResourceStore", {
					model : 'TaskResource',
					autoLoad : true,
					// autoSync : true,
					proxy : {
						type : 'ajax',
						method : 'POST',
						reader : {
							root : 'resources',
							type : 'json'
						},
						extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
						api : {
							read : basePath + 'task/resource.action'
							/*
							 * create : 'e-create.php', destroy :
							 * 'e-destroy.php', update : 'e-update.php'
							 */
						},
						writer : {
							type : 'json',
							root : 'resources',
							encode : true,
							writeAllFields : true,
							listful : true,
							allowSingle : false
						}
					}
				});

		Ext.define('TaskEvent', {
					extend : 'Sch.model.Event',
					nameField : 'Name',
					startDateField : 'StartDate',
					endDateField : 'EndDate',
					resourceIdField : 'ResourceId',
					fields : [
							// Define your own model fields
							{
						name : 'StartDate',
						type : 'date',
						dateFormat : 'Y-m-d'
					}, {
						name : 'EndDate',
						type : 'date',
						dateFormat : 'Y-m-d'
					}, 'Name', 'ResourceId', 'TaskId']
				});

		Ext.define('MyResourceModel', {
					extend : 'Gnt.model.Resource',
					fields : [{
								name : 'EmCode',
								type : 'string'
							}, {
								name : 'EmId',
								type : 'int'
							}]
				});
		var resourceStore = Ext.create("Gnt.data.ResourceStore", {
					model : 'MyResourceModel',
					proxy : {
						type : 'ajax',
						method : 'GET',
						url : basePath + 'plm/resourceassignment.action',
						root : 'resources',
						reader : {
							type : 'json'
						}
					}
				});

		Ext.define('MyTaskModel', {
					extend : 'Gnt.model.Task',
					clsField : 'TaskType',
					fields : [{
								name : 'TaskType',
								type : 'string'
							}, {
								name : 'TaskColor',
								type : 'string'
							}, {
								name : 'prjplanid',
								type : 'int'
							}, {
								name : 'prjplanname',
								type : 'string'
							}, {
								name : 'recorder',
								type : 'string'
							}, {
								name : 'recorddate',
								type : 'string'
							}, {
								name : 'taskcode',
								type : 'string'
							}, {
								name : 'id',
								type : 'int'
							}]
				});
		Ext.define('MyDependencyModel', {
					extend : 'Gnt.model.Dependency',
					fields : [{
								name : 'Id',
								type : 'int'
							}]
				});
		var taskStore = Ext.create("Gnt.data.TaskStore", {
					model : 'MyTaskModel',
					sorters : 'StartDate',
					proxy : {
						type : 'ajax',
						headers : {
							"Content-Type" : 'application/json'
						},
						extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
						api : {
							read : basePath + 'plm/gantt.action',
							create : basePath + 'plm/ganttcreate.action',
							destroy : 'webservices/Tasks.asmx/Delete',
							update : basePath + 'plm/ganttupdate.action'
						},
						writer : {
							type : 'json',
							root : 'jsonData',
							encode : true,
							nameProperty : 'data',
							allowSingle : false
						},
						reader : {
							type : 'json'
						}
					}
				});
		var allTaskStore = Ext.create("Sch.data.EventStore", {

					model : 'TaskEvent',
					resourceStore : taskResourceStore,
					autoLoad : true,
					proxy : {
						type : 'ajax',
						method : 'POST',
						reader : {
							root : 'assignments',
							type : 'json'
						},
						extraParams : {
							condition : 'prjplan_id=3147' // formCondition
						},
						api : {
							read : basePath + 'task/assignment.action'
							/*
							 * create : 'e-create.php', destroy :
							 * 'e-destroy.php', update : 'e-update.php'
							 */
						},
						writer : {
							type : 'json',
							root : 'assignments',
							encode : true,
							writeAllFields : true,
							listful : true,
							allowSingle : false
						}
					}

				})

		var gantt = this.createGantt({
					resourceStore : resourceStore,
					taskStore : taskStore
				});

		var scheduler = this.createScheduler({
					resourceStore : resourceStore,
					eventStore : taskStore,
					// Share time axis
					timeAxis : gantt.getTimeAxis(),

					lockedGridConfig : {
						resizeHandles : 'e',
						resizable : {
							pinned : true
						},
						width : 300
					},

					// Share non-working time visualization
					plugins : new Sch.plugin.Zones({
								store : gantt.getWorkingTimePlugin().store
							})
				});
		var allTask = this.createAllTask({
					resourceStore : taskResourceStore,
					eventStore : allTaskStore
				});

		var vp = new Ext.Viewport({
					layout : 'border',
					items : [gantt, scheduler, allTask]
				});

		var ganttViewEl = gantt.getSchedulingView().el;
		var schedulerViewEl = scheduler.getSchedulingView().el;

		// Sync the scrolling
		schedulerViewEl.on('scroll', function(ev, el) {
					ganttViewEl.scrollTo('left', el.scrollLeft);

				});
		ganttViewEl.on('scroll', function(ev, el) {
					schedulerViewEl.scrollTo('left', el.scrollLeft);
				});

		var fullRefresh = function() {
			scheduler.getSchedulingView().refresh();
		};

		// Since scheduler doesn't (yet) know of assignments, give it some help
		gantt.getAssignmentStore().on({

					update : fullRefresh,
					add : fullRefresh,
					remove : fullRefresh,
					refresh : fullRefresh,

					buffer : 1
				})
	},
	createAllTask : function(config) {

		return allTaskPanel = Ext.create("Sch.panel.SchedulerGrid", Ext.apply({

							title : '员工全部任务情况',
							eventBarTextField : 'Name',
							viewPreset : 'dayAndWeek',
							height : 20,
							region : 'south',
							split : true,
							timeAxis : gantt.getTimeAxis(),
							viewConfig : {
								forceFit : false
							},

							columns : [{
										header : 'Name',
										width : 130,
										dataIndex : 'Name'
									}]

						}, config));

	},

	createScheduler : function(config) {

		return A = Ext.create("Sch.SchedulerPanel", Ext.apply({
							// title : 'Resource panel',
							viewPreset : 'weekAndDayLetter',
							enableDragCreation : false,
							height : 20,
							layout : 'fit',
							region : 'south',
							split : true,

							columns : [{
										header : '员工姓名',
										width : 300,
										dataIndex : 'Name'
									}]
						}, config));
	},

	createGantt : function(config) {
		var BaseUtil = Ext.create('erp.util.BaseUtil');
		var startDate = new Date(2012, 9, 11);
		var endDate = Sch.util.Date.add(new Date(2012, 9, 11),
				Sch.util.Date.WEEK, 20);
		var start = new Date(2012, 9, 20), end = Sch.util.Date.add(new Date(
						2012, 10, 20), Sch.util.Date.WEEK, 20);
		var calendar = new Gnt.data.Calendar({
					data : [{
								Date : new Date(2010, 0, 13),
								Cls : 'gnt-national-holiday'
							}, {
								Date : new Date(2010, 1, 1),
								Cls : 'gnt-company-holiday'
							}, {
								Date : new Date(2010, 0, 16),
								IsWorkingDay : true
							}]
				});

		Ext.define('MyAssignmentModel', {
					extend : 'Gnt.model.Assignment'
				});

		var assignmentStore = Ext.create("Gnt.data.AssignmentStore", {
					model : 'MyAssignmentModel',
					autoLoad : true,

					resourceStore : config.resourceStore,
					proxy : {
						method : 'GET',
						type : 'ajax',
						url : basePath + 'plm/resourceassignment.action',
						extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
						reader : {
							type : 'json',
							root : 'assignments'
						}
					},
					listeners : {
						load : function() {
							config.resourceStore
									.loadData(this.proxy.reader.jsonData.resources);
						}
					}
				});

		var dependencyStore = Ext.create("Gnt.data.DependencyStore", {
					model : 'MyDependencyModel',
					autoLoad : true,
					proxy : {
						type : 'ajax',
						extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
						url : basePath + 'plm/gantt/getdependency.action',
						method : 'GET',
						reader : {
							type : 'json',
							root : 'dependency'
						}
					}
				});
		var formCondition = 'prjplan_id=3147'//'prjplan_id=3147';
		return gantt = Ext.create("Gnt.panel.Gantt", Ext.apply({
			id : 'ganttid',
			region : 'center',
			leftLabelField : 'Name',
			highlightWeekends : true,
			loadMask : true,
			snapToIncrement : true,
			// title : 'Gantt panel with tasks and resources',

			startDate : new Date(2012, 9, 11),
			endDate : Sch.util.Date.add(new Date(2012, 11, 11),
					Sch.util.Date.WEEK, 20),
			viewPreset : 'weekAndDayLetter',

			tbar : [/*
					 * { text : 'Previous', handler : function() { //
					 * this.up('ganttpanel').shiftPrevious(); var m = window.A;
					 * var n = Window.s2; m.height = 0; console.log(window); } }, {
					 * text : 'Next', handler : function() { window.A = 200;
					 * window.refresh; // this.up('ganttpanel').shiftNext(); } },
					 */{
						xtype : 'buttongroup',
						title : '视图工具',
						columns : 3,
						items : [{
									iconCls : 'icon-prev',
									text : '向前',
									scope : this,
									handler : function() {
										gantt.shiftPrevious();
									}
								}, {
									iconCls : 'icon-next',
									text : '向后',
									scope : this,
									handler : function() {
										gantt.shiftNext();
									}
								}, {
									text : '缩放',
									iconCls : 'zoomfit',
									handler : function() {
										gantt.zoomToFit();
									},
									scope : this
								}, {
									text : '收缩',
									iconCls : 'icon-collapseall',
									scope : this,
									handler : function() {
										gantt.collapseAll();
									}
								}, {
									text : '展开',
									iconCls : 'icon-expandall',
									scope : this,
									loader : {
										loadMask : true
									},
									handler : function() {
										var loadMask = gantt.setLoading("正在展开",
												true);
										gantt.expandAll(loadMask.hide());
										// g.expandAll();

									}
								}, {
									iconCls : 'togglebutton',
									enableToggle : true,
									id : 'readonlybutton',
									text : '只读',
									pressed : true
								}]
					}, {
						xtype : 'buttongroup',
						title : '视图解析',
						columns : 2,
						items : [{
									iconCls : 'gnt-date',
									text : '6 周',
									scope : this,
									handler : function() {
										gantt.switchViewPreset('weekAndMonth');
									}
								}, {
									iconCls : 'gnt-date',
									text : '10周',
									scope : this,
									handler : function() {
										gantt
												.switchViewPreset('weekAndDayLetter');
									}
								}, {
									iconCls : 'gnt-date',
									text : '1 年',
									scope : this,
									handler : function() {
										gantt.switchViewPreset('monthAndYear');
									}
								}, {
									iconCls : 'gnt-date',
									text : '5 年',
									scope : this,
									handler : function() {
										var start = new Date(this.getStart()
														.getFullYear(), 0);

										gantt.switchViewPreset('monthAndYear',
												start, Ext.Date.add(start,
														Ext.Date.YEAR, 5));
									}
								}]
					}, {
						xtype : 'buttongroup',
						title : '操作区',
						columns : 3,
						items : [{
									iconCls : 'x-advance-print',
									text : '打印',
									handler : function() {
										// Make sure this fits horizontally on
										// one page.
										Ext.getCmp('gantt').zoomToFit();
										Ext.getCmp('gantt').print();
									}
								}, {
									text : '添加任务',
									iconCls : 'x-advance-add',
									handler : function() {
										var selectItem = gantt.selModel.selected.items[0];
										if (selectItem) {
											if (selectItem.data.Id) {
												Ext.Ajax.request({
													url : basePath
															+ "plm/gantt/getData.action",
													params : {
														condition : formCondition
													},
													method : 'get',
													callback : function(
															options, success,
															response) {
														var rs = new Ext.decode(response.responseText);
														if (rs.exceptionInfo) {
															showError(rs.exceptionInfo);
															return;
														}
														if (rs.success) {
															var newTask = new gantt.taskStore.model(
																	{
																		Name : 'New task',
																		leaf : true,
																		PercentDone : 0,
																		prjplanid : rs.data.prjplandata[0].prjplan_id,
																		prjplanname : rs.data.prjplandata[0].prjplan_prjname,
																		// taskcode:BaseUtil.getRandomNumber(),
																		taskcode : (new Date())
																				.getTime(),
																		recorder : recorder,
																		recorddate : recorddate,
																		StartDate : selectItem.data.StartDate,// 给系统的默认时间
																		EndDate : selectItem.data.EndDate
																	});
															var record = gantt.selModel.selected.items[0];
															if (record.data.leaf) {
																record.data.leaf = false;
																record.data.expanded = true;
															}
															record
																	.appendChild(newTask);
														}
													}
												});
											} else {
												showError("任务未生成不能给他添加子任务!");
											}
										} else {
											showError("请选择添加任务的父节点!");
										}
									}
								}, {
									text : '添加父任务',
									iconCls : 'x-advance-add',
									handler : function() {
										Ext.Ajax.request({
											url : basePath
													+ "plm/gantt/getData.action",
											params : {
												condition : formCondition
											},
											method : 'get',
											callback : function(options,
													success, response) {
												var rs = new Ext.decode(response.responseText);
												if (rs.exceptionInfo) {
													showError(rs.exceptionInfo);
													return;
												}
												if (rs.success) {
													var newTask = new gantt.taskStore.model(
															{
																Name : 'New task',
																leaf : false,
																PercentDone : 0,
																prjplanid : rs.data.prjplandata[0].prjplan_id,
																prjplanname : rs.data.prjplandata[0].prjplan_prjname,
																// taskcode:BaseUtil.getRandomNumber(),
																recorder : recorder,
																recorddate : recorddate,
																parentId : 0
															});
													taskStore
															.getRootNode()
															.appendChild(newTask)
												}
											}
										});
									}
								},

								{
									iconCls : 'x-advance-save',
									id : 'savebutton',
									text : '保存',
									listeners : {
										'afterrender' : function(btn, opts) {
											if (Ext.getCmp('readonlybutton').pressed) {
												btn.setDisabled(false);
											}
										}
									},
									handler : function() {
										// g.checkstore();//验证store是否填写完整 要求必填字段
										// 如 日期
										// 并且开始时间要小于或等于结束时间
										var options = {}, me = gantt.taskStore;
										de = gantt.dependencyStore;
										as = gantt.assignmentStore;
										var toCreate = me.getNewRecords(), toUpdate = me
												.getUpdatedRecords(), toDestroy = me
												.getRemovedRecords(), toCreateDependency = de
												.getNewRecords(), toDestroyDependency = de
												.getRemovedRecords(), toCreateassign = as
												.getNewRecords(), toUpdateassign = as
												.getUpdatedRecords(), toDestroyassign = as
												.getRemovedRecords(), needsSync = false;
										if (toCreate.length > 0) {
											var create = null;
											options.create = toCreate;
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toCreate.length; i++) {
												var data = toCreate[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data)
														+ '#@';
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/gantt/ganttcreate.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toUpdate.length > 0) {
											options.update = toUpdate;
											needsSync = true;
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toUpdate.length; i++) {
												var data = toUpdate[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data)
														+ '#@';
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/gantt/ganttupdate.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toDestroy.length > 0) {
											options.destroy = toDestroy;
											needsSync = true;
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toDestroy.length; i++) {
												var data = toDestroy[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data)
														+ '#@';
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/gantt/ganttdelete.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toCreateDependency.length > 0) {
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toCreateDependency.length; i++) {
												var data = toCreateDependency[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data);
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/gantt/dependencycreate.action",
												params : {
													condition : formCondition,
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toDestroyDependency.length > 0) {
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toDestroyDependency.length; i++) {
												var data = toDestroyDependency[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data);
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/gantt/dependencydelete.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toCreateassign.length > 0) {
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toCreateassign.length; i++) {
												// var
												// ddd=g.selModel.selected.items[0];
												var data = toCreateassign[i].data;
												var id = data.TaskId;
												var task = gantt.taskStore
														.getById(id);
												var resourceId = data.ResourceId;
												var resource = gantt.resourceStore
														.getById(resourceId);
												data.StartDate = task.data.StartDate;
												data.EndDate = task.data.EndDate;
												data.resourcecode = resource.data.EmCode;
												data.detno = 1;
												data.Id = 0;
												data.resourcename = resource.data.Name;
												data_resourcetype = "";
												data.taskname = task.data.Name;
												data.emid = resource.data.EmId;
												data.prjid = task.data.prjplanid;
												data.prjname = task.data.prjplanname;
												jsonData[index++] = Ext.JSON
														.encode(data);
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/resource/createResource.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toUpdateassign.length > 0) {
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toUpdateassign.length; i++) {
												var data = toUpdateassign[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data);
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/resource/updateResource.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										if (toDestroyassign.length > 0) {
											var index = 0;
											var jsonData = new Array();
											for (var i = 0; i < toDestroyassign.length; i++) {
												var data = toDestroyassign[i].data;
												jsonData[index++] = Ext.JSON
														.encode(data);
											}
											Ext.Ajax.request({
												url : basePath
														+ "plm/resource/deleteResource.action",
												params : {
													jsonData : unescape(jsonData
															.toString()
															.replace(/\\/g, "%"))
												},
												method : 'post',
												callback : function(options,
														success, response) {
												}
											});
										}
										// Ext.Msg.alert('提示','保存成功!');
										var gridCondition = getUrlParam("gridCondition");
										window.location.href = window.location.href
												+ '?formCondition='
												+ formCondition
												+ '&gridCondition='
												+ gridCondition;
									}
								}, {
									text : '删除任务',
									iconCls : 'x-advance-delete',
									handler : function() {
										var record = gantt.selModel.selected.items[0];
										if (record) {
											var parentNode = record.parentNode;
											if (parentNode.childNodes.length == 1) {
												parentNode.data.leaf = true;
											}
											parentNode.removeChild(record);
										} else {
											showError("请选择删除要删除任务!");
										}
									}
								}, {
									text : '查看成员',
									iconCls : 'x-advance-find',
									handler : function() {
										var panel = Ext
												.getCmp("resourceproject="
														+ formCondition);
										value = formCondition.split("=")[1];
										var caller = "Resource";
										var url = basePath
												+ "jsps/oa/persontask/taskforperson/taskforperson.jsp";
										var main = parent.Ext
												.getCmp("content-panel");
										if (!panel) {
											var title = "";
											if (value.toString().length > 4) {
												title = value
														.toString()
														.substring(value
																.toString().length
																- 4);
											} else {
												title = value;
											}
											panel = {
												// title :
												// main.getActiveTab().title+'('+title+')',
												title : '执行人(' + title + ')',
												tag : 'iframe',
												tabConfig : {
													tooltip : '执行人(' + title
															+ ')'
												},
												frame : true,
												border : false,
												layout : 'fit',
												iconCls : 'x-tree-icon-tab-tab',
												html : '<iframe id="iframe_maindetail_'
														+ caller
														+ "_"
														+ value
														+ '" src="'
														+ url
														+ '?prjplanid='
														+ value
														+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
												closable : true,
												listeners : {
													close : function() {
														main
																.setActiveTab(main
																		.getActiveTab().id);
													}
												}
											};
											Ext.getCmp("ganttid").openTab(
													panel,
													"resourceproject="
															+ formCondition);
										} else {
											main.setActiveTab(panel);
										}
									},
									scope : this
								}

						]
					}, {
						xtype : 'buttongroup',
						title : '功能区',
						columns : 4,
						items : [{
									text : '突出关键链',
									iconCls : 'togglebutton',
									scope : this,
									enableToggle : true,
									handler : function(btn) {
										var v = gantt.getSchedulingView();
										if (btn.pressed) {
											v.highlightCriticalPaths(true);
										} else {
											v.unhighlightCriticalPaths(true);
										}
									}
								}, {
									iconCls : 'togglebutton',
									text : '未按时完成的任务',
									enableToggle : true,
									scope : this,
									handler : function(btn) {
										if (btn.pressed) {
											gantt.taskStore.filter(function(
													task) {
												return (task.get('EndDate') < new Date())
														&& (task
																.get('PercentDone') < 100);
											});
										} else {
											gantt.taskStore.clearFilter();
										}
									}
								}, {
									iconCls : 'action',
									text : '突出任务>7天',
									scope : this,
									handler : function(btn) {
										gantt.taskStore.getRootNode()
												.cascadeBy(function(task) {
													if (Sch.util.Date
															.getDurationInDays(
																	task
																			.get('StartDate'),
																	task
																			.get('EndDate')) > 7) {
														var el = gantt
																.getSchedulingView()
																.getElementFromEventRecord(task);
														el && el.frame('lime');
													}
												}, this);
									}
								}, {
									iconCls : 'togglebutton',
									text : '筛选: 任务进度  < 30%',
									scope : this,
									enableToggle : true,
									toggleGroup : 'filter',
									handler : function(btn) {
										if (btn.pressed) {
											gantt.taskStore.filter(function(
													task) {
												return task.get('PercentDone') < 30;
											});
										} else {
											gantt.taskStore.clearFilter();
										}
									}
								}, {
									iconCls : 'togglebutton',
									text : '级联变化',
									scope : this,
									enableToggle : true,
									handler : function(btn) {
										gantt.setCascadeChanges(btn.pressed);
									}
								}, {
									enableToggle : true,
									text : '需提醒的任务',
									scope : this,
									iconCls : 'togglebutton',
									handler : function(btn) {
										if (btn.pressed) {
											gantt.taskStore.filter(function(
													task) {
												return (Sch.util.Date
														.getDurationInDays(
																task
																		.get('StartDate'),
																new Date()) > task
														.get('Duration')
														/ 2)
														&& (Sch.util.Date
																.getDurationInDays(
																		task
																				.get('StartDate'),
																		new Date()) < task
																.get('Duration'))
														&& (task
																.get('PercentDone') < 30);
											});
										} else {
											gantt.taskStore.clearFilter();
										}
									}
								}, {
									iconCls : 'action',
									text : '最后任务',
									scope : this,

									handler : function(btn) {
										var latestEndDate = new Date(0), latest;
										gantt.taskStore.getRootNode()
												.cascadeBy(function(task) {
													if (task.get('EndDate') >= latestEndDate) {
														latestEndDate = task
																.get('EndDate');
														latest = task;
													}
												});
										gantt.getSchedulingView()
												.scrollEventIntoView(latest,
														true);
									}
								}, {
									xtype : 'textfield',
									emptyText : '搜索任务...',
									scope : this,
									width : 150,
									enableKeyEvents : true,
									listeners : {
										keyup : {
											fn : function(field, e) {
												var value = field.getValue();
												var regexp = new RegExp(Ext.String.escapeRegex(value),'i')

												if (value) {gantt.taskStore.filterTreeBy(function(task) {
														          return regexp.test(task.get('Name'))
												             });
												} else {
													gantt.taskStore
															.clearTreeFilter();
												}
											},
											scope : this
										},
										specialkey : {
											fn : function(field, e) {
												if (e.getKey() === e.ESC) {
													field.reset();
												}
												this.taskStore
														.clearTreeFilter();
											},
											scope : this
										}
									}
								}]
					}],
			lockedGridConfig : {
				width : 450,
				title : '任务表',
				collapsible : true
			},

			// Experimental
			schedulerConfig : {
				collapsible : true,
				title : '计划表'
			},

			leftLabelField : {
				dataIndex : 'Name',
				editor : {
					xtype : 'textfield'
				}
			},
			eventRenderer : function(task) {
				return {
					style : 'background-color: #' + task.data.TaskColor
				};
			},
			_fullScreenFn : (function() {
				var docElm = document.documentElement;

				if (docElm.requestFullscreen) {
					return "requestFullscreen";
				} else if (docElm.mozRequestFullScreen) {
					return "mozRequestFullScreen";
				} else if (docElm.webkitRequestFullScreen) {
					return "webkitRequestFullScreen";
				}
			})(),
			plugins : [
					Ext.create("Gnt.plugin.TaskContextMenu"),
					Ext.create("Sch.plugin.Pan"),
					assignmentEditor = Ext.create(
							'Gnt.widget.AssignmentCellEditor', {
								assignmentStore : assignmentStore,
								resourceStore : config.resourceStore
							}),
				   Ext.create('Sch.plugin.TreeCellEditing', {
								clicksToEdit : 1,
								listeners : {
									beforeedit : function() {
										return !Ext.getCmp('readonlybutton').pressed;
									}
								}
							}), new Gnt.plugin.Printable({
						printRenderer : function(task, tplData) {
							if (task.isMilestone()) {
								return;
							} else if (task.isLeaf()) {
								var availableWidth = tplData.width - 4, progressWidth = Math
										.floor(availableWidth
												* task.get('PercentDone') / 100);

								return {
									// Style borders to act as
									// background/progressbar
									progressBarStyle : Ext.String
											.format(
													'width:{2}px;border-left:{0}px solid #7971E2;border-right:{1}px solid #E5ECF5;',
													progressWidth,
													availableWidth
															- progressWidth,
													availableWidth)
								};
							} else {
								var availableWidth = tplData.width - 2, progressWidth = Math
										.floor(availableWidth
												* task.get('PercentDone') / 100);

								return {
									// Style borders to act as
									// background/progressbar
									progressBarStyle : Ext.String
											.format(
													'width:{2}px;border-left:{0}px solid #FFF3A5;border-right:{1}px solid #FFBC00;',
													progressWidth,
													availableWidth
															- progressWidth,
													availableWidth)
								};
							}
						},

						beforePrint : function(sched) {
							var v = sched.getSchedulingView();
							this.oldRenderer = v.eventRenderer;
							this.oldMilestoneTemplate = v.milestoneTemplate;
							v.milestoneTemplate = printableMilestoneTpl;
							v.eventRenderer = this.printRenderer;
						},

						afterPrint : function(sched) {
							var v = sched.getSchedulingView();
							v.eventRenderer = this.oldRenderer;
							v.milestoneTemplate = this.oldMilestoneTemplate;
						}
					})],
			tooltipTpl : new Ext.XTemplate(
					'<h4 class="tipHeader">{Name}</h4>',
					'<table class="taskTip">',
					'<tr><td>开始:</td> <td align="right">{[Ext.Date.format(values.StartDate, "y-m-d")]}</td></tr>',
					'<tr><td>结束:</td> <td align="right">{[Ext.Date.format(values.EndDate, "y-m-d")]}</td></tr>',
					'<tr><td>进度:</td><td align="right">{PercentDone}%</td></tr>',
					'</table>').compile(),
			applyPercentDone : function(value) {
				this.getSelectionModel().selected.each(function(task) {
							task.setPercentDone(value);
						});
			},

			showFullScreen : function() {
				this.el.down('.x-panel-body').dom[this._fullScreenFn]();
			},
			openTab : function(panel, id) {
				var o = (typeof panel == "string" ? panel : id || panel.id);
				var main = parent.Ext.getCmp("content-panel");
				var tab = main.getComponent(o);
				if (tab) {
					main.setActiveTab(tab);
				} else if (typeof panel != "string") {
					panel.id = o;
					var p = main.add(panel);
					main.setActiveTab(p);
				}
			},
			// Experimental, not X-browser
			_fullScreenFn : (function() {
				var docElm = document.documentElement;

				if (docElm.requestFullscreen) {
					return "requestFullscreen";
				} else if (docElm.mozRequestFullScreen) {
					return "mozRequestFullScreen";
				} else if (docElm.webkitRequestFullScreen) {
					return "webkitRequestFullScreen";
				}
			})(),

			columns : [{
						xtype : 'wbscolumn',
						header : '编号',
						width : 50
					}, {
						xtype : 'treecolumn',
						header : '任务',
						sortable : true,
						dataIndex : 'Name',
						width : 200,
						field : {
							allowBlank : false
						},
						renderer : function(v, meta, r) {
							if (!r.data.leaf)
								meta.tdCls = 'sch-gantt-parent-cell';
							return v;
						}
					}, {
						header : '执行人',
						width : 120,
						editor : assignmentEditor,
						xtype : 'resourceassignmentcolumn'

					}, {
						header : '开始时间',
						xtype : 'startdatecolumn',
						field : {
							xtype : 'datefield',
							allowBlank : false
						}
					}, {
						header : '结束时间',
						xtype : 'enddatecolumn',
						// hidden : true
						field : {
							xtype : 'datefield',
							allowBlank : false
						}
					}, {
						header : '持续时间',
						xtype : 'durationcolumn'
					}, {
						header : '完成率(%)',
						xtype : 'percentdonecolumn',
						width : 50
					},

					// column displaying task color
					{
						header : '颜色',
						xtype : 'templatecolumn',
						width : 50,
						tdCls : 'sch-column-color',
						field : {
							allowBlank : false
						},
						tpl : '<div class="color-column-inner" style="background-color:#{TaskColor}">&nbsp;</div>',
						listeners : {
							click : function(panel, el, a, b, event, record) {
								event.stopEvent();
								this.rec = record;
								this.showColumnMenu(el, event, record);
							}
						},
						showColumnMenu : function(el, event, rec) {
							// if color menu is not present, create a new
							// Ext.menu.Menu instance
							if (!this.colorMenu) {
								this.colorMenu = new Ext.menu.Menu({
									cls : 'gnt-locked-colormenu',
									plain : true,
									items : [{
										text : '更改任务颜色',
										menu : {
											showSeparator : false,
											items : [Ext.create(
													'Ext.ColorPalette', {
														listeners : {
															select : function(
																	cp, color) {
																this.rec
																		.set(
																				'TaskColor',
																				color);
															},
															scope : this
														}
													})]
										}
									}]
								});
							}

							this.colorMenu.showAt(event.xy);
						}
					}],
			assignmentStore : assignmentStore,
			dependencyStore : dependencyStore
		}, config));
	}
};
