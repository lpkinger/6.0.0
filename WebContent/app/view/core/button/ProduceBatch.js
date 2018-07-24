/**
 * 产生批号按钮
 */	
Ext.define('erp.view.core.button.ProduceBatch',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProduceBatchButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'producebatch',
    	text: $I18N.common.button.erpProduceBatchButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});