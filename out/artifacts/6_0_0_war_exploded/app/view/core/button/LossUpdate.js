/**
 * 更新物料损耗率按钮
 */	
Ext.define('erp.view.core.button.LossUpdate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLossUpdateButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'lossupdatebutton',
    	text: $I18N.common.button.erpLossUpdateButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});