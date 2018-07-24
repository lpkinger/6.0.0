
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons11',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton11',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_11',
    	text: '研发中心(硬件)评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
