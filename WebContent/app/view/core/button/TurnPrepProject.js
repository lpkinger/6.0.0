/**
 * 生成PrepProject
 */
Ext.define('erp.view.core.button.TurnPrepProject',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPrepProject',
		text: '转预立项',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});