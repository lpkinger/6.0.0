/**
 * ECN同步
 */	
Ext.define('erp.view.core.button.ECNSync',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpECNSyncButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpECNSyncButton,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});