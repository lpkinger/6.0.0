Ext.define('erp.controller.sys.step.BasicController', {
	extend: 'Ext.app.Controller',
	id:'BasicController',
	views:['sys.base.BasicPortal','sys.pr.ProductKindTree'],
	init:function(){
		var me=this;console.log('BasicController');
		this.control({
			'menuitem[itemId=topProductKind]':{
				click:me.handleTopProductKindClick
			},
			'menuitem[itemId=addProductKind]':{
				click:me.handleAddProductKindClick
			},
			'productkindtree': { 
				/*itemmousedown: function(selModel, record){
					if(!this.flag){
						return;
					}
					this.flag = false;
					var treegrid = Ext.getCmp('productkindtree');
					treegrid.selModel.select(record);
					setTimeout(function(){
						me.flag = true;
						me.loadNode(selModel, record);
					},20);

				},*/
				itemmouseenter: me.showActions,
				itemmouseleave: me.hideActions,
				beforeitemmouseenter:me.showActions,
				removeclick:me.handleRemoveClick,
				speexpandclick:me.handleSpeExpandClick
			}
			
			
		});
		var app=erp.getApplication();
		var basicportal =  Ext.widget('basicportal');
		
			activeItem.add(basicportal);
			/*var app=erp.getApplication();
			var productportal = activeItem.child('productportal');
			if(!productportal){
				productportal =  Ext.widget('productportal',{desc:'物料管理'});
				activeItem.add(productportal);
				Ext.getCmp('syspanel').setTitle(productportal.desc);
			}*/
		/*var hrportal = activeItem.child('hrportal');
		if(!hrportal){
			var hrportal =  Ext.widget('hrportal',{desc:'组织人员'});
			activeItem.add(hrportal);
		}*/
			
	},
	handleTopProductKindClick:function(c,e){
		this.addKind(true,c,'top');
	},
	handleAddKindClick: function(component, e) {
		this.addKind(true,component);
	},
	handleAddProductKindClick:function(c,e){
		this.addKind(true,c);
	},
	addKind:function(leaf,btn,type){
		var me = this,
		productkindtree= Ext.getCmp('productkindtree'),
		cellEditingPlugin = productkindtree.cellEditingPlugin,
		selectionModel = productkindtree.getSelectionModel(),
		selectedList = selectionModel.getSelection()[0];
		var parentId=selectedList && !type?selectedList.get('pk_id'):0;
		selectedList=parentId==0?productkindtree.getRootNode():selectedList;
		var pkind =this.getNewProductKind(parentId,btn);
		expandAndEdit = function() {
			if(selectedList.isExpanded()) {
				selectionModel.select(pkind);
				me.addedNode = pkind;
				cellEditingPlugin.startEdit(pkind, 0);
			} else {
				productkindtree.on('afteritemexpand', function startEdit(list) {
					if(list === selectedList) {
						selectionModel.select(newList);
						me.addedNode = newList;
						cellEditingPlugin.startEdit(pkind, 0);
						// remove the afterexpand event listener
						productkindtree.un('afteritemexpand', startEdit);
					}
				});
				selectedList.expand();
			}
		};

		selectedList.appendChild(pkind);
		productkindtree.getStore().sync();
		if(productkindtree.getView().isVisible(true)) {
			expandAndEdit();
		} else {
			productkindtree.on('expand', function onExpand() {
				expandAndEdit();
				listTree.un('expand', onExpand);
			});
			productkindtree.expand();
		}
	},
	getNewProductKind:function(parentId,btn){
		var pkind=new Object();
		parentId=parentId?parentId:0;
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'scm/sale/addProductKindByParent.action',
			params: {
				parentId: parentId
			},
			async:false,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.pkind){
					pkind=res.pkind;
					pkind.loaded=true;
					pkind.id=pkind.pk_id;
				} 
			}
		});
		return pkind;
	}, 
	showActions: function(view, list, node, rowIndex, e) {
		var icons = Ext.DomQuery.select('.x-action-col-icon', node),record=view.getRecord(node);
		Ext.each(icons, function(icon){
			Ext.get(icon).removeCls('x-hidden');
		});
	},
	hideActions: function(view, list, node, rowIndex, e) {
		var icons = Ext.DomQuery.select('.x-action-col-icon', node),record=view.getRecord(node);
		Ext.each(icons, function(icon){
			Ext.get(icon).addCls('x-hidden');
		});
	},
	handleRemoveClick: function(view, rowIndex, colIndex, column, e) {
		var record=view.getRecord(view.findTargetByEvent(e)),title=record.get('pk_code'), kindTree = Ext.getCmp('productkindtree'),
		selModel = kindTree.getSelectionModel();
		Ext.Msg.confirm('删除数据?', '确定要删除当前选中种类 ('+title+')?',
				function(choice) {
			if(choice === 'yes') {
				Ext.Ajax.request({//拿到tree数据
					url : basePath + 'scm/sale/deleteProductKind.action',
					params: {
						id: record.get('id')
					},
					async:false,
					callback : function(options,success,response){
						var res = new Ext.decode(response.responseText);
						if(res.success){
							showResult('提示','删除种类('+title+') 成功!');
							record.parentNode.removeChild(record);
							if (!selModel.hasSelection()) {
								selModel.select(0);
							}				                 
						} 
					}
				});
			}
		});   

	},
	handleSpeExpandClick:function(record){
		this.loadNode(record);
		var treegrid = Ext.getCmp('productkindtree');
		treegrid.selModel.select(record);
	},
	loadNode: function(record){
		var me = this;
		if ( record.data['pk_id']) { 
			if(record.isExpanded() && record.childNodes.length > 0){
				me.flag = true;
			} else {	
				if(record.childNodes.length == 0){
					Ext.Ajax.request({//拿到tree数据
						url : basePath + 'scm/product/getProductKindTree.action',
						params: {
							parentid: record.data['id']
						},
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.tree && res.tree.length>0){
								var tree = res.tree;
								Ext.each(tree, function(t){
									t.pk_id = t.id;
									t.pk_code=t.data.pk_code;
									t.pk_name=t.data.pk_name;
									t.leaf=false;
									t.data = null;

								});
								me.flag=true;
								record.appendChild(tree);							
								Ext.each(record.childNodes, function(){
									this.dirty = false;
								});
							} else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});
				} else {
					me.flag=true;			
				}
			}
		}
	}
});