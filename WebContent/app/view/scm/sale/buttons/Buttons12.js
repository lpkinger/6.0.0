
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons12',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton12',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_12',
    	text: '研发中心(软件)评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
