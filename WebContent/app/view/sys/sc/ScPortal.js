Ext.define('erp.view.sys.sc.ScPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.scportal', 
	requires:['erp.view.sys.sc.ScTabPanel'],
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	layout:'border',
	items:[{
		region:'center',
		xtype:'sctabpanel'
	},{
		region: 'north',
		xtype:'tabpanel',
		//collapsible: true,
		height: 200,
		minHeight: 120,
		condition:"step='SC'",
		xtype:'modulesetportal',
		split: true
	}],
	initComponent : function(){
		this.callParent(arguments);
	}
});