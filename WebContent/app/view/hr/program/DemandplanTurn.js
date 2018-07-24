Ext.define('erp.view.hr.program.DemandplanTurn',{ 
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
					anchor: '100% 50%',
					//saveUrl: 'hr/program/saveDemandplan.action',
				//	deleteUrl: 'hr/program/deleteDemandplan.action',
				//	updateUrl: 'hr/program/updateDemandplan.action',		
					getIdUrl: 'common/getId.action?seq=Demandplan_SEQ',
					/*auditUrl: 'hr/program/auditDemandplan.action',
					resAuditUrl: 'hr/program/resAuditDemandplan.action',
					submitUrl: 'hr/program/submitDemandplan.action',
					resSubmitUrl: 'hr/program/resSubmitDemandplan.action',*/
					keyField: 'dp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'dd_hrorgcode',
					keyField: 'dd_id',
					detno: 'dd_detno',
					mainField: 'dd_rpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});