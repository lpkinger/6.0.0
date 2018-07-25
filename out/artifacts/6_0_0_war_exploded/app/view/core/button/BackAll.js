Ext.define('erp.view.core.button.BackAll',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBackAllButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBackAllButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});