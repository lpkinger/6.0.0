Ext.define('erp.view.oa.persontask.myAgenda.SeeAgendaForm',{ //文档查看界面
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpSeeAgendaFormPanel',
	id: 'form', 
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 80,
        cls: 'form-field-allowBlank'
    },
    layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'displayfield',
        fieldLabel: '布置人员',
        id:'ag_arrange',
        name: 'dcl_creator'
    }, {
        xtype: 'displayfield',
        fieldLabel: '执行人员',
        id:'ag_executor',
        name: 'ag_executor'
    }, {
        xtype: 'displayfield',
        fieldLabel: '时间范围',
        name: 'time',
        id: 'time'
    }, {
    	xtype: 'displayfield',
        fieldLabel: '提醒方式',
        name: 'predict',
        id: 'predict'
    }, {
       	xtype: 'displayfield',
        fieldLabel: '是否保密',
        name: 'issecrecy',
        id: 'issecrecy'
    }, {
       	xtype: 'displayfield',
        fieldLabel: '日程类型',
        name: 'ag_type',
        id: 'ag_type'
    }, {
       	xtype: 'displayfield',
        fieldLabel: '日程标题',
        name: 'ag_title',
        id: 'ag_title'
    }, {
       	xtype: 'displayfield',
        fieldLabel: '日程内容',
        name: 'ag_content',
        id: 'ag_content'
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'update',
    	text: $I18N.common.button.erpUpdateButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray'
    },{
    	id: 'delete',
    	text: $I18N.common.button.erpDeleteButton,
    	iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray'
    },{
    	id: 'export',
    	text: $I18N.common.button.erpExportButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray'
    },{
    	id: 'print',
    	text: $I18N.common.button.erpPrintButton,
    	iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray'
    },{
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getAgendaDetail(getUrlParam('id'));
	},
	getAgendaDetail: function(id){
//		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "oa/persontask/myAgenda/getAgenda.action",
        	params: {
        		id: id
        	},
        	method : 'post',
        	callback : function(options, success, response){
//        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		console.log(response);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.agenda){
        			return;
        		} else {
        			Ext.getCmp('ag_arrange').setValue(res.agenda.ag_arrange);
        			Ext.getCmp('ag_executor').setValue(res.agenda.ag_executor.replace(/#/g, '&nbsp;&nbsp;'));
        			Ext.getCmp('ag_content').setValue(res.agenda.ag_content);
        			Ext.getCmp('ag_title').setValue(res.agenda.ag_title);
        			Ext.getCmp('ag_type').setValue(res.agenda.ag_type);
        			Ext.getCmp('time').setValue(Ext.util.Format.date(new Date(res.agenda.ag_start),'Y-m-d H:i:s') + ' 至 ' + Ext.util.Format.date(new Date(res.agenda.ag_end),'Y-m-d H:i:s'));
        			Ext.getCmp('issecrecy').setValue(res.agenda.ag_issecrecy == 0 ? '否':'是');
        			Ext.getCmp('predict').setValue(Ext.util.Format.date(new Date(res.agenda.ag_predict),'Y-m-d H:i:s'));
        		}
        	}
        });
	}
});