
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons7',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton7',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_7',
    	text: '法务评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});


