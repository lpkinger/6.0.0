
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons6',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton6',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_6',
    	text: '工程部评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});


