/**
 * 预立项转立项
 */
Ext.define('erp.view.core.button.TurnProjectTask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProjectTask',
		text: $I18N.common.button.erpTurnProjectTask,
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});