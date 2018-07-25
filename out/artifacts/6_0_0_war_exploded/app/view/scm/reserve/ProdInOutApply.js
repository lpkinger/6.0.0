Ext.define('erp.view.scm.reserve.ProdInOutApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/reserve/saveProdInOutApply.action',
				deleteUrl: 'scm/reserve/deleteProdInOutApply.action',
				updateUrl: 'scm/reserve/updateProdInOutApply.action',
				submitUrl: 'scm/reserve/submitProdInOutApply.action',
				resSubmitUrl: 'scm/reserve/resSubmitProdInOutApply.action',
				auditUrl: 'scm/reserve/auditProdInOutApply.action',
				resAuditUrl: 'scm/reserve/resAuditProdInOutApply.action',
				getIdUrl: 'common/getId.action?seq=ProdInOutApply_SEQ',
				keyField: 'pi_id',
				codeField: 'pi_code',
				statuscodeField: 'pi_statuscode',
				statusField: 'pi_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				allowExtraButtons: true,
				detno: 'pd_detno',
				keyField: 'pd_id',
				mainField: 'pd_piid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});