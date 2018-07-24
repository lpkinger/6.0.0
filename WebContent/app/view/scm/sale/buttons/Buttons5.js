
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons5',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton5',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_5',
    	text: '配套中心评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
