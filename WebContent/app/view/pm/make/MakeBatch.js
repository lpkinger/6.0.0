Ext.define('erp.view.pm.make.MakeBatch',{ 
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
					anchor: '100% 20%',
					updateUrl: 'pm/make/updateMakeBatch.action',
					keyField: 'em_id',
					codeField: 'em_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno: 'mb_detno',
					keyField: 'mb_id',
					mainField: 'mb_emid',
					condition: 'mb_emid='+em_uu
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});