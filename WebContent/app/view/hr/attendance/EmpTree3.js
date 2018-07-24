Ext.define('erp.view.hr.attendance.EmpTree3', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.EmpTree3',
	id : 'tree-panel',
	border : false,
	width : '100%',
	height : '100%',
	expandedNodes : [],
	title : "<font color=#a1a1a1; size=2>设置员工默认班次</font>",
	rootVisible : false,
//	singleExpand : false,
	animate : true,
	collapsible : true,
	autoScroll : false,
	useArrows : true,
	store : Ext.create('Ext.data.TreeStore', {
		root : {
			text : 'root',
			id : 'root',
			expanded : true
		}
	}),
    dockedItems: [{
    	id : 'toolbar',
        xtype: 'erpEmpToolbar3',
        dock: 'right',
        displayInfo: true
	}],
	bodyStyle : 'background-color:#f1f1f1;',
	initComponent : function() {
		this.getTreeRootNode(0);
		this.callParent(arguments);
	},
	getTreeRootNode : function(parentid) {
		Ext.Ajax.request({// 拿到tree数据
			url : basePath + 'hr/attendance/getAllHrOrgsTreeWDM.action',
			params : {
				caller:caller
			},
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.tree) {
					var tree = res.tree;
					Ext.getCmp('tree-panel').store.setRootNode({
						text : 'root',
						id : 'root',
						expanded : true,
						children : tree
					});
				} else if (res.exceptionInfo) {
					showError(res.exceptionInfo);
				}
				Ext.getCmp('tree-panel').listenerNode();
			}
		});
	},
	listenerNode : function(node) {
		var me = this;
		var n = node || Ext.getCmp('tree-panel').store.tree.root;
		Ext.each(n, function(e) {
			e.on('beforecollapse', function(p, o) {
			});
			if (e.data['leaf'] == false) {
				me.listenerNode(e);
			}
		});
	},
	openCloseFun : function() {
		var o = Ext.getCmp("open");
		var c = Ext.getCmp("close");
		var tree = Ext.getCmp('tree-panel');
		if (o.hidden == false && c.hidden == true) {
			tree.expandAll();
			o.hide();
			c.show();
		} else {
			tree.collapseAll();
			o.show();
			c.hide();
		}
	},
	listeners : {// 滚动条有时候没反应，添加此监听器
		scrollershow : function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);
			}
		}
	},
	/**
	 * 找到所有已展开的节点，包括当前被选中的节点
	 * 
	 * @param record
	 *            当前被选中的节点
	 */
	getExpandedItems : function(record) {
		var me = this;
		me.getRecordParents(record);
		if (record.isLeaf()) {
			me.expandedNodes.push(record);
		}
	},
	getRecordParents : function(record, parent) {
		var me = this;
		if (!parent) {
			parent = me.store.tree.root;
			me.expandedNodes = [];
		}
		if (parent.childNodes.length > 0) {
			Ext.each(parent.childNodes, function() {
				if (this.isExpanded()) {
					me.expandedNodes.push(this);
					if (this.childNodes.length > 0) {
						me.getRecordParents(record, this);
					}
				}
			});
		}
	}/*,
	getExpandItem : function(root) {
		var me = this;
		if (!root) {
			root = this.store.tree.root;
		}
		var node = null;
		if (root.childNodes.length > 0) {
			Ext.each(root.childNodes, function() {
				if (this.isExpanded()) {
					node = this;
					if (this.childNodes.length > 0) {
						var n = me.getExpandItem(this);
						node = n == null ? node : n;
					}
				}
			});
		}
		return node;
	}*/
});