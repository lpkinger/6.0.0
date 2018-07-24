/**
 * 按订单抓取批号
 */	
Ext.define('erp.view.core.button.CatchBatchByOrder',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchByOrderButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchByOrderButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
        id:'catchBatchByOrder',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});