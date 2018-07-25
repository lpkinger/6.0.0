Ext.define('erp.view.fs.cust.HXBusinessCondition',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				readOnly:readOnly==1
			}]
		}); 
		this.callParent(arguments); 
	}
});