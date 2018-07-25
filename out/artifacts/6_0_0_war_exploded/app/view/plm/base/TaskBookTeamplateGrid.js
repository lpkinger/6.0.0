Ext.QuickTips.init(); 

Ext.define('erp.view.plm.base.TaskBookTeamplateGrid', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.erpTaskBookTeamplateGrid',
	id : 'taskbookgrid',
	layout : 'fit',
	bodyStyle : 'background-color:white;',
	emptyText : '无数据',
	keyField: 'Id_temp',
	caller:'TaskBookTeamplate',
	deleteUrl:'plm/base/deleteTaskBookTeamplate.action',
	saveUrl:'plm/base/updateTaskBookTeamplates.action',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
    selType : 'rowmodel',
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit : 1
    })],
    rootVisible: false,
	autoScroll : true,
    columnLines : true,
    rowLines : true,
    collapsible : true,
    dbfinds:[{
		field:'condition_temp',
		dbGridField:"tt_name",
		trigger:null,
	},{
		field:'conditioncode_temp',
		dbGridField:"tt_code",
		trigger:null,
	}],
    columns : [
		{
			text : 'ID',
			dataIndex : 'Id_temp',
			hidden:true,
			width:0
		},{
			text : '父任务ID',
			dataIndex : 'parentId',
			hidden:true,
			width:0
		},{
			xtype:'numbercolumn',
			text : '序号',
			dataIndex : 'detno_temp',
			align:'center',
			width:50
		},{
			text : '任务节点名称',
			xtype : "treecolumn",
			dataIndex : 'name_temp',
			width:250
		},{
			text : '任务节点描述',
			dataIndex : 'description_temp',
			hidden:true,
			width:250
		},{
			text : '前置任务',
			width:70,
			dataIndex : 'pretaskdetno_temp'
		},{
			text : '检测条件',
			//xtype : "column",
			dataIndex : 'condition_temp',
			width:150,
			editor:{
				xtype:'dbfindtrigger',
				name:"condition_temp",
				editable:false,
				dbBaseCondition:"tt_checked=1",
				dbfind:'ProjectTask|tt_name',
			}
		},{
			text : '持续时间(D)',
			xtype:'numbercolumn',
			align:'center',
			width:85,
			dataIndex : 'duration_temp'
		},{
			text : '资源ID',
			width:120,
			hidden:true,
			dataIndex : 'resourceId_temp'
		},{
			text : '资源编号',
			width:120,
			hidden:true,
			dataIndex : 'resourcecode_temp'
		},{
			text : '资源名称',
			width:150,
			dataIndex : 'resourcename_temp'
		},{
			text : '资源分配(%)',
			width:150,
			dataIndex : 'resourceunits_temp'
		},{
			text : '文件名称',
			width:150,
			dataIndex : 'docname_temp',
			editor:{
				xtype:'filetreetrigger',
				editable:false,
				title:'文件列表',
				url:'plm/base/filetree.action',
				nodeId:'docId_temp',
				filetrees:[{treefield:'id_',field:'docId_temp'},{treefield:'name_',field:'docname_temp'}]
			}
		},{
			text : '文件ID',
			width:120,
			hidden:true,
			dataIndex : 'docId_temp'
		},{
			text : '任务类型',
			align:'center',
			dataIndex : 'tasktype_temp',
			width:100,
			xtype:'combocolumn',
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
			text :'任务分类',
			align :'center',
			dataIndex :'taskclass_temp',
			width  : 130,
			xtype : 'combocolumn',
			editor:{
				xtype:"combo",
				store: {
				    fields: ['display','value'],
				    data : [					    	
				        {display:'预立项任务', value:'pretask'},
				        {display:'正式立项任务', value:'normaltask'},
				        {display:'无',value :null}
					]
				},
			    queryMode: 'local', 
			    displayField: 'display',
			    valueField: 'value'			
			}
		},{
			text : '检测条件编码',
			dataIndex : 'conditioncode_temp',
			width:0,
		},],
	initComponent : function() {
		var me=this;
		//让树可编辑
        Ext.override(Ext.data.AbstractStore,{
            indexOf: Ext.emptyFn
        });
        me.store=Ext.create('Ext.data.TreeStore', {
            storeId: 'systreestore',
            fields: [{name: "Id_temp", type: "number"},
				{name: "parentId", type: "number"},
				{name: "detno_temp", type: "int"},
				{name: "name_temp", type: "string"},
				{name: "description_temp", type: "string"},
				{name: "pretaskdetno_temp", type: "string"},
				{name: "duration_temp", type: "number"},
				{name: "resourceId_temp", type: "string"},
				{name: "resourcecode_temp", type: "string"},
				{name: "resourcename_temp", type: "string"},
				{name: "resourceunits_temp", type: "string"},	
				{name: "docId_temp", type: "string"},
				{name: "docname_temp", type: "string"},
				{name: "tasktype_temp", type: "String"},
				{name: "taskclass_temp",type : "string"},
				{name:"conditioncode_temp",type:"string"},
				{name:"condition_temp",type:"string"}],
            root : {
                text: 'root',
                id: 0,
                expanded: true
            }
        });
        
        this.callParent(arguments);
	},
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		items:[{
		        xtype:'button',
		        id:'saveBtn',
			    text : '保存',
				iconCls : 'x-button-icon-save',
				cls : 'x-btn-gray',
				width : 60,
				style:'margin-left:5px;margin-top:5px'
			},{
			    xtype : 'form',
			    style : 'margin-left:5px;',
			    width : 100,
				height : 26,
			    items : [ {
					xtype : 'filefield',
					name : 'file',
					height : 26,
					buttonOnly : true,
					hideLabel : true,
					buttonConfig : {
						iconCls : 'x-button-icon-up',
						cls : 'x-btn-gray',
						text : '导入Project'
					},
					listeners : {
						change : function(field) {
							var grid = Ext.getCmp('taskbookgrid');		
			    		   	var store = grid.getStore();			    	
			    		   	if(store.getRootNode().hasChildNodes()){
			    		   		Ext.Msg.confirm('提示','当前产品类型已经存在任务模板,重新导入将清掉原有数据,请确认是否重新导入！',function(b){
			    		   			if(b=='yes'){			    		   		
			    		   				field.ownerCt.ImportProject(field);
			    		   			}else{			    		   	
			    		   				field.reset();
			    		   			}
			    		   		});	    		   		
			    		   	}else{
			    		   		field.ownerCt.ImportProject(field);
			    		   	}
						}
					}
			    }],
			    ImportProject : function(field) {
					var bool = this.fireEvent('beforeimport', this);
					if (bool != false) {
						this.getForm().submit({
							url : basePath + 'plm/base/ImportProjectFile.action?productTypeCode='+productTypeCode,
							waitMsg : "正在解析Project",
							success : function(fp, resp) {
								field.reset();
								var grid = Ext.getCmp('taskbookgrid');
								grid.setRootNode();
							},
							failure : function(fp, o) {			
								if (o.result.size) {
									showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
									field.reset();
								} else {
									showError(o.result.error);
									field.reset();
								}
							}
						});
					}
				}
			},{
		        xtype:'button',
		        id:'clearallBtn',
		        disabled:true,
			    text : '清除明细',
				iconCls : 'x-button-icon-delete',
				cls : 'x-btn-gray',
				width : 85,
				style:'margin-left:5px;margin-top:5px;margin-right:20px;'
			},'->']
		}],
		getTreeGridData:function(onlyId){
			var store = this.getStore();
			var gridStore = new Array();
			var root = store.getRootNode();
			if(root.hasChildNodes()){
				if(onlyId){
					gridStore = this.getChildNodesOnlyId(root,gridStore);
				}else{
					gridStore = this.getChildNodes(root,gridStore);
				}				
			}
			return gridStore;
		},
		getChildNodes:function(node,gridStore){
			var columns = this.columns;
			var ChildNodes = node.childNodes;
			for(var i=0;i<ChildNodes.length;i++){
				var data = new Object();
				Ext.Array.each(columns,function(column){					
					data[column.dataIndex] = ChildNodes[i].data[column.dataIndex];			
				});
				gridStore.push(data);
				if(ChildNodes[i].hasChildNodes()){
					gridStore = this.getChildNodes(ChildNodes[i],gridStore);
				}
			}
			return gridStore;
		},
		getChildNodesOnlyId:function(node,gridStore){
			var ChildNodes = node.childNodes;
			for(var i=0;i<ChildNodes.length;i++){
				var data = new Object();
				data[this.keyField] = ChildNodes[i].data[this.keyField];
				gridStore.push(data);
				if(ChildNodes[i].hasChildNodes()){
					gridStore = this.getChildNodesOnlyId(ChildNodes[i],gridStore);
				}
			}
			return gridStore;
		},
		setRootNode:function(){
			var grid = this;
			Ext.Ajax.request({
				url:basePath + 'plm/base/getTaskBookTree.action',
				params:{
					condition:productTypeCode?'PRJTYPECODE_ = \''+productTypeCode+'\'':''
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						var clearBtn = Ext.getCmp('clearallBtn');
						if(res.tree&&res.tree.length>0){
							clearBtn.setDisabled(false);
						}else{
							clearBtn.setDisabled(true);
						}
						if(res.tree){
							grid.getStore().setRootNode({
								text: 'root',
		                	    id: 0,
		                		expanded: true,
		                		children: res.tree
							});
							grid.scrollByDeltaX(1);  //为了解决grid列错位的bug
							grid.scrollByDeltaX(-1);
						}
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
		}	 
});