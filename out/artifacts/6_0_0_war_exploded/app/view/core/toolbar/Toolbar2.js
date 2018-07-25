Ext.define('erp.view.core.toolbar.Toolbar2',{ 
		extend: 'Ext.Toolbar', 
		alias: 'widget.erpToolbar2',
		dock: 'bottom',
		initComponent : function(){ 
			Ext.apply(this,{
				items: [{
					xtype: 'erpSaveButton'
				}]
			});
			this.callParent(arguments); 
		}
	});