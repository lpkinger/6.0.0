Ext.define('erp.view.core.button.ConfirmThrowQty',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmThrowQtyButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'erpConfirmThrowQtyButton',
    	text: $I18N.common.button.erpConfirmThrowQtyButton,
    	style: {
    		marginLeft: '10px'
        },
        width:100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});