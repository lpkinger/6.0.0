Ext.QuickTips.init();
Ext.define('erp.view.common.FormsDoc.FormsDocTree', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.erpFormsDocTree',
			title : '目录结构',
			bodyStyle : 'background-color:white;',
			width : 370,
			id : 'FormsDocTree',
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
				dataIndex : 'fd_name',
				sortable : true,
				flex : 1,
				editor : 'textfield'
			}],
			store : Ext.create('Ext.data.TreeStore', {
				fields : ['fd_formsid', 'fd_id', 'fd_kind', 'fd_parentid',
						'fd_name', 'fd_remark', 'fd_virtualpath', 'fd_detno',
						'fd_doccode'],
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

			setRootNode:function(){
				var me = this;
				Ext.Ajax.request({
					url:basePath + 'common/FormsDoc/getFileList.action',
					params:{
						caller: caller,
						formsid: formsid,
						kind:-1,
						_noc:_noc
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
			}
		});
