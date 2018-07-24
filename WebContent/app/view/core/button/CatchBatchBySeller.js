/**
 * 按入仓单抓取批号
 */	
Ext.define('erp.view.core.button.CatchBatchBySeller',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchBySellerButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchBySellerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
        id:'catchBatchBySeller',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});