Ext.define('erp.view.core.button.TurnPlmMainTask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPlmMainTaskButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPlmMainTaskButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});