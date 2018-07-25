/**
 * 核算按钮
 */	
Ext.define('erp.view.core.button.BussAccount',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBussAccountButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'account',
    	text: $I18N.common.button.erpBussAccountButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});