/**
 * 过账按钮
 */	
Ext.define('erp.view.core.button.CatchBatch',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchBatchButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchBatchButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});