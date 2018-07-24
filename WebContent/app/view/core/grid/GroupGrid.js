/**
 * ERP项目groupgrid样式
 */
Ext.define('erp.view.core.grid.GroupGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGroupGrid',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
    	fields: [{
        	name:'group',
        	type:'string'
        }, {
        	name:'from',
        	type:'string'
        },{
        	name:'subject',
        	type:'string'
        },{
        	name:'sendDate',
        	type:'string'
        }],
        sorters: [{
            property : 'sendDate',
            direction: 'DESC'
        }],
        groupField: 'group'//??????????
    }),
    iconCls: 'icon-grid',
    frame: true,
    bodyStyle:'background-color:#f1f1f1;',
    features: [Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} ({rows.length} 封)'
    })],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	
    }),
    dockedItems: [{
    	id : 'paging',
        xtype: 'erpMailPaging',
        dock: 'bottom',
        displayInfo: true
	}],
    columns: [{
        text: '',
        width: 80,
        dataIndex: 'group'
    },{
        text: '发件人',
        width: 150,
        dataIndex: 'ma_from'
    },{
        text: '主题',
        width: 630,
        dataIndex: 'ma_subject'
    },{
        text: '时间',
        width: 210,
        dataIndex: 'ma_senddate'
    }],
    tbar: [{
    	iconCls: 'group-delete',
		text: $I18N.common.button.erpDeleteButton,
		name: 'delete'
    },{
    	iconCls: 'group-post',
		text: "转发",
		handler: function(){
			
		}
    },{
    	iconCls: 'group-all',
		text: "查看所有邮件",
		handler: function(btn){
			url = "oa/mail/getAllReceMail.action";
			btn.ownerCt.ownerCt.getGroupData();
		}
    },{
    	iconCls: 'group-read',
		text: "查看已读邮件",
		handler: function(btn){
			url = "oa/mail/getReadMail.action";
			btn.ownerCt.ownerCt.getGroupData();
		}
    },{
    	iconCls: 'group-unread',
		text: "查看未读邮件",
		handler: function(btn){
			url = "oa/mail/getUnReadMail.action";
			btn.ownerCt.ownerCt.getGroupData();
		}
    },{
    	iconCls: 'group-draft',
		text: "查看回收站邮件",
		handler: function(btn){
			url = "oa/mail/getDeletedReadMail.action";
			btn.ownerCt.ownerCt.getGroupData();
		}
    },{
    	iconCls: 'group-close',
		text: $I18N.common.button.erpCloseButton,
		handler: function(){
			parent.Ext.getCmp("content-panel").getActiveTab().close();
		}
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		url = this.switchUrl(getUrlParam("ma_status"));
		this.getGroupData(page, pageSize);
	},
	switchUrl: function(s){
		var url = 'oa/mail/getAllReceMail.action';
		switch (s){
			case '1':
				url = 'oa/mail/getUnReadMail.action';break;
			case '2':
				url = 'oa/mail/getReadMail.action';break;
			case '3':
				url = 'oa/mail/getDeletedReadMail.action';break;
		}
		return url;
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
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.mails){
        			return;
        		} else {
        			me.store.loadData(res.mails);
        		}
        	}
        });
	}
});