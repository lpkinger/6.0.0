/**
 *按金额开票
 */	
Ext.define('erp.view.core.button.ByAmount',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpByAmountButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'byamountbtn',
    	text: $I18N.common.button.erpByAmountButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});