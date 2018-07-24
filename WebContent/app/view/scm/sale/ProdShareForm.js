Ext.define('erp.view.scm.sale.ProdShareForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ProdShare',
	id: 'form', 
	title: '<font color=#a1a1a1; size=3>料工费分摊</font>',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{
		xtype:'dbfindtrigger',
		fieldLabel:'估价单号',
		allowBlank: true,
        id: 'ev_code',
        name: 'evaluationcode'
	},{
		xtype:'textfield',
		fieldLabel:'材料分摊金额',
		allowBlank: true,
        id: 'money',
        name: 'money'
	},{
		xtype:'textfield',
		fieldLabel:'人工分摊金额',
		allowBlank: true,
        id: 'peomoney',
        name: 'peomoney'
	},{
		xtype:'textfield',
		fieldLabel:'制造费用分摊金额',
		allowBlank: true,
        id: 'makemoney',
        name: 'makemoney'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});