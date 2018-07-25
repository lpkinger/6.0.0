/**
 * 复制权限按钮
 */	
Ext.define('erp.view.core.button.CopyPower',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCopyPowerButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCopyPowerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});