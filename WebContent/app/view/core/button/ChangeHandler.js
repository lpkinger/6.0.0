/**
 * 变更处理人按钮
 */	
Ext.define('erp.view.core.button.ChangeHandler',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChangeHandlerButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpChangeHandlerButton,
    	style: {
    		marginLeft: '10px'
        },
        id:'changehandler',
        disabled:true,
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});