/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.File',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFileButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'filebtn',
    	text: $I18N.common.button.erpFileButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});