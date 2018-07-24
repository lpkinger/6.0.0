/**
 * 审核按钮
 */	
Ext.define('erp.view.core.button.MakeFlows',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMakeFlowsButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'MakeFlowsbutton',
    	text: '拆分流程单',
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});