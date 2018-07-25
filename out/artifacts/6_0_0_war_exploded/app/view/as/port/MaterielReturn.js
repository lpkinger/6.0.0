Ext.define('erp.view.as.port.MaterielReturn',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	id:'materielreturn', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'as/port/saveMaterielReturn.action',
					deleteUrl: 'as/port/deleteMaterielReturn.action',
					updateUrl: 'as/port/updateMaterielReturn.action',
					auditUrl: 'as/port/auditMaterielReturn.action',
					resAuditUrl: 'as/port/resAuditMaterielReturn.action',
					submitUrl: 'as/port/submitMaterielReturn.action',
					resSubmitUrl: 'as/port/resSubmitMaterielReturn.action',
					getIdUrl: 'common/getId.action?seq=AS_MAKEReturn_SEQ',
					keyField: 'amr_id',
					codeField:'amr_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'amrd_detno',
					//necessaryField: 'cld_itemcode',
					keyField:'amrd_id'
				 }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});