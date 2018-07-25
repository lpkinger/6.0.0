Ext.define('erp.view.pm.bom.ECNDetailLocation',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ECNTestViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/bom/saveECNDetailLocation.action',
					deleteUrl: 'pm/bom/deleteECNDetailLocation.action',
					updateUrl: 'pm/bom/updateECNDetailLocation.action',
					getIdUrl: 'common/getId.action?seq=ECN_SEQ',
					keyField: 'ed_id',
					codeField: 'ed_soncode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'edl_detno',
					keyField: 'edl_id',
					mainField: 'edl_edid',
					necessaryField: 'edl_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});