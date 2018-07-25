Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.ProjectFileList', {
    extend: 'Ext.app.Controller',
    views:['plm.project.ProjectFileList','plm.project.ProjectFileListTree','plm.project.ProjectFileListTreeGrid','core.trigger.TextAreaTrigger',
    'core.button.Close','core.button.Save','core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({    
    		'#toolbartext':{
    			afterrender:function(self){
    				Ext.Ajax.request({
    					url:basePath + 'plm/project/getProjectMsg.action',
    					method:'post',
    					params:{
    						formCondition:getUrlParam('formCondition').replace(/IS/g,'=')
    					},
    					callback:function(options,success,response){
    						var res = Ext.decode(response.responseText);
    						if(res.success){
    							self.setText('<span class="toolbartext">项目编号:</span><span class="toolbarcontent">' 
    								+ res.data[0].PRJ_CODE + '</span><span class="toolbartext">项目名称:</span><span class="toolbarcontent">' 
    								+ res.data[0].PRJ_NAME + '</span><span class="toolbartext">项目负责人:</span><span class="toolbarcontent">' 
    								+ res.data[0].PRJ_ASSIGNTO + '</span>');
    						}else if(res.exceptionInfo){
    							showError(res.exceptionInfo);
    						}else{
    							Ext.Msg.alert('未知错误');
    						}
    					}
    				});
    			}
    		},
		   	'erpProjectFileListTree' : {
				beforeitemclick:function(tree,record,item,index,e,eOpts){
					if(e.target.className.indexOf('x-tree-expander')>-1){
						return true;
					}
					return false;
				},
				select:function(treeview,record,index){
					var tree = Ext.getCmp("prjFileListTree");					
					var grid = Ext.getCmp("prjFileListTreeGrid");
					isSearch = false;
					
					grid.store.load({
						params : {
							formCondition:formCondition,
							id : record.get("pd_id"),
							kind : 0
						},
						callback:function(records, operation, success){
							var text = grid.store.getProxy().getReader().rawData ;
							if(success){
								var res = new Ext.decode(operation.response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
								}
							}
						}
					});
					
					grid.nodeId = record.get("pd_id");
					
				},
				selectionchange:function(self,selected,eOpts){
					var addbtn = Ext.getCmp('addButton');
					var delbtn = Ext.getCmp('deleteButton');
					var powerbtn = Ext.getCmp('powerButton');
					var addfilebtn = Ext.getCmp('addFileButton');
					var changebtn = Ext.getCmp('changeButton');
					var savefilbtn = Ext.getCmp('saveFileButton');
					var exportbtn = Ext.getCmp('exportButton');
					if(selected.length>0&&selected[0].data.manage){
						addbtn.setDisabled(false);
						delbtn.setDisabled(false);
						powerbtn.setDisabled(false);
						changebtn.setDisabled(false);
						addfilebtn.setDisabled(false);
						savefilbtn.setDisabled(false);
						exportbtn.setDisabled(false);
					}else{
						addbtn.setDisabled(true);
						delbtn.setDisabled(true);
						powerbtn.setDisabled(true);
						changebtn.setDisabled(true);
						addfilebtn.setDisabled(true);
						savefilbtn.setDisabled(true);
						exportbtn.setDisabled(true);
					}
				}
			},
			'erpProjectFileListTreeGrid':{
				afterrender:function(grid){
					Ext.Ajax.request({
						url:basePath + 'plm/project/ifMainTaskOpen.action',
						method:'post',
						async:false,
						params:{
							condition:'pt_prjid=' + prj_id
						},
						callback:function(options,success,response){
							var res = Ext.decode(response.responseText);
							if(res.success){
								grid.maintaskactive = res.maintaskactive;
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});		
				},
				selectionchange:function(self,selected,eOpts){			
					var deletebtn = Ext.getCmp('deleteFileButton');
					var uploadbtn = Ext.getCmp('prjFileListTreeGrid').down('filefield');
					var readfilebtn = Ext.getCmp('readFileButton');
					if(selected.length>0){
						if(selected[selected.length-1].data.pd_filepath){
							readfilebtn.setDisabled(false);
						}else{
							readfilebtn.setDisabled(true);
						}
						deletebtn.setDisabled(false);
						if(uploadbtn){
							uploadbtn.setDisabled(false);
						}
					}else{
						readfilebtn.setDisabled(true);
						deletebtn.setDisabled(true);
						if(uploadbtn){
							uploadbtn.setDisabled(true);
						}
					}				
				}
			},
			'erpProjectFileListTreeGrid gridscroller[dock=bottom]':{
				afterrender:function(scroll){
					var panel = parent.Ext.getCmp('tree-tab');
					if(panel && !panel.collapsed) {
						panel.collapse();
					}
				}
			},
			'erpProjectFileListTree button[id=addButton]' : {
				click : function(btn) {
					var node = Ext.getCmp('prjFileListTree').getSelectionModel()
							.getSelection()[0];
					var params = new Object();
					if(node.hasChildNodes()){
						params.detno = node.lastChild.get('pd_detno')+1;
					}else{
						params.detno = 1;
					}
					me.createWin(params, 'create');
				}
			},
			'erpProjectFileListTree button[id=powerButton]' : {
				click : function(btn) {
					var node = btn.ownerCt.ownerCt.getSelectionModel().getSelection()[0];
					me.createPowerWin(node);
				}
			},
			'erpProjectFileListTree button[id=addRootButton]' : {
				click : function(btn) {
					var me = this;
					var root = Ext.getCmp('prjFileListTree').getStore().getRootNode();
					var params = new Object();
					if(root.hasChildNodes()){
						params.detno = root.lastChild.get('pd_detno')+1;
					}else{
						params.detno = 1;
					}
			
					me.createWin(params, 'createRoot');
				}
			},
			'erpProjectFileListTree button[id=exportButton]' : {
				click : function(btn) {
					var me = this;
					var grid = Ext.getCmp("prjFileListTreeGrid").getStore().data.items;
					var prid = new Array();
					for(var i=0;i<grid.length;i++){//将grid里面各行的项目id获取出来
						var data = grid[i].data;
						prid.push(Ext.JSON.encode(data['pd_id']));
					}
					var formCondition=getUrlParam('formCondition').replace(/IS/g,'=');
					window.location.href=basePath+'plm/project/exportProjectExcel.action?formCondition='+formCondition+"&prids="+prid.toString();
				}
			},
			'erpProjectFileListTreeGrid button[id=readFileButton]' : {
				click : function(btn) {
					var fileList = Ext.getCmp('prjFileListTreeGrid');
					var select = fileList.selModel.lastSelected;
					var path = unescape(select.data.pd_filepath);
					var id = path.substring(path.lastIndexOf(';')+1);
					path = path.substring(0,path.lastIndexOf(';'));
					var type = path.substring(path.lastIndexOf('.') + 1);
					var folderId = select.data.pd_parentid;
					fileList.readFile(id,folderId,type);
				}
			},
			'erpProjectFileListTreeGrid button[id=addFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					var node = Ext.getCmp("prjFileListTree").getSelectionModel()
							.getSelection()[0];
					if (node) {
						var parentId = node.get('pd_id');
						if (parentId == -1) {
							Ext.Msg.alert("提示", "请先保存目录");
							return;
						}
						var virtualpath = node.get("pd_virtualpath");
						var newNode = new Object();
						var data = grid.store.data.items;
						var detno = 1;
						if(data.length>0){
							detno = data[data.length-1].data['pd_detno']+1;
						}
						newNode.data = {
							pd_name : "新建文件",
							leaf : true,
							pd_parentid : parentId,
							pd_id : -1,
							pd_virtualpath : virtualpath,
							pd_kind : 0,
							pd_remark : "",
							pd_code : null,
							pd_detno : detno,
							pd_prjid : prj_id
						};

						grid.store.loadData([newNode.data], true);
					} else {
						Ext.Msg.alert("提示", "请先选择目录");
					}

				}
			},
			'erpProjectFileListTreeGrid button[id=saveFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					grid.save(grid);

				}
			},
			'erpProjectFileListTree button[id=deleteButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var tree = Ext.getCmp("prjFileListTree");
					var node = tree.getSelectionModel().getSelection()[0];
					if (node) {
						if (node.data.pd_id != -1) {
							Ext.Msg.confirm("提示", "确定删除？", function(optional) {
										if (optional == 'yes') {
											var bool = me.deleteNode(node.data.pd_id,
													"index");
											if(bool){
												node.remove();
											}
											
										}
									});
						} else {
							node.remove();
						}

						var rootnode = tree.getRootNode();
						if (rootnode.childNodes.length == 0) {
							tree.getSelectionModel().clearSelections(); // 清除选中状态
						}
					}

				}
			},
			'erpProjectFileListTreeGrid button[id=deleteFileButton]':{
				afterrender:function(btn){
					btn.setDisabled(true);
				}
			},
			'erpProjectFileListTreeGrid #toolbartext':{
				afterrender:function(tlbar){
					tlbar.hide();
				}
			},
			'erpProjectFileListTree button[id=changeButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var node = btn.ownerCt.ownerCt.getSelectionModel()
							.getSelection()[0];
					if (node) {
						var params = new Object();
						params.name = node.get('pd_name');
						params.virtualpath = node.get('pd_virtualpath');
						params.remark = node.get("pd_remark");
						params.detno = node.get('pd_detno');
						params.code = node.get('pd_code');
						
						me.createWin(params, 'update');
					}
				}
			}
    	});
    },
     	deleteNode : function(id, type) {
		var tree = Ext.getCmp("prjFileListTree");
		var rootnode = tree.getRootNode();
		var grid = Ext.getCmp("prjFileListTreeGrid");
		//记录展开的节点
		tree.checkExpanded(rootnode,tree);
		
		Ext.Ajax.request({
			url : basePath + 'plm/project/deleteProjectFile.action',
			async : false,
			params : {
				id : id,
				type : type,
				prjid:prj_id,
				_noc: _noc
			},
			callback : function(options, success, response) {
				var res = Ext.decode(response.responseText);
				if (res.success) {
					showMessage('提示','删除成功',1000);
					
					tree.getSelectionModel().clearSelections(); // 清除选中状态
					tree.setRootNode(tree); // 重新加载树
					grid.store.removeAll();
					// 重新展开树
					var store = tree.getStore();
					tree.expandNodes.forEach(function(item){
						var node = store.getNodeById(item);
						if(node){
							node.expand();	
						}								
					});
					Ext.getCmp("toolbartext").setText("<span style='color:red'>当前路径：</span>");
					
					Ext.getCmp('addButton').setDisabled(true);
					Ext.getCmp('deleteButton').setDisabled(true);
					Ext.getCmp('changeButton').setDisabled(true);
					Ext.getCmp('addFileButton').setDisabled(true);
					Ext.getCmp('powerButton').setDisabled(true);
					return true;
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return false;
				}
				else{
					Ext.Msg.alert("提示", "未知错误");
					return false;
				}
			}
		});
	},
	createWin : function(params, type) {
		var win = Ext.create('Ext.window.Window', {
			xtype : 'form',
			title : '修改',
			height : 380,
			width : 580,
			layout : 'column',
			id : 'changeWin',
			bodyStyle : 'background:#F2F2F2;',
			border:false,
			defaults : {
				columnWidth:0.5,
				margin:'10 10 10 5'
			},
			listeners:{
				show:function(self){
					if('update'!=type){
						self.setTitle('新增');
					}				
				}
			},
			items : [{
				xtype : 'textfield',
				id : "changename",
				fieldLabel : '文件夹名称',
				border : false,
				allowBlank : false,
				value : params ? params.name : '',
				labelStyle : 'color:#FF0000'
			}, {
				xtype : 'textfield',
				id : "changecode",
				fieldLabel : '文件夹编号',
				border : false,
				readOnly : true,
				readOnlyCls : 'win-textfield-readOnly',	
				fieldStyle : 'background:#d6d6d6',
				value : params ? params.code : ''
			}, {
				xtype : 'numberfield',
				id : "changedetno",
				fieldLabel : '文件夹序号',
				border : false,
				value : params ? params.detno : ''
			},
				{
				xtype : 'textfield',
				fieldLabel : '虚拟路径',
				border : false,
				columnWidth : 0.5,
				value : params ? params.virtualpath : '',
				readOnly : true,
				readOnlyCls : 'win-textfield-readOnly',
				fieldStyle : 'background:#d6d6d6'

			}, {
				xtype : 'textarea',
				id : "fieldRemark",
				fieldLabel : '备注',
				border : false,
				columnWidth : 1,
				value : params ? params.remark : ''
			}],
			buttonAlign:'center',
			buttons:[{
				xtype : 'button',
				iconCls: 'x-button-icon-save',
				text : '保存',
				id : 'saveChangeButton',
				cls : 'x-btn-gray',
				width : 70,
				handler : function(btn) {
					var me = this;					
					var form = Ext.getCmp('changeWin');
					var ifchange = form.checkFormDirty();
					if(!ifchange){
						Ext.Msg.alert('提示','尚未添加或修改数据!');
						return;
					}
					
					var tree = Ext.getCmp("prjFileListTree");
					var node = tree.getSelectionModel().getSelection()[0];
					var name = Ext.getCmp("changename").value;
					if (name == null || name.trim() == "") {
						showError("文件名称不能为空");
						return;
					}
					var code = Ext.getCmp("changecode").value;
					var fieldRemark = Ext.getCmp("fieldRemark").value;
					var detno = Ext.getCmp('changedetno').value;
					var parentid;
					if('update'==type){
						parentid = node.get('pd_parentid');
					}else if('create'==type){
						if(node){
							parentid = node.get('pd_id');	
						}else{
							parentid = 0;	
						}							
					}else if('createRoot'==type){
						parentid = 0;	
					}
					
					tree.saveTree({
						pd_name : name,
						pd_code : code,
						pd_remark : fieldRemark,
						pd_prjid : prj_id,
						pd_detno : detno,
						pd_virtualpath : node ? ('createRoot' == type
								? ''
								: node.get("pd_virtualpath")) : '',
						pd_id : node?node.get("pd_id"):-1,
						pd_kind : -1,
						pd_parentid : parentid
					}, type);

					var grid = Ext.getCmp("prjFileListTreeGrid");
					btn.ownerCt.ownerCt.close();
				}

				}, {
					xtype : 'button',
					iconCls: 'x-button-icon-close',
					text : '关闭',
					cls : 'x-btn-gray',
					id : 'closeChangeButton',
					width : 70,
					handler : function() {
						Ext.getCmp("changeWin").close();
					}
				}
			],
			checkFormDirty:function(){
				var me = this;
				var flag = false;
				Ext.Array.each(me.items.items,function (item,index, length){
						var value = item.value == null ? "" : item.value;
						item.originalValue = item.originalValue == null ? "" : item.originalValue;
		
						if(Ext.typeOf(item.originalValue) != 'object'){
							if(item.originalValue.toString() != value.toString()){
								flag = true;
							}
		
						}
				});
				return flag;
			}
		}).show();
	},
	createPowerWin: function(treenode){
		var me = this,data = new Array();
		var path  = treenode.data.pd_virtualpath, hasChilds = treenode.hasChildNodes();
		
		var grid = Ext.create('Ext.grid.Panel',{
			 	emptyText : $I18N.common.grid.emptyText,
			    columnLines : true,
			    iconCls: 'icon-grid',
			    frame: true,
			    GridUtil: Ext.create('erp.util.GridUtil'),
			    bodyStyle:'background-color:#f1f1f1;',
			    features: [Ext.create('Ext.grid.feature.Grouping',{
			    	startCollapsed: false,
			        groupHeaderTpl: '{name} ({rows.length})'
			    })],
			    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit : 1
			    }),Ext.create('erp.view.core.grid.HeaderFilter')],
			    selModel: Ext.create('Ext.selection.CheckboxModel',{
					checkOnly:true,
					listeners:{
						'select': function(selModel, record){
							selModel.view.ownerCt.selectAllPower(record);
						},
						'deselect': function(selModel, record){
							selModel.view.ownerCt.deselectAllPower(record);
						}
					},
					onHeaderClick: function(headerCt, header, e) {
						var grid = headerCt.ownerCt;
						if (header.isCheckerHd) {
							var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
							if (!isChecked) {
								grid.store.each(function(item){
									grid.selectAllPower(item);
								});
								grid.selModel.selectAll();
							} else {
					        	grid.store.each(function(item){
									grid.deselectAllPower(item);
								});
					        	grid.selModel.deselectAll();
					        }
				        } 
						
					}
				}),
				selectAllPower: function(record){
					var set = ['pp_manage','pp_scan','pp_upload','pp_down','pp_read'];
					Ext.each(set, function(s){			
						record.set(s, true);
					});
				},
				deselectAllPower: function(record){
					var set = ['pp_manage','pp_scan','pp_upload','pp_down','pp_read'];;
					Ext.each(set, function(s){			
						record.set(s, false);
					});
				},
			    store: Ext.create('Ext.data.Store', {
			    	fields: [{
			    		name: 'pp_id',
			    		type: 'number'
			    	},{
			        	name: 'pp_emid',
			        	type: 'number'
			        },{
			        	name:'tm_employeename',
			        	type:'string'
			        },{
			        	name:'tm_functional',
			        	type:'string'
			        },{
			        	name:'pp_manage'
			        },{
			        	name:'pp_scan'
			        },{
			        	name:'pp_upload'
			        },{
			        	name:'pp_down'
			        },{
			        	name:'pp_read'
			        }],
			        groupers: [{
			        	property: 'tm_functional',
			        	direction: 'DESC',
			        	transform: function(value) {
			        		if(value=='团队外成员')
			        			return 1;
			        		else return value ;
			        		}		     	
			        }] 
			    }),
			    columns: [{
			        text: 'pp_id',
			        hidden: true,
			        dataIndex: 'pp_id'
			    },{
			        text: '人员ID',
			        hidden: true,
			        dataIndex: 'pp_emid'
			    },{
			        text: '文件ID',
			        hidden: true,
			        dataIndex: 'pp_docid',
			        defaultValue: treenode.get('pd_id')
			    },{
			    	align:'center',
			    	text:'姓名',
			        width: 120,
					logic: 'ignore',
			        flex:1.5,
			        height:45,
			        dataIndex: 'tm_employeename',
			        cls: 'x-grid-header',
			        filter: {xtype: 'textfield',width: 80,align:'center', filterName: 'tm_employeename',margin:'0 0 20 0'},
			        setPadding: Ext.emptyFn
			    },{
			        text: '管理',
			        align: 'center',
		        	width: 80,
		        	flex:1,
		        	cls: 'x-grid-header',
		            xtype: 'checkcolumn',
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            },
			        dataIndex: 'pp_manage'
			    },{
		        	text: '浏览',
		        	align: 'center',
		        	width: 80,
		        	flex:1,
		        	cls: 'x-grid-header',
		            xtype: 'checkcolumn',
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            },
		            dataIndex: 'pp_scan'
		        },{
		        	text: '阅读',
		        	align: 'center',
		        	width: 80,
		        	flex:1,
		        	cls: 'x-grid-header',
		            xtype: 'checkcolumn',
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            },
		            dataIndex: 'pp_read'
		        },{
		        	text: '上传',
		        	align: 'center',
		        	width: 80,
		        	flex:1,
		        	cls: 'x-grid-header',
		            xtype: 'checkcolumn',
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            },
		            dataIndex: 'pp_upload'
		        },{
		        	text: '下载',
		        	align: 'center',
		        	width: 80,
		        	flex:1,
		        	cls: 'x-grid-header',
		            xtype: 'checkcolumn',
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            },
		            dataIndex: 'pp_down'
			    }]
		    });
			if(hasChilds){
		    	grid.addDocked({
		    		xtype: 'toolbar',
	    			dock: 'top',
	    			items: [{
				    	xtype: 'checkbox',
				    	id: 'appyforChilds',
				    	boxLabel: '权限应用到子文件夹',
				    	checked: true
			        }]
		    	});
		    }	   
		var win = Ext.create('Ext.window.Window', {
			closeAction: 'destroy',
			modal : true,
			title : path,
			width : '55%',
			height: '90%',
			id : 'powerWin',
			layout : 'fit',
			autoScroll : true,
			items : [grid],
			buttonAlign:'center',
			buttons:[{
					text:'更多选择',
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-query',
			    	width: 95,
			    	style : {
						marginLeft : '10px'
					},
			    	handler:function(btn){
			    		var trigger = this;
			    		var dbwin=me.createWin2(grid);
			    		dbwin.show();
			    		if(this.multistore){
			    			this.showButtons();
			    		} else {
			    			trigger.multiValue = new Object();
			    			var iframe = dbwin.getEl().down('iframe');
			    			if(!iframe) {
			    				dbwin.add({
			    					tag : 'iframe',
			    					frame : true,
			    					anchor : '100% 100%',
			    					layout : 'fit',
			    					html : '<iframe src="#" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
			    				});
			    				iframe = dbwin.getEl().down('iframe');
			    			}
			    			var dbfind='Employee|em_code';
			    			var dbCondition='em_code not in (select TM_EMPLOYEECODE from teammember where TM_PRJID='+prj_id+') and em_id not in (select pp_emid from projectdocpower where pp_docid='+treenode.get("pd_id")+')';
			    			var caller='Employee';
			    			var key='em_code';
			    			var dbGridCondition='';
			    			var _config='';
			    			var dbBaseCondition='';
			    			iframe.dom.src = basePath + 'jsps/common/multidbfind.jsp?key=' + 
							key + "&dbfind=" + encodeURIComponent(dbfind) + 
							"&dbGridCondition=" + encodeURIComponent(dbGridCondition) + "&dbCondition=" + encodeURIComponent(dbCondition) + 
							"&dbBaseCondition=" + encodeURIComponent(dbBaseCondition) + "&keyValue=&caller=" + caller +"&_config="+_config+
							"&trigger=" + trigger.id;
			    		}
			    	}
					},{
					xtype : 'button',
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
					handler : function(btn){
						var grid = btn.ownerCt.ownerCt.down('gridpanel');	
						var obj = me.getGridStore(grid);
						var gridStore = obj.jsonGridData,records = obj.records;
						if(gridStore.length<1){
							Ext.Msg.alert('未修改权限！');
							return;
						}
						var appyforChilds = Ext.getCmp('appyforChilds');
						var AppyforChilds = false;
						if(appyforChilds){
							AppyforChilds = appyforChilds.getValue();
						}
						Ext.Ajax.request({
							url:basePath + 'plm/project/saveFilePowers.action',
							params : {
								appyforChilds: AppyforChilds,
								filePowers: '['+gridStore.toString()+']',
								_noc: _noc
							},
							method:'post',
							callback:function(options,success,response){
								var res = Ext.decode(response.responseText);
								if(res.success){
									showMessage('保存成功！');
									Ext.Array.each(records,function(record){
										record.commit();
									});
								}else if(res.exceptionInfo){
									showError(res.exceptionInfo);
								}
							}
						});
					}
				}, {
					xtype: 'button',
					text : $I18N.common.button.erpCloseButton,
					iconCls : 'x-button-icon-close',
					cls : 'x-btn-gray',
					width : 65,
					style : {
						marginLeft : '10px'
					},
					handler : function(btn) {
						btn.ownerCt.ownerCt.close();
					}
			}]
		});/*.show();*/
		  Ext.Ajax.request({
			url:basePath + 'plm/project/getFilePowers.action',
			params : {
				docid : treenode.get("pd_id"),
				prjid : prj_id,
				_noc: _noc
			},
			method:'post',
			callback:function(options,success,response){
				var res = Ext.decode(response.responseText);
				if(res.success){
					if(res.data.length>0){
						var arr=new Array(),arr2=new Array();
						Ext.each(res.data,function(data){
							if(data.tm_functional=='团队外成员'){
								if(data.pp_manage||data.pp_scan||data.pp_read||data.pp_down||data.pp_upload){
									arr2.push(data);
								}
							}else{
								arr.push(data);
							}
						});
						if(arr2.length>0){
							Ext.each(arr2,function(item,index){
								arr.push(item);
							});
						}
						grid.store.loadData(arr);
						win.show();
					}else{
						showError('未设置项目团队！');
					}
				}else if(res.exceptionInfo){
					Ext.getCmp('addButton').setDisabled(true);
					Ext.getCmp('deleteButton').setDisabled(true);
					Ext.getCmp('changeButton').setDisabled(true);
					Ext.getCmp('addFileButton').setDisabled(true);
					Ext.getCmp('powerButton').setDisabled(true);
					showError(res.exceptionInfo);
				}
			}
		});
	},
	 createWin2: function(grid) {
			var trigger = this;
			this.win = Ext.create('Ext.Window', {
				title: '选择',
				height: "100%",
				width: "60%",
				maximizable : true,
				buttonAlign : 'left',
				layout : 'anchor',
				items: [],
				dbtriggr: trigger,
				closeAction: 'hide',
				buttons : [{
				 xtype:'panel',
				 width:150,
				 height:25,
				 border:false,
				 bodyStyle:'background-color:#e8e8e8',
				 items:[{
				  boxLabel  : '<span style="font-size:13px;font-weight:bold;">只显示已选中数据</span>',
					xtype:'checkbox',
					style:'margin-left:10px;',
					align:'left',
					hidden:true,
					width:140,
					id:'onlyChecked',
					listeners:{
						change:function( f, newValue,  oldValue, eOpts ){
							var win = trigger.win;
							var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
							var resgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindresultgrid');//选中
							if(newValue){
								findgrid.hide();
								var datachecked=new Array();
								Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
									datachecked.push(resgrid.selectObject[k]);
								});
								resgrid.selectAll=false;
								resgrid.store.loadData(datachecked);
								resgrid.selModel.selectAll();
								resgrid.show();
							}else{
								findgrid.show();
								findgrid.selectAll=false;
								findgrid.selModel.deselectAll();
								findgrid.selectDefaultRecord();
							    resgrid.hide();
							}
						}
					}
				 }]
				},'->',{
					text : '确  认',
					iconCls: 'x-button-icon-save',
					id:'mutidbaffirm',
					cls: 'x-btn-gray',
					handler : function(btn){
						var win = trigger.win,datachecked=new Array();
						Ext.each(grid.store.data.items,function(item){//原来的数据
							datachecked.push(item);
						});	
						var resgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindresultgrid');//选中
						Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){//新选择的数据
							var obj1=new Object(),em_id=resgrid.selectObject[k].em_id,flag=true;
							obj1.pp_emid=em_id;
							obj1.tm_employeename=resgrid.selectObject[k].em_name;
							obj1.tm_functional='团队外成员';
							Ext.each(datachecked,function(item){
								var functional=item.tm_functional||item.data.tm_functional;
								var pp_emid=item.pp_emid||item.data.pp_emid;
								if(functional=='团队外成员'){
									if(em_id==pp_emid){
										flag=false;
										return false;
									}
								}
							});
							if(flag){
								datachecked.push(obj1);
							}
							});
						btn.ownerCt.ownerCt.close();
						grid.store.loadData(datachecked);
					}
				},{
					text : '关  闭',
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					style:'margin-right:140px',
					handler : function(btn){
						btn.ownerCt.ownerCt.close();
					}
				} ,'->'
				]
			});
			return this.win;
		},
	getGridStore: function(grid){
		var me = this,jsonGridData = new Array(),records = new Array(),obj = new Object();
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(s[i].dirty){
					records.push(s[i]);
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'checkcolumn'){
								if(s[i].data[c.dataIndex]){
									dd[c.dataIndex] =1;
								}else{
									dd[c.dataIndex] =0;
								}
							}else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		obj.records = records;
		obj.jsonGridData = jsonGridData;
		return obj;
	},
	findMyself:function(){
		alert
	}
});