Ext.define('erp.view.hr.wage.wageItemFormula',{
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
					saveUrl: 'hr/wage/saveWageItemFormula.action',
					deleteUrl: 'hr/wage/deleteWageItemFormula.action',
					updateUrl: 'hr/wage/updateWageItemFormula.action',
					getIdUrl: 'common/getId.action?seq=WAGEITEMFORMULA_SEQ',
					keyField: 'wif_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});