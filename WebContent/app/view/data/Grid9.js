Ext.define('DataColumn9', {
	extend : 'Ext.data.Model',
	fields : [ 'table_name', 'column_name', 'ddd_tablename', 'ddd_fieldname',
			'ddd_allowblank', 'ddd_defaultvalue', 'dd_tablename', 'data_type',
			'ddd_fieldtype', 'data_length', 'nullable', 'data_default' ],
//	idProperty : 'column_name'//不要乱加，添加后该字段不能重复，重复则给覆盖了，
});

// create the Data Store
var store = Ext.create('Ext.data.Store', {
	id : 'store9',
	pageSize : 200,
	model : 'DataColumn9',
	remoteSort : true,
	// allow the grid to interact with the paging scroller by buffering
	buffered : true,
	proxy : {
		// load using script tags for cross domain, if the data in on the same domain as
		// this page, an HttpProxy would be better
		type : 'ajax',
		url : '../../common/addDField.action',
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
Ext.define('erp.view.data.Grid9', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpGridPanel9',
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
	    text: '表名',
	    width: 160,
	    dataIndex: 'table_name' ,
	    sortable : true
	},{
	    text: '字段名',
	    width: 160,
	    dataIndex: 'column_name' ,
//	    sortable : true
	},{
	    text: '字段类型',
	    width: 160,
	    dataIndex: 'data_type',
//	    sortable : true
	},{
	    text: '字段长度',
	    width: 160,
	    dataIndex: 'data_length' 
	}],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '批量维护',
		handler: function(btn){
			var selectItem = selectionObjs;
			if (selectItem.length == 0) {
				showError("请先选中要删除的流程");
				selectionObjs = new Array();
				return;
			} else {
				var fields = new Array();
				Ext.each(selectItem, function(item, index){
					var field = {
							table_name : item.data.table_name,
							column_name : item.data.column_name,
							data_type : item.data.data_type,
							data_length : item.data.data_length
					};
					fields[index] = Ext.JSON.encode(field);
				});
				console.log(fields);
				Ext.Ajax.request({//拿到grid的columns
					url : basePath + 'common/addFields.action',
					params: {
						fields : unescape(fields.toString().replace(/\\/g,"%"))
					},
					method : 'post',
					async: false,
					callback : function(options, success, response){
						parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
						var res = new Ext.decode(response.responseText);
						if(res.exceptionInfo){
							showError(res.exceptionInfo);return;
						}
						if(res.error){
							showError(res.error);return;
						}
						if(res.success){
							alert(' 添加数据字典成功！');
						}
					}
				});
				selectionObjs = new Array();
				window.location.href = window.location.href;
			}
		}
    }]
});
// trigger the data store load
store.guaranteeRange(0, 199);