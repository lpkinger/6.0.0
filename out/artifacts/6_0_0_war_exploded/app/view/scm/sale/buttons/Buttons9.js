
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons9',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton9',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_9',
    	text: '财务部评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});

