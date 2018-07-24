/**
 * 转Bug按钮
 */	
Ext.define('erp.view.core.button.Bug',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBugButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBugButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});