Ext.define('erp.view.hr.emplmana.Turnposition',{ 
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
					saveUrl: 'hr/emplmana/saveTurnposition.action',
					deleteUrl: 'hr/emplmana/deleteTurnposition.action',
					updateUrl: 'hr/emplmana/updateTurnposition.action',		
					getIdUrl: 'common/getId.action?seq=Turnposition_SEQ',
					auditUrl: 'hr/emplmana/auditTurnposition.action',
					resAuditUrl: 'hr/emplmana/resAuditTurnposition.action',
					submitUrl: 'hr/emplmana/submitTurnposition.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTurnposition.action',
					keyField: 'tp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					keyField: 'td_id',
					detno: 'td_detno',
					mainField: 'td_tpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});