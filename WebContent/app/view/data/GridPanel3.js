Ext.define('erp.view.data.GridPanel3',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel3',
//	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'ddd_tablename',
        	type:'string'
    	},{
    		name:'ddd_fieldname',
        	type:'string'
    	},{
    		name:'ddd_fieldtype',
        	type:'string'
        }]
    }),
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} ({rows.length} 封)'
    })],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	
    }),
//    dockedItems: [{
//    	id : 'paging',
//        xtype: 'erpMailPaging',
//        dock: 'bottom',
//        displayInfo: true
//	}],
    columns: [{
        text: '表名',
        width: 160,
        dataIndex: 'ddd_tablename' 
    },{
    	text: '字段名',
        width: 160,
        dataIndex: 'ddd_fieldname' 
    },{
    	text: '字段类型',
        width: 160,
        dataIndex: 'ddd_fieldtype' 
    }],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '批量添加表字段',
		handler: function(btn){
//			var selectItem = Ext.getCmp('grid').selModel.selected.items;
//			if (selectItem.length == 0) {
//				showError("请先选中要删除的流程");return;
//			} else {
//				var ids = new Array();
//				Ext.each(selectItem, function(item, index){
//					ids[index] = item.data.jp_id;
////					alert(ids[index]);
//				});
//				Ext.Ajax.request({//拿到grid的columns
//					url : basePath + 'oa/myprocess/delete.action',
//					params: {
//						ids : ids.join(',')
//					},
//					method : 'post',
//					async: false,
//					callback : function(options, success, response){
//						parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
//						var res = new Ext.decode(response.responseText);
//						if(res.exceptionInfo){
//							showError(res.exceptionInfo);return;
//						}
//						if(res.success){
//							alert(' 删除成功！');
//						}
//					}
//				});
//				url = "oa/myprocess/getJProcessList.action";
////				url = "oa/myprocess/getMyList.action";
//				btn.ownerCt.ownerCt.getGroupData();
//			}
		}
    },{
    	iconCls: 'x-button-icon-add',
    	text: '刷新',
		id: 'refresh3',
		handler: function(btn){

		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
//		url = "oa/myprocess/getJProcessList.action";
//		url = "oa/myprocess/getMyList.action";
//		this.getGroupData(page, pageSize);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	}
});