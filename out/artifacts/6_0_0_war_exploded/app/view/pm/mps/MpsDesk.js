Ext.define('erp.view.pm.mps.MpsDesk',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				_toolbar_style:'bottom',
				xtype: 'erpFormPanel',
				anchor: '100% 52%',
				keyField: 'mm_id',
				codeField:'mm_code'
			},{
				xtype:'erpMPSDeskFormPanel',
				anchor:'100%  48%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});