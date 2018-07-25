Ext.define('erp.view.fa.gla.CountReportForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.CountReport',
	id: 'form', 
	title: '财务报表计算',
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
	},
	items: [{		
    	xtype: 'monthdatefield',
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