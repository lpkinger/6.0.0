/**
 * 按照最小包装数批量生成条码
 */	
Ext.define('erp.view.core.button.ZxbzsBarcode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpZxbzsBarcodeButton',
    	cls: 'x-btn-gray',
    	iconCls: 'x-button-icon-submit',
    	id: 'ZxbzsBarcodebtn',
    	text: $I18N.common.button.erpZxbzsBarcodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});