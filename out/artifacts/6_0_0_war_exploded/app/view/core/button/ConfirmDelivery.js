/**
 * 确认送货按钮
 */	
Ext.define('erp.view.core.button.ConfirmDelivery',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmDeliveryButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'cofirmdeliverybtn',
    	text: $I18N.common.button.erpConfirmDeliveryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});