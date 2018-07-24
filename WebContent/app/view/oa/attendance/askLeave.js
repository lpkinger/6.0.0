Ext.define('erp.view.oa.askLeave.askLeave',{ 
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
					anchor: '100% 100%',
					saveUrl: 'oa/askLeave/saveAskLeave.action',
					deleteUrl: 'oa/askLeave/deleteAskLeave.action',
					updateUrl: 'oa/askLeave/updateAskLeave.action',
					auditUrl: 'oa/askLeave/auditAskLeave.action',
					resAuditUrl: 'oa/askLeave/resAuditAskLeave.action',
					submitUrl: 'oa/askLeave/submitAskLeave.action',
					resSubmitUrl: 'oa/askLeave/resSubmitAskLeave.action',
					getIdUrl: 'common/getId.action?seq=ASKLEAVE_SEQ',
					keyField: 'al_id',
					statusField: 'al_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});