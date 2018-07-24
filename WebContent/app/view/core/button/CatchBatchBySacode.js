/**
 * 按订单抓取批号主表
 */	
Ext.define('erp.view.core.button.CatchBatchBySacode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchBySacodeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchByOrderButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});