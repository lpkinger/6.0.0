Ext.define('erp.view.oa.officialDocument.instruction.query.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpInstructionQueryGridPanel',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	store: [],
    columns: [],
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} ({rows.length} 封)'
    })],
    dockedItems: [{
    	id : 'paging',
        xtype: 'erpMailPaging',
        dock: 'bottom',
        displayInfo: true
	}],
    tbar: [{
    	iconCls: 'x-button-icon-print',
    	text: $I18N.common.button.erpPrintButton,
		id: 'print',
		handler: function(btn){
			url = "oa/officialDocument/instruction/getInstructionList.action";
			Ext.getCmp('grid').getGroupData(page, pageSize);
		}
    }],
	initComponent : function(){ 
		var gridParam = {caller: caller, condition: ''};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments);
		url = "oa/officialDocument/instruction/getInstructionList.action";
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