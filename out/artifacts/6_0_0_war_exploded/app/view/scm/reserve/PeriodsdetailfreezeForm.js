Ext.define('erp.view.scm.reserve.PeriodsdetailfreezeForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.Periodsdetailfreeze',
	id: 'form', 
	title: '库存冻结作业',
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	layout: {
		type: 'vbox',
		pack: 'center'
	},
	fieldDefaults : {
	       margin : '4 2 4 2',
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
    	readOnly:false,
    	id: 'pd_detno',
    	name: 'pd_detno'
	},{
		xtype: 'displayfield',
		fieldLabel: '当前冻结期间',
		height: 23,
		id: 'date',
		name:'date'
	}],
	buttons: [{
		xtype: 'erpFreezeButton'
	},{
		xtype:'erpCloseButton'
	}]
});