Ext.define('erp.view.hr.wage.wageDataType',{
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'hr/wage/saveWageDataType.action',
					deleteUrl: 'hr/wage/deleteWageDataType.action',
					updateUrl: 'hr/wage/updateWageDataType.action',
					getIdUrl: 'common/getId.action?seq=WAGEDATATYPE_SEQ',
					keyField: 'wdt_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});