/**
 * 加工方式
 */	
Ext.define('erp.view.core.button.ProcessingWay',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessingWayButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	id:'processingway',
    	text: $I18N.common.button.erpProcessingWayButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});