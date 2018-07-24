Ext.QuickTips.init();
Ext.define('erp.view.plm.project.ProjectDocTree', {
		extend : 'Ext.tree.Panel',
		alias : 'widget.erpProjectFileTree',
		title : '项目文件清单',		
		id : 'prjFileTree',
		layout : 'fit',
	    rootVisible: false,
	    collapsible : true,
		columnLines:true,
		autoScroll : true,
		bodyStyle : 'background-color:white;',
		initComponent:function(){
			var me = this;
			Ext.apply(me,{
				columns : [{
					header : "ID",
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_tempid",
		            align : "left",			   
		            hidden : true,
		            text : "ID"			  
		           },{
					header : "目录/文件",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_name",
		            align : "left",
		            sortable : true,
		            xtype : "treecolumn",			
		            hidden : false,
		            flex:3,
		            text : "目录/文件"
				},{
					header : "已上传",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_checked",
		            align : "center",
		            xtype : "checkcolumn",			
		            hidden : false,
		            width : 100,
		            readOnly : true,
		            headerCheckable : false,
		            text : "已上传"
				},{
					header : "文件路径",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_filepath",
		            align : "left",
		            sortable : true,		
		            hidden : true,
		            flex:2,
		            text : "文件路径"
				},{
					header : "任务名称",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_taskname",
		            align : "left",
		            sortable : true,			        	
		            hidden : false,
		            flex:3,
		            text : "任务名称"
				},{
					header : "操作人",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_operator",
		            align : "center",
		            sortable : true,		
		            hidden : false,
		            flex:1,
		            text : "操作人"
				},{
					header : "操作时间",			        
		            cls : "x-grid-header-1",			        
		            dataIndex : "pd_operatime",
		            align : "center",
		            sortable : true,			      	
		            hidden : false,
		            flex:2,
		            text : "操作时间"
				}]
			});
			this.setRootNode(this);
			this.callParent(arguments);
		},
		store : Ext.create('Ext.data.TreeStore', {
			fields : ['pd_tempid', 'pd_name', 'pd_taskname', 'pd_checked',
					'pd_filepath', 'leaf', 'pd_operator','pd_operatime'],
			root : {
				text : 'Root',
				id : 0,
				expanded : true
			}
		}),
		setRootNode:function(){
			var me = this;
			Ext.Ajax.request({
				url:basePath + 'plm/project/getProjectFileTree.action',
				params:{
					condition:Condition.replace(/IS/g,'=').replace('prj_id','pd_prjid')
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						if(res.tree){
							me.getStore().setRootNode({
								text: 'root',
		                	    id: 'root',
		                		expanded: true,
		                		children: res.tree
							});	
						}
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
		}
});
