Ext.define('erp.view.as.port.MaterielOut',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	id:'materielout', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'as/port/saveMaterielOut.action',
					deleteUrl: 'as/port/deleteMaterielOut.action',
					updateUrl: 'as/port/updateMaterielOut.action',
					auditUrl: 'as/port/auditMaterielOut.action',
					resAuditUrl: 'as/port/resAuditMaterielOut.action',
					submitUrl: 'as/port/submitMaterielOut.action',
					resSubmitUrl: 'as/port/resSubmitMaterielOut.action',
					getIdUrl: 'common/getId.action?seq=AS_MAKEOUT_SEQ',
					keyField: 'amo_id',
					codeField:'amo_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'amod_detno',
					//necessaryField: 'cld_itemcode',
					keyField:'amod_id'
				 }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});