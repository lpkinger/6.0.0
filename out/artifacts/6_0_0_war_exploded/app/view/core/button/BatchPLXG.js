/**
 * 批量操作核价单
 */	
Ext.define('erp.view.core.button.BatchPLXG',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchPLXGButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBatchPLXGButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});