Ext.define('erp.view.pm.make.MoveProductForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MoveProduct',
	id: 'form', 
	//title: '制造单整批挪料作业',
    frame : true,
    bodyStyle: 'padding-left:100px;',
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: {
		type: 'vbox'
	},
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
	items: [
	    {		   
	    	xtype: 'ftfindfield',
	    	fieldLabel: '制造单号',
	    	allowBlank: false,
	    	id: 'ma_code',
	    	name: 'ma_code'
		},
		{		   
	    	xtype: 'textfield',
	    	fieldLabel: '数量',
	    	allowBlank: false,
	    	id: 'ma_qty',
	    	name: 'ma_qty'
		},
		{		
        	xtype: 'datefield',
        	fieldLabel: '日期',
        	allowBlank: false,
        	id: 'ma_date',
        	name: 'ma_date'
	   }
		],
	buttons: [{
		xtype: 'erpConfirmButton'
	}]
});