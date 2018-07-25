
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons4',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton4',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_4',
    	text: '生产评审',
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
