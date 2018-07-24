Ext.define('erp.view.co.cost.MakeCostCloseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MakeCostCloseForm',
	id: 'form', 
	title: '主营成本结转凭证制作',
    frame : true,
	autoScroll : true,
	cls: 'singleWindowForm',
	bodyCls: 'singleWindowForm',
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	layout: 'column',
	fieldDefaults : {
	       margin : '4 10 4 10',
	       columnWidth: 1,
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
	},
	layout: {
		type : 'vbox',
		align : 'center',
		pack : 'center'
	},
	items: [{
		xtype: 'checkbox',
		id: 'account',
		name: 'account',
		boxLabel: '将结转产生的凭证立即登账'
	}],
	buttons: [{
		xtype: 'erpMakeCostCloseButton'
	},{
		xtype:'erpCloseButton'
	}]
});