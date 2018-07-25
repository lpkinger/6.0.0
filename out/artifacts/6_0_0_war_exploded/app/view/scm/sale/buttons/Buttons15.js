
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons3',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton3',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_15',
    	text: 'MC评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
