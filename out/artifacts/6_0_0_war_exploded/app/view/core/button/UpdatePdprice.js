/**
 * 询价最终判定按钮
 */	
Ext.define('erp.view.core.button.UpdatePdprice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdatePdpriceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdatePdpriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});