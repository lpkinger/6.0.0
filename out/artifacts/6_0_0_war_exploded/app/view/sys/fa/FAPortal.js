Ext.define('erp.view.sys.fa.FAPortal',{
	requires:['erp.view.sys.fa.FATabPanel'],
	extend: 'Ext.panel.Panel', 
	alias: 'widget.faportal', 
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	rtl: true,
	layout:'border',
	items:[{
		region:'center',
		xtype:'fatabpanel'
	},{
		region: 'north',
		//collapsible: true,
		height: 200,
		minHeight: 120,
		condition:"step='FA'",
		xtype:'modulesetportal',
		split: true
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});