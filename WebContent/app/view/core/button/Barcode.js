/**
 * 条形码维护按钮
 */	
Ext.define('erp.view.core.button.Barcode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBarcodeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'barcodebtn',
    	text: $I18N.common.button.erpBarcodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});