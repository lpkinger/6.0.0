/**
 * 确认平台获取的销售订单同意接收
 */	
Ext.define('erp.view.core.button.ConfirmAgree',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmAgreeButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	id: 'confirmagreebtn',
    	text: $I18N.common.button.erpConfirmAgreeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});