Ext.define('erp.view.ma.SysCheckTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpSysCheckTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
 	rootVisible: false, 
	singleExpand: true,
	containerScroll : true, 
	collapsible : true, 
	autoScroll: true,
    store: Ext.create('Ext.data.TreeStore', {
    	fields:[
				{
					type : 'int',
					name : 'orgid'
				},
				{
					type : 'string',
					name : 'orgname'
				},
				{
					type : 'int',
					name : 'warncount'
				},
				{
					type : 'int',
					name : 'publishcount'
				},
				{
					type : 'int',
					name : 'publishamountcount'
				},
				{
					type : 'string',
					name : 'orgheader'
				},
				{
					type : 'string',
					name : 'details'
				} ],
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
    columns: [ {
		name : 'orgid',
		dataIndex : 'orgid',
		id : 'orgid',
		header : '组织ID',
		width : 0,
		cls : 'x-grid-header-1'
	}, {
		name : 'orgname',
		id : 'orgname',
		dataIndex : 'orgname',
		xtype:'treecolumn',
		header : '对象名称',
		flex:1,
		cls : 'x-grid-header-1'
	}, {
		name : 'warncount',
		id : 'warncount',
		dataIndex : 'warncount',
		header : '提醒总数',
		summaryType : 'sum',
		align:'center',
		cls : 'x-grid-header-1',
	}, {
		name : 'publishcount',
		id : 'publishcount',
		dataIndex : 'publishcount',
		header : '处罚总数',
		align:'center',
		summaryType : 'sum',
		cls : 'x-grid-header-1'
	}, {
		name : 'publishamountcount',
		id : 'publishamountcount',
		dataIndex : 'publishamountcount',
		header : '处罚分总数',
		align:'center',
		summaryType : 'sum',
		cls : 'x-grid-header-1'
	}, {
		name : 'orgheader',
		id : 'orgheader',
		dataIndex : 'orgheader',
		header : '组织负责人',
		cls : 'x-grid-header-1'
	}, {
		name : 'details',
		id : 'details',
		dataIndex : 'details',
		header : '详细信息',
		width:0,
		cls : 'x-grid-header-1'
	} ],
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.getTreeGridNode();
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getTreeGridNode: function(condition){
		var me = this;
		var activeTab = me.getActiveTab();
		activeTab.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'ma/SysCheck/getTreeData.action',
        	async: false,
        	params: {
        		condition: condition, 
        		parentid:0
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.data){
        			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		/*	Ext.each(tree, function(d){
        				d.parentId = d.po_parentid;
        				d.cls = 'x-tree-cls-node';
        				d.leaf = d.po_isleaf == 'T';
        				if(!d.leaf){
        					d.cls = 'x-tree-cls-root';
        				}
        			});*/
        			me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        			Ext.each(me.store.tree.root.childNodes, function(){
        				this.dirty = false;
        			});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	checkChild: function(record){
		var me = this;
		if(!record.data['leaf']){
			if(record.childNodes.length > 0){
				if(record.data['po_id'] == null || record.data['po_id'] == ''){
					warnMsg('如果在节点' + record.data['po_powername'] + '下添加子节点，需先保存该节点，是否保存?', function(btn){
						if(btn == 'yes'){
							if(items[0].data['po_powername'] == null || items[0].data['po_powername'] == ''){
								showError('请先描述该节点');
								return;
							} else {
								me.saveNodes.push(items[0]);
								me.saveNode();
							}
	    				} else if(btn == 'no'){
	    					return;
	    				} 
					});
				}
				Ext.each(record.childNodes, function(){
					me.checkChild(this);
				});
			}
		} else {
			if(record.dirty){
				if(record.data['po_id'] == null || record.data['po_id'] == ''){
					me.saveNodes.push(record);
				} else {
					me.updateNodes.push(record);
				}
			}
		}
	},
	saveNode: function(){
		var me = this;
		me.getUpdateNodes();
		var save = new Array();
		var update = new Array();
		var index = 0;
		Ext.each(me.saveNodes, function(){
			if(this.data.po_powername != null && this.data.po_powername != ''){
				var o = {
						po_id: this.data.po_id,
						po_powername: this.data.po_powername,
						po_parentid: this.data.po_parentid,
						po_isleaf: this.data.po_isleaf,
						po_code: this.data.po_code
				};
				save[index++] = Ext.JSON.encode(o);
			}
		});
		index = 0;
		Ext.each(me.updateNodes, function(){
			if(this.data.po_powername != null && this.data.po_powername != ''){
				var o = {
						po_id: this.data.po_id,
						po_powername: this.data.po_powername,
						po_parentid: this.data.po_parentid,
						po_isleaf: this.data.po_isleaf,
						po_code: this.data.po_code
				};
				update[index++] = Ext.JSON.encode(o);
			}
		});
		if(save.length > 0 || update.length > 0){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'ma/savePower.action',
	        	params: {
	        		save: unescape(save.toString().replace(/\\/g,"%")),
	        		update: unescape(update.toString().replace(/\\/g,"%"))
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			me.saveNodes = [];
	        			me.updateNodes = [];
	        			me.getTreeGridNode(0);
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
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
	deleteNode: function(record){
		var me = this;
		if(record){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'ma/deletePower.action',
	        	params: {
	        		id: Number(record.data['po_id'])
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			record.remove(true);
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
		}
	},
	getUpdateNodes: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
			me.updateNodes = [];
		}
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.dirty){
					if(this.data['po_id'] != null && this.data['po_id'] != ''){
						me.updateNodes.push(this);
					}
				}
				if(this.data['leaf'] == false && this.childNodes.length > 0){
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(root.data['po_id'] != null && root.data['po_id'] != ''){
					me.updateNodes.push(root);
				}
			}
		}
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	}
});