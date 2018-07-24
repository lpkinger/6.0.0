/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.LoadFitting',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadFittingButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpLoadFittingButton,
    	id:'loadFittingbutton',
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
		
	});