Ext.define('erp.view.oa.info.IcqForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.icqform',
	id: 'form', 
	title: '<font color=#000800>网络寻呼</font>',
    frame : true,
    autoScroll: true,
    layout: 'column',
    fieldDefaults: {
        labelWidth: 55
    },
    keyField: 'pr_id',
    items: [{
    	xtype: 'hidden',
    	id: 'pr_id',
    	name: 'pr_id'
    },{
        xtype: 'displayfield',
        fieldLabel: '发布人',
        columnWidth: 1,
        msgTarget: 'side',
        allowBlank: true,
        readOnly: true,
        fieldStyle: 'color:#668B8B;',
        blankText : '发布人不能为空',
        id: 'pr_releaser',
        name: 'pr_releaser',
        value: em_name + "(" + em_code + ")"
    },{
        xtype: 'textarea',
        fieldLabel: '接收人',
        columnWidth: 1,
        msgTarget: 'side',
        fieldStyle: 'background:#FFFAF0;',
        allowBlank: false,
        blankText : '接收人不能为空',
        id: 'prd_recipient',
        height: 23,
        readOnly: true,
        name: 'prd_recipient'
    }, {
    	xtype: 'hidden',
        fieldLabel: '接收人ID',
        msgTarget: 'side',
        fieldStyle: 'background:#FFFAF0;',
        allowBlank: false,
        readOnly: true,
        blankText : '接收人不能为空',
        id: 'prd_recipientid',
        name: 'prd_recipientid'
    }, {
    	xtype: 'checkbox',
        boxLabel: '发短信',
        columnWidth: 1
    }, {
        xtype: 'textarea',
        fieldLabel: '手机',
        columnWidth: 1,
        msgTarget: 'side',
        fieldStyle: 'background:#FFFAF0;',
        allowBlank: true,
        readOnly: true,
        height: 23,
        blankText : '手机号码不能为空',
        id: 'prd_mobile',
        name: 'prd_mobile'
    },{
    	xtype: 'mfilefield',
    	columnWidth: 1,
    	id: 'pr_attach',
    	name: 'pr_attach'
    },{
        xtype: 'textarea',
        height: 200,
        columnWidth: 1,
        fieldLabel: '内容',
        fieldStyle: 'background:#FFFAF0;',
        maxLength: 100,
        maxLengthText: '限50字以内',
        name: 'pr_context',
        id: 'pr_context'
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'post',
    	text: '呼叫',
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray'
    },{
    	id: 'close',
    	text: '取消',
    	iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray'
    }],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});