Ext.define('erp.view.hr.emplmana.Turndecruitment',{ 
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
					saveUrl: 'hr/emplmana/saveTurndecruitment.action',
					deleteUrl: 'hr/emplmana/deleteTurndecruitment.action',
					updateUrl: 'hr/emplmana/updateTurndecruitment.action',		
					getIdUrl: 'common/getId.action?seq=Turndecruitment_SEQ',
					auditUrl: 'hr/emplmana/auditTurndecruitment.action',
					resAuditUrl: 'hr/emplmana/resAuditTurndecruitment.action',
					submitUrl: 'hr/emplmana/submitTurndecruitment.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTurndecruitment.action',
					keyField: 'td_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'td_code',
					keyField: 'td_id',
					detno: 'td_detno',
					mainField: 'td_tpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});