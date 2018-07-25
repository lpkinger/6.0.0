/**
 * 批量生成制造单
 */	
Ext.define('erp.view.core.button.BatchToMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchToMakeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'BatchToMake',
    	text: $I18N.common.button.erpBatchToMakeButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});