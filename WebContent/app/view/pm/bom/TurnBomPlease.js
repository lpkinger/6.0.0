Ext.define('erp.view.pm.bom.TurnBomPlease',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'TurnBomPleaseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/bom/saveTurnBomPlease.action',
					deleteUrl: 'pm/bom/deleteTurnBomPlease.action',
					updateUrl: 'pm/bom/updateTurnBomPlease.action',
					auditUrl: 'pm/bom/auditTurnBomPlease.action',
					resAuditUrl: 'pm/bom/resAuditTurnBomPlease.action',
					submitUrl: 'pm/bom/submitTurnBomPlease.action',
					resSubmitUrl: 'pm/bom/resSubmitTurnBomPlease.action',
					getIdUrl: 'common/getId.action?seq=TurnBomPlease_SEQ',
					keyField: 'tp_id',
					codeField: 'tp_code',
					statusField: 'tp_status',
					statuscodeField: 'tp_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});