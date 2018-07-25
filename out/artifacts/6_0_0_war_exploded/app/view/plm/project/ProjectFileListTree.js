Ext.QuickTips.init();
Ext.define('erp.view.plm.project.ProjectFileListTree', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.erpProjectFileListTree',
			title : '目录结构',
			bodyStyle : 'background-color:white;',
			width : 370,
			id : 'prjFileListTree',
			collapsible : true,
			split: true, 
			createNodes : [],
			updateNodes : [],
			expandNodes:[],
			currentNodeId:null,
			FormUtil : Ext.create('erp.util.FormUtil'),
			columns : [{
				xtype : 'treecolumn',
				text : '文件名称',
				dataIndex : 'pd_name',
				sortable : true,
				flex : 1,
				editor : 'textfield'
			}],
			store : Ext.create('Ext.data.TreeStore', {
				fields : ['pd_prjid', 'pd_id', 'pd_kind', 'pd_parentid',
						'pd_name', 'pd_remark', 'pd_virtualpath', 'pd_detno',
						'pd_code','manage'],
				autoLoad:false,
				root : {
					text : 'Root',
					id : 0,
					expanded : true
				}
			}),
			rootVisible : false,
			hideHeaders : true,
			initComponent:function(){
				this.setRootNode(this);
				this.callParent(arguments);
			},
			dockedItems : [{
				xtype : 'toolbar',
				dock : 'top',
				layout : {
					pack : 'left'
				},
				items : [
					{
						text:'子目录',
						iconCls:'x-button-icon-addgroup',
						menu :[
							{
								text : '父目录',
								id : 'addRootButton',
								xtype:'button',
								disabled:true,
								width : 68,
								iconCls:'x-button-icon-addgroup'
							},{
								text : '子目录',
								id : 'addButton',
								xtype:'button',
								disabled:true,
								width : 68,
								iconCls:'x-button-icon-addgroup'
							}
						]
					}
					,
					{
					text : '修改',
					id : 'changeButton',
					width : 60,
					iconCls : 'tree-back'
				},{
					xtype : 'button',
					text : '删除',
					id : 'deleteButton',
					width : 60,
					iconCls: 'tree-delete'
				},{
					xtype : 'button',
					disabled: true,
					text : '权限',
					id : 'powerButton',
					width : 60,
					iconCls: 'x-button-icon-Jurisdiction'
				},
				{
					xtype:'button',
					disabled:true,
					text:'导出',
					id:'exportButton',
					iconCls :'x-button-icon-excel',
					width:70
				}
				],
				border : false
			}],

			setRootNode:function(){
				var me = this;
				Ext.Ajax.request({
					url:basePath + 'plm/project/getProjectFileList.action',
					params:{
						formCondition:formCondition,
						kind:-1,
						_noc:_noc,
						canRead: canRead
					},
					method:'post',
					//async: false,
					callback:function(options,success,resp){
						var res = new Ext.decode(resp.responseText);
						if(res.success){
							if(res.datas){
								me.getStore().setRootNode({
									text: 'root',
			                	    id: '0',
			                		expanded: true,
			                		children: res.datas
								});	
								Ext.getCmp('exportButton').setDisabled(false);
							}
							if(res.addroot){
								Ext.getCmp('addRootButton').setDisabled(false);
							}
						}else if(res.exceptionInfo){
							showError(res.exceptionInfo);	
						}
					}
				});

			},
				
			checkNode:function(node,tree){
				if(node.isExpanded()){
					tree.expandNodes.push(node.data.id);
				}				
				var rootnodes = node.childNodes;					
				for(var i=0;i<rootnodes.length;i++){
					var childNode = rootnodes[i];
					tree.checkNode(childNode,tree);
				}
			},
			
			checkExpanded:function(rootnode,tree){
				tree.checkNode(rootnode,tree);
			},
			
			saveTree : function(node,type) {
				var me = this;
				var create = [];
				var update = [];
				if('create'==type||'createRoot'==type){
					create.push(node);
				}else if('update'==type){
					update.push(node);	
				}
					
				var rootnode = me.getRootNode();
				
				//记录展开的节点
				me.checkExpanded(rootnode,me);
				//如果是新增，而当前节点是叶子，也要记录到expandNodes中
				if(type!='update'){
					var selNode = me.getSelectionModel().getSelection()[0];
					if(selNode){
						if(selNode.data.leaf){
							me.expandNodes.push(selNode.data.id);
						}
						else{ //如果是父节点，且没有孩子节点也记录到expandNodes中
							if(!selNode.data.leaf&&selNode.childNodes.length==0){
								me.expandNodes.push(selNode.data.id);	
							}
						}
					}
				}
			
				Ext.Ajax.request({
					url : basePath + 'plm/project/saveAndUpdateProjectFileList.action',
					method : 'post',
					async:false,
					params : {
						create : Ext.encode(create),
						update : Ext.encode(update),
						_noc: _noc
					},
					callback : function(options, success, response) {
						me.FormUtil.setLoading(false);
						var res = Ext.JSON
								.decode(response.responseText);
						if (res.success) {
							showMessage('提示','保存成功',1000);

							var tree = Ext.getCmp("prjFileListTree");
							var store = tree.getStore();
							tree.getSelectionModel().clearSelections();	//清除选中状态
							tree.setRootNode(); //重新加载数据
							
							//重新展开树
							var store = tree.getStore();
							tree.expandNodes.forEach(function(item){
								var node = store.getNodeById(item);
								if(node){
									node.expand();	
								}								
							});
							
							if('update'==type){
								me.currentNodeId = update[0].pd_id;
							}else{
								me.currentNodeId = res.ids[0];
								
							}						
							//选中修改或新建的节点
							var currentNode = store.getNodeById(me.currentNodeId);
							me.getSelectionModel().select(currentNode);
						} else if (res.exceptionInfo) {
							showError(res.exceptionInfo);
						}
					}
				});
			}
		});
