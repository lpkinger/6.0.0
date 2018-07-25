Ext.define('erp.view.hr.emplmana.Turnfullmemb',{ 
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
					anchor: '100% 30%',
					saveUrl: 'hr/emplmana/saveTurnfullmemb.action',
					deleteUrl: 'hr/emplmana/deleteTurnfullmemb.action',
					updateUrl: 'hr/emplmana/updateTurnfullmemb.action',		
					getIdUrl: 'common/getId.action?seq=Turnfullmemb_SEQ',
					auditUrl: 'hr/emplmana/auditTurnfullmemb.action',
					resAuditUrl: 'hr/emplmana/resAuditTurnfullmemb.action',
					submitUrl: 'hr/emplmana/submitTurnfullmemb.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTurnfullmemb.action',
					keyField: 'tf_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					necessaryField: 'td_code',
					keyField: 'td_id',
					detno: 'td_detno',
					mainField: 'td_tfid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});