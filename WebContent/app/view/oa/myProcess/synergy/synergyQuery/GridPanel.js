Ext.define('erp.view.oa.myProcess.synergy.synergyQuery.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSynergyQueryGridPanel',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'id',
        	type:'string'
        },{
        	name:'title',
        	type:'string'
        },{
        	name:'date',
        	type:'date'
        },{
        	name:'type',
        	type:'string'
        },{
        	name:'releaser',
        	type:'string'
        },{
        	name:'attach',
        	type:'string'
        }]
    }),
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} ({rows.length} 封)'
    })],
//    selModel: Ext.create('Ext.selection.CheckboxModel',{
//    	
//    }),
    dockedItems: [{
    	id : 'paging',
        xtype: 'erpMailPaging',
        dock: 'bottom',
        displayInfo: true
	}],
    columns: [{
        text: 'ID',
        width: 0,
        dataIndex: 'sy_id'
    },{
        text: '标题',
        width: 160,
        dataIndex: 'sy_title' 
    },{
        text: '发布日期',
        width: 160,
        dataIndex: 'sy_date',
        renderer: function(val, meta, record){
        	return Ext.util.Format.date(new Date(val),'Y-m-d');
        }
    },{
        text: '协同类型',
        width: 160,
        dataIndex: 'sy_type'
    },{
        text: '发布人',
        width: 80,
        dataIndex: 'sy_releaser'
    },{
        text: '附件数',
        width: 80,
        dataIndex: 'sy_attach_id',
        renderer: function(val, meta, record){
        	if(val != null){
        		return val.split(',').length + ' 个';        		
        	} else {
        		return '无';
        	}
        }
    }],
//    tbar: [{
//    	iconCls: 'group-delete',
//		text: $I18N.common.button.erpDeleteButton,
//		handler: function(btn){
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
//		}
//    },{
//    	iconCls: 'x-button-icon-print',
//    	text: $I18N.common.button.erpPrintButton,
//		id: 'print',
//		handler: function(btn){
//
//		}
//    },{
//    	iconCls: 'x-button-icon-print',
//    	text: '催办',
//		id: 'fast',
//		handler: function(btn){
//
//		}
//    },{
//    	iconCls: 'x-button-icon-print',
//    	text: '控制',
//		id: 'control',
//		handler: function(btn){
//
//		}
//    }],
	initComponent : function(){ 
		this.callParent(arguments);
		url = "oa/myProcess/synergy/getSynergyList.action";
//		url = "oa/myprocess/getMyList.action";
		this.getGroupData(page, pageSize);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getGroupData: function(page, pageSize){
		var me = this;
		if(!page){
			page = 1;
		}
		if(!pageSize){
			pageSize = 15;
		}
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: {
        		page: page,
        		pageSize: pageSize
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
//        		console.log(response);
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.error){
        			showError(res.error);return;
        		}
        		if(!res.success){
        			return;
        		} else {
//        			console.log(res.jprocesslist);
        			dataCount = res.count;
        			me.store.loadData(res.success);
        		}
        	}
        });
	},
	updateWindow: function(id){
		var win = new Ext.window.Window({
			id : 'win2',
			title: "修改日程",
			height: "90%",
			width: "80%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/common/commonpage.jsp?whoami=Agenda&formCondition=ag_idIS' + id + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
		win.show();	
	}
});