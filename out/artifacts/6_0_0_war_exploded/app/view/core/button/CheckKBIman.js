/**
 * 变更处理人按钮
 */	
Ext.define('erp.view.core.button.CheckKBIman',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCheckKBImanButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCheckKBImanButton,
    	style: {
    		marginLeft: '10px'
        },
        id:'checkKBIman',
        disabled:true,
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});