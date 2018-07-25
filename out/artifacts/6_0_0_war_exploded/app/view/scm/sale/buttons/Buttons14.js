
/**
 * 按钮
 */	
Ext.define('erp.view.scm.sale.buttons.Buttons14',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPreSaleButton14',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'button_14',
    	text: '研发中心(项目)评审',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});
