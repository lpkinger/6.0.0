Ext.define('erp.view.oa.persontask.myAgenda.agendaQuery.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAgendaQueryGridPanel',
	id: 'querygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'arrange',
        	type:'string'
        },{
        	name:'executor',
        	type:'string'
        },{
        	name:'title',
        	type:'string'
        },{
        	name:'content',
        	type:'string'
        },{
        	name:'start',
        	type:'date'
        },{
        	name:'end',
        	type:'date'
        },{
        	name:'type',
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
        text: '安排人',
        width: 150,
        dataIndex: 'ag_arrange'
    },{
        text: '执行人',
        width: 150,
        dataIndex: 'ag_executor'
    },{
        text: '事件标题',
        width: 150,
        dataIndex: 'ag_title',
        renderer: function(val, meta, record){
        	var s = '';
        	if(record.data.ag_atid != 0){
        		Ext.Ajax.request({//拿到grid的columns
        			url : basePath + 'oa/persontask/myAgenda/getAgendaType.action',
        			params: {
        				id : record.data.ag_atid
        			},
        			method : 'post',
        			async: false,
        			callback : function(options, success, response){
        				console.log(response);
        				var res = new Ext.decode(response.responseText);
        				if(res.exceptionInfo){
        					showError(res.exceptionInfo);return ;
        				} 
        				if(res.success){
        					s = '<font style="color: #' + res.color +'">' + val +'</font>';            			
        				}
        			}
        		});        		
        	}
        	return s == '' ? val : s;
        }
    },{
        text: '事件内容',
        width: 150,
        dataIndex: 'ag_content'
    },{
        text: '开始时间',
        width: 150,
        dataIndex: 'ag_start',
        renderer: function(val, meta, record){
        	return Ext.util.Format.date(new Date(val),'Y-m-d H:i:s');
        }
    },{
        text: '结束时间',
        width: 150,
        dataIndex: 'ag_end',
        renderer: function(val, meta, record){
        	return Ext.util.Format.date(new Date(val),'Y-m-d H:i:s');
        }
    },{
        text: '日程类型',
        width: 150,
        dataIndex: 'ag_type'
    }],
    tbar: [{
    	iconCls: 'x-button-icon-print',
    	text: $I18N.common.button.erpPrintButton,
		id: 'print',
		handler: function(btn){
//			var name = Ext.getCmp('titlelike').value;
//			if(name != '' && name != null){
//				url = "oa/persontask/myAgenda/search.action?name=" + Ext.getCmp('titlelike').value;
//				btn.ownerCt.ownerCt.getGroupData();				
//			} else {
//				showError("请先在输入框输入类型名称");return;
//			}
		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		url = "oa/persontask/myAgenda/listAgenda.action";
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
        		if(!res.success){
        			return;
        		} else {
        			console.log(res.success);
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