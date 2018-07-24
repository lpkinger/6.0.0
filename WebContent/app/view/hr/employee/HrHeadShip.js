Ext.define('erp.view.hr.employee.HrHeadShip',{ 
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
					saveUrl: 'hr/employee/saveHrHeadShip.action',
					deleteUrl: 'hr/employee/deleteHrHeadShip.action',
					updateUrl: 'hr/employee/updateHrHeadShip.action',		
					getIdUrl: 'common/getId.action?seq=HRHEADSHIP_SEQ',
					keyField: 'hs_id',
					codeField: 'hs_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});