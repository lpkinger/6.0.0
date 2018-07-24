Ext.QuickTips.init();
Ext.define('erp.controller.plm.base.ProductType', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.grid.Panel2','plm.base.ProductType','core.grid.Panel5','plm.base.TaskNodeGrid','core.button.Add','core.button.Submit','core.button.Update','core.button.Save',
	       'core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
	       'plm.base.ProductTypeTree','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','plm.base.FileTreeTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField','core.grid.YnColumn',
	       'plm.base.TestTeamplateGrid','plm.base.DeliveryTeamplateGrid','plm.base.ProjectPhaseView','plm.base.TaskBookTeamplateGrid','plm.base.FileTreeGrid','plm.base.FileTree'],
    init:function(){
    	var me=this;
    	this.control({
    		'erpProductTypeTreePanel':{
    			afterrender:function(panel){
    				var addprokind=Ext.getCmp('addprokind');
    					addprokind.handler=function open(){
    						me.onAssertProductType();
    					};
    				var addrootprokind=Ext.getCmp('addrootprokind');
    					addrootprokind.handler=function begin(){
							me.onAssertProductType(true);
					};
    				if(panel.select==null){
    					var node = panel.getStore().tree.root.childNodes[0];
    					if(node){
	    					panel.getSelectionModel().select(node,true);
	    					productTypeCode = node.data.data;//产品编号
	    					productTypename = node.data.text;
	    					Ext.getCmp('productname').setValue("当前产品类型: "+productTypename);
	        				Ext.getCmp('productid').setValue(productTypeid);	     
    					}
    				}
    			},
    			beforeitemclick:function(tree,record,item,index,e,eOpts){
					if(e.target.className.indexOf('x-tree-expander')>-1){
						return true;
					}
					return false;
				},   			
    			selectionchange:function(model,data, eOpts ){
    				var record = data[0].data;
    				var tree = Ext.getCmp("indexTree");
					productTypeCode = record['data'];//产品编号
					productTypeDes = record['qtip'];
					productTypeid = record['id'];
					productTypename = record['text'];
					productTypeIsLeaf = record['leaf'];
    				Ext.getCmp('productname').setValue("当前产品类型: "+productTypename);
    				Ext.getCmp('productid').setValue(productTypeid);
    							  				
				    //根据产品类型显示不同的内容
				    var grid=Ext.getCmp('tab').getActiveTab();
				    if(grid.id=='fileIndex'){
				    	 tree.setRootNode();
				    	 
				    }else if(grid.id=='taskbookgrid'){
				    	 grid.setRootNode();
				    }else{
				    	 me.onReload(grid);
				    }
				    
				    Ext.getCmp("toolbartext").setText("<span style='color:red'>当前路径：</span>");
				    Ext.getCmp('indexTree').getSelectionModel().clearSelections(); // 清除选中状态
					Ext.getCmp('addButton').setDisabled(true);
					Ext.getCmp('deleteButton').setDisabled(true);
					Ext.getCmp('changeButton').setDisabled(true);
					Ext.getCmp('addFileButton').setDisabled(true);
    				Ext.getCmp('addprokind').setDisabled(false);
					Ext.getCmp('treeupdate').setDisabled(false);
					Ext.getCmp('treedelete').setDisabled(false);
					
					tree.getSelectionModel().select(tree.getStore().tree.root.childNodes[0],false,false);
     			}
    		},
    		'erpTaskBookTeamplateGrid':{
		   		activate:function(grid){
		   			grid.setRootNode();
		   		},
		   		beforeitemclick:function(tree,record,item,index,e,eOpts){
					if(e.target.className.indexOf('x-tree-expander')>-1){
						return true;
					}
					return false;
				}
		   	},
		   	'erpTaskBookTeamplateGrid #saveBtn':{
		   		click:function(btn){
		   			var grid = btn.ownerCt.ownerCt;
		   			if(me.isDirty(grid)){
		   				me.onSavaGrid(grid);
		   			}else{
		   				Ext.Msg.alert('警告','未修改数据！');
		   			}
		   		}
		   	},
		   	'erpTaskBookTeamplateGrid #clearallBtn':{
		   		click:function(btn){
		   			Ext.Msg.confirm("提示", "请确认是否清除明细数据？", function(optional) {
						if (optional == 'yes') {
							var grid = btn.ownerCt.ownerCt;
							Ext.Ajax.request({
								url : basePath + grid.deleteUrl,
								params : {
									productTypeCode:productTypeCode
								},
								method : 'post',
								callback : function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.success){
										showMessage('提示','清除明细成功！',1000);
										grid.setRootNode();
									} else if(res.exceptionInfo){
										showError(res.exceptionInfo);
										return;
									}
							  	}
							});
						}
					});
		   		}
		   	},
		   	'filetreetrigger[name=docname_temp]':{
		   		focus: function(t){
		   			t.setHideTrigger(false);
  				   	t.setReadOnly(false);
  				   	t.BaseCondition = 'PRJTYPECODE_=\''+productTypeCode+'\'';
		   		}
		   	},
    		'button[id=treeupdate]':{
    			click:function(btn){
    				if(productTypeid==''){
    					showError("请选择要更新的数据！");
    					return;
    				}
    				me.onTreeUpdate(btn);
    			}
    		},
    		'button[id=treedelete]':{
    			click:function(btn){
    				me.onProductTypeDelete(productTypeid,'plm/base/deleteProductKind.action');
    			}
    		},
    		'erpProjectPhaseView button[id=add]':{
    			click:function(btn){
    				var tab=Ext.getCmp('tab').getActiveTab();
    				var grid = btn.ownerCt.ownerCt;
    				var data = grid.store.data.items;
					var detno = 1;
					if(data.length>0){
						detno = data[data.length-1].data['PH_DETNO_TEMP']+1;
					}   				
    				Ext.getCmp('erpProjectPhaseView').store.loadData([{
    					'PRJTYPECODE_':productTypeCode,
    					'PH_DETNO_TEMP':detno
    				}],true);
    			}
    		},
    		'erpProjectPhaseView':{
    			selectionchange:function(self,selected){
    				if(selected.length>0){
    					Ext.getCmp('delete').setDisabled(false);
    				}else{
    					Ext.getCmp('delete').setDisabled(true);
    				}
    			},
    			activate:function(grid){
		   			me.onReload(grid);
		   		}
    		},
    		'erpProjectPhaseView button[id=delete]':{
    			listener:function(btn,grid){
    				if(grid.selModel.getSelection().length<1){
    					//return;
    				}else{
    					var toolbar=Ext.getCmp('tab').getActiveTab().getDockedItems('toolbar[dock="top"]')[0];
    				}
    			},
    			click:function(btn){
    				me.onDeleter();
    			}
    		},
		   	'erpFileTree' : {
				beforeitemclick:function(tree,record,item,index,e,eOpts){
					if(e.target.className.indexOf('x-tree-expander')>-1){
						return true;
					}
					return false;
				},
				select:function(treeview,record,index){
					var tree = Ext.getCmp("indexTree");		
					var grid = Ext.getCmp("treegrid");
					grid.store.load({
						params : {
							id : record.get("id_"),
							kind : 0
						}
					});
					grid.nodeId = record.get("id_");
					
					if (record.data.id_ == -1) {
						Ext.getCmp("toolbartext")
								.setText("<span style='color:red'>当前路径：</span>"
										+ record.get("virtualpath_") + "/"
										+ record.get("name_"));
					} else {
						Ext.getCmp("toolbartext")
								.setText("<span style='color:red'>当前路径：</span>"
										+ record.get("virtualpath_"));
					}
				},
				selectionchange:function(self,selected,eOpts){
					var addbtn = Ext.getCmp('addButton');
					var delbtn = Ext.getCmp('deleteButton');
					var addfilebtn = Ext.getCmp('addFileButton');
					var changebtn = Ext.getCmp('changeButton');
					var savefilbtn = Ext.getCmp('saveFileButton');
					if(selected.length>0){
						addbtn.setDisabled(false);
						delbtn.setDisabled(false);
						changebtn.setDisabled(false);
						addfilebtn.setDisabled(false);
						savefilbtn.setDisabled(false);
					}else{
						addbtn.setDisabled(true);
						delbtn.setDisabled(true);
						changebtn.setDisabled(true);
						addfilebtn.setDisabled(true);
						savefilbtn.setDisabled(true);
					}
				},
				collapse:function(tree){
					Ext.getCmp("toolbartext").show();
				},
				expand:function(tree){
					Ext.getCmp("toolbartext").hide();
				}
			},
			'erpFileTreeGrid':{
				selectionchange:function(self,selected,eOpts){			
					var deletebtn = Ext.getCmp('deleteFileButton');
					var uploadbtn = Ext.getCmp('treegrid').down('filefield');
					if(selected.length>0){
						deletebtn.setDisabled(false);
						if(uploadbtn){
							uploadbtn.setDisabled(false);
						}
					}else{
						deletebtn.setDisabled(true);
						if(uploadbtn){
							uploadbtn.setDisabled(true);
						}
					}
				}
			},
			'erpFileTree button[id=addButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var node = btn.ownerCt.ownerCt.getSelectionModel()
							.getSelection()[0];
					var params = new Object();
					if(node.hasChildNodes()){
						params.detno = node.lastChild.get('detno_')+1;
					}else{
						params.detno = 1;
					}
					
					me.createWin(params, 'create');
				}
			},
			'erpFileTree button[id=addRootButton]' : {
				click : function(btn) {
					var me = this;
					var root = btn.ownerCt.ownerCt.store.getRootNode();
					var params = new Object();
					if(root.hasChildNodes()){
						params.detno = root.lastChild.get('detno_')+1;
					}else{
						params.detno = 1;
					}
					
					me.createWin(params, 'createRoot');
				}
			},
			'erpFileTreeGrid button[id=addFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					var node = Ext.getCmp("indexTree").getSelectionModel()
							.getSelection()[0];
					if (node) {
						var parentId = node.get('id_');
						if (parentId == -1) {
							Ext.Msg.alert("提示", "请先保存目录");
							return;
						}
						var virtualpath = node.get("virtualpath_");
						var newNode = new Object();
						var data = grid.store.data.items;
						var detno = 1;
						if(data.length>0){
							detno = data[data.length-1].data['detno_']+1;
						}
						newNode.data = {
							name_ : "新建文件",
							leaf : true,
							parentid_ : parentId,
							id_ : -1,
							virtualpath_ : virtualpath,
							kind_ : 0,
							remark_ : "",
							code : -1,
							detno_ : detno,
							prjtypecode_ : productTypeCode
						};

						grid.store.loadData([newNode.data], true);
					} else {
						Ext.Msg.alert("提示", "请先选择目录");
					}

				}
			},
			'erpFileTreeGrid button[id=saveFileButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var grid = btn.ownerCt.ownerCt;
					grid.save(grid);

				}
			},
			'erpFileTreeGrid upexcel':{
				beforeimport:function(field){
					var node = Ext.getCmp('indexTree').getSelectionModel().getSelection()[0];
					if(node){
						return true;
					}else{
						Ext.Msg.alert('提示','请先选择目录!');
						field.down('filefield').reset();
						return false;
					}
				}
			},
			'erpFileTree button[id=deleteButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var tree = Ext.getCmp("indexTree");
					var node = tree.getSelectionModel().getSelection()[0];
					if (node) {
						if(node.hasChildNodes()){
							showError('当前目录存在子目录，不允许删除！');
							return;
						}
						var grid = Ext.getCmp("treegrid");
						if(grid.store.data.items.length>0){
							showError('当前目录存在文件清单，不允许删除！');
							return;
						}
						if (node.data.id_ != -1) {
							Ext.Msg.confirm("提示", "确定删除？", function(optional) {
								if (optional == 'yes') {
									me.deleteNode(node.data.id_,
											"index");
									node.remove();
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
			'erpFileTreeGrid button[id=deleteFileButton]':{
				afterrender:function(btn){
					btn.setDisabled(true);
				}
			},
			'erpFileTreeGrid #toolbartext':{
				afterrender:function(tlbar){
					tlbar.hide();
				}
			},
			'erpFileTree button[id=changeButton]' : {
				afterrender:function(btn){
					btn.setDisabled(true);
				},
				click : function(btn) {
					var node = btn.ownerCt.ownerCt.getSelectionModel()
							.getSelection()[0];
					if (node) {
						var params = new Object();
						params.name = node.get('name_');
						params.virtualpath = node.get('virtualpath_');
						params.remark = node.get("remark_");
						params.detno = node.get('detno_');
						params.code = node.get('code_');
						
						me.createWin(params, 'update');
					}
				}
			}
		});
	},
	deleteNode : function(id, type) {
		var tree = Ext.getCmp("indexTree");
		var rootnode = tree.getRootNode();
		var grid = Ext.getCmp("treegrid");
		//记录展开的节点
		tree.checkExpanded(rootnode,tree);
		
		Ext.Ajax.request({
			url : basePath + 'plm/base/deleteNode.action',
			async : false,
			params : {
				id : id,
				type : type,
				productTypeCode : productTypeCode
			},
			callback : function(options, success, response) {
				var res = Ext.decode(response.responseText);
				if (res.success) {
					showMessage('提示', '删除成功', 1000);

					tree.getSelectionModel().clearSelections(); // 清除选中状态
					tree.setRootNode(tree); // 重新加载树
					grid.store.load({
						params : {
							id : -1,
							kind : 0
						}
					});
					// 重新展开树
					var store = tree.getStore();
					tree.expandNodes.forEach(function(item) {
						var node = store.getNodeById(item);
						if (node) {
							node.expand();
						}
					});
					Ext.getCmp("toolbartext").setText("<span style='color:red'>当前路径：</span>");

					Ext.getCmp('addButton').setDisabled(true);
					Ext.getCmp('deleteButton').setDisabled(true);
					Ext.getCmp('changeButton').setDisabled(true);
					Ext.getCmp('addFileButton').setDisabled(true);
				} else {
					Ext.Msg.alert("提示", "未知错误");
				}
			}
		});
	},
	createWin : function(params, type) {
		var win = Ext.getCmp('changeWin');
		if(win){
			win.show();
		}else{
			win = Ext.create('Ext.window.Window', {
				xtype : 'form',
				title : '修改',
				height : '50%',
				width : '42%',
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
					readOnlyCls : 'win-textfield-readOnly',
					value : params ? params.detno : '',
					minValue:1
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
						
						var tree = Ext.getCmp("indexTree");
						var node = tree.getSelectionModel().getSelection()[0];
						var name = Ext.getCmp("changename").value;
						var detno = Ext.getCmp("changedetno").value;
						if (name == null || name.trim() == "") {
							showError("文件名称不能为空");
							return;
						}
						if (detno == null || detno == "") {
							detno = params.detno;
						}
						var code = Ext.getCmp("changecode").value;
						var fieldRemark = Ext.getCmp("fieldRemark").value;
						var parentid;
						if('update'==type){
							parentid = node.get('parentid_');
						}else if('create'==type){
							if(node){
								parentid = node.get('id_');	
							}else{
								parentid = 0;	
							}							
						}else if('createRoot'==type){
							parentid = 0;	
						}
						
						tree.saveTree({
							name_ : name,
							code_ : code,
							remark_ : fieldRemark,
							prjtypecode_ : productTypeCode,
							detno_ : detno,
							virtualpath_ : node ? ('createRoot' == type
									? ''
									: node.get("virtualpath_")) : '',
							id_ : node?node.get("id_"):-1,
							kind_ : -1,
							parentid_ : parentid
						}, type);
	
						var grid = Ext.getCmp("treegrid");
						if ('update' == type) {
							grid.store.load({
								params : {
									id : node.get("id_"),
									kind : 0
								}
							});
						}
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
				}],
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
			});
			win.show();
		}

	},
    //重新加载grid
    onReload:function(grid){
    	 var proxy = grid.getStore().getProxy();
    	 proxy.extraParams={
		    	condition:productTypeCode?'PRJTYPECODE_ = \''+productTypeCode+'\'':''
		 };	
    	 var toolbar=grid.getDockedItems('toolbar[dock="bottom"]')[0];
			if(toolbar){
				toolbar.moveFirst();
			}
    },
   	//child root
    onAssertProductType:function(isroot,handtype,type){
    	var me=this;
    	var win = new Ext.window.Window({
    		id : 'win',
    		height: '320',
    		width: '500',
    		maximizable : true,
    		buttonAlign : 'center',
    		layout : 'anchor',
    		title:'<h3>产品类型</h3>',
    		buttonAlign:'center',
    		bodyStyle : 'background:#F2F2F2;',
			border:false,
    		items: [{
    			bodyPadding: 5,
    			id:'baseform',
    			anchor: '100% 100%',
    			layout: 'anchor',
    			xtype: 'form',
    			bodyStyle : 'background:#F2F2F2;',
				border:false,
    			saveUrl:'plm/base/saveProductKind.action',
    			updateUrl:'plm/base/updateProductKind.action',
    			deleteUrl:'plm/base/deleteProductKind.action',
    			defaults: {
    				anchor: '100%',   		
    				xtype:'textfield'
    			},
    			items: [{
    				fieldLabel: '产品编号',
    				name: 'pt_code',
    				id:'pt_code',
    				allowBlank: true,
    				readOnly:true,
    				hidden:true,
    				fieldStyle : 'background:#E6E6E6'
    			},{
    				fieldLabel: '产品类型',
    				name: 'pt_name',
    				id:'pt_name',
    				allowBlank: false
    			},{
    				fieldLabel: '描述',
    				name: 'pt_description',
    				id:'pt_description',
    				height:100,
    				xtype:'textarea',
    				allowBlank: true
    			},{
    				hidden:true,
    				name:'pt_id',
    				id:'pt_id',
    				allowBlank: true
    			}],
    			dockedItems: [{
    				xtype: 'toolbar',
    				dock: 'bottom',
    				items:['->',{
    					xtype:'button',
    					text: $I18N.common.button.erpSaveButton,
    					iconCls: 'x-button-icon-save',
    					cls: 'x-btn-gray',
    					formBind: true,//form.isValid() == false时,按钮disabled
    					width: 60,
    					style: {
    						marginLeft: '10px'
    					},
    					handler:function(){
    						if(!handtype){
    							me.onSave(isroot,'tree');
    						}else me.onSave(isroot,'tree',handtype);
    					}
    				},{
    					xtype:'button',
    					text: $I18N.common.button.erpCloseButton,
    					iconCls: 'x-button-icon-close',
    					cls: 'x-btn-gray',
    					width: 60,
    					style: {
    						marginLeft: '10px'
    					},
    					handler:function(btn){
    						btn.ownerCt.ownerCt.ownerCt.close();
    					} 
    				},'->']
    			}]
    		}]
    	});	    	    
    	win.show();
    },
    onSave:function(isroot,type,handtype){
    	var me=this;
    	var form=Ext.getCmp('baseform');
    	var tree=Ext.getCmp('producttype');
    	var parentId =0;
    	var url = '';
    	if(handtype&&handtype=='update'){
    		url=form.updateUrl; 
    		var store = tree.getStore();
    		var node = store.getNodeById(productTypeid);
    		var id = node.parentNode.data.id;
    		parentId = id?id:0;
    	}else{
    		productTypeid = productTypeid?productTypeid:0;
    		parentId = productTypeid;
    		url=form.saveUrl; 
    	}
 
    	Ext.Ajax.request({
    		method:'POST',
    		params:{
    			formStore:unescape(Ext.JSON.encode(form.getValues()).replace(/\\/g,"%")),
    			parentId:isroot?0:parentId
    		},
    		url:basePath+url,
    		callback:function(options,success,response){
    			var productType = new Object();
    			var res = new Ext.decode(response.responseText);
    			if(res.productType){
    				productType=res.productType;
    				productType.loaded=true;
    				productType.id=productType.pt_id;
    			}
    			if(res.exceptionInfo != null){
    				showError(res.exceptionInfo);return;
    			}else if(res.success){
    				if(handtype&&handtype=='update'){
    					updateSuccess();
    				}else {
    					saveSuccess();
    				}
    				Ext.getCmp('win').close();
    				if(type=='tree'){
    					var tree=Ext.getCmp('producttype');
    					tree.getTreeRootNode(tree);
    					if(tree.select==null){
	    					var node = tree.getStore().tree.root.childNodes[0];
	    					if(node){
		    					tree.getSelectionModel().select(node,true);
		    					productTypeCode = node.data.data;//产品编号
		    					productTypename = node.data.text;
		    					Ext.getCmp('productname').setValue("当前产品类型: "+productTypename);
		        				Ext.getCmp('productid').setValue(productTypeid);		     
	    					}
    				}
    				}else if(type=='grid'){
    					me.loadnewStore();
    				}
    			} 
    		}
    	});
    },
    onProductTypeDelete:function(id,url){
    	var tree = Ext.getCmp('producttype');
    	var store = tree.getStore();
    	console.log(store);
    	console.log(id);
    	var node = store.getNodeById(id);
    	console.log(node);
    	if(!node.isLeaf()){
    		showError('该产品存在子类型，限制删除！');
    		return;
    	}
    	Ext.Msg.confirm("删除","请确认是否删除此产品类型？",function(btn){
    		if(btn=='yes'){
    			Ext.Ajax.request({
    	    		method:'POST',
    	    		params:{
    	    			id:id?id:0
    	    		},
    	    		url:basePath+url,
    	    		callback:function(options,success,response){
    	    			var res = new Ext.decode(response.responseText);
    	    			if(res.exceptionInfo != null){
    	    				showError(res.exceptionInfo);return;
    	    			}else if(res.success){	    	
    	    				delSuccess();//@i18n/i18n.js
    	    				var tree=Ext.getCmp('producttype');
    	    				tree.getTreeRootNode(tree);
    	    				var firstchild = tree.getStore().tree.root.childNodes[0];
    	    				if(firstchild){
	    	    				tree.getSelectionModel().select(firstchild,true);
	    	    				tree.fireEvent('itemmousedown',tree.getView(),tree.getStore().tree.root.childNodes[0]);
    	    				}else{
    	    					Ext.getCmp('addprokind').setDisabled(true);
								Ext.getCmp('treeupdate').setDisabled(true);
								Ext.getCmp('treedelete').setDisabled(true);	
    	    				}
    	    			} else {
    	    				delFailure();
    	    			}
    	    		}
    	    	});
    		}
    	});
    },
    onTreeUpdate:function(btn){
    	var tree=btn.ownerCt.ownerCt,me=this,selected=tree.select;
    	me.onAssertProductType('','update'); 
    	var form=Ext.getCmp('baseform');
    	form.items.items[0].setValue(productTypeCode);
    	form.items.items[1].setValue(productTypename);
    	form.items.items[2].setValue(productTypeDes);
    	form.items.items[3].setValue(productTypeid);
    },
    getMultiSelected: function(grid){
        var items = grid.selModel.getSelection();
        grid.multiselected=new Array();
        Ext.each(items, function(item, index){
        if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        	&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
           		grid.multiselected.push(item);
        	}
        });
   		var records=Ext.Array.unique(grid.multiselected);	 
   			var data = new Array();
   			Ext.each(records, function(record, index){
   				if(grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
   	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
   					bool = true;
   					var o = new Object();
   					o[grid.keyField] = record.data[grid.keyField];
   					data.push(o);
   				}
   			});
   	    return Ext.encode(data);
   	},
   	loadnewStore:function(record){
        if(record.isExpanded() && record.childNodes.length > 0) { //是根节点，且已展开
		    record.collapse(true, true); //收拢
        } else {if(record.childNodes.length == 0) {
    	Ext.Ajax.request({
        	url : basePath + 'plm/base/getRootProductType.action',
        	async: false,
        	params:{parentid:productTypeid},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;
        			Ext.Array.each(tree, function(tr) {
                        tr.cls="x-tree-cls-node";
      					tr.pt_id = tr.id;
      					if(tr.data){
      					}
                    });
        			record.appendChild(tree);
        			Ext.each(record.childNodes, function(){
						this.dirty = false;
					});	
        			record.expand(false, true); 
        		} else if(res.exceptionInfo){showError(res.exceptionInfo);}
        	}
        });
    	}else{record.expand(false, true); }}
   	},
   	//刷新
   	onRefresh:function(){
		var toolbar=Ext.getCmp('tab').getActiveTab().getDockedItems('toolbar[dock="bottom"]')[0];
		toolbar.doRefresh();
   	},
  	//maz 产品阶段计划批量删除
	onDeleter:function(){
		   var me=this;
		   var grid=Ext.getCmp('tab').getActiveTab();
		   var id=me.getMultiSelected(grid);
		   if(id=="[]"){
			   var selectionModel = grid.getSelectionModel();
	           var store=grid.getStore();
	           store.remove(selectionModel.getSelection());
			   return;
		   }
		   Ext.Ajax.request({
			   method:'POST',
			   params:{
				   id:id 
			   },
			   url:basePath+grid.deleteUrl,
			   callback:function(options,success,response){
				   var res = new Ext.decode(response.responseText);
				   if(res.exceptionInfo != null){
					   showError(res.exceptionInfo);return;
				   }else if(res.success){
					   delSuccess(me.onRefresh());//@i18n/i18n.js
				   } else {
					   delFailure();
				   }
			   }

		   });
	  	},
  	//保存列表
  	onSavaGrid:function(grid){
		var data = grid.getTreeGridData();
		for(var i=0;i<data.length-1;i++){
			if(data[i]['docId_temp']&&data[i]['docId_temp']!=''){
				var files = data[i]['docId_temp'].split(',');
			}
			if(files&&files!=''){
				for(var j=i+1;j<data.length;j++){			
					if(data[j]['docId_temp']&&data[j]['docId_temp']!=''){
						var docs = data[j]['docId_temp'].split(',');
					}
					if(docs&&docs!=''){
						for(var m=0;m<files.length;m++){
							for(var n=0;n<docs.length;n++)
							if(files[m]==docs[n]){
								showError('同一份文件同时关联多个任务，不允许保存！');
								return;
							}
						}
						docs='';
					}			
				}
				files ='';
			}
			var taskclass = data[i]['taskclass_temp'];
			var pretaskdetnos = data[i]['pretaskdetno_temp'];
			if(taskclass !=null && taskclass != "" && pretaskdetnos!=null && pretaskdetnos!= ""){
				for(var j=0;j<data.length-1;j++){
					var pretaskdetno = new Array();
					pretaskdetno = pretaskdetnos.split(",");
					for(var k = 0;k<pretaskdetno.length;k++){
						if(data[j]['detno_temp']==pretaskdetno[k]){
							if(data[j]['taskclass_temp']!=taskclass){
								showError('正式立项任务不能将预立项任务作为前置任务，正式立项任务也不能作为预立项任务的前置任务,不允许保存！');
								return false;
							}
						}
					}
				}
			}
		}
		data = Ext.encode(data);
		Ext.Ajax.request({
			url : basePath + grid.saveUrl,
			params : {
				productTypeCode:productTypeCode,
				gridStore:data
			},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					showMessage('提示','保存成功',1000);
					//saveSuccess();
					grid.setRootNode();
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				} else{
					saveFailure();//@i18n/i18n.js
					return;
				}
		  	}
		});
	 },
	 isDirty: function(grid) {
	 	var me = this;
		var store = grid.getStore();
		var bool = false;
		var root = store.getRootNode();
		if(root.hasChildNodes()){
			bool = me.getChildNodes(root,bool);			
		}
		return bool;
	},
	getChildNodes:function(node,bool){
		var me = this;
		var ChildNodes = node.childNodes;
		for(var i=0;i<ChildNodes.length;i++){
			if(ChildNodes[i].dirty==true){
				bool = true;
			}
			if(bool){
				return bool;
			}else if(ChildNodes[i].hasChildNodes()){
				bool = me.getChildNodes(ChildNodes[i],bool);
			}
		}
		return bool;
	}
});