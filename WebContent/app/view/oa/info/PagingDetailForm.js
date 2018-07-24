Ext.define('erp.view.oa.info.PagingDetailForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpPagingDetailFormPanel',
	id: 'form', 
	FormUtil: Ext.create('erp.util.FormUtil'),
    region: 'center',
    frame : true,
    fieldDefaults: {
        labelWidth: 75,
        cls: 'form-field-border',
        fieldStyle: 'background:#f1f1f1;',
        readOnly: true
    },
    layout: {
        type: 'vbox', 
        align: 'stretch'
    },
    items: [{
        xtype: 'textfield',
        fieldLabel: '发布人',
        id:'pr_releaser',
        name: 'pr_releaser'
    }, {
        xtype: 'datetimefield',
        fieldLabel: '发布时间',
        id:'pr_date',
        name: 'pr_date'
    }, {
        xtype: 'textfield',
        fieldLabel: '接收人',
        id:'prd_recipient',
        name: 'prd_recipient'
    }, {
    	 xtype: 'mfilefield',
         fieldLabel: '附件',
         name: 'pr_attach',
         id: 'pr_attach'
    },{
        xtype: 'textarea',
        height: 160,
        name: 'pr_context',
        id: 'pr_context',
        fieldLabel: '寻呼内容'
    },{
        xtype: 'textarea',
        height: 160,
        name: 'pr_context_r',
        id: 'pr_context_r',
        fieldLabel: '我的回复',
        readOnly: false,
        fieldStyle: 'background:#FFFAF0;',
    },{
    	xtype: 'hidden',
    	id: 'pr_id',
    	name: 'pr_id'
    },{
    	xtype: 'hidden',
    	id: 'prd_id',
    	name: 'prd_id'
    },{
    	xtype: 'hidden',
    	id: 'pr_releaserid',
    	name: 'pr_releaserid'
    },{
    	xtype: 'hidden',
    	id: 'prd_recipientid',
    	name: 'prd_recipientid'
    },{
    	xtype: 'hidden',
    	id: 'prd_status',
    	name: 'prd_status'
    }],
    buttonAlign: 'center',
    buttons: [{
    	id: 'post',
    	text: '回复',
    	iconCls: 'group-read',
    	cls: 'x-btn-gray'
    },{
    	id: 'delete',
    	text: '删除',
    	iconCls: 'group-delete',
    	cls: 'x-btn-gray'
    },{
    	id: 'close',
    	text: '转发',
    	iconCls: 'group-post',
    	cls: 'x-btn-gray'
    },{
    	id: 'draft',
    	iconCls: 'group-draft',
		text: "保留",
		cls: 'x-btn-gray'
	},{
    	id: 'close',
    	text: '关闭',
    	iconCls: 'group-close',
    	cls: 'x-btn-gray'
    }],
    keyField: 'prd_id',
	initComponent : function(){ 
		this.callParent(arguments);
		
		this.FormUtil.getFieldsValue('PagingRelease left join PagingReleaseDetail on pr_id=prd_prid', 
				'pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,prd_id,prd_recipientid,prd_recipient,prd_status', 
				getUrlParam('formCondition').replace(/IS/g, '='), 
				'pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,prd_id,prd_recipientid,prd_recipient,prd_status');
	}
});