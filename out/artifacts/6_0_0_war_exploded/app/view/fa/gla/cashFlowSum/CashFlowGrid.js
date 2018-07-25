/**
 * ERP项目gridpane
 */
Ext.define('erp.view.fa.gla.cashFlowSum.CashFlowGrid', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.erpCashFlowGrid',
    layout: 'fit',
    id: 'cashflowgrid',
    cls: 'u-treegrid',
    emptyText: $I18N.common.grid.emptyText,
    rootVisible: false,
    singleExpand: true,
    store: Ext.create('Ext.data.TreeStore', {
        storeId: 'cashflowstore',
        fields: [{
            "name": "cfs_catecode",
            "type": "string"
        }, {
            "name": "cfs_name",
            "type": "string"
        }, {
            "name": "cfs_credit",
            "type": "string"
        }, {
            "name": "cfs_debit",
            "type": "string"
        }, {
            "name": "leaf",
            "type": "bool"
        }, {
            "name": "cfs_typename",
            "type": "string"
        }, {
            "name": "ca_defaultcashcode",
            "type": "string"
        }, {
            "name": "ca_defaultcashflow",
            "type": "string"
        }],
        root: {
            text: 'root',
            id: 'root',
            expanded: true
        }
    }),
    columnLines: true,
    columns: [{
        "cls": "x-grid-header-1",
        "dataIndex": "cfs_catecode",
        "align": "left",
        "xtype": "treecolumn",
        "width": 200.0,
        "text": "对方科目"
    }, {
        "cls": "x-grid-header-1",
        "dataIndex": "cfs_name",
        "align": "left",
        "width": 200.0,
        "text": "科目名称"
    }, {
        "cls": "x-grid-header-1",
        "dataIndex": "cfs_credit",
        "align": "right",
        "width": 200.0,
        "text": "借方金额",
        "xtype": "numbercolumn"
    }, {
        "cls": "x-grid-header-1",
        "dataIndex": "cfs_debit",
        "align": "right",
        "width": 200.0,
        "text": "贷方金额",
        "xtype": "numbercolumn"
    }, {
        "cls": "x-grid-header-1",
        "dataIndex": "cfs_typename",
        "align": "left",
        "width": 150.0,
        "text": "类型"
    }],
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    tbar: [{
        xtype: 'monthdatefield',
        fieldLabel: '期间',
        labelWidth: 40,
        id: 'yearmonth',
        name: 'yearmonth',
        margin: '3 2 3 0'
    }, '-', {
        name: 'catchdata',
        id: 'catchdata',
        width: 100,
        text: $I18N.common.button.erpCatchDataButton,
        cls: 'u-button'
    }, '->', {
    	iconCls: null,
        cls: 'u-button',
        width: 60,
        xtype: 'erpPrintButton'
    }, '-', {
        name: 'export',
        text: $I18N.common.button.erpExportButton,
        cls: 'u-button',
        width: 60
    }, '-', {
        xtype: 'erpCloseButton',
        iconCls: null,
        cls: 'u-button',
        width: 60,
        style: {
        	marginLeft: 0
        }
    }],
    bodyStyle: 'background-color:#f1f1f1;',
    initComponent: function () {
		Ext.override(Ext.data.AbstractStore, {
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
		this.view.onItemClick = function () {
			return true;
		};
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
	listeners: { // 滚动条有时候没反应，添加此监听器
		scrollershow: function (scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();
				scroller.mon(scroller.scrollEl, 'scroll',scroller.onElScroll, scroller);
			}
		}
	},
	viewConfig: {
		listeners: {
			cellcontextmenu: function(view, td, colIdx, record, tr, rowIdx, e) {
				if(record.get('leaf')) {
					e.preventDefault();
					var contextMenu = view.contextMenu;
					if(!contextMenu) {
						contextMenu = view.contextMenu = new Ext.menu.Menu({
							items: [{
						        id: 'cashflowset',
						        text: '设置现金流量项目',
						        iconCls: 'x-button-icon-query'
						    }, '-', {
						        id: 'setVoucher',
						        text: '已设置现金流量凭证',
						        iconCls: 'x-button-icon-query'
						    }, '-', {
						        id: 'noSetVoucher',
						        text: '未设置现金流量凭证',
						        iconCls: 'x-button-icon-query'
						    }]
						});
					}
					contextMenu.record = record;
					contextMenu.showAt(e.getXY());
				}
			}
		}
	}
});