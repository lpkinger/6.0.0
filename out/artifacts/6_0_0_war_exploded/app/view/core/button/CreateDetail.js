/**
 * 生成按钮
 */	
Ext.define('erp.view.core.button.CreateDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateDetailButton',
		param: [],
		id: 'createDetail',
		text: $I18N.common.button.erpCreateDetailButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});