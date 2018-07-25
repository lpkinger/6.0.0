var workbench = {
		width: 1/3,
		setWidth: function(width){
			this.width = width;
		},
		height: 1/3,
		setHeight: function(height){
			this.height = height;
		},
		bench_task: function(){
			var me = this.workbench || this;
			var notifycount=getNotifyCount();
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">待办事宜</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JprocessScan\',\'发起流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_launcherid=\\\'' + em_code + '\\\' and jp_status=\\\'待审批\\\'  '+'\');">已发起流程</a></div>' +
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JprocessScan\',\'处理流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\\\'' + em_code + '\\\' and jp_status<>\\\'待审批\\\'  '+'\'  );">已处理流程</a>|</div>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'TransferProcess\',\'转移流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jt_acceptercode=\\\'' + em_code + '\\\' and jp_flag = 1 and jp_status=\\\'待审批\\\'  '+'\'  );">待办转移流程</a>|</div>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JProcess\',\'待办流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\\\'' + em_code + '\\\' and jp_status=\\\'待审批\\\'  '+'\'  );">更多待办流程</a>|</div>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'ResourceAssignment!ALL\',\'提醒任务列表\',\'jsps/common/datalist.jsp\',null,null,\'ra_resourcecode=\\\'' + em_code + '\\\' and ra_statuscode<>\\\'FINISHED\\\'  '+'\'  );">更多提醒任务</a>|</div>'+
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'NotifyJprocess\',\'知会信息\',\'jsps/common/datalist.jsp\',null,null,\'prd_recipientid=session:em_uu\' );"><div id="messagecount">知会('+notifycount+')|</div></a>|</div>'+
				'<div class="div-right"><a class="x-btn-link" onclick="refreshTask();">刷新</a>|</div>',
				id: 'bench_task',
				iconCls: 'main-todo',
				columnWidth: me.width, 
				height: me.height,				
				layout:'anchor',
				items:[{
					xtype:'gridpanel',
					id:'task_grid',
					hideHeaders:true,
					store: Ext.create('Ext.data.Store', {
						fields:['id', 'name','status','type','typecode','CURRENTMASTER','note'],
						groupField: 'type'
					}),
					autoScroll: true,
				    cls: 'task-grid',
					anchor: '100% 100%',
					features: [ Ext.create('Ext.grid.feature.Grouping',{						    
				        groupHeaderTpl: '{name} (共 {rows.length}项)'
				    })],
				    loadNewStore:function(){
				    	me._bench_task();
				    },
				    viewConfig :{
				    stripeRows:false
				    },
					bodyStyle: 'background: #f1f1f1;border: none;',
					columns: [{ header: 'ID',  dataIndex: 'id', hidden: true},
					          { header: '事宜名称',  dataIndex: 'name', flex:1,renderer:taskItem, id: 'topic'},					        		        
					          { header: '当前状态', dataIndex: 'status',hidden: true},
					          {header: '事宜类型', dataIndex:'type',hidden: true},
					          {header:'事宜类型编码', dataIndex:'typecode',hidden: true},
					          {header:'备注信息', dataIndex:'note',hidden: true},
					          {header:'Master',dateIndex:'CURRENTMASTER',hidden:true }]
				}]
			});
		},
		bench_flow: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">我发起未结束流程</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JProcess!Me\',\'流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\'' + em_code + '\');">发布流程</a></div>' +
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JProcess!Me\',\'流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\'' + em_code + '\');">更多流程</a>|</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_flow',
				contentEl: 'myflow',
				iconCls: 'main-todo',
				autoScroll: true,
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_overflow: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">我的超时流程</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JProcess!MeOver\',\'流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\'' + em_code + '\');">发布流程</a></div>' +
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'JProcess!MeOver\',\'流程列表\',\'jsps/common/datalist.jsp\',null,null,\'jp_nodedealman=\'' + em_code + '\');">更多流程</a>|</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_overflow',
				contentEl: 'myoverflow',
				iconCls: 'main-todo',
				autoScroll: true,
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_schedule: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">我的考勤</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'Sign\',\'考勤\',\'jsps/common/datalist.jsp\',null,null,\'si_emid=' + em_uu + '\');">我的考勤</a></div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="alert(\'没有下属\');">下属考勤</a>|</div>',
				id: 'bench_schedule',
				iconCls: 'main-forget',
				layout: 'anchor',
				bodyStyle: 'background: #f1f1f1;',
				items: [{
					xtype: 'button',
					id: 'clock',
					iconCls: 'x-button-icon-clock',
					scale: 'large',
					anchor: '100% 33.33%'
				},{
					xtype: 'button',
					iconCls: 'x-button-icon-on',
					text: '',
					id: 'signin',
					signin: false,
					intime: null,
					tooltip: '',
					anchor: '100% 33.33%',
					scale: 'large',
					handler: function(btn){
						if(btn.signin == false){
							signin();
						}
					}
				},{
					xtype: 'button',
					iconCls: 'x-button-icon-off',
					hidden: true,
					text: '',
					signout: false,
					outtime: null,
					tooltip: '',
					id: 'signout',
					anchor: '100% 33.33%',
					scale: 'large',
					handler: function(btn){
						if(btn.signout == false){
							signout();
						}
					}
				}],
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_subscription: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">日程安排</div>' + 
				'<div class="div-right"><a href="' + basePath + 'jsps/plm/calendar/NewMyCalendar.jsp" target="_blank">详细日程</a></div>' + 
				'<div class="div-right"><a href="' + basePath + 'jsps/plm/calendar/NewMyCalendar.jsp" target="_blank">下属日程</a>|</div>',
				id: 'bench_subscription',
				layout: 'border',
				items: [{
					region: 'center',
					xtype: 'holidaydatepicker',
					autoShow: true
				}],
				iconCls: 'main-schedule',
				columnWidth: me.width, 
				height: me.height
			});	
		},
		bench_notify: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">通知公告</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(0,\'Note\',\'通知\',\'jsps/oa/info/Note.jsp\',\'no_id\',null' + ')">发布通知</a></div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'Note\',\'通知\',\'jsps/common/datalist.jsp\',null,null,null);">更多通知</a>|</div>',
				id: 'bench_notify',
				iconCls: 'main-msg',
				contentEl: 'notify',
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_news: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">新闻动态</div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(0,\'News\',\'新闻\',\'jsps/oa/news/News.jsp\',\'ne_id\',null' + ')">发布新闻</a></div>' + 
				'<div class="div-right"><a class="x-btn-link" onclick="openTable(null,\'News\',\'新闻\',\'jsps/common/datalist.jsp\',null,null,null);">更多新闻</a>|</div>',
				id: 'bench_news',
				iconCls: 'main-notice',
				contentEl: 'news',
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_link: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">常用模块</div>',
				id: 'bench_link',
				iconCls: 'main-news',
				columnWidth: me.width, 
				height: me.height,
				layout: 'anchor',
				items: [{
					xtype: 'gridpanel',
					id: 'link_grid',
					cls: 'custom-grid',
					columnLines : true,
					autoScroll: true,
					anchor: '100% 100%',
					store: Ext.create('Ext.data.Store', {
						fields:['cu_id', 'cu_description', 'cu_url', 'cu_count'],
						data: []
					}),
					bodyStyle: 'background: #f1f1f1;',
					columns: [{ 
						text: 'ID',  
						dataIndex: 'cu_id', 
						hidden: true
					},{ 
						text: '模块名',  
						dataIndex: 'cu_description', 
						flex: 1, 
						renderer: function(val, meta, record){
							var ht = "<a class=\"x-btn-link\" onclick=\"openTable(" 
								+ record.data['cu_id'] + ",null,\'" + val + "\',\'" + record.data['cu_url'].replace(/\'/g, '\\\'') + "\',null,null,null,null,true)\">" + val + "</a>";
							return ht;
						}
					},{ 
						text: '链接', 
						dataIndex: 'cu_url', 
						hidden: true 
					},{ 
						text: '次数', 
						dataIndex: 'cu_count', 
						hidden: true
					},{
						xtype: 'actioncolumn',
						text: '操作', 
						flex: 0.2,
						items: [{
							icon: basePath + 'resource/images/16/up.png',
							tooltip: '上',
							handler: function(view, rowIndex, colIndex) {
								var rec = view.getStore().getAt(rowIndex);
								view.ownerCt.updateBench(rec.get('cu_id'), 1);
							}
						},{
							icon: basePath + 'resource/images/16/down.png',
							tooltip: '下',
							handler: function(view, rowIndex, colIndex) {
								var rec = view.getStore().getAt(rowIndex);
								view.ownerCt.updateBench(rec.get('cu_id'), -1);
							}
						},{
							icon: basePath + 'resource/images/16/delete.png',
							tooltip: '删除',
							handler: function(view, rowIndex, colIndex) {
								var rec = view.getStore().getAt(rowIndex);
								_delete_bench_link(rec.get('cu_id'));
							}
						}]
					}],
					updateBench : function(id, t) {
						var g = this;
						g.setLoading(true);
						Ext.Ajax.request({
							url : basePath + 'common/updateCommonUse.action',
							params : {
								_noc : 1,
								id : id,
								type : t
							},
							callback : function(o, s, r) {
								g.setLoading(false);
								var rs = Ext.decode(r.responseText);
								if (rs.commonuse) {
									g.store.loadData(rs.commonuse);
								}
							}
						});
					}
				}]
			});
		},
		bench_note: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">我的知会</div>', 
				bodyStyle: 'background: #f1f1f1;',
				iconCls: 'main-notice',
			    id: 'bench_note',
				contentEl: 'note2',
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_mytask: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">我的任务</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_mytask',
				iconCls: 'main-news',
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_plan: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title:'<div class="div-left">工作计划</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_plan',
				contentEl: 'plan',
				iconCls: 'main-news',
				autoScroll: true,
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_email: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">我的邮箱</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_email',
				iconCls: 'main-news',
				columnWidth: me.width, 
				height: me.height
			});
		},
		bench_knowledge: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">知识地图</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_knowledge',
				iconCls: 'main-news',
				columnWidth: me.width, 
				height: me.height
			});	
		},
		bench_meeting: function(){
			var me = this.workbench || this;
			return Ext.create('Ext.panel.Panel', {
				title: '<div class="div-left">待开会议</div>',
				bodyStyle: 'background: #f1f1f1',
				id: 'bench_meeting',
				iconCls: 'main-news',
				columnWidth: me.width, 
				height: me.height
			});
		},
		/**
		 * 我发起的流程
		 */
		_bench_flow: function(){
			var me = this.workbench || this;
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'JProcess!Me',
					condition:  'jp_launcherid=\'' + em_code + '\'', 
					page: 1,
					pageSize: parseInt(height*0.3/12)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						if(err.indexOf('ERR_POWER') != -1) {
							Ext.get("myflow").insertHtml('afterBegin', 
							'<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(您无权限查看此信息)</div>');
						} else {
							showError(err);
						}
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task == [] || task.length == 0){
						Ext.get("myflow").insertHtml('afterBegin', '<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(暂无任务流程)</div>');
					} else {
						Ext.create('Ext.grid.Panel', {
							autoScroll: true,
							store: Ext.create('Ext.data.Store', {
								fields:['jp_id', 'jp_form', 'jp_launchTime','enddate', 'jt_duedate','jp_nodeId'],
								data: task
							}),
							height: me.height*0.76,
							bodyStyle: 'background: #f1f1f1;border: none;',
							columns: [
							          { header: 'ID',  dataIndex: 'jp_id', hidden: true},
							          { header: '流程单据',  dataIndex: 'jp_form', flex: 1.5 , renderer: flowItem},
							          { header: '限办时间', dataIndex: 'enddate', flex: 1.5, renderer : getDueDate},
							          { header: 'nodeId',  dataIndex: 'jp_nodeId', hidden: true}
							          ],
							          renderTo: Ext.get("myflow")				        	
						});
					}
				}
			});
		},
		/**
		 * 我的超时流程
		 */
		_bench_overflow: function(){
			var me = this.workbench || this;
			/*Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'JProcess!MeOver',
					condition:  'jp_nodedealman=\'' + em_code + '\'', 
					page: 1,
					pageSize: parseInt(height*0.3/12)
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						if(err.indexOf('ERR_POWER') != -1) {
							Ext.get("myoverflow").insertHtml('afterBegin', 
							'<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(您无权限查看此信息)</div>');
						} else {
							showError(err);
						}
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task == [] || task.length == 0){
						Ext.get("myoverflow").insertHtml('afterBegin', '<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(暂无超时流程)</div>');
					} else {
						Ext.create('Ext.grid.Panel', {
							autoScroll: true,
							store: Ext.create('Ext.data.Store', {
								fields:['jp_id', 'jp_form', 'jp_launchTime','enddate', 'jt_duedate','jp_nodeId'],
								data: task
							}),
							height: me.height*0.76,
							bodyStyle: 'background: #f1f1f1;border: none;',
							columns: [
							          { header: 'ID',  dataIndex: 'jp_id', hidden: true},
							          { header: '流程单据',  dataIndex: 'jp_form', flex: 1.5 , renderer: flowOverItem},
							          { header: '限办时间', dataIndex: 'enddate', flex: 1.5, renderer : getDueDate},
							          { header: 'nodeId',  dataIndex: 'jp_nodeId', hidden: true}
							          ],
							          renderTo: Ext.get("myoverflow")				        	
						});
					}
				}
			});*/
		},
		/**
		 * 待办事宜	
		 */
		_bench_task: function(){
			var grid = Ext.getCmp('bench_task').down('gridpanel');
			grid.store.removeAll();
			if(em_code==null) em_code=getCookie('em_code');
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'JProCand',
					condition:  'jp_candidate=\'' + em_code + '\'  AND jp_status=\'待审批'+'\' AND jp_flag=1', 
					page: 1,
					pageSize: 20,
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						showError(err);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task.length > 0){
						var items = new Array();
						Ext.Array.each(task,function(item){
							item.id=item.jp_nodeId;
							item.name=item.jp_name+" "+item.jp_codevalue+"("+item.jp_launcherName+")";
							item.note=item.jp_processnote;
							item.note=item.jp_processnote;
							item.status=item.jp_status;
							item.type='可选流程';
							item.typecode='procand';
							items.push(item);
						});
						grid.store.add(items);
					}
				}
			});		
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'JProcess!Me',
					condition:  '(jp_nodedealman=\'' + em_code + '\'  AND jp_status=\'待审批'+'\') or (jp_launcherid=\'' + em_code + '\'  AND jp_status=\'未通过'+'\') ', 
					page: 1,
					pageSize:20,
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						showError(err);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task.length > 0){
						var items = new Array();
						Ext.Array.each(task,function(item){
							item.id=item.jp_nodeId;
							item.name=item.jp_name+" "+item.jp_codevalue +"("+item.jp_launcherName+")";
							item.note=item.jp_processnote;
							item.status=item.jp_status;
							if(!item.source ||  item.source==0){
							if(item.jp_status=='未通过'){
								item.type='未同意流程';
								item.typecode='unprocess';
							}else{
								item.type='待审批流程';
								item.typecode='process';
							}
							}else {
								item.type='待办转移流程';
								item.typecode='transferprocess';
							}
							items.push(item);
						});		        
						grid.store.add(items);
					}
				}
			});
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'ResourceAssignment',
					condition : '(ra_emid=\'' + em_uu + '\'  AND ra_statuscode<>\'FINISHED'+'\') or (recorder=\'' + em_name + '\'  AND ra_statuscode=\'UNCONFIRMED'+'\') ',
					page: 1,
					pageSize: 20,
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						showError(err);
						return;
					}
					var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
					if(task.length > 0){
						var items = new Array();
						Ext.Array.each(task,function(item){
							item.id=item.ra_id;
							item.name=item.ra_taskname + (item.sourcecode || '');
							item.status=item.ra_status;
							item.type='待办任务';
							item.typecode=(item.ra_type || 'worktask');
							item.sourcelink=item.sourcelink;
							item.taskId=item.ra_taskid;
							items.push(item);
						});
						grid.store.add(items);
					}
				}
			});
		},
		_bench_news: function(){
			var me = this.workbench || this;
			me.getCmpNews();
		},
		/**
		 * 内部新闻	
		 */
		getCmpNews: function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'News',
					condition: '',
					page: 1,
					pageSize: parseInt(height*0.25/23),
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						if(err.indexOf('ERR_POWER') != -1) {
							Ext.get("news").insertHtml('afterBegin', 
							'<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(您无权限查看此信息)</div>');
						} else {
							showError(err);
						}
						return;
					}
					var news = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
					/*Ext.Array.sort(news, function(a, b){
						return b.ne_releasedate > a.ne_releasedate;
					});*/
					var html = '<ul class="list-default">';
					Ext.each(news, function(n){
						var bTime = new Date().getTime();
						var eTime = Ext.Date.parse(n.ne_releasedate.substring(0, 10) + ' 23:59:59','Y-m-d H:i:s').getTime();
						var allHour = (eTime - bTime)/(60*60*1000);				    	  			
						if(allHour<-72){
							html += '<li>' + n.ne_releasedate.substring(0, 10) + '&nbsp;<img src="' + basePath + 
							'resource/images/mainpage/new.png"/>&nbsp;<a class="x-btn-link" onclick="openTable(null,null,\'新闻\',\'oa/news/view.action?ne_id=' + n.ne_id + '\',\'ne_id\',null' + ')">' 
							+ n.ne_theme + '</a></li>';	
						} else{html += '<li>' + n.ne_releasedate.substring(0, 10) + '&nbsp;<img src="' + basePath + 
							'resource/images/mainpage/new.png"/>&nbsp;<a class="x-btn-link" onclick="openTable(null,null,\'新闻\',\'oa/news/view.action?ne_id=' + n.ne_id + '\',\'ne_id\',null' + ')">' 
							+ n.ne_theme + '</a><img src="' + basePath + 
							'resource/images/newitem.gif" /></li>';}
					});
					html += '</ul>';
					Ext.get("news").insertHtml('afterBegin', html);
				}
			});
		},
		_bench_note:function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'NotifyJprocess',
					condition: 'prd_recipientid='+em_uu,
					page: 1,
					pageSize: parseInt(height*0.25/23),
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					Ext.get('note2').dom.innerHTML='';
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						if(err.indexOf('ERR_POWER') != -1) {
							Ext.get("note2").insertHtml('afterBegin', 
							'<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(您无权限查看此信息)</div>');
						} else {
							showError(err);
						}
						return;
					}
					var notes = res.data!=null?Ext.decode(res.data):[];
					var html = '<ul>';
					Ext.each(notes, function(n,index){					
						if(index<3){
							html += '<li>' +formatNote(n.PR_CONTEXT)+'<img src="' + basePath + 'resource/images/newitem.gif" /></li>';		
						} else{html += '<li>' +formatNote(n.PR_CONTEXT)+'</li>';}
					});
					html += '</ul>';
					Ext.get("note2").insertHtml('afterBegin', html);
				}
			});
		},
		/**
		 * 通知公告
		 */
		_bench_notify: function(){
			Ext.Ajax.request({
				url : basePath + 'common/datalist.action',
				params: {
					caller: 'Note',
					condition:" no_approver=\'"+em_name+"\' or no_ispublic=-1 or (no_ispublic=0 and (no_recipientid like '%org#'||\'"+em_defaultorid+"\'||'%' or no_recipientid like '%job#'||\'"+em_defaulthsid+"\'||'%' or no_recipientid like '%employee#'||\'"+em_id+"\'||'%'))",
					page: 1,
					pageSize:10,
					_noc:1
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						var err = res.exceptionInfo;
						if(err.indexOf('ERR_POWER') != -1) {
							Ext.get("notify").insertHtml('afterBegin', 
							'<div style="color:gray;font-size:26px; line-height: 60px;" align="center">(您无权限查看此信息)</div>');
						} else {
							showError(err);
						}
						return;
					}
					var notes = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
					/*Ext.Array.sort(notes, function(a, b){
						return b.no_apptime > a.no_apptime;
					});*/
					var height=Ext.getCmp('bench_notify').height;
					var html = '<div id="notifydata" style="font-size:12px;height:'+(height-45)+'px;overflow:hidden;"><ul class="list-default">';
					var count=0;
					Ext.each(notes, function(n){
						if(n.CURRENTMASTER&&n.CURRENTMASTER!='DataCenter'){
						html += '<li style="height:24px;line-height:24px;">&nbsp;<img src="' + basePath + 
						'resource/images/mainpage/blogs.png"/>&nbsp;<a class="x-btn-link" onclick="openTable(' 
						+ n.no_id + ',\'Note\',\'通知\',\'jsps/oa/info/NoteR.jsp\',\'no_id\',null,null,' +'\''+n.CURRENTMASTER+'\''+ ')">' 
						+ Ext.String.charEllipsis(n.CURRENTMASTER+"-"+n.no_title, 80) + '</a>';
						}else html += '<li style="height:24px;line-height:24px;">&nbsp;<img src="' + basePath + 
						'resource/images/mainpage/blogs.png"/>&nbsp;<a class="x-btn-link" onclick="openTable(' 
						+ n.no_id + ',\'Note\',\'通知\',\'jsps/oa/info/NoteR.jsp\',\'no_id\',null'+ ')">' 
						+ Ext.String.charEllipsis(n.no_title, 50) + '</a>';
						if(count<4){
							html +='&nbsp;<img src="' + basePath + 'resource/images/mainpage/new.png"/></li>';
						}else html +='</li>';
					  count++;
					});
					if(notes.length == 0){
						html = '<ul style="text-align:center;padding-top: 30px;color:#888">没有记录';
					}
					html += '</ul></div>';
					Ext.get("notify").insertHtml('afterBegin', html);
					/*var $=function $(element){
						 if(arguments.length>1){
						  for(var i=0,length=arguments.length,elements=[];i<length;i++){
						   elements.push($(arguments[i]));
						  }
						  return elements;
						 }
						 if(typeof element=="string"){
						  return document.getElementById(element);
						 }else{
						  return element;
						 }
						};
						var Class={
						 create:function(){
						  return function(){
						   this.initialize.apply(this,arguments);
						  };
						 }
						};
						Function.prototype.bind=function(object){
						 var method=this;
						 return function(){
						  method.apply(object,arguments);
						 };
						};
						var Scroll=Class.create();
						Scroll.prototype={
						 initialize:function(element,height){
						  this.element=$(element);
						  this.element.innerHTML+=this.element.innerHTML;
						  this.height=height;
						  this.maxHeight=this.element.scrollHeight/2;
						  this.counter=0;
						  this.scroll();
						  this.timer="";
						  this.element.onmouseover=this.stop.bind(this);
						  this.element.onmouseout=function(){this.timer=setTimeout(this.scroll.bind(this),1000);}.bind(this);
						 },
						 scroll:function(){
						  if(this.element.scrollTop<this.maxHeight){
						   this.element.scrollTop++;
						   this.counter++;
						  }else{
						   this.element.scrollTop=0;
						   this.counter=0;
						  }
						  if(this.counter<this.height){
						   this.timer=setTimeout(this.scroll.bind(this),20);
						  }else{
						   this.counter=0;
						   this.timer=setTimeout(this.scroll.bind(this),3000);
						  }
						 },
						 stop:function(){
						  clearTimeout(this.timer);
						 }
						};
						if(count*24>(height-45)) {setTimeout(function scroll(){
							new Scroll("notifydata",24);
						     },8000);
						}*/
						
				}
			});
		},
		/**
		 * 常用模块
		 */
		_bench_link: function(){
			var me = this.workbench || this;
			Ext.Ajax.request({
				url : basePath + 'common/getCommonUse.action',
				method : 'get',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					window._delete_bench_link = me._delete_bench_link;
					Ext.getCmp('bench_link').down('gridpanel').store.loadData(res.commonuse);
				}
			});
		},
		/**
		 * 设置常用模块
		 */
		_set_bench_link: function(snid){
			Ext.Ajax.request({
				url : basePath + 'common/setCommonUse.action',
				params: {
					snid: snid
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					if(Ext.isIE) {
						setTimeout(function(){
							Ext.getCmp('link_grid').store.loadData(res.commonuse);
						}, 8000);
					} else {
						Ext.getCmp('link_grid').store.loadData(res.commonuse);
					}
				}
			});
		},
		_delete_bench_link: function(id){
			Ext.Ajax.request({
				url : basePath + 'common/deleteCommonUse.action',
				params: {
					id: id
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					var grid = Ext.getCmp('link_grid');
					Ext.each(grid.store.data.items, function(item){
						if(item.data['cu_id'] == id){
							grid.store.remove(item);
						}
					});
				}
			});
		},
		/**
		 * 查看我的考勤
		 */
		_bench_schedule: function(){
			Ext.Ajax.request({
				url : basePath + 'oa/getMySign.action',
				async: false,
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exception || res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					if(res.sign != null){
						Ext.getCmp('signin').signin = true;
						var d = new Date(res.sign.si_in);
						Ext.getCmp('signin').intime = res.sign.si_in;
						var sin = Ext.Date.parse(Ext.Date.toString(new Date()) + ' 09:00:00', 'Y-m-d H:i:s').getTime();
						if(d > sin){
							Ext.getCmp('signin').tooltip = "签到时间:<font color=red>" + Ext.Date.format(d, 'H:i:s') + 
							"&nbsp;&nbsp;迟到" + Math.floor((d - sin)/(60*60*1000)) + "小时" + Math.floor(((d - sin)%(60*60*1000))/(60*1000)) + "分钟" + "</font>";
						} else {
							Ext.getCmp('signin').tooltip = "签到时间:" + Ext.Date.format(d, 'H:i:s');	
							Ext.getCmp('signin').setIconCls('x-button-icon-working');
						}
						if(res.sign.si_out) {
							d = new Date(res.sign.si_out);
							sin = Ext.Date.parse(Ext.Date.toString(new Date()) + ' 18:00:00', 'Y-m-d H:i:s').getTime();
							Ext.getCmp('signout').signout = true;
							if(d < sin) {
								Ext.getCmp('signout').setText("签退时间:<font color=red>" + Ext.Date.format(d, 'H:i:s') + 
										"&nbsp;&nbsp;早退" + Math.floor((sin - d)/(60*60*1000)) + "小时" + Math.floor(((sin - d)%(60*60*1000))/(60*1000)) + "分钟" + "</font>");
							} else {
								Ext.getCmp('signout').setText("签退时间:" + Ext.Date.format(d, 'H:i:s'));	
							}
						}
						Ext.getCmp('signout').show();
					}
				}
			});
			showClock();
		},
		refreshTask:refreshTask
};
/**
 * 刷新待办事宜
 * */
