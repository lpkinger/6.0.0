
Ext.define('erp.view.core.button.DeleteAllDetails',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteAllDetailsButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'deleteallbutton',
    	text: $I18N.common.button.erpDeleteAllDetailsButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});