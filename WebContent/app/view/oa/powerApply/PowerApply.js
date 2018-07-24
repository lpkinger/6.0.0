Ext.define('erp.view.oa.powerApply.PowerApply',{ 
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
					saveUrl: 'oa/powerApply/savePowerApply.action?caller=PowerApply&_noc=1',
					deleteUrl: 'oa/powerApply/deletePowerApply.action?caller=PowerApply&_noc=1',
					updateUrl: 'oa/powerApply/updatePowerApply.action?caller=PowerApply&_noc=1',
					getIdUrl: 'common/getId.action?seq=POWERAPPLY_SEQ',
					auditUrl: 'oa/powerApply/auditPowerApply.action',
					resAuditUrl: 'oa/powerApply/resAuditPowerApply.action',
					submitUrl: 'oa/powerApply/submitPowerApply.action?caller=PowerApply&_noc=1',
					resSubmitUrl: 'oa/powerApply/resSubmitPowerApply.action?caller=PowerApply&_noc=1',
					keyField: 'pa_id',
					codeField: 'pa_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});