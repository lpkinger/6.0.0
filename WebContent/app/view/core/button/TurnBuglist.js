/**
 * 生成BugList
 */
Ext.define('erp.view.core.button.TurnBuglist',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBuglist',
		text: $I18N.common.button.erpTurnBuglist,
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