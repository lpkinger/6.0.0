Ext.define('erp.view.hr.emplmana.Careerapply',{ 
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
					anchor: '100% 30%',
					saveUrl: 'hr/emplmana/saveCareerapply.action',
					deleteUrl: 'hr/emplmana/deleteCareerapply.action',
					updateUrl: 'hr/emplmana/updateCareerapply.action',		
					getIdUrl: 'common/getId.action?seq=Careerapply_SEQ',
					auditUrl: 'hr/emplmana/auditCareerapply.action',
					resAuditUrl: 'hr/emplmana/resAuditCareerapply.action',
					submitUrl: 'hr/emplmana/submitCareerapply.action',
					resSubmitUrl: 'hr/emplmana/resSubmitCareerapply.action',
					keyField: 'ca_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					necessaryField: 'cd_name',
					keyField: 'cd_id',
					detno: 'cd_detno',
					mainField: 'cd_caid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});