Ext.define('erp.view.hr.emplmana.Turnover',{ 
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
					saveUrl: 'hr/emplmana/saveTurnover.action',
					deleteUrl: 'hr/emplmana/deleteTurnover.action',
					updateUrl: 'hr/emplmana/updateTurnover.action',		
					getIdUrl: 'common/getId.action?seq=Turnover_SEQ',
					auditUrl: 'hr/emplmana/auditTurnover.action',
					resAuditUrl: 'hr/emplmana/resAuditTurnover.action',
					submitUrl: 'hr/emplmana/submitTurnover.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTurnover.action',
					keyField: 'to_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});