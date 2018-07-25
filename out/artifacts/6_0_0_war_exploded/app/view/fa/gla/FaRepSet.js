Ext.define('erp.view.fa.gla.FaRepSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'faRepSetViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveFaRepSet.action',
					deleteUrl: 'fa/ars/deleteFaRepSet.action',
					updateUrl: 'fa/ars/updateFaRepSet.action',
					auditUrl: 'fa/ars/auditFaRepSet.action',
					resAuditUrl: 'fa/ars/resAuditFaRepSet.action',
					submitUrl: 'fa/ars/submitFaRepSet.action',
					resSubmitUrl: 'fa/ars/resSubmitFaRepSet.action',
					getIdUrl: 'common/getId.action?seq=FaRepSet_SEQ',
					keyField: 'fs_id',
					codeField: 'fs_code',
					statusField: 'fs_status',
					statuscodeField: 'fs_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'fsd_detno',
					necessaryField: 'fsd_name',
					keyField: 'fsd_id',
					mainField: 'fsd_fsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});