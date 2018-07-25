Ext.define('erp.view.pm.bom.CommonReplace',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProdReplaceViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/bom/saveCommonReplace.action',
					deleteUrl: 'pm/bom/deleteCommonReplace.action',
					updateUrl: 'pm/bom/updateCommonReplace.action',		
					getIdUrl: 'common/getId.action?seq=PRODREPLACE_SEQ',
					keyField: 'pr_id',
					//codeField: 'bd_soncode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					detno: 'pre_detno',
					keyField: 'pre_id',
					mainField: 'pre_soncodeid',
					necessaryField: 'pre_prodcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});