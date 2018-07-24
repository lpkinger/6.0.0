Ext.QuickTips.init();
Ext.define('erp.view.plm.base.FileTree', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.erpFileTree',
			title : '目录结构',
			bodyStyle : 'background-color:white;',
			width : 350,
			id : 'indexTree',
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
				dataIndex : 'name_',
				sortable : true,
				flex : 1,
				editor : 'textfield'
			}],
			store : Ext.create('Ext.data.TreeStore', {
				fields : ['prjtypecode_', 'id_', 'kind_', 'parentid_',
						'name_', 'remark_', 'virtualpath_', 'detno_',
						'code_'],
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
				this.getRootNode(this);
				this.callParent(arguments);
			},
			dockedItems : [{
				xtype : 'toolbar',
				dock : 'top',
				layout : {
					pack : 'center'
				},
				items : [{
						text : '父目录',
						id : 'addRootButton',
						width : 70,
						iconCls:'x-button-icon-addgroup'
					},{
						text : '子目录',
						id : 'addButton',
						width : 70,
						iconCls:'x-button-icon-addgroup'
					},{
						text : '修改',
						id : 'changeButton',
						width : 60,
						iconCls: 'tree-back'
					},{
						xtype : 'button',
						text : '删除',
						id : 'deleteButton',
						width : 60,
						iconCls: 'tree-delete'

						}],
				border : false
			}],
			setRootNode:function(){
				var me = this;
				Ext.Ajax.request({
					url:basePath + 'plm/base/getFileList.action',
					params:{
						productTypeCode:productTypeCode
					},
					method:'post',
					async: false,
					callback:function(options,success,resp){
						var res = new Ext.decode(resp.responseText);
						if(res.success){
							if(res.datas){
								me.getStore().setRootNode({
									text: 'root',
			                	    id: 'root',
			                		expanded: true,
			                		children: res.datas
								});
								
								if(res.datas.length==0){									
									var grid = Ext.getCmp('treegrid');
									grid.store.removeAll();
								}
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
							if(!selNode.data.leaf&&selNode.childNodes.length==0&&type=='create'){
								me.expandNodes.push(selNode.data.id);	
							}
						}
					}
				}
				Ext.Ajax.request({
					url : basePath
							+ 'plm/base/saveAndUpdateTree.action',
					method : 'post',
					async:false,
					params : {
						create : Ext.encode(create),
						update : Ext.encode(update)
					},
					callback : function(options, success, response) {
						me.FormUtil.setLoading(false);
						var res = Ext.JSON
								.decode(response.responseText);
						if (res.success) {
							showMessage('提示','保存成功',1000);

							var tree = Ext.getCmp("indexTree");
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
								me.currentNodeId = update[0].id_;
							}else{
								me.currentNodeId = res.ids[0];
								
							}
							
							//选中修改或新建的节点。
							var currentNode = store.getNodeById(me.currentNodeId);
							me.getSelectionModel().select(currentNode);
							me.fireEvent('itemmousedown',me.getView(),currentNode);
						} else if (res.exceptionInfo) {
							showError(res.exceptionInfo);
						}
					}
				});
			}
		});
