/**
 * 批量更新制造单按钮
 */	
Ext.define('erp.view.core.button.BatchUpdateMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchUpdateMakeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'erpBatchUpdateMakeButton',
    	text: $I18N.common.button.erpBatchUpdateMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});