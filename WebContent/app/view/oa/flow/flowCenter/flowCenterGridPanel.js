Ext.QuickTips.init();

Ext.define('erp.view.oa.flow.flowCenter.flowCenterGridPanel', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpFlowCenterGridPanel',
	id : 'flowCenterGrid',
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
		text:'流程',
		cls:'x-grid-header-simple',
		dataIndex:'FD_NAME',
		flex:1,
		renderer:function(val,meta,record){
			var url='jsps/oa/flow/Flow.jsp',mastername=record.get('MASTERNAME');
			return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null,true);" target="_blank" style="padding-left:2px">{4}&nbsp;{5}</a></span>',
					record.get('FI_KEYVALUE'),
					record.get('FI_CALLER'),
					url,
					record.get('FI_KEYFIELD'),
					record.get('FD_NAME'),
					record.get('FI_CODEVALUE')
			);
		},
		btnId:'pending',
		filterJson_:{},
		filter:{
			dataIndex:"FD_NAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'标题',
		cls:'x-grid-header-simple',
		dataIndex:'FI_TITLE',
		width:120,
		btnId:'pending',
		filterJson_:{},
		filter:{
			dataIndex:"FI_TITLE",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'节点',
		cls:'x-grid-header-simple',
		dataIndex:'FI_NODENAME',
		width:120,
		btnId:'pending',
		filterJson_:{},
		filter:{
			dataIndex:"FI_NODENAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'发起人',
		cls:'x-grid-header-simple',
		width:60,
		dataIndex:'FI_STARTMAN',
		btnId:'pending',
		filterJson_:{},
		filter:{
			dataIndex:"FI_STARTMAN",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'发起时间',
		cls:'x-grid-header-simple',
		width:150,
		dataIndex:'FI_TIME',
		xtype:'datecolumn',
		renderer:function(value){
			return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
		},
		btnId:'pending',
		filterJson_:{},
		filter:{
			dataIndex:"FI_STARTTIME",
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
		text:'流程',
		cls:'x-grid-header-simple',
		dataIndex:'FD_NAME',
		flex:1,
		renderer:function(val,meta,record){
			var url='jsps/oa/flow/Flow.jsp?nodeId='+record.get('FI_NODEID'),mastername=record.get('MASTERNAME');
			return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null);" target="_blank" style="padding-left:2px">{4}&nbsp;{5}</a></span>',
					record.get('FI_KEYVALUE'),
					record.get('FI_CALLER'),
					url,
					record.get('FI_KEYFIELD'),
					record.get('FD_NAME'),
					record.get('FI_CODEVALUE')
			);
		},
		btnId:'processed',
		filterJson_:{},
		filter:{
			dataIndex:"FD_NAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'标题',
		cls:'x-grid-header-simple',
		dataIndex:'FI_TITLE',
		width:120,
		btnId:'processed',
		filterJson_:{},
		filter:{
			dataIndex:"FI_TITLE",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'节点名称',
		dataIndex:'FI_NODENAME',
		cls:'x-grid-header-simple',
		width:120,
		btnId:'processed',
		filterJson_:{},
		filter:{
			dataIndex:"FI_NODENAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'处理时间',
		dataIndex:'FI_TIME',
		cls:'x-grid-header-simple',
		width:150,
		xtype:'datecolumn',
		renderer:function(value){
			return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
		},
		btnId:'processed',
		filterJson_:{},
		filter:{
			dataIndex:"FI_TIME",
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
		text:'流程',
		cls:'x-grid-header-simple',
		flex:1,
		dataIndex:'FD_NAME',
		renderer:function(val,meta,record){
			var url='jsps/oa/flow/Flow.jsp?nodeId='+record.get('FI_NODEID'),mastername=record.get('MASTERNAME');
			return Ext.String.format('<span style="color:#436EEE;padding-left:2px"><a class="x-btn-link" onclick="openTable({0},\'{1}\',\'任务流程\',\'{2}\',\'{3}\',null,null,null);" target="_blank" style="padding-left:2px">{4}&nbsp;{5}</a></span>',
					record.get('FI_KEYVALUE'),
					record.get('FI_CALLER'),
					url,
					record.get('FI_KEYFIELD'),
					record.get('FD_NAME'),
					record.get('FI_CODEVALUE')
			);
		},
		btnId:'created',
		filterJson_:{},
		filter:{
			dataIndex:"FD_NAME",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'标题',
		cls:'x-grid-header-simple',
		dataIndex:'FI_TITLE',
		width:180,
		btnId:'created',
		filterJson_:{},
		filter:{
			dataIndex:"FI_TITLE",
			xtype:"textfield",
			hideTrigger:false,
			queryMode:"local",
			autoDim:true,
			ignoreCase:false
		}
	},{
		text:'发起时间',
		dataIndex:'FI_TIME',
		cls:'x-grid-header-simple',
		width:150,
		xtype:'datecolumn',
		renderer:function(value){
			return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
		},
		btnId:'created',
		filterJson_:{},
		filter:{
			dataIndex:"FI_TIME",
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
	}],
	store : Ext.create('Ext.data.Store', {
		storeId : 'myStore',
		pageSize : 100,
		fields : ['MASTERNAME','FI_ID','FI_FDSHORTNAME','FI_NODEID','FI_CODEVALUE','FI_KEYVALUE','FI_HANDLER','FI_HANDLERCODE','FI_TIME','FI_NODENAME','FI_STARTTIME','FI_STARTMAN',
				  'FI_STATUS','FI_STARTMANCODE','FI_KEYFIELD','FI_CALLER','FD_NAME','FI_TITLE'],   
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'common/getFlowData.action',
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
				var grid = Ext.getCmp("flowCenterGrid");
				var form = Ext.getCmp('flowCenterForm');
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
		var grid = Ext.getCmp("flowCenterGrid");
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
