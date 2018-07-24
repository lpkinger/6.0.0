Ext.define('erp.view.fa.gla.Consolidated',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				updateUrl: 'fa/gla/updateChildReport.action?_noc=1'
			},{
				xtype: 'erpGridPanel2',
				region: 'center',
				detno: 'crd_detno',  
				keyField: 'crd_id',
				mainField: 'crd_crid'
			}]
		}); 
		this.callParent(arguments); 
	}
});