function refreshTask(onlyProcess){
	var grid = Ext.getCmp('bench_task').down('gridpanel');
	grid.store.removeAll();
	Ext.Ajax.request({
		url : basePath + 'common/datalist.action',
		params: {
			caller: 'JProCand',
			condition:  'jp_candidate=\'' + em_code + '\'  AND jp_status=\'待审批'+'\' AND jp_flag=1', 
			page: 1,
			pageSize: 20,
			_noc:1
		},
		method : 'post',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				var err = res.exceptionInfo;
				showError(err);
				return;
			}
			var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
			if(task.length > 0){
				var items = new Array();
				Ext.Array.each(task,function(item){
					item.id=item.jp_nodeId;
					item.name=item.jp_name+" "+item.jp_codevalue+"("+item.jp_launcherName+")";
					item.note=item.jp_processnote;
					item.status=item.jp_status;
					item.type='可选流程';
					item.typecode='procand';
					items.push(item);
				});
				grid.store.add(items);
			}
		}
	});		
	Ext.Ajax.request({
		url : basePath + 'common/datalist.action',
		params: {
			caller: 'JProcess!Me',
			condition:  '(jp_nodedealman=\'' + em_code + '\'  AND jp_status=\'待审批'+'\') or (jp_launcherid=\'' + em_code + '\'  AND jp_status=\'未通过'+'\') ', 
			page: 1,
			pageSize:20,
			_noc:1
		},
		method : 'post',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				var err = res.exceptionInfo;
				showError(err);
				return;
			}
			var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
			if(task.length > 0){
				var items = new Array();
				Ext.Array.each(task,function(item){
					item.id=item.jp_nodeId;
					item.name=item.jp_name+" "+item.jp_codevalue +"("+item.jp_launcherName+")";
					item.note=item.jp_processnote;
					item.status=item.jp_status;
					if(!item.source ||  item.source==0){
					if(item.jp_status=='未通过'){
						item.type='未同意流程';
						item.typecode='unprocess';
					}else{
						item.type='待审批流程';
						item.typecode='process';
					}
					}else {
						item.type='待办转移流程';
						item.typecode='transferprocess';
					}
					items.push(item);
				});		        
				grid.store.add(items);
			}
		}
	});
	if(!onlyProcess){
	Ext.Ajax.request({
		url : basePath + 'common/datalist.action',
		params: {
			caller: 'ResourceAssignment',
			condition : '(ra_emid=\'' + em_uu + '\'  AND ra_statuscode<>\'FINISHED'+'\') or (recorder=\'' + em_name + '\'  AND ra_statuscode=\'UNCONFIRMED'+'\') ',
			page: 1,
			pageSize: 20,
			_noc:1
		},
		method : 'post',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				var err = res.exceptionInfo;
				showError(err);
				return;
			}
			var task = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : new Array();
			if(task.length > 0){
				var items = new Array();
				Ext.Array.each(task,function(item){
					item.id=item.ra_id;
					item.name=item.ra_taskname + (item.sourcecode || '');
					item.status=item.ra_status;
					item.type='待办任务';
					item.typecode=(item.ra_type || 'worktask');
					item.sourcelink=item.sourcelink;
					item.taskId=item.ra_taskid;
					items.push(item);
				});
				grid.store.add(items);
			}
		}
	});
	}
	getNotifyCount();
}
function getNotifyCount(){
	var count=0;
	Ext.Ajax.request({
		url : basePath + 'common/getCountByTable.action',
		params: {
			tablename: 'pagingrelease left join pagingreleasedetail on pr_id=prd_prid',
			condition:  "prd_recipientid=" + em_uu+"  and  prd_readstatus=0 and nvl(pr_from,' ')='jprocess'", 
			_noc:1
		},
		method : 'post',
		async:false,
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				var err = res.exceptionInfo;
				showError(err);
				return;
			}
		  count=res.count;
		}
	});
	var countinfo=document.getElementById('messagecount');
	if(countinfo){
		countinfo.innerHTML='知会('+count+")"; 
	}
	return count;
}
/**
 * 打开一个tab
 * @param id 当前record的ID
 * @param caller 
 * @param title 标题
 * @param link 链接
 * @param key 主表ID字段
 * @param detailKey 从表关联主表ID的字段
 * @param condition 附加条件
 */
