Ext.define('erp.view.pm.make.NoMaterial',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveNoMaterial.action',
					deleteUrl: 'pm/make/deleteNoMaterial.action',
					updateUrl: 'pm/make/updateNoMaterial.action',
					auditUrl: 'pm/make/auditNoMaterial.action',
					resAuditUrl: 'pm/make/resAuditNoMaterial.action',
					submitUrl: 'pm/make/submitNoMaterial.action',
					resSubmitUrl: 'pm/make/resSubmitNoMaterial.action',
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					codeField: 'ma_code',
					statusField: 'ma_checkstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'mm_detno',
					necessaryField: 'mm_prodcode',
					keyField: 'mm_id',
					mainField: 'mm_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});