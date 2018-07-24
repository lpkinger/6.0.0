Ext.QuickTips.init();
Ext.define('erp.view.common.messageCenter.TaskCenterGridPanel', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpTaskCenterGridPanel',
	id : 'taskGrid',
	width:'100%',
	columnLines : false,
	gridWidth:0,
	readOnly : false,
	checkTime: function(){
		var date = new Date();
    	var start=Ext.getCmp('prj_start').value;
    	var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
    	var extStart = Ext.Date.format(start,'Y-m-d');
    	
    	var end=Ext.getCmp('prj_end').value;
    	if(extStart<extCurrentDate){
    		return 'small';
    	}		
		if(start>end){
			return 'over';
		}
		return true;
    },
    constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	gridFilters:{},
	listeners: {
		'headerfiltersapply': function(grid, filters) {
			grid.gridFilters=filters;
		}
	},
	configs:[{
		//header:'状态标识',
		btnId:'doing',
		hideable:false,
		dataIndex:'',
		width:25,
		style:'text-align:center',
		filterJson_:{},
		renderer:function(val,meta,record){
			var enddate = record.get('ENDDATE');
			var startdate = record.get('STARTDATE');
			if(enddate&&startdate){
				var end = new Date(Date.parse(enddate.replace(/-/g,'/')));
				var start = new Date(Date.parse(startdate.replace(/-/g,'/')));
				var extEnd = Ext.Date.format(end,'Y-m-d');
				var extStart = Ext.Date.format(start,'Y-m-d');
				var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
				if(extEnd<extCurrentDate){
					return '<span class="tags-flag" style="background:red;border-radius:5px;">';
				}
				if(extCurrentDate<extStart){
					return '<span class="tags-flag" style="background:gray;border-radius:5px;"></span>';
				}
				return '<span class="tags-flag" style="background:green;border-radius:5px;"></span>'
			}
		}
	},{
		header:'RA_ID',
		dataIndex:'RA_ID',
		id:'RA_ID',
		hideable:false,
		hidden:true,
		group:'all',
		filterJson_:{}
	},{
		header:'RA_TASKID',
		dataIndex:'RA_TASKIDS',
		id:'RA_TASKIDS',
		hidden:true,
		hideable:false,
		group:'all',
		filterJson_:{}
	},{
		header:'RA_TYPE',
		dataIndex:'RA_TYPE',
		id:'RA_TYPE',
		hidden:true,
		hideable:false,
		group:'all',
		filterJson_:{}
	},{
		header:'任务名称',
		group:'all',
		hideable:false,
		dataIndex:'RA_TASKNAME',
		id:'RA_TASKNAME',
		align:'left',
		filterJson_:{},
		flex:2,
		renderer:function(val,meta,record){
			var rendermsg  = '';
			val=record.data['SOURCECODE']?val+'&nbsp;'+record.data['SOURCECODE']:val;
			if(record.data.RA_TYPE=='worktask' || record.data.RA_TYPE=='projecttask'){
				rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['RA_ID'] + ',\'ResourceAssignment\',\'任务\',\'jsps/plm/record/workrecord.jsp?_noc=1\',\'ra_id\',\'wr_raid\'' + ');">' + val + '</a>';
			}else if(record.data.RA_TYPE=='billtask'){
				rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['RA_ID'] + ',\'ResourceAssignment!Bill\',\'任务\',\'jsps/plm/record/billrecord.jsp?_noc=1\',\'ra_id\',null);">' + val + '</a>';
			}else if(record.data.RA_TYPE=='communicatetask'){
				rendermsg='<a class="x-btn-link" onclick="openTable(' + record.data['RA_TASKID'] + ',\'ResourceAssignment!Bill\',\'沟通任务\',\'jsps/common/JprocessCommunicate.jsp?_noc=1\',\'id\',null);">' + val + '</a>';
			}
			return rendermsg;
		},
		filter:{
            dataIndex:"RA_TASKNAME",
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
		header:'执行人',
		hideable:false,
		except:'myTask',
		dataIndex:'RA_RESOURCENAME',
		id:'RA_RESOURCENAME',		
		align:'left',
		width:100,
		filterJson_:{},
		filter:{
            dataIndex:"RA_RESOURCENAME",
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
		header:'发起人',
		hideable:false,
		btnId:'normalTask&myTask',
		dataIndex:'RECORDER',
		id:'RECORDER',
		align:'left',
		width:100,
		filterJson_:{},
		filter:{
            dataIndex:"RECORDER",
            xtype:"textfield",
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'计划开始',
		hideable:false,
		group:'all',
		dataIndex:'STARTDATE',
		id:'STARTDATE',
		align:'left',
		width:90,
		filterJson_: {value:'',type:''},
		filter:{
            dataIndex:"STARTDATE",
            xtype: "datefield",
			format: 'Y-m-d',
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'计划完成',
		hideable:false,
		group:'all',
		dataIndex:'ENDDATE',
		id:'ENDDATE',
		align:'left',
		width:90,
		filterJson_:{},
		filter:{
            dataIndex:"ENDDATE",
            xtype: "datefield",
			format: 'Y-m-d',
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},
	{
		header:'实际完成时间',
		hideable:false,
		btnId:'finished',
		dataIndex:'REALENDDATE',
		id:'REALENDDATE',
		align:'left',		
		width:150,
		filterJson_:{},
		filter:{
            dataIndex:"REALENDDATE",
            xtype: "datefield",
			format: 'Y-m-d',
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'完成比例',
		hideable:false,
		btnId:'projectTask',
		dataIndex:'RA_TASKPERCENTDONE',
		id:'RA_TASKPERCENTDONE',
		align:'left',
		width:80,
		filterJson_:{},
		renderer:function(val,meta,record){
			return val+'%';
		},
		filter:{
            dataIndex:"RA_TASKPERCENTDONE",
            xtype:"textfield",
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'状态',
		hideable:false,
		btnId:'doing',
		group:'all',
		dataIndex:'RA_STATUS',
		id:'RA_STATUS',
		align:'left',
		width:75,
		filterJson_:{},
		filter:{
            dataIndex:"RA_STATUS",
            xtype:"textfield",
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'项目名称',
		hideable:false,
		btnId:'projectTask',
		dataIndex:'PRJPLANNAME',
		id:'PRJPLANNAME',
		align:'left',
		filterJson_:{},
		//style:'text-align:center',
		//width:200,
		flex:2,
		filter:{
            dataIndex:"PRJPLANNAME",
            xtype:"textfield",
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	},{
		header:'项目经理',
		hideable:false,
		btnId:'projectTask',
		dataIndex:'PRJ_ASSIGNTO',
		id:'PRJ_ASSIGNTO',
		align:'left',			
		width:100,
		filterJson_:{},
		//flex:1,
		filter:{
            dataIndex:"PRJ_ASSIGNTO",
            xtype:"textfield",
            hideTrigger:false,
            queryMode:"local",
            autoDim:true,
            ignoreCase:false
		}
	}],
	store : Ext.create('Ext.data.Store', {
		storeId : 'myStore',
		pageSize : 500,
		fields : ['RA_ID','RA_TASKID','RA_TYPE','RA_TASKNAME', 'RA_RESOURCENAME', 'RECORDER', 'STARTDATE', 'ENDDATE',
				'RA_TASKPERCENTDONE', 'RA_STATUS', 'PRJPLANNAME','SOURCECODE','PRJ_ASSIGNTO','REALENDDATE'],
		autoLoad : false,
		proxy : {
			type : 'ajax',
			url : basePath + 'common/getTaskData.action',
			reader : {
				type : 'json',
				root : 'data',
				totalProperty : 'total'
			},
			actionMethods: {
	            			read   : 'POST'
	       	 		}
			//async:false
		},
		listeners : {
			beforeload : function() {
				var grid = Ext.getCmp("taskGrid");
				var form = Ext.getCmp('centerForm');
				Ext.apply(grid.getStore().proxy.extraParams, {
					condition:form.defaultCondition,
					likestr:form.likestr
				});
			}
		}
	}),	
	columns:[],
	reconfigureColumn:function(group){
		var columns = this.configs;
		var newColumn = new Array();
		var grid = Ext.getCmp("taskGrid");
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
				if(item.btnId&&group.indexOf(item.btnId)>-1){
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
			if(group.indexOf('doing')==-1){
				tbtext.hide();
			}else{
				tbtext.show();
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
	reconfigureGridWidth:function(width){
		var height = this.getHeight();
		var width = 0;
		Ext.Array.each(this.columns,function(column){
			width += column.getWidth();
		});
		this.gridWidth = width;
		this.setSize(width + 50,height);
	},
	hideColumns:function(group){
		var grid = this;
		Ext.Array.each(grid.columns,function(column){
			if(column.group.indexOf(group)==-1){
				if(column.isVisible()){
					column.setVisible(false);
				}
			}else{
				if(!column.isVisible()){
					column.setVisible(true);
				}
			}
		});
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
					text:'<span class="tags-flag" style="background:green;border-radius:5px;"></span><span class="tags-font">正常</span>' +
							'<span class="tags-flag" style="background:red;border-radius:5px;"></span><span class="tags-font">超时</span>' +
							'<span class="tags-flag" style="background:gray;border-radius:5px;"></span><span class="tags-font">未开始</span>'
				});
				
				toolbar.insert(1,{
					xtype:'tbfill'
				});
			}
		}
	}]
});
