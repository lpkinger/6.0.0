Ext.define('erp.view.vendbarcode.main.vendbarcodeTreePanel', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.vendbarcodeTreePanel',
	id : 'vendbarcodeTreePanel',
	 region: 'west', 
	 width : '20%',
	 
	margins : '0 0 -1 1',
	border : false,
	enableDD : false,
	split : true,
    title: $I18N.common.main.navigation,
	toggleCollapse : function() {
		if (this.collapsed) {
			this.expand(this.animCollapse);
		} else {
			this.title = $I18N.common.main.navigation;
			this.collapse(this.collapseDirection, this.animCollapse);
		}
		return this;
	},
	singleExpand : true,
	rootVisible : false,
	containerScroll : true,
	// collapsible : true,
	autoScroll : false,
	useArrows : true,
	bodyStyle : 'background-color:#f1f1f1;',
	store : 'TreeStore',
	hideHeaders : true,
	columnLines : false,
	initComponent : function() {
		var me = this;
		me.columns = [
				{
					xtype : 'treecolumn',
					dataIndex : 'text',
					flex : 1
				},
				{
					xtype : 'actioncolumn',
					width : 24,
					icon : (window.basePath || '')
							+ 'resource/images/tree/add2.png',
					iconCls : 'x-hidden',
					renderer : function(val, meta, record) {
						meta.tdCls = record.get('cls');
						if (record.get('addurl') != null
								|| (record.raw && record.raw.addurl))
							meta.tdAttr = record.get('leaf') ? 'data-qtip="新增'
									+ record.get('text') + '"'
									: 'data-qtip="查看导航图"';
					},
					handler : Ext.bind(me.handleAddClick, me)
				} ];
		this.getTreeRootNode(0);// 页面加载时，只将parentId = 0的节点加载进来
		this.callParent(arguments);
		me.addEvents('addclick');
	},
	getTreeRootNode : function(parentId) {
		var json = {};
		Ext.Ajax.request({
			url : basePath + 'resource/uucloud/vendbarcode.json',
			async : true,
			success : function(response) {
				var text = response.responseText;
				json = new Ext.decode(text);
				Ext.getCmp('vendbarcodeTreePanel').store.setRootNode({
					text : 'text',
					id : 'root',
					expanded : true,
					children : json 
				});
				//默认打开采购单(未送货)
            	var tree = Ext.getCmp('vendbarcodeTreePanel');
            	var rootNode = tree.getRootNode();
            	tree.expandAll();
            	tree.fireEvent('itemclick',tree.getSelectionModel(),tree.getRootNode().getChildAt(0).getChildAt(1));
            }
		});
		
	},
	/*tbar : {
		xtype : 'erpTreeToolbar'
	},*/
	openCloseFun : function() {
		var o = Ext.getCmp("open");
		var c = Ext.getCmp("close");
		var tree = Ext.getCmp('vendbarcodeTreePanel');
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
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll,
						scroller);
			}
		}
	},
	handleAddClick : function(gridView, rowIndex, colIndex, column, e) {
		this.fireEvent('addclick', gridView, rowIndex, colIndex, column, e);
	},
});