
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons10',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton10',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_10',
    	text: '总经理评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});

