Ext.define('erp.view.fs.fspledge.PledgeInout',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'fs/fspledge/savePledgeInout.action',
				deleteUrl: 'fs/fspledge/deletePledgeInout.action',
				updateUrl: 'fs/fspledge/updatePledgeInout.action',
				submitUrl: 'fs/fspledge/submitPledgeInout.action',
				resSubmitUrl:'fs/fspledge/resSubmitPledgeInout.action',
				auditUrl: 'fs/fspledge/auditPledgeInout.action',
				resAuditUrl:'fs/fspledge/resAuditPledgeInout.action',
				getIdUrl: 'common/getId.action?seq=FSPLEDGEINOUT_SEQ',
				keyField: 'pio_id',
				statusField:'pio_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});