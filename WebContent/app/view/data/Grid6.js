Ext.define('DataColumn6', {
	extend : 'Ext.data.Model',
	fields : [ 'table_name', 'column_name', 'ddd_tablename', 'ddd_fieldname',
			'ddd_allowblank', 'ddd_defaultvalue', 'dd_tablename', 'data_type',
			'ddd_fieldtype', 'data_length', 'nullable', 'data_default' ],
//	idProperty : 'table_name'
});

// create the Data Store
var store = Ext.create('Ext.data.Store', {
	id : 'store6',
	pageSize : 150,
	model : 'DataColumn6',
	remoteSort : true,
	// allow the grid to interact with the paging scroller by buffering
	buffered : true,
	proxy : {
		// load using script tags for cross domain, if the data in on the same domain as
		// this page, an HttpProxy would be better
		type : 'ajax',
		url : '../../common/addData.action',
		extraParams : {
			total : 50000
		},
		reader : {
			root : 'list',
			totalProperty : 'totalCount'
		},
		// sends single sort as multi parameter
		simpleSortMode : true
	},
	sorters : [ {
		property : 'table_name',
		direction : 'DESC'
	} ]
});
var selectionObjs; //定义全局量
Ext.define('erp.view.data.Grid6', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpGridPanel6',
//	title: this.title + totalProperty,
	store : store,
	iconCls : 'icon-grid',
	frame : true,
	emptyText : '无数据',
	columnLines : true,
	autoScroll : true,
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	plugins : [ Ext.create('Ext.ux.grid.GridHeaderFilters') ],
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	bodyStyle : 'background-color:#f1f1f1;',
	verticalScrollerType : 'paginggridscroller',
	loadMask : true,
	disableSelection : false,
	invalidateScrollerOnRefresh : false,
	viewConfig : {
		trackOver : false
	},
	selModel : Ext.create('Ext.selection.CheckboxModel', {
		listeners : {
			selectionchange : function(sm) {
			        var length = sm.getCount(); //获得选择列数

			        selectionObjs = sm.getSelection(); //获得所有选择的对象
			}
		}
	}),
	// grid columns
	columns : [ {
		xtype : 'rownumberer',
		width : 50,
		sortable : false
	}, {
//		id : 'table_name',
		text : '表名',
		width : 160,
		dataIndex : 'table_name',
//		renderer: selectedRec,
		sortable : true
	}],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '添加数据字典',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			console.log(selectionObjs);
//			var selectItem = Ext.getCmp('grid6').selModel.selected.items;
			var selectItem = selectionObjs;
			if (selectItem.length == 0 || selectItem.length > 3) {
				showError("请先选中要添加的表，并且每次不要打开过多(最多3个)，想要同时处理更多请选择批量添加");
				selectionObjs = new Array();
				return;
			} else {
				console.log(selectItem);
				Ext.each(selectItem, function(item, index){
					var tablename = item.data.table_name;
					var panel = Ext.getCmp("datadictionary_"+tablename); 
					var main = parent.Ext.getCmp("content-panel");
					if(!panel){ 
						var title = "添加数据字典(" + tablename + ")";
						panel = { 
								title : title,
								tag : 'iframe',
								tabConfig:{tooltip: title},
								frame : true,
								border : false,
								layout : 'fit',
								iconCls : 'x-tree-icon-tab-tab1',
								html : '<iframe id="iframe_' + tablename + '" src="' + basePath + 'jsps/ma/dataDictionary.jsp?tablename=' + tablename + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
								closable : true,
								listeners : {
									close : function(){
										main.setActiveTab(main.getActiveTab().id); 
									}
								} 
						};
						me.FormUtil.openTab(panel, "datadictionary_"+tablename); 
					}else{ 
						main.setActiveTab(panel); 
					}					
				});
				selectionObjs = new Array();
			}
		}
    },{
    	iconCls: 'x-button-icon-add',
    	text: '批量添加',
//		id: 'print',
		handler: function(btn){
			alert("不推荐使用，暂不开放");
		}
    },{
    	iconCls: 'x-button-icon-print',
    	text: '刷新',
//		id: 'print',
		handler: function(btn){
			window.location.href = window.location.href;
		}
    }],
//	  dockedItems : [ {
//		xtype: 'pagingtoolbar',
//        store: store,   // s
//		dock : 'bottom',
//		displayInfo : true
//	}],
});
function selectedRec(value, p, record) {
//    record.selected = true;
	console.log(this.selModel);
}
// trigger the data store load
store.guaranteeRange(0, 149);