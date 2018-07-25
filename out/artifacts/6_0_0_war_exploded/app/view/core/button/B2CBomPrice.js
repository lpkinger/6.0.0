/**
 * 商城核价
 */	
Ext.define('erp.view.core.button.B2CBomPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpB2CBomPriceButton',
		iconCls: 'x-button-icon-check',
		id:'erpB2CBomPriceButton',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpB2CBomPriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});