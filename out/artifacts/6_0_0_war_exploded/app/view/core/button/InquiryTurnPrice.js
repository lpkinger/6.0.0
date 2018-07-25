/**
 * 转报价单
 */	
Ext.define('erp.view.core.button.InquiryTurnPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpInquiryTurnPriceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpInquiryTurnPriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});