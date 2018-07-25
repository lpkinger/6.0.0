/**
 * 转询价单按钮
 */	
Ext.define('erp.view.core.button.TurnInquiry',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnInquiryButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnInquiryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});