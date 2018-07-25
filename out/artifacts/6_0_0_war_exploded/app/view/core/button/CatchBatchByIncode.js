/**
 * 按入仓单抓取批号
 */	
Ext.define('erp.view.core.button.CatchBatchByIncode',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchByIncodeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchByIncodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
        id:'catchBatchByIncode',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});