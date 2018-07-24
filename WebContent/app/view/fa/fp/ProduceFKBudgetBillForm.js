Ext.define('erp.view.fa.fp.ProduceFKBudgetBillForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ProduceFKBudgetBill',
	id: 'form', 
	title: '批量生成付款预算单',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
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
		xtype: 'monthdatefield',
    	fieldLabel: '期间',
    	allowBlank: false,
//    	readOnly:true,
    	id: 'date',
    	name: 'date'
	}],
	buttons: [{
		xtype: 'erpProduceFKBudgetBillButton'
	},{
		xtype:'erpCloseButton'
	}]
});