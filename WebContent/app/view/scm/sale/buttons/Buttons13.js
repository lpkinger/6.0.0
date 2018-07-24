
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons13',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton13',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_13',
    	text: '研发中心(包装)评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
