Ext.define('erp.view.scm.product.ProductKindTree',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.prodkindtree', 
	id: 'tree-panel', 
	border : false, 
	enableDD : false, 
	split: true, 
	width : '100%',
//	height: '100%',
	singleExpand: true,
	expandedNodes: [],
	toggleCollapse: function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = $I18N.common.main.navigation;
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	store: Ext.create('Ext.data.TreeStore', {
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
	bodyStyle:'background-color:#f1f1f1;',
	//需要物料种类维护能查看失效
	allKind:false,
	hideHeaders: true,
	initComponent : function(){ 
		var me = this;
		if(me.isAnother){
			me.columns =[{
		        xtype: 'treecolumn',
		        dataIndex: 'text',
		        flex: 1
		    }, {
		    	xtype: 'actioncolumn',
			    width: 24,
			    icon: (window.basePath || '') + 'resource/images/upgrade/bluegray/icon/add1.png',
			    iconCls: 'x-hidden x-tree-node-icon',
			    handler: function(treeview, rowIdx, colIdx, e) {
			    	me.clickTime = new Date();
			    	var record = treeview.store.data.get(rowIdx);
			    	me.addItem(record);
			    }
		    }, {
		    	xtype: 'actioncolumn',
			    width: 38,
			    icon: (window.basePath || '') + 'resource/images/upgrade/bluegray/icon/delete1.png',
			    iconCls: 'x-hidden x-tree-node-icon',
			    handler: function(treeview, rowIdx, colIdx, e) {
			    	me.clickTime = new Date();
			    	var record = treeview.store.data.get(rowIdx);
			    	me.deleteItem(record);
			    }
		    }];
		}
		me.getTreeRootNode(0);
		me.callParent(arguments);
	},
	getTreeRootNode: function(parentid){
		var url = this.getUrl();
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + url,
        	params: {
        		parentid: parentid,
        		allKind : this.allKind
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.tree){
        			var tree = res.tree;       			
                	Ext.getCmp('tree-panel').store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	getUrl: function(){
		type = type || 'Product';
		var url = 'scm/product/getProductKindTree.action';
		switch (type) {
			case 'Vendor': 
				url = 'scm/purchase/getVendorKindTree.action';break;
			case 'Customer': 
				url = 'scm/sale/getCustomerKindTree.action';break;	
			case 'FeePlease!YZSYSQ': 
				url = 'oa/fee/getContractTypeTree.action';break;	
		}
		return url;
	},
    openCloseFun: function(){
	 	  var o = Ext.getCmp("open");
	 	  var c = Ext.getCmp("close");
	 	  var tree = Ext.getCmp('tree-panel');
	 		  if(o.hidden==false&&c.hidden==true){
	 			  tree.expandAll();
	 			  o.hide();
	 			  c.show();
	 		  }else{
	 			  tree.collapseAll();
	 			  o.show();
	 			  c.hide();
	 		  }
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		itemmouseenter: function(th, record, item, index, e, eOpts) {
			var imgs = item.getElementsByClassName('x-tree-node-icon');
			Ext.Array.each(imgs, function(img) {
				img.classList.remove('x-hidden');
			});
		},
		itemmouseleave: function(th, record, item, index, e, eOpts) {
			var imgs = item.getElementsByClassName('x-tree-node-icon');
			Ext.Array.each(imgs, function(img) {
				img.classList.add('x-hidden');
			});
		},
		beforeitemexpand: function(p, animate, eOpts) {
			var treePanel = Ext.getCmp('tree-panel');
			if(treePanel.clickTime) {
				treePanel.clickTime = null;
				return false;
			}else {
				treePanel.clickTime = null;
				return true;
			}
		},
		beforeitemcollapse: function(p, animate, eOpts) {
			var treePanel = Ext.getCmp('tree-panel');
			if(treePanel.clickTime) {
				treePanel.clickTime = null;
				return false;
			}else {
				treePanel.clickTime = null;
				return true;
			}
		}
	},
	/**
	 * 找到所有已展开的节点，包括当前被选中的节点
	 * @param record 当前被选中的节点
	 */
	getExpandedItems: function(record){
		var me = this;
		me.getRecordParents(record);
		if(record.isLeaf()){
			me.expandedNodes.push(record);
		}
	},
	getRecordParents: function(record, parent){
		var me = this;
		if(!parent){
			parent = me.store.tree.root;
			me.expandedNodes = [];
		}
		if(parent.childNodes.length > 0){
			Ext.each(parent.childNodes, function(){
				if(this.isExpanded()){
					me.expandedNodes.push(this);
					if(this.childNodes.length > 0){
						me.getRecordParents(record, this);
					}
				}
			});
		}
	},
	getExpandItem: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
		}
		var node = null;
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.isExpanded()){
					node = this;
					if(this.childNodes.length > 0){
						var n = me.getExpandItem(this);
						node = n == null ? node : n;
					}
				}
			});
		}
		return node;
	},
	addItem: function(record) {
		var treegrid = Ext.getCmp('tree-panel');
		if(record.isLeaf() == true){
			if(record.data['id'] == null || record.data['id'] == ''){
				showError('请先描述该节点');
			} else {
				record.data['leaf'] = false;
				record.data['cls'] = 'x-tree-cls-parent';
				record.dirty = true;
			}
		}
		var o = {
			cls: "x-tree-cls-node",
			parentId: record.data['id'],
			leaf: true,
			level: (record.data['depth'] + 1),
			allowDrag: true
		};
		record.appendChild(o);
		if(!record.isExpanded()) {
			record.expand();
		}
	},
	deleteItem: function(record) {
		var me = this;
		if(record.data['pk_id'] != null || record.data['pk_id'] != ''){
			if(record.isLeaf() == true){
				warnMsg('确定删除节点[' + record.data['text'] + ']？', function(btn){
					if(btn == 'yes'){
						me.deleteNode(record);
    				} else if(btn == 'no'){
    					return;
    				} 
				});
			} else {
				warnMsg('确定删除节点[' + record.data['text'] + ']及其子节点？', function(btn){
					if(btn == 'yes'){
						me.deleteNode(record);
    				} else if(btn == 'no'){
    					return;
    				} 
				});
			}
		} else {
			record.remove(true);
		}
	},
	deleteNode: function(record){
		var me = this;
		if(record.data['id'] && record.data['id'] != ''){			
			var form = Ext.getCmp('form');
			if(form.deleteUrl.indexOf('caller=') == -1){
				form.deleteUrl = form.deleteUrl + "?caller=" + caller;
			}
			me.up('viewport').el.mask('处理中,请稍后...')
			Ext.Ajax.request({
				url : basePath + form.deleteUrl,
				params: {
					id: record.data['id']
				},
				method : 'post',
				callback : function(options,success,response){
					me.up('viewport').el.unmask();
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						showError(localJson.exceptionInfo);return;
					}
					if(localJson.success){
						delSuccess(function(){	
							window.location.href = window.location.href;
						});//@i18n/i18n.js
					}else {
						delFailure();
					}
				}
			});
		} else {
			record.remove(true);
		}
	},
});