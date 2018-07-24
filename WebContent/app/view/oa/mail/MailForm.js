Ext.define('erp.view.oa.mail.MailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpMailFormPanel',
	id: 'form', 
    region: 'center',
    frame : true,
    height: 'auto',
    fieldDefaults: {
        labelWidth: 55
    },
    layout: {
        type: 'vbox',//hbox水平盒布局 
        align: 'stretch'  // Child items are stretched to full width子面板高度充满父容器 
    },
    items: [{
        xtype: 'textfield',
        fieldLabel: '收件人',
        msgTarget: 'side',
        allowBlank: true,
        blankText : '收件人不能为空',
        id:'receAddr',
        name: 'receAddr'
    }, {
        xtype: 'textfield',
        fieldLabel: '主题',
        name: 'subject',
        id: 'subject'
    },{
    	xtype: 'filefield',
    	fieldLabel: '附件',
    	id: 'attach',
    	name: 'file',
    	msgTarget: 'side',
    	allowBlank: false,
    	buttonText: '浏览...'
    },{
    	xtype: 'displayfield',
    	id: 'attachs',
    	cls: 'mail-attach',
    	height: 'auto'
    },{
        xtype: 'htmleditor',
        fieldLabel: '',
        height: 600,
        name: 'context',
        id: 'context',
        anchor: '100%'//添加编辑框******
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'post',
    	text: '发送',
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray'
    },{
    	id: 'save',
    	text: '保存草稿',
    	width: 120,
    	iconCls: 'x-button-icon-change',
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