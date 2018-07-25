/**
 * 提前结束按钮
 */	
Ext.define('erp.view.core.button.AdvanceEnd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAdvanceEndButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'advanceend',
    	text: $I18N.common.button.erpAdvanceEndButton,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});