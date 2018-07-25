Ext.define('erp.view.pm.bom.ECRDetailLocation',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ECRTestViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/bom/saveECRDetailLocation.action',
					deleteUrl: 'pm/bom/deleteECRDetailLocation.action',
					updateUrl: 'pm/bom/updateECRDetailLocation.action',
					getIdUrl: 'common/getId.action?seq=ECR_SEQ',
					_noc:1,
					keyField: 'ecrd_id',
					codeField: 'ecrd_soncode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'edl_detno',
					keyField: 'edl_id',
					mainField: 'edl_ecrdid',
					necessaryField: 'edl_code',
					_noc:1
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});