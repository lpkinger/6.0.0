Ext.define('erp.view.core.button.TurnTask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnTaskButton',
		iconCls: 'x-button-icon-change',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnTaskButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});