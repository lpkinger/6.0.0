Ext.define('erp.view.oa.officialDocument.sendODManagement.manage.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSODManageGridPanel',
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
        	name:'sod_id',
        	type:'int'
        },{
        	name:'sod_type',
        	type:'string'
        },{
        	name:'sod_title',
        	type:'string'
        },{
        	name:'sod_cs_organ',
        	type:'string'
        },{
        	name:'sod_drafter',
        	type:'string'
        },{
        	name:'date',
        	type:'date'
        },{
        	name:'sod_status',
        	type:'string'
        },{
        	name:'sod_number',
        	type:'string'
        },{
        	name:'sod_statuscode',
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
//    	renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
//            if(record.data.rod_statuscode != 'OVERED'){
//            	metaData.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
//            	return '<div class="' + Ext.baseCSSPrefix + 'grid-row-checker">&#160;</div>';            	
//            }
//        }
    	onRowMouseDown: function(view, record, item, index, e) {
            view.el.focus();
            var me = this,
                checker = e.getTarget('.' + Ext.baseCSSPrefix + 'grid-row-checker');
                
            if (!me.allowRightMouseSelection(e)) {
                return;
            }

            // checkOnly set, but we didn't click on a checker.
            if (me.checkOnly && !checker) {
                return;
            }

            if (checker) {
                var mode = me.getSelectionMode();
                // dont change the mode if its single otherwise
                // we would get multiple selection
                if (mode !== 'SINGLE') {
                    me.setSelectionMode('SIMPLE');
                }
                me.selectWithEvent(record, e);
                me.setSelectionMode(mode);
            } else {
                me.selectWithEvent(record, e);
            }
        }
    }),
    dockedItems: [{
    	id : 'paging',
        xtype: 'erpMailPaging',
        dock: 'bottom',
        displayInfo: true
	}],
	 columns: [{
	        text: 'ID',
	        width: 0,
	        dataIndex: 'sod_id'
	    },{
	        text: '发文类型',
	        width: 90,
	        dataIndex: 'sod_type'	        
	    },{
	        text: '发文标题',
	        width: 80,
	        dataIndex: 'sod_title'
	    },{
	        text: '抄送部门',
	        width: 150,
	        dataIndex: 'sod_cs_organ'
	    },{
	        text: '拟稿人',
	        width: 80,
	        dataIndex: 'sod_drafter'
	    },{
	        text: '拟稿日期',
	        width: 90,
	        dataIndex: 'sod_date',
	        renderer: function(val, meta, record){
	        	return Ext.util.Format.date(new Date(val),'Y-m-d');
	        }
	    },{
	        text: '审批状态',
	        width: 80,
//	        id: 'sod_status',
//	        xtype:'gridcolumn',
	        dataIndex: 'sod_status'
	    },{
	        text: '发文字号',
	        width: 80,
	        dataIndex: 'sod_number'
	    },{
	        text: '审批状态码',
	        width: 0,
//	        xtype:'gridcolumn',
	        dataIndex: 'sod_statuscode'
	 }],
    tbar: [{
    	iconCls: 'group-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(btn){
			var selectItem = Ext.getCmp('grid').selModel.selected.items;
			console.log(selectItem);
//			if (selectItem.length == 0) {
//				showError("请先选中要删除的收文");return;
//			} else {
//				var ids = new Array();
//				Ext.each(selectItem, function(item, index){
//					ids[index] = item.data.rod_id;
////					alert(ids[index]);
//				});
//				Ext.Ajax.request({//拿到grid的columns
//					url : basePath + 'oa/officialDocument/receiveODM/deleteROD.action',
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
//				url = "oa/officialDocument/receiveODM/getRODList.action";
//				btn.ownerCt.ownerCt.getGroupData();
//			}
		}
    },{
    	iconCls: 'x-button-icon-print',
    	text: $I18N.common.button.erpPrintButton,
		id: 'print',
		handler: function(btn){

		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		url = "oa/officialDocument/sendODM/getSODList.action";
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
        		console.log(response);
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
//	updateWindow: function(id){
//		var win = new Ext.window.Window({
//			id : 'win2',
//			title: "修改协同",
//			height: "90%",
//			width: "80%",
//			maximizable : false,
//			buttonAlign : 'left',
//			layout : 'anchor',
//			items: [{
//				tag : 'iframe',
//				frame : true,
//				anchor : '100% 100%',
//				layout : 'fit',
//				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/common/commonpage.jsp?whoami=Agenda&formCondition=ag_idIS' + id + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
//			}]
//		});
//		win.show();	
//	}
});