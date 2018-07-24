/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.ManualEvaluation',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpManEvalButton',
		iconCls: 'x-button-icon-code',
    	cls: 'x-btn-gray',
    	id: 'manevalbtn',
    	text: '人工评定',
    	style: {
    		marginLeft: '10px'
        },
        width: 85,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});