/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.GenerateBarcode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGenerateBarcodeButton',
    	cls: 'x-btn-gray',
    	id: 'generateBarcodebtn',
    	text: $I18N.common.button.erpGenerateBarcodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});