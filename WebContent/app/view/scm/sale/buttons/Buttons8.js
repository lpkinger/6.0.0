
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons8',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton8',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_8',
    	text: '质量部评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
