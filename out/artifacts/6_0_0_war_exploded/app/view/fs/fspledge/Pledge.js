Ext.define('erp.view.fs.fspledge.Pledge',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fs/fspledge/savePledge.action',
				deleteUrl: 'fs/fspledge/deletePledge.action',
				updateUrl: 'fs/fspledge/updatePledge.action',
				submitUrl: 'fs/fspledge/submitPledge.action',
				resSubmitUrl:'fs/fspledge/resSubmitPledge.action',
				auditUrl: 'fs/fspledge/auditPledge.action',
				resAuditUrl:'fs/fspledge/resAuditPledge.action',
				getIdUrl: 'common/getId.action?seq=FSPLEDGE_SEQ',
				keyField: 'pl_id',
				statusField:'pl_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});