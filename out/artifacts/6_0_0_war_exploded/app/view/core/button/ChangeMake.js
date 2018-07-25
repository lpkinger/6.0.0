/**
 * SMT 上料feeder 切换 工单
 */	
Ext.define('erp.view.core.button.ChangeMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChangeMakeButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'changeMakeBtn',
    	text: $I18N.common.button.erpChangeMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});