/**
 * 按委托方抓取批号
 */	
Ext.define('erp.view.core.button.CatchBatchByClient',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchByClientButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchByClientButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
        id:'catchBatchByClient',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});