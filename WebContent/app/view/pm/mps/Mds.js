Ext.define('erp.view.pm.mps.Mds',{ 
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
					anchor: '100% 25%',
					saveUrl: 'pm/mds/saveMDS.action',
					deleteUrl: 'pm/mds/deleteMDS.action',
					updateUrl: 'pm/mds/updateMDS.action',
					auditUrl:'pm/mds/auditMDS.action',
					resAuditUrl:'pm/mds/resAuditMDS.action',
					submitUrl:'pm/mds/submitMDS.action',
					resSubmitUrl:'pm/mds/resSubmitMDS.action',
					deleteAllDetailsUrl:'pm/mds/deleteAllDetails.action',
					getIdUrl: 'common/getId.action?seq=MDS_SEQ',
					keyField: 'mds_id',
					codeField:'mds_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%',
					detno: 'mdd_detno',
					keyField: 'mdd_id',
					mainField: 'mdd_mainid'
				  }] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});