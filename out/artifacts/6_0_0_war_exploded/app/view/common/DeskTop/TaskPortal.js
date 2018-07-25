Ext.require([
    'Ext.ux.PreviewPlugin'
]);
Ext.define('erp.view.common.DeskTop.TaskPortal',{ 
	extend: 'erp.view.common.DeskTop.Portlet', 
	alias: 'widget.taskportal',
	title: '<div class="div-left">待办任务：</div><button class="fa-tab-btn fa-tab-btn-active">待处理</button><button class="fa-tab-btn">已下达</button><button class="fa-tab-btn">已处理</button>',
	enableTools:true,
	cls:'task-portal',
	iconCls: 'main-schedule',
	animCollapse: false,
	pageCount:10,
	activeRefresh:true,
	//activeRefresh:true,
	initComponent : function(){
		var me=this;
		Ext.apply(this,{
			items:[Ext.widget('tabpanel',{
				autoShow: true, 
				tabPosition:'top',
				minHeight:200,
				frame:true,
				bodyBorder: false,
				border: false,
				items:[me._toDo(),me._alreadyLaunch(),me._alreadyDone()]
			})]
		});
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function() {
			var me = this;
			var buttons = me.el.dom.getElementsByClassName('fa-tab-btn');
			Ext.Array.each(buttons, function(btn, i) {
				btn.onclick = function(){
					me.el.dom.getElementsByClassName('fa-tab-btn-active')[0].classList.remove('fa-tab-btn-active');
					this.classList.add('fa-tab-btn-active')
					me.tabChange(i);
				}
			});
		}
	},
	gridConfig:function(b,c){
		return Ext.apply(c,{
			autoScroll:false,
			viewConfig :{
				stripeRows:false,
				trackOver: false
			},
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			columns:[{
				text:'任务详情',
				dataIndex:'ra_taskname',
				cls:'x-grid-header-simple',
				flex:1,
				fixed:true,
				renderer:function(val,meta,record){
					meta.tdCls='x-grid-cell-topic';
					var bTime = new Date().getTime();
					var eTime = Ext.Date.parse(record.data['ra_enddate'].substring(0, 10) + ' 23:59:59','Y-m-d H:i:s');
					var allHour = (eTime - bTime)/(60*60*1000);
					if(!(record.data['ra_enddate'])){
						var allHour=1195.2307597222223;
					}
					var type = record.data.ra_type;
					var status=record.data.ra_status;
				if(type === 'billtask'){	
					if(allHour<0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
						);
						}else {	var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
								record.get('ra_id'),
								record.get('ra_taskname'),
								record.get('sourcecode')
						);}
					}else if(allHour>=0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
						);
						}else {
							var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
							);
						}
						
					}				
				}else if(type==='communicatetask'){
					if(allHour<0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
						);
						}else{
							var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null,null,null);">{1} {2}</a>',
									record.get('ra_taskid'),
									record.get('ra_taskname'),
									record.get('sourcecode')
							); 
						}
					} 
					else if(allHour>=0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
						);
						}else {
							var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null,null,null);">{1} {2}</a>',
									record.get('ra_taskid'),
									record.get('ra_taskname'),
									record.get('sourcecode')
							);
						}
					 }
				}else {
					
					if(allHour<0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
						);
						}else{
							var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\',null,null);">{1} {2}</a>',
									record.get('ra_id'),
									record.get('ra_taskname'),
									record.get('sourcecode')
							);
						}
						
				}else if(allHour>=0){
					if(status==='已完成'){
						var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
								record.get('ra_id'),
								record.get('ra_taskname'),
								record.get('sourcecode')
					);
					}else{
						var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\',null,null);">{1} {2}</a>',
								record.get('ra_id'),
								record.get('ra_taskname'),
								record.get('sourcecode')
						);		
					}	
					}	
				
				}			
					var prjname = '';
					if(record.get('prj_name')){
						if(record.get('prj_code')){
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'&nbsp;&nbsp;&nbsp;项目编号:' + record.get('prj_code') + '</font>'; 
						}else{
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'</font>'; 
						}
					}
					return '<span>'+e+prjname+'</span>';
				
				}
			},{
				text:'发起人',
				dataIndex:'recorder',
				cls:'x-grid-header-simple',
				width:80,
				fixed:true
			},{
				text:'状态',
				dataIndex:'ra_status',
				cls:'x-grid-header-simple',
				width:100,
				fixed:true,
				renderer:function(val,meta,record){
					meta.tdCls='x-grid-cell-topic';
					if(record.data.statuscode){
						if(record.data.statuscode=='STOP'){
							return '暂停中';
						}						
					}
					return val;
				}
			},{
				text:'发起日期',
				dataIndex:'ra_startdate',
				cls:'x-grid-header-simple',
				xtype:'datecolumn',
				format:'Y-m-d',
				width:150,
				fixed:true
			}
		]});
	},
	gridC:function(c){
		return Ext.apply(c,{
			autoScroll:false,
			viewConfig :{
				stripeRows:false,
				trackOver: false
			},
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			columns:[{
				text:'任务详情',
				dataIndex:'name',
				cls:'x-grid-header-simple',
				flex:1,
				fixed:true,
				renderer:function(val,meta,record){
					meta.tdCls='x-grid-cell-topic';
					var bTime = new Date().getTime();
					var eTime = Ext.Date.parse(record.data['enddate'].substring(0, 10) + ' 23:59:59','Y-m-d H:i:s');
					var allHour = (eTime - bTime)/(60*60*1000);
					var type = record.data['class'];
					var status=record.data.handstatus;
					if(!(record.data['enddate'])){
						var allHour=1195.2307597222223;
					}
					if(type === 'billtask'){
						if(allHour<0){
							if(status==='已完成'){
								var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
							);
							}else {	var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
									record.get('id'),
									record.get('name'),
									record.get('sourcecode')
							);}
						}else if(allHour>=0){
							if(status==='已完成'){
								var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
							);
							}else {
								var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
								);
							}
							
						}				
					}else if(type==='communicatetask'){
						if(allHour<0){
							if(status==='已完成'){
								var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
							);
							}else{
								var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
								); 
							}
						} 
						else if(allHour>=0){
							if(status==='已完成'){
								var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
							);
							}else {
								var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
								);
							}
						 }
					}else {
						if(allHour<0){
							if(status==='已完成'){
								var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
							);
							}else{
								var e= Ext.String.format('<img src="../../resource/images/renderer/important.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_taskid\',\'wr_taskid\',null,null);">{1} {2}</a>',
										record.get('id'),
										record.get('name'),
										record.get('sourcecode')
								);
							}
							
					}else if(allHour>=0){
						if(status==='已完成'){
							var e= Ext.String.format('<img src="../../resource/images/readed.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_taskid\',null,null,null);">{1} {2}</a>',
									record.get('id'),
									record.get('name'),
									record.get('sourcecode')
						);
						}else{
							var e= Ext.String.format('<img src="../../resource/images/renderer/doing.png"><a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_taskid\',\'wr_taskid\',null,null);">{1} {2}</a>',
									record.get('id'),
									record.get('name'),
									record.get('sourcecode')
							);		
						}	
						}	
					
					}
					
					var prjname = '';
					if(record.get('prj_name')){
						if(record.get('prj_code')){
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'&nbsp;&nbsp;&nbsp;项目编号:' + record.get('prj_code') + '</font>'; 
						}else{
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'</font>'; 
						}
					}
					return '<span>'+e+prjname+'</span>';
				
				}
			},{
				text:'发起人',
				dataIndex:'recorder',
				cls:'x-grid-header-simple',
				width:80,
				fixed:true
			},{
				text:'状态',
				dataIndex:'handstatus',
				cls:'x-grid-header-simple',
				width:100,
				fixed:true,
				renderer:function(val,meta,record){
					if(record.data.statuscode){
						if(record.data.statuscode=='STOP'&&record.data.handstatus!='已完成'){
							return '暂停中';
						}						
					}
					return val;
				}
			},{
				text:'发起日期',
				dataIndex:'startdate',
				cls:'x-grid-header-simple',
				xtype:'datecolumn',
				format:'Y-m-d',
				width:150,
				fixed:true
			}
		]});
	
	},
	gridConf:function(c){
		return Ext.apply(c,{
			autoScroll:false,
			viewConfig :{
				stripeRows:false,
				trackOver: false
			},
			listeners:{
				activate:function(grid){
					grid.getStore().load({
						params:{
							count:grid.ownerCt.pageCount
						}
					});
				}
			},
			columns:[{
				text:'任务详情',
				dataIndex:'ra_taskname',
				cls:'x-grid-header-simple',
				flex:1,
				fixed:true,
				renderer:function(val,meta,record){
					meta.tdCls='x-grid-cell-topic';
					var type = record.data.ra_type;
					var e;
					if(type === 'billtask'){							
						e =  Ext.String.format('<a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp\',\'ra_id\',null,null,null);">{1} {2}</a>',
								record.get('ra_id'),
								record.get('ra_taskname'),
								record.get('sourcecode')
						);
					}else if(type==='communicatetask'){
						e =  Ext.String.format('<a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp\',\'id\',null,null,null);">{1} {2}</a>',
								record.get('ra_taskid'),
								record.get('ra_taskname'),
								record.get('sourcecode')
						); 
					}else{
						e =  Ext.String.format('<a class="x-btn-link" onclick="openTable({0},\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp\',\'ra_id\',\'wr_raid\',null,null);">{1} {2}</a>',
								record.get('ra_id'),
								record.get('ra_taskname'),
								record.get('sourcecode')
						);
					}
					var prjname = '';
					if(record.get('prj_name')){
						if(record.get('prj_code')){
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'&nbsp;&nbsp;&nbsp;项目编号:' + record.get('prj_code') + '</font>'; 
						}else{
							prjname = '<br><font color="#777">项目名称:'+record.get('prj_name')+'</font>'; 
						}
					}
					return '<span>'+e+prjname+'</span>';
				
				}
			},{
				text:'发起人',
				dataIndex:'recorder',
				cls:'x-grid-header-simple',
				width:80,
				fixed:true
			},{
				text:'发起日期',
				dataIndex:'ra_startdate',
				cls:'x-grid-header-simple',
				xtype:'datecolumn',
				format:'Y-m-d',
				width:150,
				fixed:true
			}
			]});
	
	},
	_toDo:function(){
		var me=this;
		var fields=['ra_taskname', 'recorder','ra_startdate','ra_taskid','ra_type','ra_id','sourcecode','ra_status','ra_statuscode','ra_startdate','ra_enddate','statuscode','prj_name','prj_code']; 
	    var condition="(ra_emid="+ em_id+"  AND ra_taskpercentdone<100 AND NVL(handstatuscode,' ') <> 'UNACTIVE' and nvl(ra_statuscode,' ')<>'ENDED') OR (recorderid="+em_id+" and ra_statuscode='UNCONFIRMED') ";
	    var caller='ResourceAssignment!Bill';
		return Ext.widget('gridpanel',me.gridConfig(true,{
			title:'待处理',	
			store:me.getQueryStore(fields,condition,caller)
		}));
	},
	_alreadyLaunch:function(){
		var me=this;_toDo=false;
		//var fields=['name', 'recorder','ra_startdate','ra_taskid','ra_id','sourcecode','ra_status','ra_statuscode','ra_startdate','enddate','recorderid','startdate','handstatus','ra_taskid','id','class',,'ra_type'];
		var fields=['id','ra_id','taskcode','name','startdate','enddate','handstatus','recorder','sourcecode','resourcename','class','statuscode','prj_name','prj_code']
		//var fields=['ra_taskname', 'recorder','ra_startdate','ra_taskid','ra_type','ra_id','sourcecode','ra_status','ra_statuscode','ra_startdate','ra_enddate'];
		var condition='recorder=\'' + em_name+ '\''; //不能通过发起人姓名取数据，因为有可能重名。 可以通过发起人ID取数据。
		var caller='ProjectTask!Bill';
		return Ext.widget('gridpanel',me.gridC({
			title:'已下达',
			store:me.getQueryStore(fields,condition,caller)
		}));
	},
	_alreadyDone:function(){
		var me=this;
		var fields=['ra_taskname', 'recorder','ra_startdate','ra_taskid','ra_type','ra_id','sourcecode','ra_status','ra_statuscode','ra_startdate','ra_enddate','prj_name','prj_code'];
        var condition= 'ra_emid=' + em_id + ' AND ra_taskpercentdone=100  or (ra_emid=' + em_id + " and ra_statuscode='ENDED')"; 
        var caller='ResourceAssignment!Bill';
		return Ext.widget('gridpanel',me.gridConf({
			title:'已处理',
			id:'_alreadyDone',
			store:me.getQueryStore(fields,condition,caller)
		}));
	},
	getQueryStore:function(fields,condition,caller,autoLoad){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/datalist/data.action',
				method : 'POST',
				extraParams:{
					caller: caller,
					condition:condition,
					page: 1,
					pageSize:me.pageCount,
					_noc:1
				},
				actionMethods:{
					    read: 'POST'
					   },
				reader: {
					type: 'json',
					root: 'data'
				}
			}, 
			autoLoad:false  
		});	
	},
