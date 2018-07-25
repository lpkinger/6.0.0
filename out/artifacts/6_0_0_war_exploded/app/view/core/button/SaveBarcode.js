
Ext.define('erp.view.core.button.SaveBarcode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaveBarcodeButton',
		param: [],
		id: 'saveBarcode',
		text: $I18N.common.button.erpSaveButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});