function openTable(id, caller, title, link, key, detailKey, condition,relateMaster,limit){
	var main = parent.Ext.getCmp("content-panel");
	var item=main.items.items[0];
	item.firstGrid=Ext.getCmp('task_grid');
	var panel = Ext.getCmp('' + id);
	var url = parseUrl(link);
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
			url += '&urlcondition=' + parseUrl(condition);
		}
	}
	if(!limit){
    if(url.indexOf('?') > 0)
		url += '&_noc=1';
	else
		url += '?_noc=1';
	}
	if(relateMaster){
		url+='&newMaster='+relateMaster;
	}
	if( relateMaster ){
		var currentMaster = parent.window.sob;
		if ( currentMaster && currentMaster != relateMaster) {// 与当前账套不一致
			if (parent.Ext) {
	    		Ext.Ajax.request({
					url: basePath + 'common/changeMaster.action',
					params: {
						to: relateMaster
					},
					callback: function(opt, s, r) {						
						if (s) {
							url+='&_center=1';
							var localJson = new Ext.decode(r.responseText);
							var win = parent.Ext.create('Ext.Window', {
				    			width: '100%',
				    			height: '100%',
				    			draggable: false,
				    			closable: false,
				    			modal: true,
				    			title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
				    			id:'modalwindow',
				    			historyMaster:currentMaster,
				    			relateMaster:relateMaster,
				    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
				    			buttonAlign: 'center',
				    			buttons: [{
									text: $I18N.common.button.erpCloseButton,
									cls: 'x-btn-blue',
									id: 'close',
									handler: function(b) {
										Ext.Ajax.request({
											url: basePath + 'common/changeMaster.action',
											params: {
												to: currentMaster
											},
											callback: function(opt, s, r) {
												if (s) {
													b.up('window').close();
												} else {
													alert('切换到原账套失败!');
												}
											}
										});
									}
								}]
				    		});
							win.show();
						} else {
							alert('无法创建到账套' + relateMaster + '的临时会话!');
						}
					}
				});
	    	}
			return;
		}
	}
	if(!panel){ 
		panel = { 
				title : title.substring(0, title.toString().length > 5 ? 5 : title.toString().length),
				tag : 'iframe',
				tabConfig:{tooltip: title},
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
function parseUrl(url) {
	var id = url.substring(url.lastIndexOf('?')+1);
	if (id == null) {
		id = url.substring(0,url.lastIndexOf('.'));
	}
	if(contains(url, 'session:em_uu', true)){
		url = url.replace(/session:em_uu/,em_uu);
	}
	if(contains(url, 'session:em_code', true)){
		url = url.replace(/session:em_code/, "'" + em_code + "'");
	}
	if(contains(url, 'sysdate', true)){
		url = url.replace(/sysdate/, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
	}
	if(contains(url, 'session:em_name', true)){
		url = url.replace(/session:em_name/,"'"+em_name+"'" );
	}
	return url;
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
	var rendermsg='';
	if(record.data.CURRENTMASTER){
	   if(record.data.typecode=='worktask' || record.data.typecode=='projecttask'){
		   rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\',null,' +'\''+record.data.CURRENTMASTER+'\''+ ');">' + val + '</a>';
	   } else if(record.data.typecode=='billtask' || record.data.typecode=='mrptask'){
		   rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,\''+record.data.CURRENTMASTER+'\');">' + val + '</a>';
	   } else if(record.data.typecode=='kbitask' || record.data.typecode=='mrptask'){
		   rendermsg='<a class="x-btn-link" onclick="openUrl(' +'\''+record.data['sourcelink'] + '\',\''+record.data.CURRENTMASTER+'\');">' + val + '</a>';
	   }else if(record.data.typecode=='process'||record.data.typecode=='unprocess' || record.data.typecode=='transferprocess') return '<a href="javascript:openTable(' + record.data['id'] + ',\'JProcess!Me\',\'任务流程\',\'jsps/common/jprocessDeal.jsp\',\'jp_nodeId\',null,null,' +'\''+record.data.CURRENTMASTER+'\'' +');">' + val + '</a>';
	   else rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'JProCand\',\'任务流程\',\'jsps/common/jtaketask.jsp\',\'jp_nodeId\',jp_flag=1,null,' +'\''+record.data.CURRENTMASTER+'\''+');">' + val + '</a>';
	} else {
		if(record.data.typecode=='worktask' || record.data.typecode=='projecttask'){
			rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\'' + ');">' + val + '</a>';
		} else if(record.data.typecode=='billtask' || record.data.typecode=='mrptask'){
			rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null);">' + val + '</a>';
		}  else if(record.data.typecode=='communicatetask'){
			rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['taskId'] + ',\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null);">' + val + '</a>';
		} else if(record.data.typecode=='kbitask' ){
			rendermsg='<a class="x-btn-link" onclick="openUrl(' +'\''+record.data['sourcelink'] + '\',\''+record.data.CURRENTMASTER+'\');">' + val + '</a>';
		}else if(record.data.typecode=='process'||record.data.typecode=='unprocess' || record.data.typecode=='transferprocess') {
			rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'JProcess!Me\',\'任务流程\',\'jsps/common/jprocessDeal.jsp\',\'jp_nodeId\',null' + ');" target="_blank">' + val + '</a>';
			if(record.data.note && record.data.note!=null && record.data.note !='null' ){
				rendermsg+='</br><font color="#669933" >'+record.data.note+'</font>';
			}
		}
		else rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['id'] + ',\'JProCand\',\'任务流程\',\'jsps/common/jtaketask.jsp\',\'jp_nodeId\',jp_flag=1' + ');">' + val + '</a>';
	}
	if(record.data.RN && record.data.RN==1){
	     return rendermsg+'&nbsp;<img src="' + basePath + 'resource/images/mainpage/new.png"/>';
	}else return rendermsg;
}
function showMaster(val,meta,record){
	if(record.data.CURRENTMASTER){
		return val+'-<span style="color:red;">' + record.data.CURRENTMASTER + '</span>';
	}else return val;
}
function flowItem(val, meta, record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['jp_nodeId'] + ',\'JProcess!Me\',\'任务流程\',\'jsps/common/jprocessDeal.jsp\',\'jp_nodeId\',null' + ');">' + val + '</a>';
}
function flowOverItem(val, meta, record){
	return '<a class="x-btn-link" onclick="openTable(' + record.data['jp_nodeId'] + ',\'JProcess!MeOver\',\'任务流程\',\'jsps/common/jprocessDeal.jsp\',\'jp_nodeId\',null' + ');">' + val + '</a>';
}
function getSurPlus(val, meta, record){
	if(Ext.isEmpty(val))
		return '--';
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
function showClock(){
	var now = new Date();
	var html = '';  
	var hours = now.getHours();
	var minutes = now.getMinutes();  
	var seconds = now.getSeconds();
	if(hours < 9 && Ext.getCmp('signin').signin == false){
		Ext.getCmp('signin').setText("签到&nbsp;&nbsp;剩余" + (8 - hours) + "小时" + (60 - minutes) + "分钟" + (60 - seconds) + "秒");
	} else {
		if (Ext.getCmp('signin').signin == true) {
			var intime = Number(Ext.getCmp('signin').intime);
			if(intime && intime > 0) {
				var time = now.getTime();
				var h = Math.floor((time - intime)/(60*60*1000));
				var m = Math.floor(((time - intime)%(60*60*1000))/(60*1000));
				var s = Math.floor(((time - intime)%(60*1000))/(1000));
				Ext.getCmp('signin').setText(Ext.getCmp('signin').tooltip + "<br/>&nbsp;<font color=blue>已工作" + (h) + "小时" + (m) + "分钟" + (s) + "秒</font>");
			} else {
				Ext.getCmp('signin').setText(Ext.getCmp('signin').tooltip);
			}
			if(Ext.getCmp('signout').signout == false) {
				if(hours < 18){
					Ext.getCmp('signout').setText("签退<br/>&nbsp;<font color=blue>距离下班" + (18 - hours) + "小时" + (60 - minutes) + "分钟" + (60 - seconds) + "秒</font>");
				} else {
					Ext.getCmp('signout').setText("签退<br/>&nbsp;<font color=blue>下班时间已到</font>");
				}
			}
		} else {//还没签到
			Ext.getCmp('signin').setText("签到&nbsp;&nbsp;<font color=red>迟到" + (hours - 9) + "小时" + (minutes) + "分钟" + (seconds) + "秒</font>");					
		}
	}
	if(hours <= 9){
		hours = '0' + hours;
	} 
	if(minutes <= 9){
		minutes = '0' + minutes;
	}
	if(seconds < 10){
		seconds = '0' + seconds;
	}  
	var array = Ext.Array.toArray('' + hours + '-' + minutes + '-' + seconds);
	Ext.each(array, function(n){
		if (n == '-') {
			html += '<a class="number" style="background-position: -' + 24*10 + 'px -0px"></a>';
		} else {
			html += '<a class="number" style="background-position: -' + 24*n + 'px -0px"></a>';
		}
	});  
	Ext.getCmp('clock').setText(html);
	setTimeout("showClock();", 1000);
}
function signin(){
	var hours = new Date().getHours();
	if(hours >= 9){
		Ext.MessageBox.prompt("原因", "请如实填写迟到原因", function (btn, text) { 
			if(btn == 'ok'){
				_signin(text);
			}    
		}, this, true, '睡觉睡过头了!');
	} else {
		_signin(null);
	}
}
function _signin(reason){
	Ext.Ajax.request({
		url : basePath + 'oa/signin.action',
		method : 'post',
		async: false,
		params: {
			reason: reason
		},
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				showError(res.exceptionInfo);
				return;
			}
			if(res.success){
				Ext.getCmp('signin').signin = true;
				Ext.getCmp('signout').show();
				var d = new Date();
				Ext.getCmp('signin').intime = new Date().getTime();
				var sin = Ext.Date.parse(Ext.Date.toString(new Date()) + ' 09:00:00', 'Y-m-d H:i:s').getTime();
				if(d > sin) {
					Ext.getCmp('signin').setText("签到时间:<font color=red>" + Ext.Date.format(d, 'H:i:s') + 
							"&nbsp;&nbsp;迟到" + Math.floor((d - sin)/(60*60*1000)) + "小时" + Math.floor(((d - sin)%(60*60*1000))/(60*1000)) + "分钟" + "</font>");
					Ext.getCmp('signin').tooltip = "签到时间:<font color=red>" + Ext.Date.format(d, 'H:i:s') + 
					"&nbsp;&nbsp;迟到" + Math.floor((d - sin)/(60*60*1000)) + "小时" + Math.floor(((d - sin)%(60*60*1000))/(60*1000)) + "分钟" + "</font>";
				} else {
					Ext.getCmp('signin').setText("签到时间:" + Ext.Date.format(d, 'H:i:s'));
					Ext.getCmp('signin').tooltip = "签到时间:" + Ext.Date.format(d, 'H:i:s');	
				}
				Ext.getCmp('signin').setIconCls('x-button-icon-working');
				alert("签到时间:" + Ext.Date.format(d, 'Y-m-d H:i:s'));
			}
		}
	});
}
function signout(){
	var sout = Ext.Date.parse(Ext.Date.toString(new Date()) + ' 18:00:00', 'Y-m-d H:i:s');
	if(sout > new Date()){
		Ext.MessageBox.prompt("原因", "请如实填写早退原因", function (btn, text) { 
			if(btn == 'ok'){
				_signout(text);
			}    
		}, this, true, '太累了!');
	} else {
		_signout(null);
	}
}
function _signout(reason) {
	Ext.Ajax.request({
		url : basePath + 'oa/signout.action',
		method : 'post',
		params: {
			reason: reason
		},
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exception || res.exceptionInfo){
				showError(res.exceptionInfo);
				return;
			}
			if(res.success){
				Ext.getCmp('signout').signout = true;
				var d = new Date();
				var sin = Ext.Date.parse(Ext.Date.toString(new Date()) + ' 18:00:00', 'Y-m-d H:i:s').getTime();
				if(d < sin) {
					Ext.getCmp('signout').setText("签退时间:<font color=red>" + Ext.Date.format(d, 'Y-m-d H:i:s') + 
							"&nbsp;&nbsp;早退" + Math.floor((sin - d)/(60*60*1000)) + "小时" + Math.floor(((sin - d)%(60*60*1000))/(60*1000)) + "分钟" + "</font>");
				} else {
					Ext.getCmp('signout').setText("签退时间:" + Ext.Date.format(d, 'Y-m-d H:i:s'));	
				}
				alert("签退时间:" + Ext.Date.format(d, 'Y-m-d H:i:s'));
			}
		}
	});
}
function getMyBench(){
	Ext.Ajax.request({
		url : basePath + 'common/getWorkBench.action',
		method : 'get',
		callback : function(options,success,response){
			var res = new Ext.decode(response.responseText);
			if(res.exceptionInfo) {
				showError(res.exceptionInfo);
			} else {
				var bench = res.benchs;
				if(bench.length == 0){
					bench = getDefaultBench();
				}
				var e = Ext.getCmp('bench').getEl().dom,
				h = parseInt(Number(e.style.height.replace('px', ''))/3) - 1;
				Ext.each(bench, function(b){
					workbench.setWidth(b.wb_width);
					if(b.wb_height > 0.6) {
						workbench.setHeight(h*2);
					} else {
						workbench.setHeight(h);
					}
					var panel = workbench[b.wb_name].apply();
					Ext.getCmp('bench').add(panel);
				});
				Ext.each(bench, function(b){
					if(workbench["_" + b.wb_name]){
						workbench["_" + b.wb_name].apply();
					}
				});
			}
		}
	});
}
function getDefaultBench(){
	return [{
		wb_name: 'bench_task',
		wb_width: 2/3,
		wb_height: 2/3
	},{
		wb_name: 'bench_link',
		wb_width: 1/3,
		wb_height: 2/3
	},{
		wb_name: 'bench_notify',
		wb_width: 1/3,
		wb_height: 1/3
	},{
		wb_name: 'bench_news',
		wb_width: 1/3,
		wb_height: 1/3
	},{
		wb_name:'bench_subscription',
		wb_width: 1/3,
		wb_height: 1/3
	}];
}
function getDueDate(val, meta, record){
	var endDate = '';
	if(record.data['jt_duedate'] == 0){
		return "无限时";
	} else{
		endDate = Ext.Date.format(new Date(Ext.Date.parse(record.data['jp_launchTime'], 'Y-m-d H:i:s')
				.getTime() + record.data['jt_duedate']*3600), 'Y-m-d H:i:s'); 
	}
	return endDate;
}
function formatNote(context){
	context=context.replace(/<\s*\/?br>/g, " ");
	context=context.replace('你有新流程需处理!'," ");
	context=context.replace(/&nbsp;&nbsp;&nbsp;&nbsp;/g," ");
	return context;
}
function getDefaultShort(){
	return [{
		sc_name: 'short_setting'
	},{
		sc_name: 'short_news'
	},{
		sc_name: 'short_address'
	},{
		sc_name: 'short_calendar'
	},{
		sc_name: 'short_email'
	},{
		sc_name: 'short_bbs'
	}];
}