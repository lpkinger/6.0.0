Ext.define('erp.view.ma.logic.LogicDesc',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 70%',
				saveUrl: 'ma/logic/saveLogicDesc.action',
				updateUrl: 'ma/logic/updateLogicDesc.action',
				getIdUrl: 'common/getId.action?seq=LOGICDESC_SEQ',
				keyField: 'ld_id'
			},{
				xtype: 'erpGridPanel2', 
				anchor: '100% 30%',
				necessaryField: 'ld_field',
				keyField: 'ldf_id',
				mainField: 'ldf_ldid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});