/*	getMore:function(){
		openTable(null,null,'更多任务',"jsps/common/moretask.jsp",null,null);				
	},*/
	getMore:function(){
		//openTable(null,null,'任务中心',"jsps/common/messageCenter/TaskCenter.jsp",null,null);
		var main=parent.Ext.getCmp("content-panel");
		var panel=parent.Ext.getCmp('taskcenter');
		if(!panel){
			var url=basePath+'jsps/common/messageCenter/TaskCenter.jsp',
	    	panel = { 
	    			title : '任务中心',
	    			id:'taskcenter',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',    	
	    			items: {xtype: 'component',
							id:'iframe_detail_task',									
							autoEl: {
									tag: 'iframe',
									style: 'height: 100%; width: 100%; border: none;',
									src: url}
	    				},
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
			var p = main.add(panel); 
			main.setActiveTab(p);
		}else{ 
	    	main.setActiveTab(panel); 
		} 
	},
	_dorefresh:function(panel){
		var activeTab=panel.down('tabpanel').getActiveTab();
		if(activeTab) {
			//解决刷新时 panel丢失高度 导致panel显示出错
			if(!activeTab._firstWidth){
				activeTab._firstWidth = activeTab.preLayoutSize.width
			}
			if(activeTab._firstWidth!=activeTab.preLayoutSize.width){
				activeTab.setWidth(activeTab._firstWidth);
			}
			activeTab.fireEvent('activate',activeTab);
		}
	},
	tabChange: function(index) {
		this.down('tabpanel').setActiveTab(index);
	}
});