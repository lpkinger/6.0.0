Ext.define('erp.view.sys.sale.SalePortal',{
	requires:['erp.view.sys.sale.SaleTabPanel','erp.view.sys.base.ModuleSetPortal'],
	extend: 'Ext.panel.Panel', 
	alias: 'widget.saleportal', 
	animCollapse: false,
	constrainHeader: true,
	bodyBorder: false,
	border: false,
	autoShow: true,
	rtl: true,
	layout:'border',
	items:[{
		region:'center',
		xtype:'saletabpanel'
	}/*,{
		region: 'north',
		//collapsible: true,
		height: 200,
		minHeight: 120,
		condition:"step='Sale'",
		xtype:'modulesetportal',
		split: true
	}*/],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});