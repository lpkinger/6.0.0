Ext.define('erp.view.hr.emplmana.Retire',{ 
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
					saveUrl: 'hr/emplmana/saveRetire.action',
					deleteUrl: 'hr/emplmana/deleteRetire.action',
					updateUrl: 'hr/emplmana/updateRetire.action',		
					getIdUrl: 'common/getId.action?seq=Retire_SEQ',
					auditUrl: 'hr/emplmana/auditRetire.action',
					resAuditUrl: 'hr/emplmana/resAuditRetire.action',
					submitUrl: 'hr/emplmana/submitRetire.action',
					resSubmitUrl: 'hr/emplmana/resSubmitRetire.action',
					keyField: 're_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'rd_code',
					keyField: 'rd_id',
					detno: 'rd_detno',
					mainField: 'rd_reid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});