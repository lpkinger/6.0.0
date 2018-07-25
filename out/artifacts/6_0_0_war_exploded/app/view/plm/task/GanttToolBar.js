Ext.define('erp.view.plm.task.GanttToolBar',{ 
	extend : 'Ext.Toolbar',
	alias : 'widget.gantt_toolbar',
	id:'gantt_toolbar',
	dock : 'top',
	initComponent : function() {
		var me = this;
		var items = [{
			xtype: 'buttongroup',
			title: '视图工具',
			columns: 4,
			height:69,
			margin:'0 5 0 0',
			items: [ {
				iconCls : 'icon-fullscreen',
				text : '全屏',
				scope : this,
				handler : function() {
					window.open(window.location.href);
				}
			},{
				iconCls : 'icon-prev',
				text : '向前',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.shiftPrevious();
				}
			},
			{
				iconCls : 'icon-next',
				text : '向后',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.shiftNext();
				}
			},
			{
				text : '缩放',
				iconCls : 'zoomfit',
				handler : function() {
					var g=this.getGantt();
					g.zoomToFit();
				},
				scope : this
			},
			{
				text : '收缩',
				iconCls : 'icon-collapseall',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.collapseAll();
				}
			},
			{
				text : '展开',
				iconCls : 'icon-expandall',
				scope : this,
				loader:{
					loadMask:true,
				},
				handler : function() {
					var g=this.getGantt();
					var loadMask= g.setLoading("正在展开",true);
					g.expandAll(loadMask.hide());
				}
			}]
		},
		{
			xtype: 'buttongroup',
			title: '视图解析',
			columns: 2,
			margin:'0 5 0 0',
			height:69,
			items: [{
				iconCls : 'gnt-date',
				text: '6 周',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.switchViewPreset('weekAndDayLetter');
					//g.switchViewPreset('weekAndMonth');
				}
			},{
				iconCls : 'gnt-date',
				text: '10周',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.switchViewPreset('weekAndDayLetter');
				}
			},{ 
				iconCls : 'gnt-date',
				text: '1 年',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					g.switchViewPreset('monthAndYear');
				}
			},{iconCls : 'gnt-date',
				text: '5 年',
				scope : this,
				handler : function() {
					var g=this.getGantt();
					var start = new Date(g.startDate.getFullYear(), 0);
					g.switchViewPreset('monthAndYear', start, Ext.Date.add(start, Ext.Date.YEAR, 5));
				}
			}]},{
				xtype: 'buttongroup',
				title: '操作区',
				columns: 3,  
				margin:'0 5 0 0',
				height:69,
				items: [ {
					iconCls : 'x-advance-print',
					text : '打印预览',
					scope:this,
					handler : function() {
						var g=this.getGantt();
						//g.zoomToFit();
						//document.body.innerHTML=document.getElementById('gantt_panel-locked').innerHTML;
						g.print();
					}
				},{
					iconCls : 'x-advance-save',
					id:'save',
					text :'保存修改',
					disabled:getUrlParam('readOnly')==1
				},{
					text: '导入Project',
					iconCls : 'x-advance-find',
					disabled:getUrlParam('readOnly')==1,
					handler: function () {
						var window =  Ext.create('erp.view.plm.task.ProjectImportPanel');
						window.show();
					},
					listeners:{
						afterrender:function(btn){
							var gantt=Ext.getCmp("gantt_panel");
							var statuscode=gantt.prjData.prj_statuscode;
							if(statuscode=='FINISHED'){
								btn.disable();
							}
						}		
					},
					scope:this
				},{
					text: '查看日志',
					iconCls: 'x-nbutton-icon-log',
					handler: function () {
						this.getLog(prjplanid);						
					},
					scope:this	
				}/*,{
					text: '查看资源',
					iconCls : 'x-advance-find',
					handler: function () {

					},
					scope:this
				},{
					iconCls : 'togglebutton',
					enableToggle: true,
					id : 'readonlybutton',
					text: '只读模式',
					pressed: false,
					scope:this,
					handler: function (btn) {
						var g=this.getGantt();
						g.setReadOnly(btn.pressed);
						Ext.getCmp('save').setDisabled(btn.pressed);
					}
				}*/
				]},{
					xtype: 'buttongroup',
					title: '功能区',
					columns : 4,
					height:69,
					items: [{
						text : '突出关键链',
						iconCls : 'togglebutton',
						scope : this,
						enableToggle : true,
						handler : function(btn) {
							var g=this.getGantt();
							var v = g.getSchedulingView();
							if (btn.pressed) {
								v.highlightCriticalPaths(true);
							} else {
								v.unhighlightCriticalPaths(true);
							}
						}
					},
					{
						iconCls : 'togglebutton',
						text : '未按时完成的任务',
						enableToggle : true,
						scope : this,
						handler : function(btn) {
							var g=this.getGantt();
							if (btn.pressed) {
								g.taskStore.filter(function(task) {
									return (task.get('EndDate')<new Date())&&(task.get('PercentDone') <100);
								});
							} else {
								g.taskStore.clearFilter();
							}
						}
					},{
						iconCls : 'action',
						text : '突出任务>7天',
						scope : this,
						handler : function(btn) {
							var g=this.getGantt();
							g.taskStore.getRootNode().cascadeBy(function(task) {
								if (Sch.util.Date.getDurationInDays(task.get('StartDate'), task.get('EndDate')) > 7) {
									var el =g.getSchedulingView().getElementFromEventRecord(task);
									el && el.frame('lime');
								}
							}, this);
						}
					},{
						iconCls : 'togglebutton',
						text : '筛选: 任务进度  < 30%',
						scope : this,
						enableToggle : true,
						toggleGroup : 'filter',
						handler : function(btn) { 
							var g=this.getGantt();
							if (btn.pressed) {
								g.taskStore.filter(function(task) {
									return task.get('PercentDone') < 30;
								});
							} else {
								g.taskStore.clearFilter();
							}
						}
					},{
						enableToggle : true,
						text : '需提醒的任务',
						scope : this,
						iconCls : 'togglebutton',   
						handler : function(btn) {
							var g=this.getGantt();
							if (btn.pressed) {
								g.taskStore.filter(function(task) {
									return  (Sch.util.Date.getDurationInDays(task.get('StartDate'), new Date())>task.get('Duration')/2)&&(Sch.util.Date.getDurationInDays(task.get('StartDate'), new Date())<task.get('Duration'))&&(task.get('PercentDone')<30);                                
								});
							} else {
								g.taskStore.clearFilter();
							}
						}
					}, {
			        	   iconCls : 'action',
			        	   text : '最后任务',
			        	   scope : this,
			        	   handler : function(btn) {
			        		   var latestEndDate = new Date(0),
			        		   latest;
			        		   var g=this.getGantt();
			        		   g.taskStore.getRootNode().cascadeBy(function(task) {
			        			   if (task.get('EndDate') >= latestEndDate) {
			        				   latestEndDate = task.get('EndDate');
			        				   latest = task;
			        			   }
			        		   });
			        		   g.getSchedulingView().scrollEventIntoView(latest, true);
			        	   }
			           },{
						xtype:'combo',
						scope : this,
						id:'combo',
						width:100,
						emptyText:'筛选类型',
						fieldStyle:'background:#F5F5F5',
						editable:false,
						store: Ext.create('Ext.data.Store', {
							fields: ['display', 'value'],
							data : [    		    
							        {"display":"任务名称", "value": "1"},
							        {"display":"员工名称", "value": "2"}	     
							        ]
						}),
						queryMode: 'local',
						displayField: 'display',
						valueField: 'value',
					},
					{
						xtype : 'textfield',
						emptyText : '搜索...',
						fieldStyle:'background:#F5F5F5',
						scope : this,
						width:150,
						padding: '0 0 -20 0',
						enableKeyEvents : true,
						listeners : {
							keypress : {
								fn : function(field, e) {
									var value   = field.getValue(); 
									var type=Ext.getCmp('combo').getValue();
									if (value&&type==2) {
										console.log(value); 
										g.taskStore.filter(function(task) {
											return  (task.get('resourcename').indexOf(value)>0);                                
										});
									} 
									else if(value){
										g.taskStore.filter('Name', value, true, false);
									}else {
										g.taskStore.clearFilter();
									}
								},
								scope : this
							},
							specialkey : {
								fn : function(field, e) {
									if (e.getKey() === e.ESC) {
										field.reset();
									}
									g.taskStore.clearFilter();
								},
								scope : this
							}
						}
					}
					]
				}];
		Ext.apply(this, {items : items});
		this.callParent(arguments);
	},
	getGantt:function(){
		return this.ownerCt;
	},
	getLog:function(id){
		var store=Ext.create('Ext.data.Store',{
			fields: ['TL_DATE', 'TL_RECORDMAN', 'TL_TYPE','TL_STARTDATE', 'TL_NAME','TL_ENDDATE','TL_RESOURCE','TL_RESOCCUPY','TL_DOCNAME'],
			pageSize:15,
			proxy:{
				type:'ajax',
				method:'post',
				url:basePath+'plm/gantt/getLogByCondition.action',
				reader:{
					type:"json",		
					root:'logs',
					totalProperty:'num',
				},
					actionMethods: {
			            read   : 'POST'
			        }
			},
		listeners:{
			beforeload : function(store) {
					var val = Ext.getCmp('docname').value;
					Ext.apply(store.proxy.extraParams, {
						prjplanid:id,
						docname:val
					});
			},
		},
		autoLoad:true		
		});						
		Ext.create('Ext.window.Window', {
			id : 'win' + id,
			title: '<span style="color:#CD6839;">操作日志</span>',
			iconCls: 'x-nbutton-icon-log',
			closeAction: 'destroy',
			height: "90%",
			width: "90%",
			modal:true,
			maximizable : true,
			buttonAlign : 'center',
			layout : 'border',
		//	iframe:true,
			items: [{
				xtype:'panel',
				region:'north',
				layout:"column",
			    defaults: {
		        	xtype : 'textfield',
		        	anchor: '100%'
		        },
				items:[{
					fieldLabel:'<span style="color:blue;font-weight:bold;margin:0px 0px 0px 15px">任务名称</span>',
					labelWidth:100,
					labelSeparator:':',
					id:'docname',
					allowblank:true
				},{
					xtype:'button',
					id:"search",
					iconCls:'x-button-search',
					handler:function(btn){
					var grid=Ext.getCmp('logPanel');
						grid.store.removeAll();
						grid.store.reload();								
					},				
				}],
			},{
				xtype: 'gridpanel',
				id:"logPanel",
				bodyStyle: 'background:#f1f1f1;',
				autoScroll: true,
				region:'center',
				store:store,
				columnLines: true,
				columns: [
			          { header: '时间', dataIndex: 'TL_DATE',height:30,width:140, renderer: function(val){
			        	  if(val != ''){
			        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
			        	  }
			          }},
			          { header: '操作人员', dataIndex: 'TL_RECORDMAN',width:60,renderer: function(val){
			        	  if(val == em_name){
			        		  return '<font color=red>' + val + '</font>';
			        	  } else {
			        		  return val;
			        	  }
			          }},
			          { header: '操作类型', dataIndex: 'TL_TYPE', width:120},
			          { header: '任务名称', dataIndex: 'TL_NAME',width:100,},
			          { header: '开始时间', dataIndex: 'TL_STARTDATE', width:90, renderer: function(val){
			        	  if(val != ''){
			        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
			        	  }
			          }},
			          { header: '结束时间', dataIndex: 'TL_ENDDATE', width:90,renderer: function(val){
			        	  if(val != ''){
			        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
			        	  }
			          }},
			          { header: '资源分配', dataIndex: 'TL_RESOURCE', width:80,},
			          { header: '资源占比', dataIndex: 'TL_RESOCCUPY', width:80,},
			          { header: '任务文件', dataIndex: 'TL_DOCNAME', width:120,},
			          { header: '备注', dataIndex: 'TL_REMARK', width:120,},
			        ],							     
				dockedItems:[{
					xtype:'pagingtoolbar',//分页组件
					pageSize:15,
					store:store,
					dock:'bottom',
					displayInfo:true,
					displayMsg:'当前显示第{0}到{1}条数据,一共有{2}条',
					emptyMsg: "没有数据",
					beforePageText: "当前第",
					afterPageText: "页,共{0}页"
						}],
					}],
				buttons : [{
					text : '关  闭',
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					handler : function(){
						Ext.getCmp('win' + id).close();
					}
					}],
				listeners:{
			 		render:function(){
			 			var me = this;
			 			this.el.on('keyup',function(e,input){
			 				if(e.button==12){
			 					var btn=Ext.getCmp('search');
			 					btn.handler();
			 				}
			 			});
			 		}
				},
				}).show();
		}
});
