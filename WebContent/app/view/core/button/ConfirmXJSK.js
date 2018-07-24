// 确认现金收款按钮
Ext.define('erp.view.core.button.ConfirmXJSK',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmXJSKButton',
		param: [],
		id: 'erpConfirmXJSKButton',
		text: $I18N.common.button.erpConfirmXJSKButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});