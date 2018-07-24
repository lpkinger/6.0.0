/**
 * 生成Project
 */
Ext.define('erp.view.core.button.TurnProject',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProject',
		text: $I18N.common.button.erpTurnProject,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});