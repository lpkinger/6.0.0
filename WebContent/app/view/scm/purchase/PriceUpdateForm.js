Ext.define('erp.view.scm.purchase.PriceUpdateForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.PriceUpdate',
	id: 'form', 
	title: '采购最近/平均单价计算更新',
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
        	xtype: 'condatefield',
        	fieldLabel: '日期',
        	allowBlank: false,
        	id: 'date',
        	name: 'date'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});