Ext.define('erp.view.data.GridPanel4',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel4',
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
        	name:'table_name',
        	type:'string'
    	},{
    		name:'column_name',
        	type:'string'
    	},{
    		name:'data_type',
        	type:'string'
    	},{
    		name:'data_length',
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
        dataIndex: 'table_name' 
    },{
    	text: '字段名',
        width: 160,
        dataIndex: 'column_name' 
    },{
    	text: '字段类型',
        width: 160,
        dataIndex: 'data_type' 
    },{
    	text: '字段长度',
        width: 160,
        dataIndex: 'data_length' 
    }],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '批量维护',
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
    }],
	initComponent : function(){ 
		this.callParent(arguments);
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