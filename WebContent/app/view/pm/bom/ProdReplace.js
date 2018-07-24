Ext.define('erp.view.pm.bom.ProdReplace',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'pm/bom/saveProdReplace.action?caller=ProdReplace',
				deleteUrl: 'pm/bom/deleteProdReplace.action?caller=ProdReplace',
				updateUrl: 'pm/bom/updateProdReplace.action?caller=ProdReplace',		
				getIdUrl: 'common/getId.action?seq=PRODREPLACE_SEQ',
				keyField: 'bd_id',
				codeField: 'bd_soncode',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 75%', 
				detno: 'pre_detno',
				keyField: 'pre_id',
				mainField: 'pre_bdid',
				necessaryField: 'pre_prodcode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});