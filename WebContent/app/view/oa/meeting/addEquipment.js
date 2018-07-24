Ext.define('erp.view.oa.meeting.addEquipment',{ 
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
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=EQUIPMENT_SEQ',
					keyField: 'eq_id',
					mainField:'eq_mrid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});