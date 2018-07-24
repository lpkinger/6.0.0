/**
 * 禁用按钮
 */	
Ext.define('erp.view.core.button.Banned',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBannedButton',
		iconCls: 'x-button-icon-banned',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBannedButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});