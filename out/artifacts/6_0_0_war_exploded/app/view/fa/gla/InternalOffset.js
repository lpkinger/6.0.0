Ext.define('erp.view.fa.gla.InternalOffset',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'north',
				updateUrl: 'fa/gla/updateInternalOffset.action?_noc=1'
			},{
				xtype: 'erpGridPanel2',
				region: 'center',
				detno: 'iod_detno',  
				keyField: 'iod_id',
				mainField: 'iod_ioid'
			}]
		}); 
		this.callParent(arguments); 
	}
});