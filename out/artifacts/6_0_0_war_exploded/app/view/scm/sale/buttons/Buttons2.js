
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons2',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton2',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_2',
    	text: 'PC评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
