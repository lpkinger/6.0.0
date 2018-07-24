Ext.define('erp.view.pm.bom.DevelopBOM',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'DevelopBOMViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/bom/saveDevelopBOM.action',
					deleteUrl: 'pm/bom/deleteDevelopBOM.action',
					updateUrl: 'pm/bom/updateDevelopBOM.action',
					auditUrl: 'pm/bom/auditDevelopBOM.action',
					resAuditUrl: 'pm/bom/resAuditDevelopBOM.action',
					submitUrl: 'pm/bom/submitDevelopBOM.action',
					resSubmitUrl: 'pm/bom/resSubmitDevelopBOM.action',
					getIdUrl: 'common/getId.action?seq=BOM_SEQ',
					keyField: 'bo_id',
					codeField: 'bo_code',
					statusField: 'bo_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'bd_detno',
					keyField: 'bd_id',
					mainField: 'bd_bomid',
					necessaryField: 'bd_soncode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});