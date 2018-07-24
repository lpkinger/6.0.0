/**
 * 转正式物料按钮
 */	
Ext.define('erp.view.core.button.Temporary',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTemporaryButton',
		iconCls: 'x-button-icon-delete',
		id: 'turnTemporary',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTemporaryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments);
		}
	});