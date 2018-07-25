Ext.QuickTips.init();

Ext.define('erp.view.common.messageCenter.JProcessCenterGridPanel', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpJProcessCenterGridPanel',
	id : 'jprocessGrid',
	columnLines : false,
	readOnly : false,
	defaults : {
		autoScroll : false
	},
	columns:[],
		constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
			Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	gridFilters:{},
	configs:[{
		//header:'状态标识',
		btnId:'toDo',
		dataIndex:'',
		hideable:false,
		width:25,
		style:'text-align:center',
		filterJson_:{},
		renderer:function(val,meta,record){
			var enddate = record.get('JP_REMINDDATE');
			if(enddate){
				var end = new Date(Date.parse(enddate.replace(/-/g,'/')));
				var extEnd = Ext.Date.format(end,'Y-m-d H:i:s');
				var extCurrentDate = Ext.Date.format(new Date,'Y-m-d H:i:s');
				if(extEnd<extCurrentDate){
					return '<span class="tags-flag" style="background:red;border-radius:5px;"></span>';
				}else{
					return '';
				}
			}
		}

	},{
		xtype: 'checkcolumn',
		width: 30,
		dataIndex: 'undoCheck',
		headerCheckable:false,
		id:'undoCheck',
		btnId:'alreadyLaunchUndo',
		cls : 'x-grid-header-1',
		hideable:false,
		filterJson_:{},
		editor: {
			xtype: 'checkbox',
			cls : 'x-grid-checkheader-editor'
		},
		renderer : function(v, meta, record) {
					record.dirty = v;
					return '<input type="checkbox"'
							+ (v == true ? " checked" : "") + '/>';
		}
	},{
		header:'发起人',
		btnId:'toDo,alreadyDo',		
		hideable:false,
		dataIndex:'JP_LAUNCHERNAME',
		id:'JP_LAUNCHERNAME',
		//flex:1,
		width:100,
		filterJson_:{},
		filter:{
			dataIndex:"JP_LAUNCHERNAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		 }
	},{
		header:'接收人',
		btnId:'alreadyLaunchUndo,alreadyLaunchDone',
		hideable:false,
		dataIndex:'JP_NODEDEALMANNAME',
		id:'JP_NODEDEALMANNAME',
		//flex:1,
		width:100,
		filterJson_:{},
		filter:{
			dataIndex:"JP_NODEDEALMANNAME",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},/*{
		header:'<span>&nbsp流程名称</span>',
		except:'toLaunch',
		hideable:false,
		dataIndex:'JP_NAME',
		id:'JP_NAME',
		flex:2,
		renderer:function(val,meta,record){
			var switchbtn = Ext.getCmp('centerForm').query('erpSwitchButton')[0];
			var processType = switchbtn.activeButton.type;		
			var url;
			if(processType=='toDo'){ //我的待审批
				url='jsps/common/flow.jsp';
			}else{
				url = 'jsps/common/flow.jsp?_do=1'
			}
			
			var note=record.get('JP_PROCESSNOTE'),
				currentMaster=record.get('CURRENTMASTER'),
				typeCode=record.get('TYPECODE');
			if(note==null || note =='' || note=='null') {
				note='';
			}else{
				note='</br><font color="#777">'+note+'</font>';
			}
			if(typeCode=='procand'){
				url='jsps/common/jtaketask.jsp';
			}
			return Ext.String.format('<span style="color:#436EEE;padding-left:2px">' +
					'<a href="javascript:openTable(\'{3}\',\'JProcess!Me\',\'任务流程\',\'{5}\',\'jp_nodeId\',null,null,\'{4}\');' +
					'" target="_blank" style="padding-left:2px">{0}&nbsp;{1}</a>{2}</span>',
					record.get('JP_NAME'),
					record.get('JP_CODEVALUE'),
					note,
					record.get('JP_NODEID'),
					currentMaster,
					url
			);
		},
		filter:{
			dataIndex:"JP_NAME",
			xtype:"textfield",			
		 }
	},*/{
		header:'<span>&nbsp流程名称</span>',
		except:'toLaunch',
		hideable:false,
		dataIndex:'JP_PROCESSDESC',
		id:'JP_PROCESSDESC',
		flex:2,
		filterJson_:{},
		renderer:function(val,meta,record){
			var switchbtn = Ext.getCmp('centerForm').query('erpSwitchButton')[0];
			var processType = switchbtn.activeButton.type;		
			var url;
			if(processType=='toDo'){ //我的待审批
				url='jsps/common/flow.jsp';
			}else{
				url = 'jsps/common/flow.jsp?_do=1'
			} 
			
			var note=record.get('JP_PROCESSNOTE'),
				currentMaster=record.get('CURRENTMASTER'),
				typeCode=record.get('TYPECODE');
				
			if(typeCode=='unprocess'){
				if(url.indexOf('?')>0) {
					url += '&_disagree=1';
				}else 
					url += '?_disagree=1';
			}
			if(note==null || note =='' || note=='null') {
				note='';
			}else{
				note='</br><font color="#777">'+note+'</font>';
			}
			if(typeCode=='procand'){
				url='jsps/common/jtaketask.jsp';
			}
			return Ext.String.format('<span style="color:#436EEE;padding-left:2px">' +
					'<a class="x-btn-link" onclick="openTable(\'{2}\',\'JProcess!Me\',\'任务流程\',\'{4}\',\'jp_nodeId\',null,null,\'{3}\');' +
					'" target="_blank" style="padding-left:2px">{0}</a>{1}</span>',
					record.get('JP_PROCESSDESC'),
					note,
					record.get('JP_NODEID'),
					currentMaster,
					url
			);
		},
		filter:{
			xtype:"textfield",
			dataIndex:"JP_PROCESSDESC",
					filtertype:"",
					disabled:false,
					hideTrigger:false,
					queryMode:"local",
					displayField:"display",
					valueField:"value",
					store:null,
					autoDim:true,
					ignoreCase:false,
					exactSearch:false,
					args:null
		 }
	},{
		header:'单据编号',
		hideable:false,
		btnId:'alreadyLaunchUndo,alreadyLaunchDone',
		dataIndex:'JP_CODEVALUE',
		id:'JP_CODEVALUE',
		//flex:2,
		width:160,
		filterJson_:{},
		filter:{
			dataIndex:"JP_CODEVALUE",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'流程描述',
		hideable:false,
		btnId:'toDo',
		dataIndex:'JP_PROCESSNOTE',
		id:'JP_PROCESSNOTE',
		flex:2,
		filterJson_:{},
		renderer:function(val){
			if(!val||val=='null'){
				return '';
			}
			return val;
		},
		filter:{
			dataIndex:"JP_PROCESSNOTE",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'发起时间',
		hideable:false,
		except:'toLaunch',
		dataIndex:'JP_LAUNCHTIME',
		id:'JP_LAUNCHTIME',
		width:150,
		filterJson_:{},
		filter:{
			dataIndex:"JP_LAUNCHTIME",
			xtype: "datefield",
			format:'Y-m-d H:i:s',
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'预计结束时间',
		hideable:false,
		btnId:'toDo,alreadyDo',
		dataIndex:'JP_REMINDDATE',
		id:'JP_REMINDDATE',
		width:150,
		filterJson_:{},
		filter:{
			dataIndex:"JP_REMINDDATE",
			xtype: "datefield",
			format: 'Y-m-d H:i:s',
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'实际结束时间',
		hideable:false,
		btnId:'alreadyDo,alreadyLaunchDone',
		dataIndex:'JN_DEALTIME',
		id:'JN_DEALTIME',
		width:150,
		filterJson_:{},
		filter:{
			dataIndex:"JN_DEALTIME",
			xtype: "datefield",
			format: 'Y-m-d H:i:s',
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'<span>&nbsp流程名称</span>',
		hideable:false,
		btnId:'toLaunch',
		dataIndex:'TITLE',
		id:'TITLE',
		align:'left',
		flex:2.5,
		filterJson_:{},
		filter:{
			dataIndex:"TITLE",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'单据编号',
		hideable:false,
		btnId:'toLaunch',
		dataIndex:'CODE',
		id:'CODE',
		align:'left',
		//style:'text-align:center',
		//flex:1,
		width:160,
		filterJson_:{},
		renderer:function(val,meta,record){
			return Ext.String.format('<a href="javascript:openUrl(\'{0}\',null);" target="_blank">{1}</a>',
					record.get('PAGELINK'),
					record.get('CODE')
			);
		},
		filter:{
			dataIndex:"CODE",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'PAGELINK',
		hidden:true,
		hideable:false,
		btnId:'toLaunch',
		dataIndex:'PAGELINK',
		id:'PAGELINK',
		style:'text-align:center',
		align:'left',
		width:250,
		filterJson_:{},
		filter:{
			dataIndex:"PAGELINK",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'CURRENTMASTER',
		hidden:true,
		hideable:false,
		except:'toLaunch',
		dataIndex:'CURRENTMASTER',
		id:'CURRENTMASTER',
		style:'text-align:center',
		align:'left',
		width:250,
		filterJson_:{},
		filter:{
			dataIndex:"CURRENTMASTER",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'JP_NODEID',
		hidden:true,
		hideable:false,
		except:'toLaunch',
		dataIndex:'JP_NODEID',
		style:'text-align:center',
		align:'left',
		width:250,
		filterJson_:{}
	},{
		header:'TYPECODE',
		hidden:true,
		group:'all',
		hideable:false,
		filterJson_:{}
	},{
		header:'发起人部门',
		hideable:false,
		btnId:'alreadyDo,toDo',
		dataIndex:'EM_DEPART',
		id:'EM_DEPART',
		width:120,
		filterJson_:{},
		filter:{
			dataIndex:"EM_DEPART",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
	},{
		header:'接收人部门',
		hideable:false,
		btnId:'alreadyLaunchUndo,alreadyLaunchDone',
		dataIndex:'EM_DEPART',
		id:'EM_DEPART',
		width:120,
		filterJson_:{},
		filter:{
			dataIndex:"EM_DEPART",
			xtype:"textfield",
			filtertype:"",
			hideTrigger:false,
			queryMode:"local",
			displayField:"display",
			valueField:"value",
			store:null,
			autoDim:true,
			ignoreCase:false,
			exactSearch:false,
			args:null
		 }
		}],
	store : Ext.create('Ext.data.Store', {
		storeId : 'myStore',
		pageSize : 500,
		fields : ['JP_ID','JP_NODEID','JP_LAUNCHERNAME','JP_NAME','JP_CODEVALUE',
		'JP_PROCESSNOTE','JP_LAUNCHTIME','JP_REMINDDATE','JN_DEALTIME','TITLE','CODE','PAGELINK','CURRENTMASTER','TYPECODE','JP_NODEDEALMANNAME','JP_NODEDEALMAN','JP_PROCESSDESC','EM_DEPART'],
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'common/getProcessData.action',
			reader : {
				type : 'json',
				root : 'data',
				totalProperty : 'total'
			},
			actionMethods: {
	            read   : 'POST'
	       	}
		},
		listeners : {
			beforeload : function() {
				var grid = Ext.getCmp("jprocessGrid");
				var form = Ext.getCmp('centerForm');
				Ext.apply(grid.getStore().proxy.extraParams, {
					type:form.processType,
					likestr:form.likestr
				});
			}
		}
	}),	
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		'headerfiltersapply': function(grid, filters) {
			grid.gridFilters=filters;
		}
	},
	reconfigureColumn:function(group){
		var columns = this.configs;
		var newColumn = new Array();
		var grid = Ext.getCmp("jprocessGrid");
		var currentColumns = grid.columns;
		//去除已经存在的combocolumn中过滤器store中的“-所有-”和“-无-”避免重复添加
		if(currentColumns.length>0){
		Ext.Array.each(currentColumns,function(currentColumn,index1){
			for(var index2 =0;index2< columns.length;index2++){//如果下拉框已经被渲染,则去掉filter的store中data前两位（“-所有-”和“-无-”）
				if(columns[index2].dataIndex===currentColumn.dataIndex&&currentColumns[index1].xtype==='combocolumn'){
						grid.columns[index1].filter.store.data.splice(0,2);
					break;
				}
			}
		});
		}
		Ext.Array.each(columns,function(item){
			if(item.group=='all'){					
				newColumn.push(item);
			}else{
				if(item.except){
					if(group.indexOf(item.except)==-1){
						newColumn.push(item);
					}
				}
				if(item.btnId&&item.btnId.indexOf(group)>-1){
					newColumn.push(item);
				}
			}
		});
		if(this.headerFilterPlugin){
			this.headerFilterPlugin.destroyFilters();  
		}      
		this.reconfigure(null,newColumn);	
		
		var tbtext = Ext.getCmp('toolbarDisplayField');
		if(tbtext){
			if(group.indexOf('toDo')==-1){
				tbtext.hide();
			}else{
				tbtext.show();
			}			
		}
		var remindBtn = Ext.getCmp('remindBtn');
		if(remindBtn){
			if(group.indexOf('alreadyLaunchUndo')==-1){
				remindBtn.hide();
				if(Ext.getCmp('checkBtn'))Ext.getCmp('checkBtn').hide();
			}else{
				remindBtn.show();
				if(Ext.getCmp('checkBtn'))Ext.getCmp('checkBtn').show();
			}			
		}
		this.ownerCt.fireResize();

		if (this.getWidth()>10){
			this.setWidth(this.getWidth()-1);
		}
		if(this.headerFilterPlugin){
			this.headerFilterPlugin.adjustFilterWidth();	
		}
	},
	dockedItems:[{
		xtype : 'pagingtoolbar',
		dock : 'bottom',
		displayInfo : true,
		store : Ext.data.StoreManager.lookup('myStore'),
		displayMsg:"显示{0}-{1}条数据，共{2}条数据",
		beforePageText: '第',
		afterPageText: '页,共{0}页',
		listeners:{
			afterrender:function(toolbar){
				var tbfill;
				Ext.Array.each(toolbar.items.items,function(item){
					if(item.xtype=='tbfill'){
						tbfill = item;
						return false;
					}
				});
				toolbar.remove(tbfill);
				
				toolbar.insert(0,{
					xtype:'tbtext',
					id:'toolbarDisplayField',
					margin:0,
					padding:0,
					text:'<span class="tags-flag" style="background:red;border-radius:5px;"></span><span class="tags-font">超时</span>'								
				});
				toolbar.insert(1,{
					xtype : 'checkbox',
					boxLabel : '全选',
					id : 'checkBtn'
				});
				toolbar.insert(2,{
					xtype : 'button',	
					id:'remindBtn',
					cls: 'x-button-processmind',
					width: 86
				});
				toolbar.insert(3,{
					xtype:'tbfill'
				});
			}
		}
	}]
});
