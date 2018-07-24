/**
 * 抓取工作内容
 */	
Ext.define('erp.view.core.button.CatchWorkContent',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchWorkContentButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'erpCatchWorkContentButton',
    	text: $I18N.common.button.erpCatchWorkContentButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});