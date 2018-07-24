Ext.define('erp.view.data.GridPanel2',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel2',
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
        	name:'dd_tablename',
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
//        flex: 1,
        dataIndex: 'dd_tablename' 
    }],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '批量创建表',
		handler: function(btn){
			var selectItem = Ext.getCmp('grid2').selModel.selected.items;
			if (selectItem.length == 0) {
				showError("请先选中要删除的流程");return;
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
			}
		}
    },{
    	iconCls: 'x-button-icon-add',
    	text: '刷新',
		id: 'refresh2',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			var url = "common/dataTable.action";
			me.FormUtil.getActiveTab().setLoading(true);
			Ext.Ajax.request({//拿到form的items
	        	url : basePath + url,
	        	params: {
	        		em_uu:em_uu
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		me.FormUtil.getActiveTab().setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}
	        		console.log(res);
	        		if(res.success){
	        			var grid = Ext.getCmp('grid2');
	        			grid.getStore().removeAll();
	        			if(res.list.length>0){//数据字典中缺省的表
	        				Ext.each(res.list,function(dc, index){
	        					grid.getStore().insert(index, {'dd_tablename': dc.dd_tablename});
	        				});
	        				console.log(grid.title);
	        				console.log(grid.title.split(' (')[0]);
	        				grid.setTitle(grid.title.split(' (')[0] + ' (' + res.list.length + ')');
	        			}
	        		}
	        	}
			});
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