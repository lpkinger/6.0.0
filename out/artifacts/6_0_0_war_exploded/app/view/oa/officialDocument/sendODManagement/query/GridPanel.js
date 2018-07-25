Ext.define('erp.view.oa.officialDocument.sendODManagement.query.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSODQueryGridPanel',
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