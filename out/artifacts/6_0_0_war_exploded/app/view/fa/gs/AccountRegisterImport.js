Ext.define('erp.view.fa.gs.AccountRegisterImport',{ 
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
					saveUrl: 'fa/gs/saveAccountRegisterImport.action',
					updateUrl: 'fa/gs/updateAccountRegisterImport.action',
					deleteUrl: 'fa/gs/deleteAccountRegisterImport.action',
					getIdUrl: 'common/getId.action?seq=UPDATEMAINFORM_SEQ',
					keyField: 'em_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno: 'ari_detno',
					keyField: 'ari_id',
					mainField: 'ari_emid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});