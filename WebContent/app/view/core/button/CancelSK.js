// 取消收款按钮
Ext.define('erp.view.core.button.CancelSK',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelSKButton',
		param: [],
		id: 'erpCancelSKButton',
		text: $I18N.common.button.erpCancelSKButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});