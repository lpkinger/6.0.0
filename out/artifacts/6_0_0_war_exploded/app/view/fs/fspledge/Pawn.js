Ext.define('erp.view.fs.fspledge.Pawn',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fs/fspledge/savePawn.action',
				deleteUrl: 'fs/fspledge/deletePawn.action',
				updateUrl: 'fs/fspledge/updatePawn.action',
				submitUrl: 'fs/fspledge/submitPawn.action',
				resSubmitUrl:'fs/fspledge/resSubmitPawn.action',
				auditUrl: 'fs/fspledge/auditPawn.action',
				resAuditUrl:'fs/fspledge/resAuditPawn.action',
				getIdUrl: 'common/getId.action?seq=FSPLEDGE_SEQ',
				keyField: 'pl_id',
				statusField:'pl_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});