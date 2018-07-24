/**
 * 删除导入失败工单
 */	
Ext.define('erp.view.core.button.CleanFailed',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCleanFailedButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'CleanFailed',
    	text: $I18N.common.button.erpCleanFailedButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});