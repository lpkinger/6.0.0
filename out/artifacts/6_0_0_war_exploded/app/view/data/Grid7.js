Ext.define('DataColumn7', {
	extend : 'Ext.data.Model',
	fields : [ 'table_name', 'column_name', 'ddd_tablename', 'ddd_fieldname',
			'ddd_allowblank', 'ddd_defaultvalue', 'dd_tablename', 'data_type',
			'ddd_fieldtype', 'data_length', 'nullable', 'data_default' ],
//	idProperty : 'dd_tablename'
});

// create the Data Store
var store = Ext.create('Ext.data.Store', {
	id : 'store7',
	pageSize : 150,
	model : 'DataColumn7',
	remoteSort : true,
	// allow the grid to interact with the paging scroller by buffering
	buffered : true,
	proxy : {
		// load using script tags for cross domain, if the data in on the same domain as
		// this page, an HttpProxy would be better
		type : 'ajax',
		url : '../../common/addTable.action',
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
		property : 'dd_tablename',
		direction : 'DESC'
	} ]
});
var selectionObjs; //定义全局量
Ext.define('erp.view.data.Grid7', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpGridPanel7',
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
//		id : 'dd_tablename',
		text : '表名',
		width : 160,
		dataIndex : 'dd_tablename',
//		renderer: selectedRec,
		sortable : true
	}],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '批量创建表',
		handler: function(btn){
//			var selectItem = Ext.getCmp('grid7').selModel.selected.items;
			var selectItem = selectionObjs;
			if (selectItem.length == 0) {
				showError("请先选中要新建的表");
				selectionObjs = new Array();
				return;
			} else {
				var tablenames = new Array();
				Ext.each(selectItem, function(item, index){
					tablenames[index] = item.data.dd_tablename;
				});
				Ext.Ajax.request({//拿到grid的columns
					url : basePath + 'common/createTable.action',
					params: {
						tablenames : tablenames
					},
					method : 'post',
					async: false,
					callback : function(options, success, response){
						parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
						var res = new Ext.decode(response.responseText);
						if(res.exceptionInfo){
							showError(res.exceptionInfo);return;
						}
						if(res.success){
							alert(' 新建表成功！');
						}
					}
				});
				selectionObjs = new Array();
				window.location.href = window.location.href;
			}
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