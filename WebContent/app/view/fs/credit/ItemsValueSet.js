Ext.define('erp.view.fs.credit.ItemsValueSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	initComponent : function(){ 
		var tbar = new Array();
			tbar = ['->',{
				xtype:'erpSaveButton'
			},{
				xtype:'erpDeleteButton',
				style:'margin-left:10px;margin-right:10px;'
			}];
		Ext.apply(this, { 
			items: [{				
					xtype: 'erpGridPanel2',
					layout:'fit',
					tbar:tbar,
					bbar: {xtype: 'erpToolbar',id:'toolbar',enableAdd : false,enableExport : false},
					keyField: 'cct_id'	
				}]
		}); 
		
		this.callParent(arguments); 
	}
});