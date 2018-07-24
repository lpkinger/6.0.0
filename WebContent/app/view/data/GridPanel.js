Ext.define('erp.view.data.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel',
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
    }],
    tbar: [{
    	iconCls: 'x-button-icon-add',
		text: '添加数据字典',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			console.log(me);
			var selectItem = Ext.getCmp('grid1').selModel.selected.items;
			if (selectItem.length == 0 || selectItem.length > 3) {
				showError("请先选中要添加的表，并且每次不要打开过多(最多3个)，想要同时处理更多请选择批量添加");return;
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
			}
		}
    },{
    	iconCls: 'x-button-icon-add',
    	text: '批量添加',
		id: 'print',
		handler: function(btn){

		}
    },{
    	iconCls: 'x-button-icon-add',
    	text: '刷新',
		id: 'refresh1',
		handler: function(btn){
			var me = btn.ownerCt.ownerCt;
			var url = "common/dataDD.action";
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
	        			var grid = Ext.getCmp('grid1');
	        			grid.getStore().removeAll();
	        			if(res.list.length>0){//数据字典中缺省的表
	        				Ext.each(res.list,function(dc, index){
	        					grid.getStore().insert(index, {'table_name': dc.table_name});
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