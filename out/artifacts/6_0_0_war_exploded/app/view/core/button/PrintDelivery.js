/**
 * 宇声集团送货单纸打印按钮
 */	
Ext.define('erp.view.core.button.PrintDelivery',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintDeliveryButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintDeliveryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});