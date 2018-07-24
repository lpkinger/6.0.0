/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.BatchGenBarcode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchGenBarcodeButton',
    	cls: 'x-btn-gray',
    	id: 'batchGenBarcodebtn',
    	iconCls: 'x-button-icon-save',
    	text: $I18N.common.button.erpBatchGenBarcodeButton,
    	formBind: true,//form.isValid() == false时,按钮disabled
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});