/**
 * 汇总按钮
 */	
Ext.define('erp.view.core.button.Gather',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGatherButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpGatherButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});