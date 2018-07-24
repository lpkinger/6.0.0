/**
 * 重置批号按钮
 */	
Ext.define('erp.view.core.button.Resetbatch',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResetbatchButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'resetbatch',
    	text: $I18N.common.button.erpResetbatchButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});