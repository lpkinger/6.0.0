Ext.define('erp.view.drp.distribution.AgentAskSale',{ 
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
					anchor: '100% 70%',
					saveUrl: 'drp/distribution/saveAgentAskSale.action',
					deleteUrl: 'drp/distribution/deleteAgentAskSale.action',
					updateUrl: 'drp/distribution/updateAgentAskSale.action',
					auditUrl: 'drp/distribution/auditAgentAskSale.action',
					resAuditUrl: 'drp/distribution/resAuditAgentAskSale.action',
					submitUrl: 'drp/distribution/submitAgentAskSale.action',
					resSubmitUrl: 'drp/distribution/resSubmitAgentAskSale.action',
					bannedUrl: 'drp/distribution/bannedAgentAskSale.action',
					resBannedUrl: 'drp/distribution/resBannedAgentAskSale.action',
					getIdUrl: 'common/getId.action?seq=AGENTASKSALE_SEQ',
					keyField: 'aa_id',
					codeField: 'aa_code',
					statusField:'aa_status'
				},{
					xtype:'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_aaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});