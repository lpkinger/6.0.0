/**
 * 按客户抓取批号
 */	
Ext.define('erp.view.core.button.CatchBatchByCust',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchByCustButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchByCustButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
        id:'catchBatchByCust',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});