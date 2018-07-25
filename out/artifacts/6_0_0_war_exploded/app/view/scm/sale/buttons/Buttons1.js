/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons1',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton1',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_1',
    	text: '销售部评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});


