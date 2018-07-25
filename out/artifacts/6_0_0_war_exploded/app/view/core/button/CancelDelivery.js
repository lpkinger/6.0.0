/**
 * 取消送货按钮
 */	
Ext.define('erp.view.core.button.CancelDelivery',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelDeliveryButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'canceldeliverybtn',
    	text: $I18N.common.button.erpCancelDeliveryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});