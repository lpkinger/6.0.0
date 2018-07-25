Ext.define('erp.view.scm.reserve.Packing',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/reserve/savePacking.action',
				deleteUrl: 'scm/reserve/deletePacking.action',
				updateUrl: 'scm/reserve/updatePacking.action',
				submitUrl: 'scm/reserve/submitPacking.action',
				resSubmitUrl: 'scm/reserve/resSubmitPacking.action',
				auditUrl: 'scm/reserve/auditPacking.action',
				resAuditUrl: 'scm/reserve/resAuditPacking.action',
				printUrl: 'scm/reserve/printPacking.action',
				postUrl: 'scm/reserve/postPacking.action',
				resPostUrl: 'scm/reserve/resPostPacking.action',
				getIdUrl: 'common/getId.action?seq=Packing_SEQ',
				keyField: 'pi_id',
				codeField: 'pi_code',
				statuscodeField: 'pi_statuscode',
				statusField: 'pi_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pd_detno',
				keyField: 'pd_id',
				mainField: 'pd_piid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});