var workbentch={
		days:7,
		height:0.5,
		width: 0.5,
		setWidth: function(width){
			this.width = width;
		},
		setHeight: function(height){
			this.height = height;
		},
		setDays:function(days){
			this.days=days;
		},
		WorkDaily:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>工作日报</font>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'Diary\',\'工作日报\',\'jsps/common/datalist.jsp\',null,null,\'di_emid=' + emid + '\');">更多工作日报</a></div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'WorkDaily',
				iconCls: 'main-news',
				contentEl:'mydairy',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		Agenda:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>工作计划</font>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'Plan\',\'工作计划\',\'jsps/common/datalist.jsp\',null,null,\'pl_emid=' + emid + '\');">更多工作计划</a></div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'plan',
				contentEl:'myworkplan',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});

		},
		WorkAttendance:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>考勤</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'WorkAttendance',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		JProcess2DealByMe:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>待审批的流程</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'JProcess2DealByMe',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});    
		},
		JProcessDeal:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>发起的流程</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'JProcessDeal',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		ProjectPlan:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>未完成的项目</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'ProjectPlan',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		WorkRecord: function(){
			var me = this.workbench||this;
			return Ext.create('Ext.panel.Panel', {
				title:'<font color=green>任务报告(最近'+me.workbentch.days+'天)</font>'+
		'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'WorkRecord\',\'任务日报\',\'jsps/common/datalist.jsp\',null,null,\'wr_recorderemid=' + emid + '\');">更多任务日报</a></div>',
				bodyStyle: 'background: #f1f1f1',
				//style:'margin: 1px;',
				id: 'WR',
				contentEl: 'mytask',
				iconCls: 'main-todo',
				autoScroll: true,
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		newSynergy:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green> 内部协同</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'newSynergy',
				iconCls: 'main-news',
				contentEl:'mySynergy',
				columnWidth: 1/3, 
				height: me.height*0.4
			});  
		},
		ProjectFeePlease:function(days){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>发出的费用申请单(最近'+me.workbentch.days+'天)</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_email9',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		ProjectFeeClaim:function(days){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>发出的费用报销单(最近'+me.workbentch.days+'天)</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_email99',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		Meeting:function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<font color=green>参加的会议</font>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_email91',
				iconCls: 'main-news',
				columnWidth: 1/3, 
				height: me.height*0.4
			});
		},
		ToDoTask:function(){
			var me = this.workbench || this;
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'ResourceAssignment',
					condition:  'ra_emid=' + emid, 
					page: 1,
					pageSize: parseInt(height*0.2/12)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task == [] || task.length == 0){
						Ext.get("myToDo").insertHtml('afterBegin', '<span style="color:gray;font-size:26px; padding: 5 5 5 5;">(暂无任务)</span>');
					} else {
						Ext.create('Ext.grid.Panel', {
							autoScroll: true,
							store: Ext.create('Ext.data.Store', {
								fields:['ra_id', 'ra_taskname', 'ra_startdate', 'ra_enddate', 'surplus', 'ra_taskpercentdone'],
								data: task
							}),
							height: me.height*0.5,
							bodyStyle: 'background: #f1f1f1;border: none;',
							columns: [
							          { header: 'ID',  dataIndex: 'ra_id', hidden: true},
							          { header: '任务名称',  dataIndex: 'ra_taskname', flex: 2 , renderer: taskItem},
							          { header: '开始时间', dataIndex: 'ra_startdate', flex: 1 },
							          { header: '结束时间', dataIndex: 'ra_enddate', flex: 1 },
							          { header: '剩余时间', dataIndex: 'surplus', flex: 1.5, renderer: getSurPlus },
							          { header: '完成率(%)', dataIndex: 'ra_taskpercentdone', flex: 1, renderer: percentdone}
							          ],
							          renderTo: Ext.get("myToDo")
						});
					}
				}
			});
		},
		_WorkRecord: function(){
			var me = this.workbench || this;
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'WorkRecord',
					condition:  'wr_recorderemid=' + emid, 
					page: 1,
					pageSize: parseInt(100*0.3/12)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task == [] || task.length == 0){
						Ext.get("mytask").insertHtml('afterBegin', '<span style="color:gray;font-size:26px; padding: 5 5 5 5;">(暂无任务报告)</span>');
					} else {
						Ext.create('Ext.grid.Panel', {
							autoScroll: true,
							store: Ext.create('Ext.data.Store', {
								fields:['wr_id', 'wr_taskname', 'wr_recorddate', 'wr_percentdone', 'wr_taskpercentdone'],
								data: task
							}),
							height: me.height*1/3,
							bodyStyle: 'background: #f1f1f1;border: none;',
							columns: [
							          { header: 'ID',  dataIndex: ' wr_id', hidden: true},
							          { header: '任务名称',  dataIndex: 'wr_taskname', flex: 2 ,renderer:workrecord},
							          { header: '提交日期', dataIndex: 'wr_recorddate', flex: 1 },
							          { header: '提交完成率', dataIndex: 'wr_percentdone', flex: 1 },
							          /** { header: '任务已完成率', dataIndex: 'wr_taskpercentdone', flex: 1.5 },**/
							          ],
							          renderTo: Ext.get("mytask")
						});
					}
				}
			});
		}, 
		_WorkDaily:function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'Diary',
					condition: 'di_emid=' + emid,
					page: 1,
					pageSize: parseInt(height*0.27/23)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var notes = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];					        		
					var html = '<ul>';
					Ext.each(notes, function(n){
						html += '<li>' + n.di_time .substring(0, 10) + '&nbsp;<img src="' + basePath + 
						'resource/images/mainpage/icon_headerFooter.gif"/>&nbsp;<a class="x-btn-link" onclick="openTable(' 
						+ n.di_id + ',\'Diary\',\'工作日记\',\'jsps/oa/persontask/Diary/DiaryR.jsp\',\'di_id\',null' + ')">' 
						+n.di_thoughts.substring(0, 10) + '...</a></li>';
					});
					if(notes.length == 0){
						html = '<ul>没有记录';
					}
					html += '</ul>';
					Ext.get("mydairy").insertHtml('afterBegin', html);
				}
			});
		},
		_Agenda:function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'Plan',
					condition:  'pl_emid=' + emid, 
					contentEl: 'Plan',
					page: 1,
					pageSize: parseInt(height*0.3/27)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					Ext.create('Ext.grid.Panel', {
						autoScroll: true,
						store: Ext.create('Ext.data.Store', {
							fields:['pl_id', 'pl_title', 'pl_start', 'pl_end', 'pl_emergency', 'pl_received'],
							data: task
						}),
						columns: [
						          { header: 'ID',  dataIndex: 'pl_id', hidden: true},
						          { header: '计划标题',  dataIndex: 'pl_title', flex: 1,renderer:workplan},
						          { header: '开始时间', dataIndex: 'pl_start', flex: 1 },
						          { header: '结束时间', dataIndex: 'pl_end', flex: 1 },
						          { header: '紧急程度', dataIndex: 'pl_emergency', flex: 0.5 }
						          ],

						          renderTo: Ext.get("myworkplan")
					});

					if(task == [] || task.length == 0){
						Ext.get("myworkplan").insertHtml('afterEnd', '<font color=red>(暂无计划)</font>');
					}
				}
			});
		},
		_newSynergy:function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'Synergy!Data',
					condition: 'sy_releaser_id=' + emid, 

					page: 1,
					pageSize: parseInt(height*0.3/27)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					Ext.create('Ext.grid.Panel', {
						autoScroll: true,
						store: Ext.create('Ext.data.Store', {
							fields:['sy_id', 'sy_title', 'sy_process','sy_type','sy_date'],
							data: data
						}),
						columns: [
						          { header: 'ID',  dataIndex: 'sy_id', hidden: true},
						          { header: '协同标题',  dataIndex: 'sy_title', flex: 1,renderer:Synergy},
						          { header: '流程名称', dataIndex:'sy_process',flex:1},
						          { header: '协同类型', dataIndex: 'sy_type', flex: 1 },
						          { header: '时间', dataIndex: 'sy_date', flex: 1 },  
						          ],

						          renderTo: Ext.get("mySynergy")
					});

					if(data == [] || data.length == 0){
						Ext.get("newSynergy").insertHtml('afterEnd', '<font color=red>(暂无协同)</font>');
					}
				}
			});
		}
};
function getColumn(){
	Ext.Ajax.request({//拿到grid的columns
		url : basePath +'oa/attention/getAttentionByEmId.action',
		params:{
			emid:emid
		},
		method : 'get',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			var records=res.data;
			//默认添加一个模块  用来查看员工信息
			getDefault(); 
			Ext.Array.each(records,function(data){       	
				workbentch.setDays(data.currentMap.AP_DAYS);
				var panel=workbentch[data.currentMap.AP_SUBCODE].apply();
				Ext.getCmp('bench').add(panel);
			});
			Ext.each(res.data,function(data){
				if(workbentch["_"+data.currentMap.AP_SUBCODE]){
					workbentch["_"+data.currentMap.AP_SUBCODE].apply();
				}
			});
		}
	});
}
function getDefault(){
	var me = this.workbench || this;
	var data=null;
	Ext.Ajax.request({
		url : basePath + 'common/getAttentionEmployee.action',
		params: {
			caller: 'AttentionEmployee',
			emid: emid, 
		},
		method : 'post',
		async: false,
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			console.log(res);
			if(res.exception || res.exceptionInfo){
				showError(res.exceptionInfo);
				return;
			}
			data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();

		}
	});
	var panel= Ext.create('Ext.form.Panel', {
		title: '<font color=green>员工信息</font>',
		bodyPadding: 5,
		iconCls: 'main-activeuser',
		layout: 'column',
		id:'form',
		defaults: {
			anchor: '100%',
			readOnly:true,
			columnWidth:0.5,
			fieldStyle : 'background:#f0f0f0;border-bottom:none;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;color:#CD661D;border-bottom-style:1px solid;border-left:none;font-weight: bold; ',
		},
		columnWidth: 0.5, 
		height: me.height*0.6,
		frame:true,
		defaultType: 'textfield',
		items: [{
			html:'<div style="font-weight:bold; font-size:15px">基本信息</div>',
			columnWidth:1
		},{
			fieldLabel: '姓名',
			name: 'em_name',
			id:'em_name',
			columnWidth:0.4
		},{
			id:'online',
			columnWidth:0.2,
			fieldStyle:'color:green;font-weight: bold;background:#f0f0f0;border:none'
		},{
			xtype:'button',
			layout:'fit',
			columnWidth:0,
			iconCls:'x-button-icon-paging',
			listeners:{
				click:function(btn,e){
					var othername=Ext.getCmp('em_name').getValue();
					showDialogBox(e,null,emid,othername);
				}
			}
		},{
			fieldLabel:'照片',
			name:'em_photourl',
			xtype:'photofield',
			value:data.em_photourl,
		},{
			fieldLabel: '编号',
			name: 'em_code',
		},{
			fieldLabel:'性别',
			name:'em_sex',
		},{
			fieldLabel:'出生年月',
			name:'em_birthday'
		},{
			fieldLabel:'联系电话',
			name:'em_tel',
		},{
			fieldLabel: '移动电话',
			name: 'em_mobile',
		},{
			fieldLabel: '默认邮箱',
			name: 'em_email',
		},{
			fieldLabel:'籍贯',
			name:'em_native'
		},{
			fieldLabel:'工作年限',
			name:'em_worktime',
		},{
			html:'<div style="font-weight:bold; font-size:15px">组织信息</div>',
			columnWidth:1
		},{
			fieldLabel:'所属组织',
			name:'em_worktime',
		},{
			fieldLabel:'所属部门',
			name:'em_birthday'
		},{
			fieldLabel:'部门负责人',
			name:'or_headmanname'
		}],
		renderTo: Ext.getCmp('mydata'),
		listeners:{
			afterrender:function(btn,e){
				Ext.getCmp('photobutton').hide();
			}
		}
	});			
	panel.getForm().setValues(data);
	Ext.getCmp('bench').add(panel);
	var ToDo = Ext.create('Ext.panel.Panel', {
		title:'<font color=green>待办事宜</font>' + 
		'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'ResourceAssignment\',\'任务列表\',\'jsps/common/datalist.jsp\',null,null,\'ra_emid=' + data.em_id + '\');">发布任务</a></div>' +
		'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'ResourceAssignment\',\'任务列表\',\'jsps/common/datalist.jsp\',null,null,\'ra_emid=' + data.em_id + '\');">更多任务</a>|</div>' + 
		'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'ResourceAssignment\',\'任务列表\',\'jsps/common/datalist.jsp\',null,null,\'ra_emid=' + data.em_id + '\');">下属任务</a>|</div>',
		bodyStyle: 'background: #f1f1f1',
		//style:'margin: 1px;',
		id: 'ToDo',
		contentEl: 'myToDo',
		iconCls: 'main-todo',
		autoScroll: true,
		columnWidth: 0.5, 
		height: me.height*0.6,
	});
	Ext.getCmp('bench').add(ToDo);
	workbentch['ToDoTask'].apply();
	checkOnline();
}
function checkOnline(){
	Ext.Ajax.request({
		url: basePath + 'oa/attention/CheckISOnline.action',
		params: {
			emid:emid
		},
		method: 'POST',
		callback : function(options,success,response){
			var res = Ext.decode(response.responseText);
			var data = res.data;
			if(data=='YES'){
				Ext.getCmp('online').setValue('在线');
			}else { 
				Ext.getCmp('online').setValue('不在线');
			}
		}
	});
}
function openTable(id, caller, title, link, key, detailKey, condition){
	var main = parent.Ext.getCmp("content-panel");
	var panel = Ext.getCmp('' + id);
	var url = link;
	if(caller){
		panel = Ext.getCmp(caller);
		url = link + '?whoami=' + caller;
	}
	if(id){
		if(caller){
			panel = Ext.getCmp(caller + id);
			url = link + '?whoami=' + caller + '&formCondition=' + key + '=' + id + '&gridCondition=' + detailKey + '=' + id;
		}
	} else {
		if(condition != null){
			url += '&urlcondition=' + condition;
		}
	}
	if(!panel){ 
		panel = { 
				title : title.substring(0, title.toString().length > 5 ? 5 : title.toString().length),
				tag : 'iframe',
				tabConfig:{tooltip: title},
				frame : true,
				border : false,
				layout : 'fit',
				iconCls : 'x-tree-icon-tab-tab',
				html : '<iframe id="iframe" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
				closable : true,
				listeners : {
					close : function(){
						main.setActiveTab(main.getActiveTab().id); 
					}
				} 
		};
		openTab(panel, panel.id);
	}else{ 
		main.setActiveTab(panel); 
	} 
}
function openTab(panel, id){ 
	var o = (typeof panel == "string" ? panel : id || panel.id); 
	var main = parent.Ext.getCmp("content-panel");
	var tab = main.getComponent(o); 
	if (tab) { 
		main.setActiveTab(tab); 
	} else if(typeof panel!="string"){ 
		panel.id = o; 
		var p = main.add(panel); 
		main.setActiveTab(p); 
	} 
}
function taskItem(val, meta, record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['ra_id'] + ',\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\'' + ');">' + val + '</a>';
}
function Synergy(val,meta,record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['sy_id'] + ',\'Synergy\',\'协同\',\'jsps/oa/myProcess/synergy/newSynergy.jsp\',\'sy_id\',\'\'' + ');">' + val + '</a>';
}
function workrecord(val,meta,record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['wr_id'] + ',\'WorkRecord\',\'工作日报\',\'jsps/plm/record/recordlog.jsp\',\'wr_id\',\'\'' + ');">' + val + '</a>';
}
function getSurPlus(val, meta, record){
	if(record.data.ra_taskpercentdone == 100){
		return '<img src="' + basePath + 'resource/images/renderer/award2.png">' + '<span style="color:green;padding-left:2px">已完成</span>';
	} else {
		var bTime = new Date().getTime();
		var eTime = Ext.Date.parse(record.data['ra_enddate'].substring(0, 10) + ' 23:59:59','Y-m-d H:i:s').getTime();
		var allHour = (eTime - bTime)/(60*60*1000);
		if(allHour < 0){
			if(allHour < -24){
				val = "<font color =red>过期" + Math.floor(Math.abs(allHour)/24) + "天" + Math.floor(Math.abs(allHour)%24) + "小时";
			} else {
				val = "<font color =red>过期" + Math.floor(Math.abs(allHour)) + "小时";  					
			}
		} else {
			val = "<font color =blue>剩余" + Math.floor(allHour/24) + "天" + Math.floor(allHour%24) + "小时";
		}
		return val;
	}
}
function percentdone(val, meta, record){
	val == null || 0;
	if(val < 30)
		return '<img src="' + basePath + 'resource/images/renderer/remind2.png">'+'<span style="color:#436EEE;padding-left:2px">' + val + '</span>';
	else if(val > 30 && val < 50) 
		return '<img src="' + basePath + 'resource/images/renderer/remind.png">'+'<span style="color:#5F9EA0;padding-left:2px">' + val + '</span>';
	else if(val == 100){
		return '<img src="' + basePath + 'resource/images/renderer/award1.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
	}else if(val > 80){
		return '<img src="' + basePath + 'resource/images/renderer/award2.png">'+'<span style="color:green;padding-left:2px">' + val + '</span>';
	}
	else 
		return val;
}
function workplan(val,meta,record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['pl_id'] + ',\'Plan\',\'工作计划\',\'jsps/oa/persontask/workPlan/register.jsp\',\'pl_id\',\'\'' + ');">' + val + '</a>';
}
function showDialogBox(e,id, otherId, other, date, context){
	var panel = Ext.getCmp('dialog-win-' + otherId);
	if(!panel){
		panel = Ext.create('erp.view.core.window.DialogBox', {
			other: other,
			autoShow: false,
			otherId: otherId
		});
		panel.showAt(e.getXY());
	}
	if(!Ext.isEmpty(id)){
		panel.insertDialogItem(other, date, context);
		if(Ext.getCmp('dialog-min-' + otherId)){
			Ext.getCmp('dialog-min-' + otherId).setText("<font color=red>有新消息...</font>" );
		} else {
			updatePagingStatus(id, 1);
		}
	}
}

