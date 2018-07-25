Ext.define('erp.view.hr.attendance.HandCard',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'hr/attendance/saveHandCard.action',
					deleteUrl: 'hr/attendance/deleteHandCard.action',
					updateUrl: 'hr/attendance/updateHandCard.action',
					auditUrl: 'hr/attendance/auditHandCard.action',
					getIdUrl: 'common/getId.action?seq=HandCard_SEQ',
					keyField: 'hc_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					//necessaryField: 'ppd_costname',
					keyField: 'hcd_id',
					detno: 'hcd_detno',
					mainField: 'hcd_hcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});