Ext.define('erp.view.pm.bom.ProdFeature',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'pm/bom/saveProdFeature.action',
				deleteUrl: 'pm/bom/deleteProdFeature.action',
				updateUrl: 'pm/bom/updateProdFeature.action',
				getIdUrl: 'common/getId.action?seq=PRODFEATURE_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 75%', 
				detno: 'pf_detno',
				keyField: 'pf_id',
				mainField: 'pf_prid'
				/*tbar:['->',{
					text:'新增特征',
					xtype:'button',
					id:'addFeatrue'	
				}]*/
			}]
		}); 
		me.callParent(arguments); 
	} 
});