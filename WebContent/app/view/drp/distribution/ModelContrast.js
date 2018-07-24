Ext.define('erp.view.drp.distribution.ModelContrast',{ 
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
					anchor: '100% 20%',
					updateUrl: 'drp/distribution/updateModelContrast.action',			
					getIdUrl: 'common/getId.action?seq=ModelContrast_SEQ',
					keyField: 'pr_id',
					codeField: 'pr_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					keyField: 'mc_id',
					mainField: 'mc_prid',
					detno: 'mc_detno'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});