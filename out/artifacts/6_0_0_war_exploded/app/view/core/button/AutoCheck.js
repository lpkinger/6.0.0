/**
 * 自动对账
 */	
Ext.define('erp.view.core.button.AutoCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAutoCheckButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAutoCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});